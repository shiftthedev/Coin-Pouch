package com.shiftthedev.vaultcoinpouch.mixins.invhud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import dlovin.inventoryhud.gui.renderers.ArmorRenderer;
import dlovin.inventoryhud.gui.renderers.CuriosRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = CuriosRenderer.class, remap = false)
public abstract class CuriosRendererMixin extends ArmorRenderer {
    public CuriosRendererMixin(Minecraft mc) {
        super(mc);
    }

    @Inject(method = "pushAndRender", at = @At("HEAD"), cancellable = true)
    private void pushAndRender_coinpouch(PoseStack mat, int x, int y, boolean right, ResourceLocation resourceLocation, ItemStack itemStack, String text, boolean over, float scale, CallbackInfo ci) {
        if (itemStack != null && itemStack.is(VCPRegistry.COIN_POUCH) && VCPConfig.GENERAL.invCoinsEnabled()) {
            Font fontRenderer = ((ArmorRendererAccessor) this).getFontRenderer();

            // Draw coins
            ItemStack[] coinsArray = CoinPouchItem.getContainedStacks(itemStack);
            int coinX = 0;
            int coinY = 0;

            for (int i = 0; i < coinsArray.length; i++) {
                ItemStack coinStack = coinsArray[i];
                mat.pushPose();

                if (VCPConfig.GENERAL.invHorizontalEnabled()) {
                    coinX = x + (i * 14);
                    if (i % 2 != 0) {
                        coinY = y + 5;
                    } else {
                        coinY = y;
                    }
                } else {
                    coinX = x;
                    coinY = y + (i * 10);
                }

                mat.translate(coinX, coinY, 0.0);
                mat.scale(scale, scale, 1.0F);

                if (VCPConfig.GENERAL.invShortEnabled()) {
                    this.renderElement(mat, coinX, coinY, scale, right, resourceLocation, coinStack, VaultCoinPouch.formatCount(coinStack.getCount()), over, fontRenderer);
                } else {
                    this.renderElement(mat, coinX, coinY, scale, right, resourceLocation, coinStack, "" + coinStack.getCount(), over, fontRenderer);
                }

                mat.popPose();
            }

            ci.cancel();
            return;
        }
    }

    private void renderElement(PoseStack mat, int x, int y, float scale, boolean right, @Nullable ResourceLocation res, @Nullable ItemStack item, @Nullable String text, boolean overlay, Font fontRenderer) {
        ItemRenderer itemRenderer = ((ArmorRendererAccessor) this).getItemRenderer();

        if (res != null) {
            RenderSystem.setShaderTexture(0, res);
            blit(mat, 0, 0, 16.0F, 16.0F, 16, 16, 16, 16);
        } else {
            PoseStack matr = RenderSystem.getModelViewStack();
            matr.pushPose();
            matr.translate((double) x, (double) y, -256.0);
            matr.scale(scale, scale, 1.0F);
            RenderSystem.applyModelViewMatrix();
            assert item != null;
            itemRenderer.renderAndDecorateItem(item, 0, 0);
            if (overlay) {
                itemRenderer.renderGuiItemDecorations(fontRenderer, item, 0, 0, text);
            }

            matr.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private String getCount(int count) {
        if (count > 1000000000) {
            return Math.floorDiv(count, 1000000000) + "B";
        }

        if (count > 1000000) {
            return Math.floorDiv(count, 1000000) + "M";
        }

        if (count > 1000) {
            return Math.floorDiv(count, 1000) + "K";
        }

        return String.valueOf(count);
    }
}
