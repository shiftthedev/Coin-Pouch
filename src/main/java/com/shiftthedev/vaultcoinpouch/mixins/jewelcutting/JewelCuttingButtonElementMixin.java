package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.JewelCuttingStationHelper;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.JewelCuttingButtonElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JewelCuttingButtonElement.class, remap = false, priority = 1100)
public abstract class JewelCuttingButtonElementMixin extends ButtonElement
{
    @Inject(method = "<init>", at = @At("RETURN"))
    private void inti_coinpouch(IPosition position, Runnable onClick, VaultJewelCuttingStationContainer container, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            this.tooltip(Tooltips.multi(() -> JewelCuttingStationHelper.tooltip(container)));
        }
    }

    public JewelCuttingButtonElementMixin(IPosition position, ButtonTextures textures, Runnable onClick)
    {
        super(position, textures, onClick);
    }
}
