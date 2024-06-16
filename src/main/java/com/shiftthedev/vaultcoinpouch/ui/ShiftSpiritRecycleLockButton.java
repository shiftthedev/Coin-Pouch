package com.shiftthedev.vaultcoinpouch.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.container.SpiritExtractorContainer;
import org.jetbrains.annotations.NotNull;

public class ShiftSpiritRecycleLockButton extends ButtonElement<ShiftSpiritRecycleLockButton>
{
    private final SpiritExtractorContainer container;
    
    public ShiftSpiritRecycleLockButton(IPosition position, SpiritExtractorContainer container, Runnable onClick) {
        super(position, ScreenTextures.BUTTON_TOGGLE_OFF_TEXTURES, onClick);
        this.container = container;
    }

    public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        ButtonElement.ButtonTextures textures = this.container.isRecycleUnlocked() ? ScreenTextures.BUTTON_TOGGLE_ON_TEXTURES : ScreenTextures.BUTTON_TOGGLE_OFF_TEXTURES;
        TextureAtlasRegion texture = textures.selectTexture(this.isDisabled(), this.containsMouse((double)mouseX, (double)mouseY), false);
        renderer.render(texture, poseStack, this.worldSpatial);
    }
}
