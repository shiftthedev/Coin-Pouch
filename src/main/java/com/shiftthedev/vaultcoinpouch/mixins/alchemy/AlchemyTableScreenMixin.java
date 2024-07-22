package com.shiftthedev.vaultcoinpouch.mixins.alchemy;

import com.shiftthedev.vaultcoinpouch.client_helpers.AlchemyTableClientHelper;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.server_helpers.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.element.AlchemyCraftSelectorElement;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.screen.block.AlchemyTableScreen;
import iskallia.vault.container.AlchemyTableContainer;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Supplier;

@Mixin(value = AlchemyTableScreen.class, remap = false, priority = 1100)
public abstract class AlchemyTableScreenMixin extends AbstractElementContainerScreen<AlchemyTableContainer>
{
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/framework/element/ButtonElement;setDisabled(Ljava/util/function/Supplier;)Liskallia/vault/client/gui/framework/element/ButtonElement;"))
    private ButtonElement craftButtonDisable_coinpouch(ButtonElement craftButton, Supplier<Boolean> disabled)
    {
        if (VCPConfig.GENERAL.alchemyTableEnabled())
        {
            craftButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> AlchemyTableClientHelper.tooltip_coinpouch(this.selectedOption, this.playerInventory, this.getMenu(), tooltipRenderer, poseStack, mouseX, mouseY));
            craftButton.setDisabled(() -> AlchemyTableClientHelper.setDisabled_coinpouch(this.getMenu(), this.selectedOption, this.playerInventory));
        }
        else
        {
            craftButton.setDisabled(() -> AlchemyTableClientHelper.setDisabled_vh(this.getMenu(), this.selectedOption, this.playerInventory));
        }

        return craftButton;
    }

    @Redirect(method = "tryCraft", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;getMissingInputs(Ljava/util/List;Lnet/minecraft/world/entity/player/Inventory;)Ljava/util/List;"))
    private List<ItemStack> tryCraft_getMissingInputs_coinpouch(List<ItemStack> recipeInputs, Inventory playerInventory)
    {
        if (VCPConfig.GENERAL.alchemyTableEnabled())
        {
            return ShiftInventoryUtils.getMissingInputs(recipeInputs, playerInventory);
        }

        return InventoryUtil.getMissingInputs(recipeInputs, playerInventory);
    }

    @Shadow
    private AlchemyCraftSelectorElement.CraftingOption selectedOption;

    @Shadow
    @Final
    private Inventory playerInventory;

    public AlchemyTableScreenMixin(AlchemyTableContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<AlchemyTableContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
