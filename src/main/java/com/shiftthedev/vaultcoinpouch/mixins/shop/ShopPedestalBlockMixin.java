package com.shiftthedev.vaultcoinpouch.mixins.shop;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.ShopPedestalHelper;
import iskallia.vault.block.ShopPedestalBlock;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ShopPedestalBlock.class, remap = false, priority = 900)
public abstract class ShopPedestalBlockMixin
{
    @Inject(method = "hasEnoughCurrency", at = @At("HEAD"), cancellable = true)
    private void hasEnoughtCurrency_coinpouch(List<InventoryUtil.ItemAccess> allItems, ItemStack currency, CallbackInfoReturnable<Boolean> cir)
    {
        if (VCPConfig.GENERAL.shopPedestalEnabled())
        {
            cir.setReturnValue(ShopPedestalHelper.hasEnoughCurrency(allItems, currency));
            cir.cancel();
            return;
        }
    }

    @Inject(method = "extractCurrency", at = @At("HEAD"), cancellable = true)
    private void extractCurrency_coinpouch(Player player, List<InventoryUtil.ItemAccess> allItems, ItemStack price, CallbackInfoReturnable<Boolean> cir)
    {
        if (VCPConfig.GENERAL.shopPedestalEnabled())
        {
            cir.setReturnValue(ShopPedestalHelper.extractCurrency(player, allItems, price));
            cir.cancel();
            return;
        }
    }
}
