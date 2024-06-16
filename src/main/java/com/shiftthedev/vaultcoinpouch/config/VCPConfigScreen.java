package com.shiftthedev.vaultcoinpouch.config;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import iskallia.vault.client.gui.framework.element.spi.ILayoutStrategy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public class VCPConfigScreen extends Screen
{
    private Screen parent;
 
    public VCPConfigScreen()
    {
        super(new TranslatableComponent("configs.vaultcoinpouch.title"));
    }
    
    public void setup(Minecraft minecraft, Screen parent)
    {
        this.minecraft = minecraft;
        this.parent = parent;
        
        //super(new TranslatableComponent("configs.vaultcoinpouch.title"), ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
        //Window window = Minecraft.getInstance().getWindow();
        //this.setGuiSize(Spatials.size(window.getGuiScaledWidth(), window.getGuiScaledHeight()));
        //int padding = 4;
        //int widgetWidth = 150;
        //int widgetHeight = 20;
        //int startX = 0;
        //int startY = 0;

        //this.addElement((ToggleButtonElement)(new ToggleButtonElement(Spatials.positionXY(startX, startY).size(widgetWidth, widgetHeight), new TranslatableComponent("configs.vaultcoinpouch.soulbound.name"), () -> {
        //    return VCPConfig.GENERAL.soulboundEnabled() ? "ON" : "OFF";
        //}, () -> {
        //    VCPConfig.GENERAL.cycleSoulbound();
        //}).tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) ->
        //{
        //    tooltipRenderer.renderTooltip(poseStack, new TranslatableComponent("configs.vaultcoinpouch.soulbound.tooltip"), mouseX, mouseY, TooltipDirection.RIGHT);
        //    return true;   
        //})
        //).layout(this.translateWorldSpatial()));
    }
    
    @Override
    protected void init()
    {
        super.init();
        this.addRenderableWidget(new Button(this.width / 2 - 102, this.height / 4 + 24 + -16, 204, 20, new TextComponent("teste"),
                button -> {
                    this.minecraft.mouseHandler.grabMouse();
                    //VCPConfig.saveConfig();
                    onClose();
                }));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose()
    {
        super.onClose();
        if(this.parent != null)
            Minecraft.getInstance().setScreen(this.parent);
    }
    
    @Override
    public void renderDirtBackground(int pVOffset)
    {
        super.renderDirtBackground(pVOffset);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0, (double)this.height - 32.0, 0.0).uv(0.0F, (float)this.height / 32.0F + (float)pVOffset).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex((double)this.width, (double)this.height - 32.0, 0.0).uv((float)this.width / 32.0F, (float)this.height / 32.0F + (float)pVOffset).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex((double)this.width, 32.0, 0.0).uv((float)this.width / 32.0F, (float)pVOffset).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(0.0, 32.0, 0.0).uv(0.0F, (float)pVOffset).color(32, 32, 32, 255).endVertex();
        tesselator.end();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(0.0, 36.0, 0.0).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex((double)this.width, 36.0, 0.0).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex((double)this.width, 32.0, 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(0.0, 32.0, 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(0.0, (double)(this.height - 32), 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex((double)this.width, (double)(this.height - 32), 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex((double)this.width, (double)(this.height - 36), 0.0).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex(0.0, (double)(this.height - 36), 0.0).color(0, 0, 0, 0).endVertex();
        tesselator.end();
    }

    private @NotNull ILayoutStrategy translateWorldSpatial()
    {
        return (screen, gui, parent, world) -> {
            Window window = Minecraft.getInstance().getWindow();
            world.translateXY(window.getGuiScaledWidth() / 2 - 150 - 4, 36);
        };
    }
}
