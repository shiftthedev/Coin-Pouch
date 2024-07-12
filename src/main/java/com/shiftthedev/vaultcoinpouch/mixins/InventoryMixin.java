package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import com.shiftthedev.vaultcoinpouch.helpers.InventoryHelper;
import iskallia.vault.block.CoinPileDecorBlock;
import iskallia.vault.world.data.InventorySnapshotData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Inventory.class, priority = 900)
public abstract class InventoryMixin implements InventorySnapshotData.InventoryAccessor
{
    @Inject(method = "add(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    public void add_coinpouch(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir)
    {
        if (itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof CoinPileDecorBlock)
        {
            if (!(this.player.containerMenu instanceof CoinPouchContainer))
            {
                if(InventoryHelper.try_pickupCoinToPouch(this.player, itemStack, (Inventory) (Object) this))
                {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Shadow
    @Final
    public Player player;

    public InventoryMixin()
    {
    }
}
