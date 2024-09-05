package com.shiftthedev.vaultcoinpouch.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import com.shiftthedev.vaultcoinpouch.mixins.shop.ShopPedestalBlockMixin;
import com.shiftthedev.vaultcoinpouch.server_helpers.ShopPedestalHelper;
import iskallia.vault.block.ShopPedestalBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Iterator;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    protected abstract boolean canRenderCrosshairForSpectator(HitResult p_93025_);

    @Shadow
    @Final
    protected ItemRenderer itemRenderer;

    @Shadow
    protected int screenWidth;

    @Shadow
    protected int screenHeight;

    private final static ItemStack GoldStack = new ItemStack(ModBlocks.GOLD_COIN_PILE, 1);

    @Inject(method = "renderCrosshair", at = @At("TAIL"))
    private void renderCrosshair_coinpouch(PoseStack p, CallbackInfo ci)
    {
        if (this.minecraft.options.getCameraType().isFirstPerson())
        {
            if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult))
            {
                if (this.minecraft.hitResult.getType() == HitResult.Type.BLOCK)
                {
                    BlockPos blockPos = ((BlockHitResult) this.minecraft.hitResult).getBlockPos();
                    if (isValidTarget(blockPos))
                    {
                        int count = getCoinsCount(GoldStack);

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
                        this.itemRenderer.renderAndDecorateItem(this.minecraft.player, GoldStack, x, y, x + y);
                        this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, GoldStack, x, y, getFormattedCount(count));
                        this.itemRenderer.blitOffset = 0F;

                        posestack.popPose();
                        RenderSystem.applyModelViewMatrix();
                        RenderSystem.enableDepthTest();
                    }
                }
            }
        }
    }

    private boolean isValidTarget(BlockPos blockPos) 
    {
        BlockState blockState = this.minecraft.level.getBlockState(blockPos);
        return (blockState.is(ModBlocks.SHOP_PEDESTAL) && this.minecraft.level.getBlockState(blockPos).getValue(ShopPedestalBlock.ACTIVE)) 
                || blockState.is(ModBlocks.GATE_LOCK);
    }

    private int getCoinsCount(ItemStack coinType)
    {
        Player player = this.minecraft.player;
        int count = 0;

        Iterator it = InventoryUtil.findAllItems(player).iterator();
        while(it.hasNext())
        {
            ItemStack stack = ((InventoryUtil.ItemAccess) it.next()).getStack();
            if (!stack.isEmpty())
            {
                if (stack.is(VCPRegistry.COIN_POUCH))
                {
                    count += CoinPouchItem.getCoinCount(stack, coinType);
                }
                else if(stack.is(ModBlocks.GOLD_COIN_PILE.asItem()))
                {
                    count += stack.getCount();
                }
                else
                {
                    count += (ShopPedestalHelper.getCoinDefinition(stack.getItem()).map(shiftCoinDefinition -> {
                        return shiftCoinDefinition.coinValue() * stack.getCount();
                    }).orElse(0)) / 81;
                }
            }
        }

        return count;
    }

    private String getFormattedCount(int count)
    {
        if (count > 1000000000)
        {
            return Math.floorDiv(count, 1000000000) + "B";
        }

        if (count > 1000000)
        {
            return Math.floorDiv(count, 1000000) + "M";
        }

        if (count > 1000)
        {
            return Math.floorDiv(count, 1000) + "K";
        }

        return String.valueOf(count);
    }
}
