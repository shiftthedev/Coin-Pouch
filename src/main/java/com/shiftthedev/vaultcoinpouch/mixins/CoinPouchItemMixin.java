package com.shiftthedev.vaultcoinpouch.mixins;

import iskallia.vault.item.CoinPouchItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(CoinPouchItem.class)
public class CoinPouchItemMixin {
    @Inject(method = "getTotalBronzeValue(Lnet/minecraft/world/entity/player/Inventory;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addCustomCoinPouchBronze(Inventory playerInventory, CallbackInfoReturnable<Integer> cir, AtomicInteger totalBronzeValue, Player player, int slot, ItemStack stack) {
        if (stack.getItem() instanceof com.shiftthedev.vaultcoinpouch.item.CoinPouchItem) {
            totalBronzeValue.addAndGet(com.shiftthedev.vaultcoinpouch.item.CoinPouchItem.getCoinCount(stack));
        }
    }

    @Inject(method = "getGoldAmount", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void addCustomCoinPouchGold(Inventory playerInventory, CallbackInfoReturnable<Integer> cir, AtomicInteger totalBronzeValue, Player player, int slot, ItemStack stack) {
        if (stack.getItem() instanceof com.shiftthedev.vaultcoinpouch.item.CoinPouchItem) {
            totalBronzeValue.addAndGet(com.shiftthedev.vaultcoinpouch.item.CoinPouchItem.getCoinCount(stack) / 81);
        }
    }
}
