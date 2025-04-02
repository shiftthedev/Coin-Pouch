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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.MOD_ID;

public class VCPConfigScreen extends Screen
{
    private static List<TranslatableComponent> PAGES_TITLES = List.of(
            new TranslatableComponent("configs." + MOD_ID + ".page.interactions"),
            new TranslatableComponent("configs." + MOD_ID + ".page.soulbound"),
            new TranslatableComponent("configs." + MOD_ID + ".page.hud"));

    private Screen parent;
    private int pageIndex = 0;

    private HashMap<Integer, List<Button>> buttons = new HashMap<>();

    public VCPConfigScreen()
    {
        super(new TranslatableComponent("configs." + MOD_ID + ".title"));
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
        this.inti_header();
        this.init_interactions();
        this.init_soulbound();
        this.init_hud();

        buttons.get(pageIndex).forEach(button1 -> button1.visible = true);
    }

    private void inti_header()
    {
        int padding = 30;
        int widgetWidth = 170;
        int widgetHeight = 20;
        int y = 35;
        int xLeft = (this.width / 2) - widgetWidth - padding;
        int xRight = (this.width / 2) + padding;

        this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".page.prev"),
                button -> {
                    buttons.get(pageIndex).forEach(button1 -> button1.visible = false);

                    if (pageIndex - 1 < 0)
                    {
                        pageIndex = PAGES_TITLES.size() - 1;
                    }
                    else
                    {
                        pageIndex -= 1;
                    }

                    buttons.get(pageIndex).forEach(button1 -> button1.visible = true);
                })
        );

        this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".page.next"),
                button -> {
                    buttons.get(pageIndex).forEach(button1 -> button1.visible = false);

                    if (pageIndex + 1 >= PAGES_TITLES.size())
                    {
                        pageIndex = 0;
                    }
                    else
                    {
                        pageIndex += 1;
                    }

                    buttons.get(pageIndex).forEach(button1 -> button1.visible = true);
                })
        );
    }

    private void init_interactions()
    {
        int padding = 4;
        int widgetWidth = 170;
        int widgetHeight = 20;
        int y = 74;
        int xLeft = (this.width / 2) - widgetWidth - (padding / 2);
        int xRight = (this.width / 2) + (padding / 2);

        List<Button> buttonList = new ArrayList<>();

        // LINE 1
        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".shopPedestalInteraction.name", VCPConfig.GENERAL.shopPedestalEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleShopPedestal();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".shopPedestalInteraction.name", VCPConfig.GENERAL.shopPedestalEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".shopPedestalInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.add(this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".vaultForgeInteraction.name", VCPConfig.GENERAL.vaultForgeEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleVaultForge();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".vaultForgeInteraction.name", VCPConfig.GENERAL.vaultForgeEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".vaultForgeInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        // LINE 2
        y += widgetHeight + padding;

        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".toolStationInteraction.name", VCPConfig.GENERAL.toolStationEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleToolStation();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".toolStationInteraction.name", VCPConfig.GENERAL.toolStationEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".toolStationInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.add(this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".inscriptionTableInteraction.name", VCPConfig.GENERAL.inscriptionTableEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleInscriptionTable();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".inscriptionTableInteraction.name", VCPConfig.GENERAL.inscriptionTableEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".inscriptionTableInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        // LINE 3
        y += widgetHeight + padding;

        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".modifierWorkbenchInteraction.name", VCPConfig.GENERAL.modifierWorkbenchEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleModifierWorkbench();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".modifierWorkbenchInteraction.name", VCPConfig.GENERAL.modifierWorkbenchEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".modifierWorkbenchInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.add(this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".alchemyTableInteraction.name", VCPConfig.GENERAL.alchemyTableEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleAlchemyTable();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".alchemyTableInteraction.name", VCPConfig.GENERAL.alchemyTableEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".alchemyTableInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        // LINE 4
        y += widgetHeight + padding;

        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".transmogTableInteraction.name", VCPConfig.GENERAL.transmogTableEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleTransmogTable();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".transmogTableInteraction.name", VCPConfig.GENERAL.transmogTableEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".transmogTableInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.add(this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".vaultArtisanStationInteraction.name", VCPConfig.GENERAL.vaultArtisanStationEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleVaultArtisanStation();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".vaultArtisanStationInteraction.name", VCPConfig.GENERAL.vaultArtisanStationEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".vaultArtisanStationInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        // LINE 5
        y += widgetHeight + padding;

        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".jewelCuttingStationInteraction.name", VCPConfig.GENERAL.jewelCraftingTableEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleJewelCraftingStation();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".jewelCuttingStationInteraction.name", VCPConfig.GENERAL.jewelCraftingTableEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".jewelCuttingStationInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.add(this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".spiritExtractorInteraction.name", VCPConfig.GENERAL.spiritExtractorEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleSpiritExtractor();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".spiritExtractorInteraction.name", VCPConfig.GENERAL.spiritExtractorEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".spiritExtractorInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        // LINE 6
        y += widgetHeight + padding;

        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".paradoxDoorInteraction.name", VCPConfig.GENERAL.paradoxDoorsEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleParadoxDoors();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".paradoxDoorInteraction.name", VCPConfig.GENERAL.paradoxDoorsEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".paradoxDoorInteraction.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.forEach(button -> button.visible = false);
        buttons.put(0, buttonList);
    }

    private void init_soulbound()
    {
        int padding = 4;
        int widgetWidth = 170;
        int widgetHeight = 20;
        int y = 74;
        int xLeft = (this.width / 2) - widgetWidth - (padding / 2);
        int xRight = (this.width / 2) + (padding / 2);

        List<Button> buttonList = new ArrayList<>();

        // LINE 1
        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".soulbound.name", VCPConfig.GENERAL.soulboundEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleSoulbound();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".soulbound.name", VCPConfig.GENERAL.soulboundEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".soulbound.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.add(this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".shardPouchSoulbound.name", VCPConfig.GENERAL.shardPouchSoulboundEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleShardPouchSoulbound();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".shardPouchSoulbound.name", VCPConfig.GENERAL.shardPouchSoulboundEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".shardPouchSoulbound.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.forEach(button -> button.visible = false);
        buttons.put(1, buttonList);
    }

    private void init_hud()
    {
        int padding = 4;
        int widgetWidth = 170;
        int widgetHeight = 20;
        int y = 74;
        int xLeft = (this.width / 2) - widgetWidth - (padding / 2);
        int xRight = (this.width / 2) + (padding / 2);

        List<Button> buttonList = new ArrayList<>();

        // LINE 1
        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".showCoinCountInInventoryHud.name", VCPConfig.GENERAL.invCoinsEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleInvCoins();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".showCoinCountInInventoryHud.name", VCPConfig.GENERAL.invCoinsEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".showCoinCountInInventoryHud.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.add(this.addRenderableWidget(new Button(xRight, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".useShortCoinCountInInventoryHud.name", VCPConfig.GENERAL.invShortEnabled() ? "ON" : "OFF"),
                button -> {
                    VCPConfig.GENERAL.cycleInvShort();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".useShortCoinCountInInventoryHud.name", VCPConfig.GENERAL.invShortEnabled() ? "ON" : "OFF"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".useShortCoinCountInInventoryHud.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        // LINE 2
        y += widgetHeight + padding;

        buttonList.add(this.addRenderableWidget(new Button(xLeft, y, widgetWidth, widgetHeight,
                new TranslatableComponent("configs." + MOD_ID + ".horizontalAlignInInventoryHud.name", VCPConfig.GENERAL.invHorizontalEnabled() ? "HORIZONTAL" : "VERTICAL"),
                button -> {
                    VCPConfig.GENERAL.cycleHorizontal();
                    button.setMessage(new TranslatableComponent("configs." + MOD_ID + ".horizontalAlignInInventoryHud.name", VCPConfig.GENERAL.invHorizontalEnabled() ? "HORIZONTAL" : "VERTICAL"));
                },
                (button, poseStack, p_93755_, p_93756_) ->
                {
                    VCPConfigScreen.this.renderTooltip(
                            poseStack,
                            VCPConfigScreen.this.minecraft.font.split(
                                    new TranslatableComponent("configs." + MOD_ID + ".horizontalAlignInInventoryHud.tooltip"),
                                    Math.max((VCPConfigScreen.this.width / 2) - 43, 200)),
                            getTooltipX(p_93755_),
                            getTooltipY(p_93756_));
                })
        ));

        buttonList.forEach(button -> button.visible = false);
        buttons.put(2, buttonList);
    }

    private void init_footer()
    {
        this.addRenderableWidget(new Button((this.width / 2) - 140, this.height - 27, 100, 20, new TranslatableComponent("configs.vaultcoinpouch.save"),
                button -> {
                    this.minecraft.mouseHandler.grabMouse();
                    VCPConfig.saveCommonConfigs();
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
                    VCPConfig.reloadCommonConfig();
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
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 10, 16777215);
        drawCenteredString(poseStack, this.font, PAGES_TITLES.get(pageIndex), this.width / 2, 20, 16777215);
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
