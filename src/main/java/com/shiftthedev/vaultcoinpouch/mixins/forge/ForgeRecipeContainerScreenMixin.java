package com.shiftthedev.vaultcoinpouch.mixins.forge;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.server_helpers.ShiftInventoryUtils;
import com.shiftthedev.vaultcoinpouch.network.ShiftVaultForgeRequestCraftMessage;
import iskallia.vault.block.entity.InscriptionTableTileEntity;
import iskallia.vault.block.entity.ToolStationTileEntity;
import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.screen.block.base.ForgeRecipeContainerScreen;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ForgeRecipeContainerScreen.class, remap = false, priority = 1100)
public abstract class ForgeRecipeContainerScreenMixin<V extends ForgeRecipeTileEntity, T extends ForgeRecipeContainer<V>> extends AbstractElementContainerScreen<T>
{
    @Shadow
    public abstract Inventory getPlayerInventory();

    @Shadow
    private VaultForgeRecipe selectedRecipe;

    @Shadow
    protected abstract int getCraftedLevel();

    @Inject(method = "getMissingRecipeInputs", at = @At("HEAD"), cancellable = true)
    private void getMissingRecipeInputs_coinpouch(List<ItemStack> inputs, CallbackInfoReturnable<List<ItemStack>> cir)
    {
        ForgeRecipeTileEntity tile = ((ForgeRecipeContainer) this.menu).getTile();
        if (tile == null)
        {
            cir.setReturnValue(inputs);
            cir.cancel();
            return;
        }

        if (tile instanceof VaultForgeTileEntity && VCPConfig.GENERAL.vaultForgeEnabled())
        {
            cir.setReturnValue(ShiftInventoryUtils.getMissingInputs(inputs, this.getPlayerInventory(), tile.getInventory()));
            cir.cancel();
            return;
        }
        else if (tile instanceof ToolStationTileEntity && VCPConfig.GENERAL.toolStationEnabled())
        {
            cir.setReturnValue(ShiftInventoryUtils.getMissingInputs(inputs, this.getPlayerInventory(), tile.getInventory()));
            cir.cancel();
            return;
        }
        else if (tile instanceof InscriptionTableTileEntity && VCPConfig.GENERAL.inscriptionTableEnabled())
        {
            cir.setReturnValue(ShiftInventoryUtils.getMissingInputs(inputs, this.getPlayerInventory(), tile.getInventory()));
            cir.cancel();
            return;
        }
    }

    @Inject(method = "onCraftClick", at = @At("HEAD"), cancellable = true)
    private void onCraftClick_coinpouch(CallbackInfo ci)
    {
        if (this.selectedRecipe != null)
        {
            ForgeRecipeTileEntity tile = ((ForgeRecipeContainer) this.menu).getTile();
            if (tile != null)
            {
                if (tile instanceof VaultForgeTileEntity && VCPConfig.GENERAL.vaultForgeEnabled())
                {
                    ModNetwork.CHANNEL.sendToServer(new ShiftVaultForgeRequestCraftMessage(this.selectedRecipe.getId(), this.getCraftedLevel()));
                    ci.cancel();
                    return;
                }
                else if (tile instanceof ToolStationTileEntity && VCPConfig.GENERAL.toolStationEnabled())
                {
                    ModNetwork.CHANNEL.sendToServer(new ShiftVaultForgeRequestCraftMessage(this.selectedRecipe.getId(), this.getCraftedLevel()));
                    ci.cancel();
                    return;
                }
                else if (tile instanceof InscriptionTableTileEntity && VCPConfig.GENERAL.inscriptionTableEnabled())
                {
                    ModNetwork.CHANNEL.sendToServer(new ShiftVaultForgeRequestCraftMessage(this.selectedRecipe.getId(), this.getCraftedLevel()));
                    ci.cancel();
                    return;
                }
            }
        }
    }

    public ForgeRecipeContainerScreenMixin(T container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<T>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
