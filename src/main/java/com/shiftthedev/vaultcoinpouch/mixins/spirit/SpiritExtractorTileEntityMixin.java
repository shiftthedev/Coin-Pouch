package com.shiftthedev.vaultcoinpouch.mixins.spirit;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.SpiritExtractorHelper;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpiritExtractorTileEntity.class, remap = false)
public abstract class SpiritExtractorTileEntityMixin extends BlockEntity
{
    @Redirect(method = "spewItems", at = @At(value = "INVOKE", target = "Liskallia/vault/block/entity/SpiritExtractorTileEntity;coinsCoverTotalCost()Z"))
    private boolean spewItems_coinsCoverTotalCost_coinpouch(SpiritExtractorTileEntity tile, Player player)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            return SpiritExtractorHelper.coinsCoverTotalCost(this.paymentInventory, this.getRecoveryCost().getTotalCost(), player);
        }

        return this.coinsCoverTotalCost();
    }

    @Inject(method = "spewItems", at = @At(value = "INVOKE", target = "Liskallia/vault/container/oversized/OverSizedInventory;setItem(ILnet/minecraft/world/item/ItemStack;)V"))
    private void spewItems_coinpouch(Player player, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            SpiritExtractorHelper.withdraw(this.recoveryCost, this.paymentInventory, player);
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
