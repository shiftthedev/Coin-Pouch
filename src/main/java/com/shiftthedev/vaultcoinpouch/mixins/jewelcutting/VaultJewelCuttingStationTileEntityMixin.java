package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.server_helpers.JewelCuttingStationServerHelper;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = VaultJewelCuttingStationTileEntity.class, priority = 1100)
public abstract class VaultJewelCuttingStationTileEntityMixin extends BlockEntity implements MenuProvider
{
    @Redirect(method = "cutJewel", at = @At(value = "INVOKE", target = "Liskallia/vault/block/entity/VaultJewelCuttingStationTileEntity;canCraft()Z"), remap = false)
    private boolean cutJewel_canCraft_coinpouch(VaultJewelCuttingStationTileEntity tile, VaultJewelCuttingStationContainer container, ServerPlayer player)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            return JewelCuttingStationServerHelper.canCraft(tile, player);
        }

        return this.canCraft();
    }

    @Redirect(method = "cutJewel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", ordinal = 1), remap = true)
    private void cutJewel_shrink_coinpouch(ItemStack instance, int cost, VaultJewelCuttingStationContainer container, ServerPlayer player)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            JewelCuttingStationServerHelper.withdraw_coinpouch(instance, cost, this.getRecipeInput().getSecondInput(), player);
        }
        else
        {
            JewelCuttingStationServerHelper.withdraw_vh(instance, cost);
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
