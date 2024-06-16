package com.shiftthedev.vaultcoinpouch.mixins.spirit;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import com.shiftthedev.vaultcoinpouch.ui.ShiftSpiritDynamicLabel;
import com.shiftthedev.vaultcoinpouch.ui.ShiftSpiritItemSelectorWithTooltipEntry;
import com.shiftthedev.vaultcoinpouch.ui.ShiftSpiritRecycleLockButton;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import com.shiftthedev.vaultcoinpouch.utils.SpiritExtractorHelper;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Iterator;
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
        if(VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            this.removeAllElements();
            
            this.setGuiSize(Spatials.size(176, 182));
            this.addElement((NineSliceElement)(new NineSliceElement(Spatials.positionXY(0, -10).size(0, 10), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui).size(Spatials.copy(gui).add(Spatials.size(0, 10)));
            }));
            this.addElement((LabelElement)(new LabelElement(Spatials.positionXY(7, -4), (new TextComponent("Spirit Extractor")).withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((ScrollableItemStackSelectorElement)(new ScrollableItemStackSelectorElement(Spatials.positionXY(7, 6).height(54), 8, new ScrollableItemStackSelectorElement.SelectorModel<ScrollableItemStackSelectorElement.ItemSelectorEntry>() {
                public List<ScrollableItemStackSelectorElement.ItemSelectorEntry> getEntries() {
                    return (List)((SpiritExtractorContainer)SpiritExtractorScreenMixin.this.getMenu()).getStoredItems().stream().map(ShiftSpiritItemSelectorWithTooltipEntry::new).collect(Collectors.toList());
                }
            })).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            MutableComponent inventoryName = inventory.getDisplayName().copy();
            inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
            this.addElement((LabelElement)(new LabelElement(Spatials.positionXY(8, ((SpiritExtractorContainer)this.getMenu()).getSlot(0).y - 12), inventoryName, LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((SlotsElement)(new SlotsElement(this)).layout((screen, gui, parent, world) -> {
                world.positionXY(gui);
            }));
            ButtonElement<?> recycleButton = (ButtonElement)(new ButtonElement(Spatials.positionXY(8, 69), ScreenTextures.BUTTON_BUTTON_REROLL_TEXTURES, () -> {
                ((SpiritExtractorContainer)this.getMenu()).recycle();
            })).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            });
            recycleButton.setDisabled(() -> {
                return !((SpiritExtractorContainer)this.getMenu()).isRecycleUnlocked() || !((SpiritExtractorContainer)this.getMenu()).hasSpirit() || ((SpiritExtractorContainer)this.getMenu()).isSpewingItems();
            });
            recycleButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                tooltipRenderer.renderComponentTooltip(poseStack, this.getRecycleButtonTooltipLines(), mouseX, mouseY, TooltipDirection.RIGHT);
                return true;
            });
            this.addElement(recycleButton);
            ButtonElement<?> recycleLockButton = (ButtonElement)(new ShiftSpiritRecycleLockButton(Spatials.positionXY(28, 69), this.menu, () -> {
                ((SpiritExtractorContainer)this.getMenu()).toggleRecycleLock();
            })).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            });
            this.addElement(recycleLockButton);
            this.addElement((ShiftSpiritDynamicLabel)(new ShiftSpiritDynamicLabel(Spatials.positionXY(40, 74), () -> {
                return (new TranslatableComponent("screen.the_vault.spirit_extractor.total_revives", new Object[]{((SpiritExtractorContainer)this.getMenu()).getSpiritRecoveryCount()})).withStyle(Style.EMPTY.withColor(-12632257));
            }, LabelTextStyle.defaultStyle(), new TextComponent(""))).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((ShiftSpiritDynamicLabel)((ShiftSpiritDynamicLabel)(new ShiftSpiritDynamicLabel(Spatials.positionXY(130, 90), () -> {
                return (new TextComponent(String.format("%.2f", ((SpiritExtractorContainer)this.getMenu()).getMultiplier()))).withStyle(Style.EMPTY.withColor(-12632257));
            }, LabelTextStyle.defaultStyle(), new TextComponent("0.00"))).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            })).tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                tooltipRenderer.renderComponentTooltip(poseStack, List.of(new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.multiplier_explained")), mouseX, mouseY, TooltipDirection.RIGHT);
                return true;
            }));
            ButtonElement<?> purchaseButton = (ButtonElement)(new ButtonElement(Spatials.positionXY(150, 69), ScreenTextures.BUTTON_PAY_TEXTURES, () -> {
                ((SpiritExtractorContainer)this.getMenu()).startSpewingItems();
            })).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            });
            purchaseButton.setDisabled(() -> {
                return !this.menu.hasSpirit() || !SpiritExtractorHelper.coinsCoverTotalCost(this.menu.getSlot(36), this.menu.getTotalCost(), inventory.player) || ((SpiritExtractorContainer)this.getMenu()).isSpewingItems();
            });
            purchaseButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                tooltipRenderer.renderComponentTooltip(poseStack, shift_getPurchaseButtonTooltipLines(), mouseX, mouseY, TooltipDirection.RIGHT);
                return true;
            });
            this.addElement(purchaseButton);
        }
    }
    
    private @NotNull List<Component> shift_getPurchaseButtonTooltipLines() {
        List<Component> purchaseButtonTooltips = new ArrayList();
        SpiritExtractorTileEntity.RecoveryCost recoveryCost = ((SpiritExtractorContainer)this.getMenu()).getRecoveryCost();
        ItemStack totalCost = recoveryCost.getTotalCost();
        if (totalCost.getCount() > 0) {
            if (((SpiritExtractorContainer)this.getMenu()).hasSpirit()) {
                purchaseButtonTooltips.add(new TextComponent("Cost for recovering items"));
                
                int paymentStackCount = ((SpiritExtractorContainer)this.getMenu()).getSlot(36).getItem().getCount();
                
                Iterator it = this.getMenu().getPlayer().getInventory().items.iterator();
                while (it.hasNext())
                {
                    ItemStack plStack = (ItemStack) it.next();
                    if(ShiftInventoryUtils.isEqualCrafting(plStack, totalCost))
                    {
                        paymentStackCount += plStack.getCount();
                    }
                    else if(plStack.is(VCPRegistry.COIN_POUCH))
                    {
                        paymentStackCount += CoinPouchItem.getCoinCount(plStack, totalCost);
                    }
                }
                
                ChatFormatting textColor = paymentStackCount < totalCost.getCount() ? ChatFormatting.RED : ChatFormatting.YELLOW;
                float var10000 = (float)paymentStackCount / (float)totalCost.getCount();
                String percentString = (int)(var10000 * 100.0F) + "%";
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.total_cost", new Object[]{percentString, totalCost.getItem().getName(totalCost).getString(), totalCost.getCount()})).withStyle(textColor));
            } else {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.would_be_cost", new Object[]{totalCost.getCount(), totalCost.getItem().getName(totalCost).getString()})).withStyle(ChatFormatting.GREEN));
            }

            purchaseButtonTooltips.add(TextComponent.EMPTY);
            float baseCostCount = recoveryCost.getBaseCount();
            int levels = Math.max(1, ((SpiritExtractorContainer)this.getMenu()).getPlayerLevel());
            purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.base_cost", new Object[]{String.format("%.0f", baseCostCount * (float)levels), String.format("%.2f", baseCostCount), levels})).withStyle(ChatFormatting.GRAY));
            recoveryCost.getStackCost().forEach((t) -> {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.item_cost", new Object[]{t.getB(), ((ItemStack)t.getA()).getHoverName()})).withStyle(ChatFormatting.GRAY));
            });
            float multiplier = ((SpiritExtractorContainer)this.getMenu()).getMultiplier();
            if (!Mth.equal(multiplier, 1.0F)) {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.multiplier", new Object[]{String.format("%.2f", multiplier)})).withStyle(ChatFormatting.GRAY));
            }

            float heroDiscount = ((SpiritExtractorContainer)this.getMenu()).getHeroDiscount();
            if ((double)heroDiscount >= 0.01) {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.hero_discount", new Object[]{String.format("%.0f%%", heroDiscount * 100.0F)})).withStyle(ChatFormatting.GRAY));
            }

            float rescuedBonus = ((SpiritExtractorContainer)this.getMenu()).getRescuedBonus();
            if (rescuedBonus > 0.0F) {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.rescued_bonus", new Object[]{String.format("%.0f%%", rescuedBonus * 100.0F)})).withStyle(ChatFormatting.GRAY));
            }
        }

        return purchaseButtonTooltips;
    }

    public SpiritExtractorScreenMixin(SpiritExtractorContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<SpiritExtractorContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
