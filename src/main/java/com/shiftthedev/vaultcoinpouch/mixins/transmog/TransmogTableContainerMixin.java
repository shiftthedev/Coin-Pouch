package com.shiftthedev.vaultcoinpouch.mixins.transmog;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.TransmogTableHelper;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TransmogTableContainer.class, remap = false)
public abstract class TransmogTableContainerMixin extends OverSizedSlotContainer
{
    @Inject(method = "priceFulfilled", at = @At("HEAD"), cancellable = true)
    private void priceFulfilled_impl(CallbackInfoReturnable<Boolean> cir)
    {
        if (VCPConfig.GENERAL.transmogTableEnabled())
        {
            cir.setReturnValue(TransmogTableHelper.PriceFulfilled(this.copperCost(), this.getSlot(this.internalInventoryIndexRange.getContainerIndex(1)), this.player));
            cir.cancel();
            return;
        }
    }

    @Shadow
    protected SlotIndexRange internalInventoryIndexRange;

    @Shadow
    public abstract int copperCost();

    protected TransmogTableContainerMixin(MenuType<?> menuType, int id, Player player)
    {
        super(menuType, id, player);
    }
}
