package com.shiftthedev.vaultcoinpouch.mixins.artisan;

import com.shiftthedev.vaultcoinpouch.client.elements.CoinPouchElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.screen.block.VaultArtisanStationScreen;
import iskallia.vault.container.VaultArtisanStationContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultArtisanStationScreen.class, remap = false, priority = 1100)
public abstract class VaultArtisanStationScreenMixin extends AbstractElementContainerScreen<VaultArtisanStationContainer>
{
    public VaultArtisanStationScreenMixin(VaultArtisanStationContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<VaultArtisanStationContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init_coinpouch(VaultArtisanStationContainer container, Inventory inventory, Component title, CallbackInfo ci)
    {
        this.addElement((CoinPouchElement) (new CoinPouchElement(Spatials.positionXYZ(-30, 128, 20), () -> {
            return Spatials.positionXY(-8, this.topPos + 14);
        }, inventory.player)));
    }
}
