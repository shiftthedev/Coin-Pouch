package com.shiftthedev.vaultcoinpouch.mixins.transmog;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.TransmogTableHelper;
import iskallia.vault.network.message.transmog.TransmogButtonMessage;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = TransmogButtonMessage.class, remap = false)
public class TransmogButtonMessageMixin
{
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private static void handle_impl(TransmogButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.transmogTableEnabled())
        {
            TransmogTableHelper.Handle(message, contextSupplier);
            ci.cancel();
            return;
        }
    }


}
