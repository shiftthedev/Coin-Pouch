package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.network.ShiftAlchemyTableEffectCraftMessage;
import com.shiftthedev.vaultcoinpouch.network.ShiftModifierWorkbenchCraftMessage;
import com.shiftthedev.vaultcoinpouch.network.ShiftVaultForgeRequestCraftMessage;
import iskallia.vault.init.ModNetwork;
import net.minecraftforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModNetwork.class, remap = false)
public abstract class ModNetworkMixin
{
    @Shadow
    @Final
    public static SimpleChannel CHANNEL;

    @Inject(method = "initialize", at = @At("RETURN"))
    private static void initialize_impl(CallbackInfo ci)
    {
        CHANNEL.registerMessage(ModNetwork.nextId(), ShiftVaultForgeRequestCraftMessage.class, ShiftVaultForgeRequestCraftMessage::encode, ShiftVaultForgeRequestCraftMessage::decode, ShiftVaultForgeRequestCraftMessage::handle);
        CHANNEL.registerMessage(ModNetwork.nextId(), ShiftModifierWorkbenchCraftMessage.class, ShiftModifierWorkbenchCraftMessage::encode, ShiftModifierWorkbenchCraftMessage::decode, ShiftModifierWorkbenchCraftMessage::handle);
        CHANNEL.registerMessage(ModNetwork.nextId(), ShiftAlchemyTableEffectCraftMessage.class, ShiftAlchemyTableEffectCraftMessage::encode, ShiftAlchemyTableEffectCraftMessage::decode, ShiftAlchemyTableEffectCraftMessage::handle);
    }
}
