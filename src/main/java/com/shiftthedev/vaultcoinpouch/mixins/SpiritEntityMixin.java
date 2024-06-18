package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import iskallia.vault.entity.entity.SpiritEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SpiritEntity.class, remap = false)
public class SpiritEntityMixin
{
    @Inject(method = "shouldAddItem", at = @At("HEAD"), cancellable = true)
    private static void shouldAddItem_impl(ItemStack stack, CallbackInfoReturnable<Boolean> cir)
    {
        if (stack.is(VCPRegistry.COIN_POUCH) && !VCPConfig.GENERAL.soulboundEnabled())
        {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }
    }
}
