package com.shiftthedev.vaultcoinpouch.mixins.spirit;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.SpiritExtractorHelper;
import com.shiftthedev.vaultcoinpouch.ui.ShiftSpiritDynamicLabel;
import com.shiftthedev.vaultcoinpouch.ui.ShiftSpiritItemSelectorWithTooltipEntry;
import com.shiftthedev.vaultcoinpouch.ui.ShiftSpiritRecycleLockButton;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.*;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.block.SpiritExtractorScreen;
import iskallia.vault.container.SpiritExtractorContainer;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = SpiritExtractorScreen.class, remap = false)
public abstract class SpiritExtractorScreenMixin extends AbstractElementContainerScreen<SpiritExtractorContainer>
{
    @Shadow
    protected abstract List<Component> getRecycleButtonTooltipLines();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init_impl(SpiritExtractorContainer container, Inventory inventory, Component title, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            this.removeAllElements();

            this.setGuiSize(Spatials.size(176, 182));
            this.addElement((NineSliceElement) (new NineSliceElement(Spatials.positionXY(0, -10).size(0, 10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui).size(Spatials.copy(gui).add(Spatials.size(0, 10)));
            }));
            this.addElement((LabelElement) (new LabelElement(Spatials.positionXY(7, -4), (new TextComponent("Spirit Extractor")).withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((ScrollableItemStackSelectorElement) (new ScrollableItemStackSelectorElement(Spatials.positionXY(7, 6).height(54), 8, new ScrollableItemStackSelectorElement.SelectorModel<ScrollableItemStackSelectorElement.ItemSelectorEntry>()
            {
                public List<ScrollableItemStackSelectorElement.ItemSelectorEntry> getEntries()
                {
                    return (List) ((SpiritExtractorContainer) SpiritExtractorScreenMixin.this.getMenu()).getStoredItems().stream().map(ShiftSpiritItemSelectorWithTooltipEntry::new).collect(Collectors.toList());
                }
            })).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            MutableComponent inventoryName = inventory.getDisplayName().copy();
            inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
            this.addElement((LabelElement) (new LabelElement(Spatials.positionXY(8, ((SpiritExtractorContainer) this.getMenu()).getSlot(0).y - 12), inventoryName, LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((SlotsElement) (new SlotsElement(this)).layout((screen, gui, parent, world) -> {
                world.positionXY(gui);
            }));
            ButtonElement<?> recycleButton = (ButtonElement) (new ButtonElement(Spatials.positionXY(8, 69), ScreenTextures.BUTTON_BUTTON_REROLL_TEXTURES, () -> {
                ((SpiritExtractorContainer) this.getMenu()).recycle();
            })).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            });
            recycleButton.setDisabled(() -> {
                return !((SpiritExtractorContainer) this.getMenu()).isRecycleUnlocked() || !((SpiritExtractorContainer) this.getMenu()).hasSpirit() || ((SpiritExtractorContainer) this.getMenu()).isSpewingItems();
            });
            recycleButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                tooltipRenderer.renderComponentTooltip(poseStack, this.getRecycleButtonTooltipLines(), mouseX, mouseY, TooltipDirection.RIGHT);
                return true;
            });
            this.addElement(recycleButton);
            ButtonElement<?> recycleLockButton = (ButtonElement) (new ShiftSpiritRecycleLockButton(Spatials.positionXY(28, 69), this.menu, () -> {
                ((SpiritExtractorContainer) this.getMenu()).toggleRecycleLock();
            })).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            });
            this.addElement(recycleLockButton);
            this.addElement((ShiftSpiritDynamicLabel) (new ShiftSpiritDynamicLabel(Spatials.positionXY(40, 74), () -> {
                return (new TranslatableComponent("screen.the_vault.spirit_extractor.total_revives", new Object[]{((SpiritExtractorContainer) this.getMenu()).getSpiritRecoveryCount()})).withStyle(Style.EMPTY.withColor(-12632257));
            }, LabelTextStyle.defaultStyle(), new TextComponent(""))).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((ShiftSpiritDynamicLabel) ((ShiftSpiritDynamicLabel) (new ShiftSpiritDynamicLabel(Spatials.positionXY(130, 90), () -> {
                return (new TextComponent(String.format("%.2f", ((SpiritExtractorContainer) this.getMenu()).getMultiplier()))).withStyle(Style.EMPTY.withColor(-12632257));
            }, LabelTextStyle.defaultStyle(), new TextComponent("0.00"))).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            })).tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                tooltipRenderer.renderComponentTooltip(poseStack, List.of(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.multiplier_explained")), mouseX, mouseY, TooltipDirection.RIGHT);
                return true;
            }));
            ButtonElement<?> purchaseButton = (ButtonElement) (new ButtonElement(Spatials.positionXY(150, 69), ScreenTextures.BUTTON_PAY_TEXTURES, () -> {
                ((SpiritExtractorContainer) this.getMenu()).startSpewingItems();
            })).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            });
            purchaseButton.setDisabled(() -> {
                return !this.menu.hasSpirit() || !SpiritExtractorHelper.coinsCoverTotalCost(this.menu.getSlot(36), this.menu.getTotalCost(), inventory.player) || ((SpiritExtractorContainer) this.getMenu()).isSpewingItems();
            });
            purchaseButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                tooltipRenderer.renderComponentTooltip(poseStack,
                        SpiritExtractorHelper.GetPurchaseButtonTooltipLines(this.getMenu()),
                        mouseX, mouseY, TooltipDirection.RIGHT);
                return true;
            });
            this.addElement(purchaseButton);
        }
    }

    public SpiritExtractorScreenMixin(SpiritExtractorContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<SpiritExtractorContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
