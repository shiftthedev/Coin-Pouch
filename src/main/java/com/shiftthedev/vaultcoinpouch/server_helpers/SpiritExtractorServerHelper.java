package com.shiftthedev.vaultcoinpouch.server_helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Iterator;

public class SpiritExtractorServerHelper
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

        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
        {
            ItemStack pouchStack = CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack();
            deductedAmount = Math.min(coinsRemaining, CoinPouchItem.getCoinCount(pouchStack, costStack));
            CoinPouchItem.extractCoins(pouchStack, costStack, deductedAmount);
            coinsRemaining -= deductedAmount;
        }

        if (coinsRemaining <= 0)
        {
            return;
        }

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

        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
        {
            ItemStack pouchStack = CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack();
            toRemove = Math.min(totalCost, CoinPouchItem.getCoinCount(pouchStack, costStack));
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
