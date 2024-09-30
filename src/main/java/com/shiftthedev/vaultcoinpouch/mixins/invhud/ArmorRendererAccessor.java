package com.shiftthedev.vaultcoinpouch.mixins.invhud;

import dlovin.inventoryhud.gui.renderers.ArmorRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ArmorRenderer.class, remap = false)
public interface ArmorRendererAccessor
{
    @Accessor("itemRenderer")
    public ItemRenderer getItemRenderer();

    @Accessor("fontRenderer")
    public Font getFontRenderer();
}
