package com.shiftthedev.vaultcoinpouch.mixins.artisan;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.VaultArtisanStationHelper;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(value = GearModificationAction.class, remap = false)
public abstract class GearModificationActionMixin
{
    @Shadow
    @Nullable
    public abstract Slot getCorrespondingSlot(VaultArtisanStationContainer container);

    @Shadow
    public abstract GearModification modification();

    @Shadow
    @Final
    private static Random rand;

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void apply_impl(VaultArtisanStationContainer container, ServerPlayer player, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.vaultArtisanStationEnabled())
        {
            VaultArtisanStationHelper.Apply(container, player, this.getCorrespondingSlot(container), this.modification(), this.rand);
            ci.cancel();
            return;
        }
    }

    @Inject(method = "canApply", at = @At("HEAD"), cancellable = true)
    private void canApply_impl(VaultArtisanStationContainer container, Player player, CallbackInfoReturnable<Boolean> cir)
    {
        if (VCPConfig.GENERAL.vaultArtisanStationEnabled())
        {
            cir.setReturnValue(VaultArtisanStationHelper.CanApply(container, player, this.getCorrespondingSlot(container), this.modification(), this.rand));
            cir.cancel();
            return;
        }
    }
}
