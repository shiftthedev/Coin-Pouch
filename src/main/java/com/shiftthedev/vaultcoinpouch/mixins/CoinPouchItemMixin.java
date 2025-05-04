package com.shiftthedev.vaultcoinpouch.mixins;

import iskallia.vault.item.CoinPouchItem;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CoinPouchItem.class, remap = false)
public class CoinPouchItemMixin {
    @Inject(method = "getTotalBronzeValue(Lnet/minecraft/world/entity/player/Inventory;)I", at = @At("RETURN"), cancellable = true)
    private static void addCustomCoinPouchBronze(Inventory playerInventory, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() + com.shiftthedev.vaultcoinpouch.item.CoinPouchItem.getCoinCount(playerInventory));
    }

    @Inject(method = "getGoldAmount", at = @At(value = "RETURN"), cancellable = true)
    private static void addCustomCoinPouchGold(Inventory playerInventory, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValueI() + (com.shiftthedev.vaultcoinpouch.item.CoinPouchItem.getCoinCount(playerInventory) / 81));
    }
}
