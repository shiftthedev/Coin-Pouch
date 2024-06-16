package com.shiftthedev.vaultcoinpouch.mixins.spirit;

import com.mojang.authlib.GameProfile;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import com.shiftthedev.vaultcoinpouch.utils.SpiritExtractorHelper;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.world.data.InventorySnapshot;
import iskallia.vault.world.data.PlayerSpiritRecoveryData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
import java.util.Iterator;

@Mixin(value = SpiritExtractorTileEntity.class, remap = false)
public abstract class SpiritExtractorTileEntityMixin extends BlockEntity
{
    @Inject(method = "spewItems", at = @At("HEAD"), cancellable = true)
    private void spewItems_impl(Player player, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.spiritExtractorEnabled())
        {
            shift_spewItems(player);
            ci.cancel();
            return;
        }
    }

    private void shift_spewItems(Player player)
    {
        if (!this.spewingItems && SpiritExtractorHelper.coinsCoverTotalCost(this.paymentInventory, this.getRecoveryCost().getTotalCost(), player))
        {
            if (this.level.isClientSide())
            {
                this.spawnParticles();
            }
            else
            {
                Level var3 = this.level;
                if (var3 instanceof ServerLevel)
                {
                    ServerLevel serverLevel = (ServerLevel) var3;
                    if (this.gameProfile != null)
                    {
                        PlayerSpiritRecoveryData data = PlayerSpiritRecoveryData.get(serverLevel);
                        data.increaseMultiplierOnRecovery(this.gameProfile.getId());
                        data.removeHeroDiscount(this.gameProfile.getId());
                    }
                }

                int coinsRemaining = this.getRecoveryCost().getTotalCost().getCount();
                coinsRemaining -= this.paymentInventory.getItem(0).getCount();
                this.paymentInventory.setItem(0, ItemStack.EMPTY);

                ItemStack costStack = this.getRecoveryCost().getTotalCost();
                int deductedAmount;
                NonNullList<ItemStack> pouchStacks = NonNullList.create();
                Iterator it = player.getInventory().items.iterator();
                while (it.hasNext())
                {
                    if (coinsRemaining <= 0)
                    {
                        break;
                    }

                    ItemStack plStack = (ItemStack) it.next();
                    if (ShiftInventoryUtils.isEqualCrafting(plStack, costStack))
                    {
                        deductedAmount = Math.min(coinsRemaining, plStack.getCount());
                        plStack.shrink(deductedAmount);
                        coinsRemaining -= deductedAmount;
                    }

                    if (plStack.is(VCPRegistry.COIN_POUCH))
                    {
                        pouchStacks.add(plStack);
                    }
                }

                it = pouchStacks.iterator();
                while (it.hasNext())
                {
                    if (coinsRemaining <= 0)
                    {
                        break;
                    }

                    ItemStack pouchStack = (ItemStack) it.next();
                    deductedAmount = Math.min(coinsRemaining, CoinPouchItem.getCoinCount(pouchStack, costStack));
                    CoinPouchItem.extractCoins(pouchStack, costStack, deductedAmount);
                    coinsRemaining -= deductedAmount;
                }

                this.rescuedBonus = 0.0F;
                this.recoveryCost = new SpiritExtractorTileEntity.RecoveryCost();
                if (this.inventorySnapshot != null && !this.inventorySnapshot.getItems().isEmpty())
                {
                    this.inventorySnapshot.apply(player);
                    this.inventorySnapshot = null;
                    this.removeSpirit();
                }
                else
                {
                    this.spewingItems = true;
                    this.spewingCooldownTime = this.level.getGameTime() + 20L;
                }

                this.level.playSound((Player) null, this.getBlockPos(), SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 0.5F);
            }
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
