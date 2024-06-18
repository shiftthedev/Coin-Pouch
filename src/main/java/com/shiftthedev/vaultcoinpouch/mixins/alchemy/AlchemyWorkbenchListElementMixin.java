package com.shiftthedev.vaultcoinpouch.mixins.alchemy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.AlchemyTableHelper;
import iskallia.vault.client.gui.framework.element.AlchemyCraftSelectorElement;
import iskallia.vault.client.gui.framework.element.SelectableButtonElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = AlchemyCraftSelectorElement.WorkbenchListElement.class, remap = false)
public abstract class AlchemyWorkbenchListElementMixin<E extends AlchemyCraftSelectorElement.WorkbenchListElement<E>> extends SelectableButtonElement<E>
{
    @Shadow
    protected abstract List<ItemStack> getInputs();

    @Shadow
    private List<ItemStack> inputs;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render_impl(IElementRenderer renderer, PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.alchemyTableEnabled())
        {
            super.render(renderer, poseStack, mouseX, mouseY, partialTick);
            AlchemyTableHelper.Render(poseStack, this.worldSpatial, this.getInputs());
            ci.cancel();
            return;
        }
    }

    public AlchemyWorkbenchListElementMixin(IPosition position, ButtonTextures textures, Runnable onClick)
    {
        super(position, textures, onClick);
    }
}
