package com.shiftthedev.vaultcoinpouch.mixins.spirit;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.server_helpers.SpiritExtractorServerHelper;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = SpiritExtractorTileEntity.class, priority = 1100)
public abstract class SpiritExtractorTileEntityMixin extends BlockEntity
{
    @Redirect(method = "spewItems", at = @At(value = "INVOKE", target = "Liskallia/vault/block/entity/SpiritExtractorTileEntity;coinsCoverTotalCost()Z"), remap = false)
    private boolean spewItems_coinsCoverTotalCost_coinpouch(SpiritExtractorTileEntity tile, Player player)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            return SpiritExtractorServerHelper.coinsCoverTotalCost(this.paymentInventory, this.getRecoveryCost().getTotalCost(), player);
        }

        return this.coinsCoverTotalCost();
    }

    @Redirect(method = "spewItems", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedInventory;setItem(ILnet/minecraft/world/item/ItemStack;)V"), remap = true)
    private void spewItems_setItem_coinpouch(OverSizedInventory instance, int pIndex, ItemStack pStack, Player player)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            SpiritExtractorServerHelper.withdraw_coinpouch(this.recoveryCost.getTotalCost(), instance, pIndex, player);
        }
        else
        {
            SpiritExtractorServerHelper.withdraw_vh(instance, pIndex);
        }
    }

    @Shadow
    @Final
    private OverSizedInventory paymentInventory;

    @Shadow
    private SpiritExtractorTileEntity.RecoveryCost recoveryCost;

    @Shadow
    public abstract SpiritExtractorTileEntity.RecoveryCost getRecoveryCost();

    @Shadow
    public abstract boolean coinsCoverTotalCost();

    public SpiritExtractorTileEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_)
    {
        super(p_155228_, p_155229_, p_155230_);
    }
}
