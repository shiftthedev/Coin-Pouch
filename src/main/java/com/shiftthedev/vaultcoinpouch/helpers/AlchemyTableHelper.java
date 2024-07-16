package com.shiftthedev.vaultcoinpouch.helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.mixins.alchemy.AlchemyTableEffectCraftMessageAccessor;
import iskallia.vault.block.entity.AlchemyTableTileEntity;
import iskallia.vault.client.gui.framework.element.AlchemyCraftSelectorElement;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.container.AlchemyTableContainer;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.network.message.AlchemyTableEffectCraftMessage;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AlchemyTableHelper
{
    /**
     * Called in mixins/AlchemyTableEffectCraftMessageMixin
     **/
    public static void enqueueWork(NetworkEvent.Context context, AlchemyTableEffectCraftMessage message)
    {
        ServerPlayer player = context.getSender();
        BlockPos pos = ((AlchemyTableEffectCraftMessageAccessor) message).getPos();
        BlockEntity tile = player.getLevel().getBlockEntity(pos);
        if (tile instanceof AlchemyTableTileEntity alchemyTableTile)
        {
            ItemStack input = alchemyTableTile.getInventory().getItem(0);
            if (!input.isEmpty())
            {
                AlchemyTableConfig cfg = ModConfigs.VAULT_ALCHEMY_TABLE;
                ItemStack inputCopy = input.copy();
                List<ItemStack> cost = new ArrayList();
                AlchemyTableConfig.CraftableEffectConfig effectConfig = cfg.getConfig(((AlchemyTableEffectCraftMessageAccessor) message).getEffectId());
                if (effectConfig != null)
                {
                    if (effectConfig.hasPrerequisites(player))
                    {
                        Optional<BottleItem.Type> var10000 = BottleItem.getType(input);
                        if (var10000.isPresent())
                        {
                            Objects.requireNonNull(effectConfig);
                            BottleEffect createdEffect = effectConfig.createEffect(var10000.get()).orElse(null);
                            if (createdEffect != null)
                            {
                                cost.addAll(effectConfig.createCraftingCost(inputCopy));
                                List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(cost, player.getInventory());
                                if (missing.isEmpty())
                                {
                                    if (ShiftInventoryUtils.consumeInputs(cost, player.getInventory(), true))
                                    {
                                        if (ShiftInventoryUtils.consumeInputs(cost, player.getInventory(), false))
                                        {
                                            alchemyTableTile.startCrafting();
                                            BottleItem.setEffect(input, createdEffect);
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Called in mixins/AlchemyTableScreenMixin
     **/
    public static boolean tooltip(AlchemyCraftSelectorElement.CraftingOption selectedOption, Inventory playerInventory, AlchemyTableContainer menu, ITooltipRenderer tooltipRenderer, @NotNull PoseStack poseStack, int mouseX, int mouseY)
    {
        if (selectedOption == null)
        {
            return false;
        }
        else
        {
            ItemStack bottle = menu.getInput();
            if (bottle.isEmpty())
            {
                return false;
            }
            else
            {
                List<ItemStack> inputs = selectedOption.getCraftingCost(bottle);
                List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
                if (missing.isEmpty())
                {
                    List<Component> tooltip = new ArrayList();
                    tooltip.add(bottle.getHoverName());
                    BottleItem.getEffect(bottle).ifPresent(BottleEffect::getTooltip);
                    tooltipRenderer.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, TooltipDirection.RIGHT);
                    return true;
                }
                else
                {
                    Component cmp = (new TranslatableComponent("the_vault.gear_workbench.missing_inputs")).withStyle(ChatFormatting.RED);
                    tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                    return true;
                }
            }
        }
    }

    /**
     * Called in mixins/AlchemyTableScreenMixin
     **/
    public static boolean setDisabled_coinpouch(AlchemyTableContainer menu, AlchemyCraftSelectorElement.CraftingOption selectedOption, Inventory playerInventory)
    {
        ItemStack potion = menu.getInput();
        if (potion.isEmpty())
        {
            return true;
        }
        else if (selectedOption != null)
        {
            List<ItemStack> inputs = selectedOption.getCraftingCost(potion);
            List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
            return !missing.isEmpty();
        }
        else
        {
            return true;
        }
    }

    /**
     * Called in mixins/AlchemyTableScreenMixin
     **/
    public static boolean setDisabled_vh(AlchemyTableContainer menu, AlchemyCraftSelectorElement.CraftingOption selectedOption, Inventory playerInventory)
    {
        ItemStack potion = menu.getInput();
        if (potion.isEmpty())
        {
            return true;
        }
        else if (selectedOption != null)
        {
            List<ItemStack> inputs = selectedOption.getCraftingCost(potion);
            List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, playerInventory);
            return !missing.isEmpty();
        }
        else
        {
            return true;
        }
    }
}
