package com.shiftthedev.vaultcoinpouch.mixins.artisan;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.VaultArtisanStationHelper;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.ModificationButtonElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.modification.GearModification;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = ModificationButtonElement.class, remap = false, priority = 900)
public abstract class ModificationButtonElementMixin extends ButtonElement
{
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init_coinpouch(IPosition position, Runnable onClick, VaultArtisanStationContainer container, GearModification modification, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.vaultArtisanStationEnabled())
        {
            this.tooltip(Tooltips.multi(() -> VaultArtisanStationHelper.tooltip(container, modification, rand)));
        }
    }

    @Shadow
    @Final
    private static Random rand;

    public ModificationButtonElementMixin(IPosition position, ButtonTextures textures, Runnable onClick)
    {
        super(position, textures, onClick);
    }
}
