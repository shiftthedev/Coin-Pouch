package com.shiftthedev.vaultcoinpouch.server_helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import iskallia.vault.init.ModBlocks;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class InventoryHelper
{
    public static boolean try_pickupCoinToPouch(Player player, ItemStack itemStack, Inventory thisInventory)
    {
        ItemStack pouchStack = ItemStack.EMPTY;

        AtomicReference<ItemStack> curiosStack = new AtomicReference<>(ItemStack.EMPTY);
        CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).ifPresent(slotResult -> curiosStack.set(slotResult.stack()));
        if(!curiosStack.get().isEmpty())
        {
            pouchStack = curiosStack.get();
        }
        else
        {
            Optional<ItemStack> possiblePouch = thisInventory.items.stream().filter(plStack -> plStack.is(VCPRegistry.COIN_POUCH)).findFirst();
            if (possiblePouch.isEmpty())
            {
                return false;
            }

            pouchStack = possiblePouch.get();
        }

        pouchStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> handleCoinPouch(itemStack, iItemHandler));
        if (itemStack.isEmpty())
        {
            return true;
        }

        return false;
    }

    private static void handleCoinPouch(ItemStack itemStack, IItemHandler iItemHandler)
    {
        ItemStack remainder = ItemStack.EMPTY;

        if (itemStack.getItem().asItem() == ModBlocks.BRONZE_COIN_PILE.asItem())
        {
            remainder = iItemHandler.insertItem(0, itemStack, false);
        }
        if (itemStack.getItem().asItem() == ModBlocks.SILVER_COIN_PILE.asItem())
        {
            remainder = iItemHandler.insertItem(1, itemStack, false);
        }
        if (itemStack.getItem().asItem() == ModBlocks.GOLD_COIN_PILE.asItem())
        {
            remainder = iItemHandler.insertItem(2, itemStack, false);
        }
        if (itemStack.getItem().asItem() == ModBlocks.PLATINUM_COIN_PILE.asItem())
        {
            remainder = iItemHandler.insertItem(3, itemStack, false);
        }

        itemStack.setCount(remainder.getCount());
    }
}
