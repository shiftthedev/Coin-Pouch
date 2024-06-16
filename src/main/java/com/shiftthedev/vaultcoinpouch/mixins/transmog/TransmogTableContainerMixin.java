package com.shiftthedev.vaultcoinpouch.mixins.transmog;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(value = TransmogTableContainer.class, remap = false)
public abstract class TransmogTableContainerMixin extends OverSizedSlotContainer
{
    @Inject(method = "priceFulfilled", at = @At("HEAD"), cancellable = true)
    private void priceFulfilled_impl(CallbackInfoReturnable<Boolean> cir)
    {
        if(VCPConfig.GENERAL.transmogTableEnabled())
        {
            cir.setReturnValue(shift_priceFulfilled());
            cir.cancel();
            return;
        }
    }
    
    private boolean shift_priceFulfilled()
    {
        int remaining = this.copperCost();
        Slot bronzeSlot = this.getSlot(this.internalInventoryIndexRange.getContainerIndex(1));
        if (bronzeSlot.hasItem() && bronzeSlot.getItem().is(ModBlocks.VAULT_BRONZE))
        {
            remaining -= bronzeSlot.getItem().getCount();
            if (remaining <= 0)
            {
                return true;
            }
        }

        NonNullList<ItemStack> playerItems = player.getInventory().items;
        ItemStack pouchStack = null;
        Iterator it = playerItems.iterator();

        while (it.hasNext())
        {
            ItemStack plStack = (ItemStack) it.next();
            if (remaining <= 0)
            {
                return true;
            }

            if (ShiftInventoryUtils.isEqualCrafting(plStack, bronzeSlot.getItem()))//plStack.is(ModBlocks.VAULT_BRONZE))
            {
                remaining -= plStack.getCount();
            }

            if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                pouchStack = plStack;
            }
        }

        if (pouchStack == null)
        {
            return false;
        }

        remaining -= CoinPouchItem.getCoinCount(pouchStack);
        return remaining <= 0;
    }

    @Shadow
    protected SlotIndexRange internalInventoryIndexRange;

    @Shadow
    public abstract int copperCost();

    protected TransmogTableContainerMixin(MenuType<?> menuType, int id, Player player)
    {
        super(menuType, id, player);
    }
}
