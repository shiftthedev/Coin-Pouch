package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.JewelCuttingStationHelper;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultJewelCuttingStationTileEntity.class, remap = false, priority = 1100)
public abstract class VaultJewelCuttingStationTileEntityMixin
{
    @Redirect(method = "cutJewel", at = @At(value = "INVOKE", target = "Liskallia/vault/block/entity/VaultJewelCuttingStationTileEntity;canCraft()Z"))
    private boolean cutJewel_canCraft_coinpouch(VaultJewelCuttingStationTileEntity tile, VaultJewelCuttingStationContainer container, ServerPlayer player)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            return JewelCuttingStationHelper.canCraft(tile, player);
        }

        return this.canCraft();
    }

    @Inject(method = "cutJewel", at = @At("HEAD"))
    private void cutJewel_coinpouch(VaultJewelCuttingStationContainer container, ServerPlayer player, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            if (container.getJewelInputSlot() != null)
            {
                if (JewelCuttingStationHelper.canCraft((VaultJewelCuttingStationTileEntity) (Object) this, player))
                {
                    if (!container.getJewelInputSlot().getItem().isEmpty())
                    {
                        JewelCuttingStationHelper.withdraw(container, player, this.getRecipeInput());
                    }
                }
            }
        }
    }

    @Shadow
    public abstract boolean canCraft();

    @Shadow
    public abstract VaultJewelCuttingConfig.JewelCuttingInput getRecipeInput();
}
