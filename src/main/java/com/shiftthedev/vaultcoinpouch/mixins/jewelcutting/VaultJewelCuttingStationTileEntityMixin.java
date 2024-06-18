package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.JewelCuttingStationHelper;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultJewelCuttingStationTileEntity.class, remap = false)
public abstract class VaultJewelCuttingStationTileEntityMixin extends BlockEntity implements MenuProvider
{
    @Inject(method = "cutJewel", at = @At("HEAD"), cancellable = true)
    private void cutJewel_impl(VaultJewelCuttingStationContainer container, ServerPlayer player, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            JewelCuttingStationHelper.CutJewel(container, player, this.getJewelCuttingRange().getRandom(), this.inventory, this.getBlockPos(), this.getRecipeOutput(), this.getRecipeInput(), this.level);
            ci.cancel();
            return;
        }
    }

    @Shadow
    public abstract VaultJewelCuttingConfig.JewelCuttingRange getJewelCuttingRange();

    @Shadow
    protected abstract void breakJewel();

    @Shadow
    public abstract VaultJewelCuttingConfig.JewelCuttingInput getRecipeInput();

    @Shadow
    public abstract VaultJewelCuttingConfig.JewelCuttingOutput getRecipeOutput();

    @Shadow
    @Final
    private OverSizedInventory inventory;

    public VaultJewelCuttingStationTileEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_)
    {
        super(p_155228_, p_155229_, p_155230_);
    }

}
