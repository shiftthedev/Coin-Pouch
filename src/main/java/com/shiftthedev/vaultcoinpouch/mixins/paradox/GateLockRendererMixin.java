package com.shiftthedev.vaultcoinpouch.mixins.paradox;

import com.shiftthedev.vaultcoinpouch.server_helpers.ShopPedestalHelper;
import iskallia.vault.block.render.GateLockRenderer;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = GateLockRenderer.class, remap = false, priority = 1100)
public class GateLockRendererMixin
{
    @Inject(method = "check", at = @At("HEAD"), cancellable = true)
    private void check_coinpouch(List<ItemStack> items, ItemStack stack, boolean simulate, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(ShopPedestalHelper.hasEnoughCurrency(InventoryUtil.findAllItems(Minecraft.getInstance().player), stack));
        cir.cancel();
        return;
    }
}
