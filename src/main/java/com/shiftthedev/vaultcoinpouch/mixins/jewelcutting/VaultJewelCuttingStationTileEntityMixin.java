package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.server_helpers.JewelCuttingStationServerHelper;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultJewelCuttingStationTileEntity.class, remap = false, priority = 1100)
public abstract class VaultJewelCuttingStationTileEntityMixin extends BlockEntity implements MenuProvider
{
    @Redirect(method = "cutJewel", at = @At(value = "INVOKE", target = "Liskallia/vault/block/entity/VaultJewelCuttingStationTileEntity;canCraft()Z"))
    private boolean cutJewel_canCraft_coinpouch(VaultJewelCuttingStationTileEntity tile, VaultJewelCuttingStationContainer container, ServerPlayer player)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            return JewelCuttingStationServerHelper.canCraft(tile, player);
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
                if (JewelCuttingStationServerHelper.canCraft((VaultJewelCuttingStationTileEntity) (Object) this, player))
                {
                    if (!container.getJewelInputSlot().getItem().isEmpty())
                    {
                        JewelCuttingStationServerHelper.withdraw(container, player, this.getRecipeInput());
                    }
                }
            }
        }
    }

    @Shadow
    public abstract boolean canCraft();

    @Shadow
    public abstract VaultJewelCuttingConfig.JewelCuttingInput getRecipeInput();

    public VaultJewelCuttingStationTileEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_)
    {
        super(p_155228_, p_155229_, p_155230_);
    }
}
