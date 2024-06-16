package com.shiftthedev.vaultcoinpouch.mixins.modifier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.element.SelectableButtonElement;
import iskallia.vault.client.gui.framework.element.WorkbenchCraftSelectorElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.helper.LightmapHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = WorkbenchCraftSelectorElement.WorkbenchListElement.class, remap = false)
public abstract class WorkbenchListElementMixin<E extends WorkbenchCraftSelectorElement.WorkbenchListElement<E>> extends SelectableButtonElement<E>
{
    @Shadow
    protected abstract List<ItemStack> getInputs();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render_impl(IElementRenderer renderer, PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci)
    {
        if(VCPConfig.GENERAL.modifierWorkbenchEnabled())
        {
            shift_render(renderer, poseStack, mouseX, mouseY, partialTick);
            ci.cancel();
            return;
        }
    }
    
    private void shift_render(IElementRenderer renderer, PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        super.render(renderer, poseStack, mouseX, mouseY, partialTick);
        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
        Font font = Minecraft.getInstance().font;
        int offsetX = this.worldSpatial.x() + this.worldSpatial.width() - 18;
        int offsetY = this.worldSpatial.y() + this.worldSpatial.height() - 18;
        List<ItemStack> inputs = this.getInputs();
        List<ItemStack> missingInputs = new ArrayList();
        if (Minecraft.getInstance().player != null) {
            missingInputs = ShiftInventoryUtils.getMissingInputs(inputs, Minecraft.getInstance().player.getInventory());
        }

        for(Iterator var12 = inputs.iterator(); var12.hasNext(); offsetX -= 17) {
            ItemStack stack = (ItemStack)var12.next();
            ir.renderGuiItem(stack, offsetX, offsetY);
            MutableComponent text = new TextComponent(String.valueOf(stack.getCount()));
            if (((List)missingInputs).contains(stack)) {
                text.withStyle(ChatFormatting.RED);
            }

            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 200.0);
            MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(text, (float)(offsetX + 17 - font.width(text)), (float)(offsetY + 9), 16777215, true, poseStack.last().pose(), buffers, false, 0, LightmapHelper.getPackedFullbrightCoords());
            buffers.endBatch();
            poseStack.popPose();
        }
    }
    
    public WorkbenchListElementMixin(IPosition position, ButtonTextures textures, Runnable onClick)
    {
        super(position, textures, onClick);
    }
}
