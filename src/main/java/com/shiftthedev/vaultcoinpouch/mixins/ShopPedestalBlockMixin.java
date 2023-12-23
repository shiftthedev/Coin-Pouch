package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.ShopPedestalBlock;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Iterator;
import java.util.List;

@Mixin(value = ShopPedestalBlock.class, remap = false)
public class ShopPedestalBlockMixin {

    /**
     * @author ShiftTheDev
     * @reason Add Coin Pouch integration
     */
    @Overwrite
    private boolean hasEnoughCurrency(List<InventoryUtil.ItemAccess> allItems, ItemStack currency) {
        int amount = 0;
        Iterator var4 = allItems.iterator();

        while (var4.hasNext()) {
            InventoryUtil.ItemAccess itemAccess = (InventoryUtil.ItemAccess) var4.next();
            ItemStack stack = itemAccess.getStack();
            if (stack.is(currency.getItem())) {
                amount += stack.getCount();
                if (amount >= currency.getCount()) {
                    return true;
                }
            } else if (stack.is(VCPRegistry.COIN_POUCH)) {
                return CoinPouchItem.hasEnoughCoins(stack, currency);
            }
        }

        return false;
    }

    /**
     * @author ShiftTheDev
     * @reason Add Coin Pouch integration
     */
    @Overwrite
    private void extractCurrency(List<InventoryUtil.ItemAccess> allItems, ItemStack currency) {
        int required = currency.getCount();
        Iterator var4 = allItems.iterator();

        while (var4.hasNext()) {
            InventoryUtil.ItemAccess itemAccess = (InventoryUtil.ItemAccess) var4.next();
            ItemStack stack = itemAccess.getStack();
            if (stack.is(currency.getItem())) {
                int min = Math.min(required, stack.getCount());
                itemAccess.setStack(ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - min));
                required -= min;
                if (required <= 0) {
                    break;
                }
            } else if (stack.is(VCPRegistry.COIN_POUCH)) {
                required -= CoinPouchItem.extractCoins(stack, currency);
                itemAccess.setStack(stack.copy());
                if (required <= 0) {
                    break;
                }
            }
        }
    }
}
