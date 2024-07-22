package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.shiftthedev.vaultcoinpouch.client_helpers.JewelCuttingStationClientHelper;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.JewelCuttingButtonElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.screen.block.VaultJewelCuttingStationScreen;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(value = VaultJewelCuttingStationScreen.class, remap = false, priority = 1100)
public abstract class VaultJewelCuttingStationScreenMixin extends AbstractElementContainerScreen<VaultJewelCuttingStationContainer>
{
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/framework/element/JewelCuttingButtonElement;setDisabled(Ljava/util/function/Supplier;)Liskallia/vault/client/gui/framework/element/ButtonElement;"))
    private ButtonElement cutButtonDisabled_coinpouch(JewelCuttingButtonElement cutButton, Supplier supplier)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            cutButton.setDisabled(() -> JewelCuttingStationClientHelper.setDisabled_coinpouch(this.getMenu(), playerInventory.player));
        }
        else
        {
            cutButton.setDisabled(() -> JewelCuttingStationClientHelper.setDisabled_vh(this.getMenu()));
        }

        return cutButton;
    }

    @Shadow
    @Final
    private Inventory playerInventory;

    public VaultJewelCuttingStationScreenMixin(VaultJewelCuttingStationContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<VaultJewelCuttingStationContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
