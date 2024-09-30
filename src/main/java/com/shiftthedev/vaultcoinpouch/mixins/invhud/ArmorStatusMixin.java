package com.shiftthedev.vaultcoinpouch.mixins.invhud;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import dlovin.inventoryhud.armorstatus.ArmorStatus;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ArmorStatus.class, remap = false)
public class ArmorStatusMixin
{
    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    private static void getDamage_coinpouch(ItemStack stack, CallbackInfoReturnable<Integer> cir)
    {
        if (stack.is(VCPRegistry.COIN_POUCH) && VCPConfig.GENERAL.invCoinsEnabled())
        {
            cir.setReturnValue(1);
            cir.cancel();
            return;
        }
    }
}
