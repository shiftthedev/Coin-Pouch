package com.shiftthedev.vaultcoinpouch.server_helpers;

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

public class AlchemyTableServerHelper
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
}
