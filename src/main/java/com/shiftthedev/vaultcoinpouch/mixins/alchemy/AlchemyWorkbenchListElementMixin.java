package com.shiftthedev.vaultcoinpouch.mixins.alchemy;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.element.AlchemyCraftSelectorElement;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = AlchemyCraftSelectorElement.WorkbenchListElement.class, remap = false, priority = 900)
public abstract class AlchemyWorkbenchListElementMixin
{
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Liskallia/vault/util/InventoryUtil;getMissingInputs(Ljava/util/List;Lnet/minecraft/world/entity/player/Inventory;)Ljava/util/List;"))
    private List<ItemStack> render_getMissingInputs_coinpouch(List<ItemStack> recipeInputs, Inventory playerInventory)
    {
        if (VCPConfig.GENERAL.alchemyTableEnabled())
        {
            return ShiftInventoryUtils.getMissingInputs(recipeInputs, playerInventory);
        }

        return InventoryUtil.getMissingInputs(recipeInputs, playerInventory);
    }
}
