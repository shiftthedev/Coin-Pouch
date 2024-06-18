package com.shiftthedev.vaultcoinpouch.helpers;

import com.mojang.authlib.GameProfile;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.SpiritExtractorContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.InventorySnapshot;
import iskallia.vault.world.data.PlayerSpiritRecoveryData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpiritExtractorHelper
{
    public static void SpewItems(Player player, boolean spewingItems, long spewingCooldownTime, OverSizedInventory paymentInventory, SpiritExtractorTileEntity.RecoveryCost recoveryCost,
                                 Level level, BlockPos blockPos, GameProfile gameProfile, float rescuedBonus, SpiritExtractorTileEntity.RecoveryCost cost, InventorySnapshot inventorySnapshot, OnRemovingSpirit onRemovingSpirit)
    {
        if (!spewingItems && coinsCoverTotalCost(paymentInventory, recoveryCost.getTotalCost(), player))
        {
            if (level.isClientSide())
            {
                spawnParticles(level, blockPos);
            }
            else
            {
                Level var3 = level;
                if (var3 instanceof ServerLevel)
                {
                    ServerLevel serverLevel = (ServerLevel) var3;
                    if (gameProfile != null)
                    {
                        PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevel);
                        data.increaseMultiplierOnRecovery(gameProfile.getId());
                        data.removeHeroDiscount(gameProfile.getId());
                    }
                }

                int coinsRemaining = recoveryCost.getTotalCost().getCount();
                coinsRemaining -= paymentInventory.getItem(0).getCount();
                paymentInventory.setItem(0, ItemStack.EMPTY);

                ItemStack costStack = recoveryCost.getTotalCost();
                int deductedAmount;
                NonNullList<ItemStack> pouchStacks = NonNullList.create();
                Iterator it = player.getInventory().items.iterator();
                while (it.hasNext())
                {
                    if (coinsRemaining <= 0)
                    {
                        break;
                    }

                    ItemStack plStack = (ItemStack) it.next();
                    if (plStack.is(ModBlocks.VAULT_GOLD))//ShiftInventoryUtils.isEqualCrafting(plStack, costStack))
                    {
                        deductedAmount = Math.min(coinsRemaining, plStack.getCount());
                        plStack.shrink(deductedAmount);
                        coinsRemaining -= deductedAmount;
                    }

                    if (plStack.is(VCPRegistry.COIN_POUCH))
                    {
                        pouchStacks.add(plStack);
                    }
                }

                it = pouchStacks.iterator();
                while (it.hasNext())
                {
                    if (coinsRemaining <= 0)
                    {
                        break;
                    }

                    ItemStack pouchStack = (ItemStack) it.next();
                    deductedAmount = Math.min(coinsRemaining, CoinPouchItem.getCoinCount(pouchStack, costStack));
                    CoinPouchItem.extractCoins(pouchStack, costStack, deductedAmount);
                    coinsRemaining -= deductedAmount;
                }

                rescuedBonus = 0.0F;
                recoveryCost = new SpiritExtractorTileEntity.RecoveryCost();
                if (inventorySnapshot != null && !inventorySnapshot.getItems().isEmpty())
                {
                    inventorySnapshot.apply(player);
                    inventorySnapshot = null;
                    onRemovingSpirit.onRemoveSpirit();
                }
                else
                {
                    spewingItems = true;
                    spewingCooldownTime = level.getGameTime() + 20L;
                }

                level.playSound((Player) null, blockPos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 0.5F);
            }
        }

    }

    private static boolean coinsCoverTotalCost(OverSizedInventory paymentInventory, ItemStack costStack, Player player)
    {
        int totalCost = costStack.getCount();
        if (totalCost <= 0)
        {
            return true;
        }

        int toRemove = 0;
        if (canMerge(costStack, paymentInventory.getItem(0)))
        {
            if (paymentInventory.getItem(0).getCount() >= totalCost)
            {
                return true;
            }

            toRemove = Math.min(totalCost, paymentInventory.getItem(0).getCount());
            totalCost -= toRemove;
        }

        if (totalCost <= 0)
        {
            return true;
        }

        Iterator it = player.getInventory().items.iterator();
        while (it.hasNext())
        {
            if (totalCost <= 0)
            {
                break;
            }

            ItemStack plStack = (ItemStack) it.next();
            if (canMerge(plStack, costStack))
            {
                toRemove = Math.min(totalCost, plStack.getCount());
                totalCost -= toRemove;
            }
            else if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                toRemove = Math.min(totalCost, CoinPouchItem.getCoinCount(plStack, costStack));
                totalCost -= toRemove;
            }
        }

        return totalCost <= 0;
    }

    public static boolean coinsCoverTotalCost(Slot slot, ItemStack costStack, Player player)
    {
        int totalCost = costStack.getCount();
        if (totalCost <= 0)
        {
            return true;
        }

        int toRemove = 0;
        if (canMerge(costStack, slot.getItem()))
        {
            if (slot.getItem().getCount() >= totalCost)
            {
                return true;
            }

            toRemove = Math.min(totalCost, slot.getItem().getCount());
            totalCost -= toRemove;
        }

        if (totalCost <= 0)
        {
            return true;
        }

        Iterator it = player.getInventory().items.iterator();
        while (it.hasNext())
        {
            if (totalCost <= 0)
            {
                break;
            }

            ItemStack plStack = (ItemStack) it.next();
            if (canMerge(plStack, costStack))
            {
                toRemove = Math.min(totalCost, plStack.getCount());
                totalCost -= toRemove;
            }
            else if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                toRemove = Math.min(totalCost, CoinPouchItem.getCoinCount(plStack, costStack));
                totalCost -= toRemove;
            }
        }

        return totalCost <= 0;
    }

    private static boolean canMerge(ItemStack stack, ItemStack other)
    {
        return stack.getItem() == other.getItem() && ItemStack.tagMatches(stack, other);
    }

    public static @NotNull List<Component> GetPurchaseButtonTooltipLines(SpiritExtractorContainer menu)
    {
        List<Component> purchaseButtonTooltips = new ArrayList();
        SpiritExtractorTileEntity.RecoveryCost recoveryCost = menu.getRecoveryCost();
        ItemStack totalCost = recoveryCost.getTotalCost();
        if (totalCost.getCount() > 0)
        {
            if (menu.hasSpirit())
            {
                purchaseButtonTooltips.add(new TextComponent("Cost for recovering items"));

                int paymentStackCount = menu.getSlot(36).getItem().getCount();

                Iterator it = menu.getPlayer().getInventory().items.iterator();
                while (it.hasNext())
                {
                    ItemStack plStack = (ItemStack) it.next();
                    if (plStack.is(ModBlocks.VAULT_GOLD))//ShiftInventoryUtils.isEqualCrafting(plStack, totalCost))
                    {
                        paymentStackCount += plStack.getCount();
                    }
                    else if (plStack.is(VCPRegistry.COIN_POUCH))
                    {
                        paymentStackCount += CoinPouchItem.getCoinCount(plStack, totalCost);
                    }
                }

                ChatFormatting textColor = paymentStackCount < totalCost.getCount() ? ChatFormatting.RED : ChatFormatting.YELLOW;
                float var10000 = (float) paymentStackCount / (float) totalCost.getCount();
                String percentString = (int) (var10000 * 100.0F) + "%";
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.total_cost", new Object[]{percentString, totalCost.getItem().getName(totalCost).getString(), totalCost.getCount()})).withStyle(textColor));
            }
            else
            {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.would_be_cost", new Object[]{totalCost.getCount(), totalCost.getItem().getName(totalCost).getString()})).withStyle(ChatFormatting.GREEN));
            }

            purchaseButtonTooltips.add(TextComponent.EMPTY);
            float baseCostCount = recoveryCost.getBaseCount();
            int levels = Math.max(1, menu.getPlayerLevel());
            purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.base_cost", new Object[]{String.format("%.0f", baseCostCount * (float) levels), String.format("%.2f", baseCostCount), levels})).withStyle(ChatFormatting.GRAY));
            recoveryCost.getStackCost().forEach((t) -> {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.item_cost", new Object[]{t.getB(), ((ItemStack) t.getA()).getHoverName()})).withStyle(ChatFormatting.GRAY));
            });
            float multiplier = menu.getMultiplier();
            if (!Mth.equal(multiplier, 1.0F))
            {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.multiplier", new Object[]{String.format("%.2f", multiplier)})).withStyle(ChatFormatting.GRAY));
            }

            float heroDiscount = menu.getHeroDiscount();
            if ((double) heroDiscount >= 0.01)
            {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.hero_discount", new Object[]{String.format("%.0f%%", heroDiscount * 100.0F)})).withStyle(ChatFormatting.GRAY));
            }

            float rescuedBonus = menu.getRescuedBonus();
            if (rescuedBonus > 0.0F)
            {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.rescued_bonus", new Object[]{String.format("%.0f%%", rescuedBonus * 100.0F)})).withStyle(ChatFormatting.GRAY));
            }
        }

        return purchaseButtonTooltips;
    }

    private static void spawnParticles(Level level, BlockPos blockPos)
    {
        int numberOfParticles = 15;

        for (int i = 0; i < numberOfParticles; ++i)
        {
            double x = (double) blockPos.getX() + level.random.nextDouble();
            double y = (double) blockPos.getY() + 0.5 + level.random.nextDouble() * 0.5;
            double z = (double) blockPos.getZ() + level.random.nextDouble();
            level.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, level.random.nextGaussian() * 0.02, level.random.nextGaussian() * 0.02, level.random.nextGaussian() * 0.02);
        }

    }

    public interface OnRemovingSpirit
    {
        void onRemoveSpirit();
    }
}
