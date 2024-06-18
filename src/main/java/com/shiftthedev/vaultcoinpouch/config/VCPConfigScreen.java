package com.shiftthedev.vaultcoinpouch.config;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
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
    }

    @Override
    protected void init()
    {
        super.init();
        this.init_footer();
        this.init_options();
    }

    private void init_options()
    {
        int padding = 4;
        int widgetWidth = 170;
        int widgetHeight = 20;
        int y = 50;
        int xLeft = (this.width / 2) - widgetWidth - (padding / 2);
        int xRight = (this.width / 2) + (padding / 2);

        this.addRenderableWidget(new Button((this.width / 2) - 75, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.soulbound.name", VCPConfig.GENERAL.soulboundEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleSoulbound();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.soulbound.name", VCPConfig.GENERAL.soulboundEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.soulbound.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        // LINE 1
        y += widgetHeight + padding;

        this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.shopPedestalInteraction.name", VCPConfig.GENERAL.shopPedestalEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleShopPedestal();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.shopPedestalInteraction.name", VCPConfig.GENERAL.shopPedestalEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.shopPedestalInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.vaultForgeInteraction.name", VCPConfig.GENERAL.vaultForgeEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleVaultForge();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.vaultForgeInteraction.name", VCPConfig.GENERAL.vaultForgeEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.vaultForgeInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        // LINE 2
        y += widgetHeight + padding;

        this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.toolStationInteraction.name", VCPConfig.GENERAL.toolStationEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleToolStation();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.toolStationInteraction.name", VCPConfig.GENERAL.toolStationEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.toolStationInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.inscriptionTableInteraction.name", VCPConfig.GENERAL.inscriptionTableEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleInscriptionTable();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.inscriptionTableInteraction.name", VCPConfig.GENERAL.inscriptionTableEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.inscriptionTableInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        // LINE 3
        y += widgetHeight + padding;

        this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.modifierWorkbenchInteraction.name", VCPConfig.GENERAL.modifierWorkbenchEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleModifierWorkbench();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.modifierWorkbenchInteraction.name", VCPConfig.GENERAL.modifierWorkbenchEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.modifierWorkbenchInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.alchemyTableInteraction.name", VCPConfig.GENERAL.alchemyTableEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleAlchemyTable();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.alchemyTableInteraction.name", VCPConfig.GENERAL.alchemyTableEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.alchemyTableInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        // LINE 4
        y += widgetHeight + padding;

        this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.transmogTableInteraction.name", VCPConfig.GENERAL.transmogTableEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleTransmogTable();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.transmogTableInteraction.name", VCPConfig.GENERAL.transmogTableEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.transmogTableInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.vaultArtisanStationInteraction.name", VCPConfig.GENERAL.vaultArtisanStationEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleVaultArtisanStation();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.vaultArtisanStationInteraction.name", VCPConfig.GENERAL.vaultArtisanStationEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.vaultArtisanStationInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        // LINE 5
        y += widgetHeight + padding;

        this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.jewelCuttingStationInteraction.name", VCPConfig.GENERAL.jewelCuttingStationEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleJewelCuttingStation();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.jewelCuttingStationInteraction.name", VCPConfig.GENERAL.jewelCuttingStationEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.jewelCuttingStationInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs.vaultcoinpouch.spiritExtractorInteraction.name", VCPConfig.GENERAL.spiritExtractorEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleSpiritExtractor();
                    button.setMessage(new TranslatableComponent("configs.vaultcoinpouch.spiritExtractorInteraction.name", VCPConfig.GENERAL.spiritExtractorEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.spiritExtractorInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );
    }

    private void init_footer()
    {
        this.addRenderableWidget(new Button((this.width / 2) - 140, this.height - 27, 100, 20, new TranslatableComponent("configs.vaultcoinpouch.save"),
                button -> {
                    this.minecraft.mouseHandler.grabMouse();
                    VCPConfig.saveConfig();
                    onClose();
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.save.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );

        this.addRenderableWidget(new Button((this.width / 2) + 50, this.height - 27, 100, 20, new TranslatableComponent("configs.vaultcoinpouch.cancel"),
                button -> {
                    this.minecraft.mouseHandler.grabMouse();
                    VCPConfig.reloadConfig();
                    onClose();
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs.vaultcoinpouch.cancel.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        );
    }

    private int getTooltipX(int mouseX)
    {
        return mouseX < (this.width / 2) ? mouseX : mouseX + 10;
    }

    private int getTooltipY(int mouseY)
    {
        return mouseY < (this.height / 2) ? mouseY + 20 : mouseY;
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
        if (this.parent != null)
        {
            Minecraft.getInstance().setScreen(this.parent);
        }
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
        bufferbuilder.vertex(0.0, (double) this.height - 32.0, 0.0).uv(0.0F, (float) this.height / 32.0F + (float) pVOffset).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex((double) this.width, (double) this.height - 32.0, 0.0).uv((float) this.width / 32.0F, (float) this.height / 32.0F + (float) pVOffset).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex((double) this.width, 32.0, 0.0).uv((float) this.width / 32.0F, (float) pVOffset).color(32, 32, 32, 255).endVertex();
        bufferbuilder.vertex(0.0, 32.0, 0.0).uv(0.0F, (float) pVOffset).color(32, 32, 32, 255).endVertex();
        tesselator.end();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(0.0, 36.0, 0.0).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex((double) this.width, 36.0, 0.0).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex((double) this.width, 32.0, 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(0.0, 32.0, 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex(0.0, (double) (this.height - 32), 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex((double) this.width, (double) (this.height - 32), 0.0).color(0, 0, 0, 255).endVertex();
        bufferbuilder.vertex((double) this.width, (double) (this.height - 36), 0.0).color(0, 0, 0, 0).endVertex();
        bufferbuilder.vertex(0.0, (double) (this.height - 36), 0.0).color(0, 0, 0, 0).endVertex();
        tesselator.end();
    }
}
