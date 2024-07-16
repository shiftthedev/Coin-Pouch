package com.shiftthedev.vaultcoinpouch.mixins.spirit;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.SpiritExtractorHelper;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.screen.block.SpiritExtractorScreen;
import iskallia.vault.container.SpiritExtractorContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Supplier;

@Mixin(value = SpiritExtractorScreen.class, remap = false, priority = 1100)
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

    @Inject(method = "getPurchaseButtonTooltipLines", at = @At(value = "HEAD"), cancellable = true)
    private void getPurchaseButtonTooltipLines_paymentStackCount_coinpouch(CallbackInfoReturnable<List<Component>> cir)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            cir.setReturnValue(SpiritExtractorHelper.getPurchaseButtonTooltipLines(this.getMenu()));
            return;
        }
    }

    public SpiritExtractorScreenMixin(SpiritExtractorContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<SpiritExtractorContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
