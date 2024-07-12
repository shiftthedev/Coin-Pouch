package com.shiftthedev.vaultcoinpouch.mixins.spirit;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.SpiritExtractorHelper;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderFunction;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.screen.block.SpiritExtractorScreen;
import iskallia.vault.container.SpiritExtractorContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(value = SpiritExtractorScreen.class, remap = false, priority = 900)
public abstract class SpiritExtractorScreenMixin extends AbstractElementContainerScreen<SpiritExtractorContainer>
{
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/framework/element/ButtonElement;setDisabled(Ljava/util/function/Supplier;)Liskallia/vault/client/gui/framework/element/ButtonElement;", ordinal = 1))
    private ButtonElement purchaseButton_coinpouch(ButtonElement purchaseButton, Supplier<Boolean> disabled, SpiritExtractorContainer container, Inventory inventory, Component title)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            purchaseButton.setDisabled(() -> SpiritExtractorHelper.setDisabled_coinpouch(this.getMenu(), inventory));
        }
        else
        {
            purchaseButton.setDisabled(() -> SpiritExtractorHelper.setDisabled_vh(this.getMenu()));
        }

        return purchaseButton;
    }

    @Redirect(method = "getPurchaseButtonTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 1))
    private int getPurchaseButtonTooltipLines_paymentStackCount_coinpouch(ItemStack instance)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            return SpiritExtractorHelper.getPouchCoinsCount(this.getMenu());
        }

        return ((SpiritExtractorContainer) this.getMenu()).getSlot(36).getItem().getCount();
    }

    public SpiritExtractorScreenMixin(SpiritExtractorContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<SpiritExtractorContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
