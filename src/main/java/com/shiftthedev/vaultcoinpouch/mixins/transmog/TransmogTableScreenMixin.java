package com.shiftthedev.vaultcoinpouch.mixins.transmog;

import com.shiftthedev.vaultcoinpouch.client.elements.CoinPouchElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.screen.block.TransmogTableScreen;
import iskallia.vault.container.TransmogTableContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TransmogTableScreen.class, remap = false, priority = 1100)
public abstract class TransmogTableScreenMixin extends AbstractElementContainerScreen<TransmogTableContainer>
{
    public TransmogTableScreenMixin(TransmogTableContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<TransmogTableContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init_coinpouch(TransmogTableContainer container, Inventory inventory, Component title, CallbackInfo ci)
    {
        this.addElement((CoinPouchElement) (new CoinPouchElement(Spatials.positionXYZ(-30, 64, 20), () -> {
            return Spatials.positionXY(-8, this.topPos + 14);
        }, inventory.player)));
    }
}
