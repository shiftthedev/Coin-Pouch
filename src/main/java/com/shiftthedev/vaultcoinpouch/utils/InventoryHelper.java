package com.shiftthedev.vaultcoinpouch.utils;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.config.CoinData;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.Optional;

public class InventoryHelper {
    public static boolean pouchPickupCoin(Player player, ItemStack itemStack, Inventory thisInventory) {
        ItemStack pouchStack;
        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent()) {
            pouchStack = CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack();
        } else {
            Optional<ItemStack> possiblePouch = thisInventory.items.stream().filter(plStack -> plStack.is(VCPRegistry.COIN_POUCH)).findFirst();
            if (possiblePouch.isEmpty()) {
                return false;
            }
            pouchStack = possiblePouch.get();
        }
        pouchStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> handleCoinPouch(itemStack, iItemHandler));
        return itemStack.isEmpty();
    }

    private static void handleCoinPouch(ItemStack itemStack, IItemHandler iItemHandler) {
        ItemStack remainder = itemStack.copy();
        Optional<CoinData> itemData = VCPConfig.getCoinDataList().stream().filter(coinData -> coinData.coin_id.equals(Registry.ITEM.getKey(itemStack.getItem()).toString())).findFirst();
        if(itemData.isPresent())
        {
            remainder = iItemHandler.insertItem(itemData.get().index, itemStack, false);
        }
        itemStack.setCount(remainder.getCount());
    }
}
