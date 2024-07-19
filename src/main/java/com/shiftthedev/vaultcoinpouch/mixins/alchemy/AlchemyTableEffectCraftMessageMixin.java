package com.shiftthedev.vaultcoinpouch.mixins.alchemy;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.server_helpers.AlchemyTableServerHelper;
import iskallia.vault.network.message.AlchemyTableEffectCraftMessage;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = AlchemyTableEffectCraftMessage.class, remap = false, priority = 1100)
public abstract class AlchemyTableEffectCraftMessageMixin
{
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private static void handle_coinpouch(AlchemyTableEffectCraftMessage message, Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.alchemyTableEnabled())
        {
            NetworkEvent.Context context = (NetworkEvent.Context) contextSupplier.get();
            context.enqueueWork(() -> AlchemyTableServerHelper.enqueueWork(context, message));
            context.setPacketHandled(true);

            ci.cancel();
            return;
        }
    }
}
