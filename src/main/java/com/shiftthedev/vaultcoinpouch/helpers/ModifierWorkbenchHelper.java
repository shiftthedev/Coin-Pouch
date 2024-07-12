package com.shiftthedev.vaultcoinpouch.helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.mixins.modifier.ModifierWorkbenchCraftMessageAccessor;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.network.message.ModifierWorkbenchCraftMessage;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModifierWorkbenchHelper
{
    /**
     * Called in mixins/ModifierWorkbenchCraftMessageMixin
     **/
    public static void enqueueWork(NetworkEvent.Context context, ModifierWorkbenchCraftMessage message)
    {
        ServerPlayer player = context.getSender();
        BlockPos pos = ((ModifierWorkbenchCraftMessageAccessor) message).getPos();
        BlockEntity tile = player.getLevel().getBlockEntity(pos);
        if (tile instanceof ModifierWorkbenchTileEntity workbenchTile)
        {
            ItemStack input = workbenchTile.getInventory().getItem(0);
            if (!input.isEmpty() && input.getItem() instanceof VaultGearItem && AttributeGearData.hasData(input))
            {
                if (VaultGearData.read(input).isModifiable())
                {
                    VaultGearWorkbenchConfig.getConfig(input.getItem()).ifPresent((cfg) -> getConfig(input, message, cfg, player, tile));
                }
            }
        }
    }

    /**
     * Called in mixins/ModifierWorkbenchScreenMixin
     **/
    public static boolean tooltip(ModifierWorkbenchContainer menu, iskallia.vault.gear.crafting.ModifierWorkbenchHelper.CraftingOption selectedOption, Inventory playerInventory, ITooltipRenderer tooltipRenderer, @NotNull PoseStack poseStack, int mouseX, int mouseY)
    {
        if (selectedOption == null)
        {
            return false;
        }
        else
        {
            ItemStack gear = menu.getInput();
            if (gear.isEmpty())
            {
                return false;
            }
            else if (AttributeGearData.hasData(gear) && !AttributeGearData.read(gear).isModifiable())
            {
                Component cmp = (new TranslatableComponent("the_vault.gear_modification.unmodifiable")).withStyle(ChatFormatting.RED);
                tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                return true;
            }
            else
            {
                List<ItemStack> inputs = selectedOption.getCraftingCost(gear);
                List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
                if (missing.isEmpty())
                {
                    List<Component> tooltip = new ArrayList();
                    tooltip.add(gear.getHoverName());
                    Item patt5771$temp = gear.getItem();
                    if (patt5771$temp instanceof VaultGearTooltipItem)
                    {
                        VaultGearTooltipItem gearTooltipItem = (VaultGearTooltipItem) patt5771$temp;
                        tooltip.addAll(gearTooltipItem.createTooltip(gear, GearTooltip.craftingView()));
                    }

                    tooltipRenderer.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, TooltipDirection.RIGHT);
                    return true;
                }
                else
                {
                    Component cmpx = (new TranslatableComponent("the_vault.gear_workbench.missing_inputs")).withStyle(ChatFormatting.RED);
                    tooltipRenderer.renderTooltip(poseStack, cmpx, mouseX, mouseY, TooltipDirection.RIGHT);
                    return true;
                }
            }
        }
    }

    /**
     * Called in mixins/ModifierWorkbenchScreenMixin
     **/
    public static boolean setDisabled_coinpouch(ModifierWorkbenchContainer menu, iskallia.vault.gear.crafting.ModifierWorkbenchHelper.CraftingOption selectedOption, Inventory playerInventory)
    {
        ItemStack gear = menu.getInput();
        if (gear.isEmpty())
        {
            return true;
        }
        else if (selectedOption != null)
        {
            List<ItemStack> inputs = selectedOption.getCraftingCost(gear);
            List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
            return !missing.isEmpty();
        }
        else
        {
            return true;
        }
    }

    /**
     * Called in mixins/ModifierWorkbenchScreenMixin
     **/
    public static boolean setDisabled_vh(ModifierWorkbenchContainer menu, iskallia.vault.gear.crafting.ModifierWorkbenchHelper.CraftingOption selectedOption, Inventory playerInventory)
    {
        ItemStack gear = menu.getInput();
        if (gear.isEmpty())
        {
            return true;
        }
        else if (selectedOption != null)
        {
            List<ItemStack> inputs = selectedOption.getCraftingCost(gear);
            List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, playerInventory);
            return !missing.isEmpty();
        }
        else
        {
            return true;
        }
    }


    private static void getConfig(ItemStack input, ModifierWorkbenchCraftMessage message, VaultGearWorkbenchConfig cfg, ServerPlayer player, BlockEntity tile)
    {
        ItemStack inputCopy = input.copy();
        VaultGearModifier.AffixType targetAffix = null;
        VaultGearModifier<?> createdModifier = null;
        List<ItemStack> cost = new ArrayList();
        if (((ModifierWorkbenchCraftMessageAccessor) message).getCraftModifierIdentifier() == null)
        {
            if (!iskallia.vault.gear.crafting.ModifierWorkbenchHelper.hasCraftedModifier(inputCopy))
            {
                return;
            }

            cost.addAll(cfg.getCostRemoveCraftedModifiers());
        }
        else
        {
            VaultGearWorkbenchConfig.CraftableModifierConfig modifierConfig = cfg.getConfig(((ModifierWorkbenchCraftMessageAccessor) message).getCraftModifierIdentifier());
            if (modifierConfig == null)
            {
                return;
            }

            if (!modifierConfig.hasPrerequisites(player))
            {
                return;
            }

            boolean hadCraftedModifiers = iskallia.vault.gear.crafting.ModifierWorkbenchHelper.hasCraftedModifier(inputCopy);
            iskallia.vault.gear.crafting.ModifierWorkbenchHelper.removeCraftedModifiers(inputCopy);
            VaultGearData data = VaultGearData.read(inputCopy);
            if (data.getItemLevel() < modifierConfig.getMinLevel())
            {
                return;
            }

            targetAffix = modifierConfig.getAffixGroup().getTargetAffixType();
            if (targetAffix == VaultGearModifier.AffixType.PREFIX)
            {
                if (!VaultGearModifierHelper.hasOpenPrefix(inputCopy))
                {
                    return;
                }
            }
            else if (!VaultGearModifierHelper.hasOpenSuffix(inputCopy))
            {
                return;
            }

            createdModifier = (VaultGearModifier) modifierConfig.createModifier().orElse(null);
            if (createdModifier == null)
            {
                return;
            }

            Set<String> existingModGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
            if (existingModGroups.contains(createdModifier.getModifierGroup()))
            {
                return;
            }

            cost.addAll(modifierConfig.createCraftingCost(inputCopy));
            if (hadCraftedModifiers)
            {
                cost.addAll(cfg.getCostRemoveCraftedModifiers());
            }
        }

        List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(cost, player.getInventory());
        if (missing.isEmpty())
        {
            if (ShiftInventoryUtils.consumeInputs(cost, player.getInventory(), true))
            {
                if (ShiftInventoryUtils.consumeInputs(cost, player.getInventory(), false))
                {
                    if (createdModifier == null)
                    {
                        iskallia.vault.gear.crafting.ModifierWorkbenchHelper.removeCraftedModifiers(input);
                    }
                    else
                    {
                        createdModifier.setCategory(VaultGearModifier.AffixCategory.CRAFTED);
                        createdModifier.setGameTimeAdded(player.getLevel().getGameTime());
                        iskallia.vault.gear.crafting.ModifierWorkbenchHelper.removeCraftedModifiers(input);
                        VaultGearData datax = VaultGearData.read(input);
                        datax.addModifier(targetAffix, createdModifier);
                        datax.write(input);
                    }

                    player.getLevel().levelEvent(1030, tile.getBlockPos(), 0);
                }

            }
        }
    }
}
