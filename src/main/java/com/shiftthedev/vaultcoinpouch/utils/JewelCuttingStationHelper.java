package com.shiftthedev.vaultcoinpouch.utils;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.util.MiscUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;

public class JewelCuttingStationHelper
{
    public static boolean canCraft(VaultJewelCuttingStationTileEntity tileEntity, Player player)
    {
        VaultJewelCuttingConfig.JewelCuttingOutput output = tileEntity.getRecipeOutput();
        VaultJewelCuttingConfig.JewelCuttingInput input = tileEntity.getRecipeInput();
        OverSizedInventory inventory = tileEntity.getInventory();
        
        if(input == null || output == null)
            return false;
        
        if(!VaultJewelCuttingStationTileEntity.canMerge(inventory.getItem(0), input.getMainInput()))
            return false;
        if(inventory.getItem(0).getCount() < input.getMainInput().getCount())
            return false;

        if(!hasGold(input.getSecondInput(), inventory.getItem(1), player))
            return false;
        
        if(!MiscUtils.canFullyMergeIntoSlot(inventory, 2, output.getMainOutputMatching()))
            return false;
        if(!MiscUtils.canFullyMergeIntoSlot(inventory, 3, output.getExtraOutput1Matching()))
            return false;
        
        return MiscUtils.canFullyMergeIntoSlot(inventory, 4, output.getExtraOutput2Matching());
    }
    
    private static boolean hasGold(ItemStack goldInput, ItemStack goldInventory, Player player)
    {
        int goldMissing = goldInput.getCount();
        if(VaultJewelCuttingStationTileEntity.canMerge(goldInventory, goldInput))
        {
            if(goldInventory.getCount() >= goldMissing)
                return true;

            goldMissing -= goldInput.getCount();
        }

        Iterator it = player.getInventory().items.iterator();
        int toRemove = 0;
        while(it.hasNext())
        {
            if(goldMissing <= 0)
                break;
            
            ItemStack plStack = (ItemStack) it.next();
            if(VaultJewelCuttingStationTileEntity.canMerge(plStack, goldInput))
            {
                toRemove = Math.min(goldMissing, plStack.getCount());
                goldMissing -= toRemove;
            }
            else if(plStack.is(VCPRegistry.COIN_POUCH))
            {
                toRemove = Math.min(goldMissing, CoinPouchItem.getCoinCount(plStack, goldInput));
                goldMissing -= toRemove;
            }
        }
        
        return goldMissing <= 0;
    }
}
