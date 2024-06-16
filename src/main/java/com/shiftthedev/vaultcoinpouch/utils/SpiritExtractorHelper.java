package com.shiftthedev.vaultcoinpouch.utils;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.container.oversized.OverSizedInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public class SpiritExtractorHelper
{
    public static boolean coinsCoverTotalCost(OverSizedInventory paymentInventory, ItemStack costStack, Player player)
    {
        int totalCost = costStack.getCount();
        if(totalCost <= 0)
            return true;
        
        int toRemove = 0;
        if(canMerge(costStack, paymentInventory.getItem(0)))
        {
            if(paymentInventory.getItem(0).getCount() >= totalCost)
                return true;
            
            toRemove = Math.min(totalCost, paymentInventory.getItem(0).getCount());
            totalCost -= toRemove;
        }
        
        if(totalCost <= 0)
        {
            return true;
        }

        Iterator it = player.getInventory().items.iterator();
        while(it.hasNext())
        {
            if(totalCost <= 0)
                break;
            
            ItemStack plStack = (ItemStack) it.next();
            if(canMerge(plStack, costStack))
            {
                toRemove = Math.min(totalCost, plStack.getCount());
                totalCost -= toRemove;
            }
            else if(plStack.is(VCPRegistry.COIN_POUCH))
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
        if(totalCost <= 0)
            return true;

        int toRemove = 0;
        if(canMerge(costStack, slot.getItem()))
        {
            if(slot.getItem().getCount() >= totalCost)
                return true;

            toRemove = Math.min(totalCost, slot.getItem().getCount());
            totalCost -= toRemove;
        }

        if(totalCost <= 0)
        {
            return true;
        }

        Iterator it = player.getInventory().items.iterator();
        while(it.hasNext())
        {
            if(totalCost <= 0)
                break;

            ItemStack plStack = (ItemStack) it.next();
            if(canMerge(plStack, costStack))
            {
                toRemove = Math.min(totalCost, plStack.getCount());
                totalCost -= toRemove;
            }
            else if(plStack.is(VCPRegistry.COIN_POUCH))
            {
                toRemove = Math.min(totalCost, CoinPouchItem.getCoinCount(plStack, costStack));
                totalCost -= toRemove;
            }
        }

        return totalCost <= 0;
    }

    private static boolean canMerge(ItemStack stack, ItemStack other) {
        return stack.getItem() == other.getItem() && ItemStack.tagMatches(stack, other);
    }
}
