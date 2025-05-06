package com.shiftthedev.vaultcoinpouch.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.MOD_ID;

public class CoinPouchScreen extends AbstractContainerScreen<CoinPouchContainer>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/coin_pouch.png");

    private int slotStartX = 0;

    public CoinPouchScreen(CoinPouchContainer screenContainer, Inventory inventory, Component titleIn)
    {
        super(screenContainer, inventory, titleIn);
        this.imageWidth = 176;
        this.imageHeight = 137;
        this.titleLabelY = 3;
        this.titleLabelX = 3;
        this.inventoryLabelY = 45;
    }

    @Override
    protected void init()
    {
        super.init();

        slotStartX = this.leftPos + 8;
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float partialTicks, int x, int y)
    {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int offsetX = (this.width - this.imageWidth) / 2;
        int offsetY = (this.height - this.imageHeight) / 2;
        this.blit(poseStack, offsetX, offsetY, 0, 0, this.imageWidth, this.imageHeight);

        int slots = this.menu.coinSlotCount();
        for (int i = 0; i < slots; i++)
        {
            this.blit(poseStack, slotStartX + (i * 20), this.topPos + (i % 2 == 0 ? 16 : 24), 0, 144, 18, 18);
        }
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
