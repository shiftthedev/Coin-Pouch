package com.shiftthedev.vaultcoinpouch.helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.SpiritExtractorContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public class SpiritExtractorHelper
{
    /**
     * Called in mixins/SpiritExtractorTileEntityMixin
     **/
    public static void withdraw(SpiritExtractorTileEntity.RecoveryCost recoveryCost, OverSizedInventory paymentInventory, Player player)
    {
        int coinsRemaining = recoveryCost.getTotalCost().getCount();
        coinsRemaining -= paymentInventory.getItem(0).getCount();

        if (coinsRemaining <= 0)
        {
            return;
        }

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
            if (plStack.is(ModBlocks.VAULT_GOLD))
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

        if (coinsRemaining <= 0)
        {
            return;
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
    }

    /**
     * Called in mixins/SpiritExtractorTileEntityMixin
     **/
    public static boolean coinsCoverTotalCost(OverSizedInventory paymentInventory, ItemStack costStack, Player player)
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

    /**
     * Called in mixins/SpiritExtractorScreenMixin
     **/
    public static boolean setDisabled_coinpouch(SpiritExtractorContainer menu, Inventory inventory)
    {
        return !menu.hasSpirit() ||
                !SpiritExtractorHelper.coinsCoverTotalCost(menu.getSlot(36), menu.getTotalCost(), inventory.player) ||
                menu.isSpewingItems();
    }

    /**
     * Called in mixins/SpiritExtractorScreenMixin
     **/
    public static boolean setDisabled_vh(SpiritExtractorContainer menu)
    {
        return !menu.coinsCoverTotalCost() || menu.isSpewingItems();
    }

    /**
     * Called in mixins/SpiritExtractorScreenMixin
     **/
    public static int getPouchCoinsCount(SpiritExtractorContainer menu)
    {
        SpiritExtractorTileEntity.RecoveryCost recoveryCost = menu.getRecoveryCost();
        ItemStack totalCost = recoveryCost.getTotalCost();
        int paymentStackCount = menu.getSlot(36).getItem().getCount();

        Iterator it = menu.getPlayer().getInventory().items.iterator();
        while (it.hasNext())
        {
            ItemStack plStack = (ItemStack) it.next();
            if (plStack.is(ModBlocks.VAULT_GOLD))
            {
                paymentStackCount += plStack.getCount();
            }
            else if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                paymentStackCount += CoinPouchItem.getCoinCount(plStack, totalCost);
            }
        }

        return paymentStackCount;
    }

    private static boolean coinsCoverTotalCost(Slot slot, ItemStack costStack, Player player)
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
}
