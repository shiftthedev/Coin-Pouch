package com.shiftthedev.vaultcoinpouch.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import iskallia.vault.block.ShopPedestalBlock;
import iskallia.vault.init.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Unique private static ItemStack goldStack = ItemStack.EMPTY;

    @Shadow @Final protected Minecraft minecraft;
    @Shadow @Final protected ItemRenderer itemRenderer;
    @Shadow protected int screenWidth;
    @Shadow protected int screenHeight;

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void renderShopGold(PoseStack p, CallbackInfo ci) {
        if (!this.minecraft.options.getCameraType().isFirstPerson()
                || (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR && !canRenderCrosshairForSpectator(this.minecraft.hitResult))
                || !(this.minecraft.hitResult instanceof BlockHitResult block)
                || !isValidTarget(block.getBlockPos())) {
            return;
        }

        p.pushPose();
        RenderSystem.disableDepthTest();
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        this.itemRenderer.blitOffset = 100F;
        RenderSystem.enableDepthTest();

        int x = (this.screenWidth / 2);
        int y = (this.screenHeight / 2);

        if (goldStack == ItemStack.EMPTY) {
            goldStack = new ItemStack(ModBlocks.GOLD_COIN_PILE, 1);
        }

        int count = iskallia.vault.item.CoinPouchItem.getGoldAmount(this.minecraft.player.getInventory());
        this.itemRenderer.renderAndDecorateItem(this.minecraft.player, goldStack, x, y, x + y);
        this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, goldStack, x, y, VaultCoinPouch.formatCount(count));
        this.itemRenderer.blitOffset = 0F;

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
        p.popPose();
    }

    @Unique
    private boolean isValidTarget(BlockPos blockPos) {
        BlockState blockState = this.minecraft.level.getBlockState(blockPos);
        return (blockState.is(ModBlocks.SHOP_PEDESTAL) && this.minecraft.level.getBlockState(blockPos).getValue(ShopPedestalBlock.ACTIVE))
                || blockState.is(ModBlocks.GATE_LOCK);
    }

    @Shadow
    protected abstract boolean canRenderCrosshairForSpectator(HitResult p_93025_);
}
