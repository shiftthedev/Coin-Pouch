package com.shiftthedev.vaultcoinpouch.mixins.spirit;

import com.mojang.authlib.GameProfile;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.SpiritExtractorHelper;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.world.data.InventorySnapshot;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(value = SpiritExtractorTileEntity.class, remap = false)
public abstract class SpiritExtractorTileEntityMixin extends BlockEntity
{
    @Inject(method = "spewItems", at = @At("HEAD"), cancellable = true)
    private void spewItems_impl(Player player, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            SpiritExtractorHelper.SpewItems(player, this.spewingItems, this.spewingCooldownTime, this.paymentInventory, this.getRecoveryCost(), this.level, this.getBlockPos(), this.gameProfile,
                    this.rescuedBonus, this.recoveryCost, this.inventorySnapshot, this::removeSpirit);
            ci.cancel();
            return;
        }
    }

    @Shadow
    private boolean spewingItems;

    @Shadow
    protected abstract void spawnParticles();

    @Shadow
    @Nullable
    private GameProfile gameProfile;

    @Shadow
    @Final
    private OverSizedInventory paymentInventory;

    @Shadow
    private float rescuedBonus;

    @Shadow
    private SpiritExtractorTileEntity.RecoveryCost recoveryCost;

    @Shadow
    @Nullable
    private InventorySnapshot inventorySnapshot;

    @Shadow
    public abstract void removeSpirit();

    @Shadow
    private long spewingCooldownTime;

    @Shadow
    public abstract SpiritExtractorTileEntity.RecoveryCost getRecoveryCost();

    public SpiritExtractorTileEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_)
    {
        super(p_155228_, p_155229_, p_155230_);
    }
}
