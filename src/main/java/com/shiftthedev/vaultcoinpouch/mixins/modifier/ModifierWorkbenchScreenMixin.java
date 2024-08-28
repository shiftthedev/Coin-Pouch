package com.shiftthedev.vaultcoinpouch.mixins.modifier;

import com.shiftthedev.vaultcoinpouch.client.elements.CoinPouchElement;
import com.shiftthedev.vaultcoinpouch.client_helpers.ModifierWorkbenchClientHelper;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.server_helpers.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.screen.block.ModifierWorkbenchScreen;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Supplier;

@Mixin(value = ModifierWorkbenchScreen.class, remap = false, priority = 1100)
public abstract class ModifierWorkbenchScreenMixin extends AbstractElementContainerScreen<ModifierWorkbenchContainer>
{
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init_coinpouch(ModifierWorkbenchContainer container, Inventory inventory, Component title, CallbackInfo ci)
    {
        this.addElement((CoinPouchElement) (new CoinPouchElement(Spatials.positionXYZ(-30, 110, 20), () -> {
            return Spatials.positionXY(-8, this.topPos + 14);
        }, inventory.player)));
    }
    
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Liskallia/vault/client/gui/framework/element/ButtonElement;setDisabled(Ljava/util/function/Supplier;)Liskallia/vault/client/gui/framework/element/ButtonElement;"))
    private ButtonElement craftButton_coinpouch(ButtonElement craftButton, Supplier<Boolean> disabled)
    {
        if (VCPConfig.GENERAL.modifierWorkbenchEnabled())
        {
            craftButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> ModifierWorkbenchClientHelper.tooltip(this.getMenu(), this.selectedOption, this.playerInventory, tooltipRenderer, poseStack, mouseX, mouseY));
            craftButton.setDisabled(() -> ModifierWorkbenchClientHelper.setDisabled_coinpouch(this.getMenu(), this.selectedOption, this.playerInventory));
        }
        else
        {
            craftButton.setDisabled(() -> ModifierWorkbenchClientHelper.setDisabled_vh(this.getMenu(), this.selectedOption, this.playerInventory));
        }

        return craftButton;
    }

    @Redirect(method = "tryCraft", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;getMissingInputs(Ljava/util/List;Lnet/minecraft/world/entity/player/Inventory;)Ljava/util/List;"))
    private List<ItemStack> tryCraft_getMissingInputs_coinpouch(List<ItemStack> recipeInputs, Inventory playerInventory)
    {
        if (VCPConfig.GENERAL.modifierWorkbenchEnabled())
        {
            return ShiftInventoryUtils.getMissingInputs(recipeInputs, playerInventory);
        }

        return InventoryUtil.getMissingInputs(recipeInputs, playerInventory);
    }

    @Shadow
    private iskallia.vault.gear.crafting.ModifierWorkbenchHelper.CraftingOption selectedOption;

    @Shadow
    @Final
    private Inventory playerInventory;

    public ModifierWorkbenchScreenMixin(ModifierWorkbenchContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<ModifierWorkbenchContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
