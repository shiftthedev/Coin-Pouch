package com.shiftthedev.vaultcoinpouch.server_helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.util.MiscUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Iterator;

public class JewelCuttingStationServerHelper
{
    /**
     * Called in mixins/VaultJewelCuttingStationTileEntityMixin
     **/
    public static void withdraw(VaultJewelCuttingStationContainer container, ServerPlayer player, VaultJewelCuttingConfig.JewelCuttingInput recipeInput)
    {
        int bronzeCount = container.getBronzeSlot().getItem().getCount();
        ItemStack secondInput = recipeInput.getSecondInput();
        int recipeCount = secondInput.getCount();
        int remaining = recipeCount - bronzeCount;

        if (remaining <= 0)
        {
            return;
        }

        NonNullList<ItemStack> pouchStacks = NonNullList.create();
        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
        {
            pouchStacks.add(CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack());
        }

        Iterator it = player.getInventory().items.iterator();
        int toRemove = 0;
        while (it.hasNext())
        {
            if (remaining <= 0)
            {
                break;
            }

            ItemStack plStack = (ItemStack) it.next();
            if (VaultJewelCuttingStationTileEntity.canMerge(plStack, secondInput))
            {
                toRemove = Math.min(remaining, plStack.getCount());
                plStack.shrink(toRemove);
                remaining -= toRemove;
            }

            if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                pouchStacks.add(plStack);
            }
        }

        if (remaining <= 0)
        {
            return;
        }

        it = pouchStacks.iterator();
        while (it.hasNext())
        {
            if (remaining <= 0)
            {
                break;
            }

            ItemStack pouchStack = (ItemStack) it.next();
            toRemove = Math.min(remaining, CoinPouchItem.getCoinCount(pouchStack, secondInput));
            CoinPouchItem.extractCoins(pouchStack, secondInput, toRemove);
            remaining -= toRemove;
        }
    }

    /**
     * Called in mixins/VaultJewelCuttingStationTileEntityMixin
     **/
    public static boolean canCraft(VaultJewelCuttingStationTileEntity tileEntity, ServerPlayer player)
    {
        VaultJewelCuttingConfig.JewelCuttingOutput output = tileEntity.getRecipeOutput();
        VaultJewelCuttingConfig.JewelCuttingInput input = tileEntity.getRecipeInput();
        OverSizedInventory inventory = tileEntity.getInventory();

        if (input == null || output == null)
        {
            return false;
        }

        if (!VaultJewelCuttingStationTileEntity.canMerge(inventory.getItem(0), input.getMainInput()))
        {
            return false;
        }
        if (inventory.getItem(0).getCount() < input.getMainInput().getCount())
        {
            return false;
        }

        if (!hasGold(input.getSecondInput(), inventory.getItem(1), player))
        {
            return false;
        }

        if (!MiscUtils.canFullyMergeIntoSlot(inventory, 2, output.getMainOutputMatching()))
        {
            return false;
        }
        if (!MiscUtils.canFullyMergeIntoSlot(inventory, 3, output.getExtraOutput1Matching()))
        {
            return false;
        }

        return MiscUtils.canFullyMergeIntoSlot(inventory, 4, output.getExtraOutput2Matching());
    }

    private static boolean hasGold(ItemStack goldInput, ItemStack goldInventory, ServerPlayer player)
    {
        int goldMissing = goldInput.getCount();
        if (VaultJewelCuttingStationTileEntity.canMerge(goldInventory, goldInput))
        {
            if (goldInventory.getCount() >= goldMissing)
            {
                return true;
            }

            goldMissing -= goldInput.getCount();
        }

        Iterator it = player.getInventory().items.iterator();
        int toRemove = 0;
        while (it.hasNext())
        {
            if (goldMissing <= 0)
            {
                break;
            }

            ItemStack plStack = (ItemStack) it.next();
            if (VaultJewelCuttingStationTileEntity.canMerge(plStack, goldInput))
            {
                toRemove = Math.min(goldMissing, plStack.getCount());
                goldMissing -= toRemove;
            }
            else if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                toRemove = Math.min(goldMissing, CoinPouchItem.getCoinCount(plStack, goldInput));
                goldMissing -= toRemove;
            }
        }

        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
        {
            goldMissing -= CoinPouchItem.getCoinCount(CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack(), goldInput);
        }

        return goldMissing <= 0;
    }
}
