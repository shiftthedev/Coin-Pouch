package com.shiftthedev.vaultcoinpouch.client.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

public class StacksElement<E extends StacksElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement
{
    private static final TextureAtlasRegion SLOT_BACKGROUND = ScreenTextures.INSET_ITEM_SLOT_BACKGROUND;

    private ItemRenderer itemRenderer;
    private int coinpouchSlot;
    private Player player;
    private boolean fromCurios;
    private boolean visible;

    public StacksElement(IPosition position, Player player, int coinpouchSlot)
    {
        super(Spatials.positionXYZ(position).size(18, 72));
        this.coinpouchSlot = coinpouchSlot;
        this.player = player;
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();

        if(coinpouchSlot == -1)
        {
            fromCurios = CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent();
        }
        
        this.setVisible(coinpouchSlot != -1 || fromCurios);
    }

    @Override
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    @Override
    public boolean isVisible()
    {
        return this.visible;
    }

    @Override
    public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        ItemStack[] coins = getCoins();
        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = coins[i];
            renderer.render(SLOT_BACKGROUND, poseStack, this.worldSpatial.x() + 5, (i * 18) + this.worldSpatial.y() + 5, this.worldSpatial.z());

            if (!stack.isEmpty())
            {
                this.renderItemStack(stack, this.worldSpatial.x() + 6, (i * 18) + this.worldSpatial.y() + 6, this.worldSpatial.z() + 1);
            }
        }
    }
    
    private ItemStack[] getCoins()
    {
        if(this.fromCurios)
        {
            return CoinPouchItem.getContainedStacks(CuriosApi.getCuriosHelper().findFirstCurio(this.player, VCPRegistry.COIN_POUCH).get().stack());
        }
            
        return CoinPouchItem.getContainedStacks(player.getInventory().getItem(this.coinpouchSlot));
    }

    private void renderItemStack(ItemStack itemStack, int x, int y, int z)
    {
        RenderSystem.disableDepthTest();
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        this.itemRenderer.blitOffset = 100F;
        RenderSystem.enableDepthTest();
        assert Minecraft.getInstance().player != null;
        this.itemRenderer.renderAndDecorateItem(Minecraft.getInstance().player, itemStack, x, y, x + y);
        this.itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, itemStack, x, y, getCount(itemStack.getCount()));
        this.itemRenderer.blitOffset = 0F;

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.enableDepthTest();
    }

    private String getCount(int count)
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
