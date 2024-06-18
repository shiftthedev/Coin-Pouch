package com.shiftthedev.vaultcoinpouch.mixins.modifier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.ModifierWorkbenchHelper;
import iskallia.vault.client.gui.framework.element.SelectableButtonElement;
import iskallia.vault.client.gui.framework.element.WorkbenchCraftSelectorElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = WorkbenchCraftSelectorElement.WorkbenchListElement.class, remap = false)
public abstract class ModifierWorkbenchListElementMixin<E extends WorkbenchCraftSelectorElement.WorkbenchListElement<E>> extends SelectableButtonElement<E>
{
    @Shadow
    protected abstract List<ItemStack> getInputs();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render_impl(IElementRenderer renderer, PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.modifierWorkbenchEnabled())
        {
            super.render(renderer, poseStack, mouseX, mouseY, partialTick);
            ModifierWorkbenchHelper.Render(poseStack, this.worldSpatial, this.getInputs());
            ci.cancel();
            return;
        }
    }

    public ModifierWorkbenchListElementMixin(IPosition position, ButtonTextures textures, Runnable onClick)
    {
        super(position, textures, onClick);
    }
}
