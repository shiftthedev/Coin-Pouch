package com.shiftthedev.vaultcoinpouch.mixins.modifier;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.ModifierWorkbenchHelper;
import iskallia.vault.network.message.ModifierWorkbenchCraftMessage;
import net.minecraftforge.network.NetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = ModifierWorkbenchCraftMessage.class, remap = false, priority = 1100)
public abstract class ModifierWorkbenchCraftMessageMixin
{
    @Inject(method = "handle", at = @At("HEAD"), cancellable = true)
    private static void handle_coinpouch(ModifierWorkbenchCraftMessage message, Supplier<NetworkEvent.Context> contextSupplier, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.modifierWorkbenchEnabled())
        {
            NetworkEvent.Context context = (NetworkEvent.Context) contextSupplier.get();
            context.enqueueWork(() -> ModifierWorkbenchHelper.enqueueWork(context, message));
            context.setPacketHandled(true);

            ci.cancel();
            return;
        }
    }
}
