package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.config.CoinData;
import com.shiftthedev.vaultcoinpouch.config.VCPData;
import iskallia.vault.item.CoinPouchItem;
import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nonnull;

@Mixin(value = CoinPouchItem.Handler.class, remap = false)
public abstract class CoinPouchHandlerMixin extends ItemStackHandler
{
    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public int getSlotLimit(int slot)
    {
        if (slot >= VCPData.getCoinDataCount())
        {
            return 0;
        }

        int size = 2147483097;
        for (int i = 0; i <= slot; i++)
        {
            CoinData data = VCPData.getCoinData(i);
            if (data.previous_coin_count_to_upgrade == 0)
            {
                continue;
            }

            size = Mth.intFloorDiv(size, data.previous_coin_count_to_upgrade);
        }

        return size;
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        CoinData data = VCPData.getCoinData(slot);
        if (data == null)
        {
            return false;
        }

        return data.coin_id.equals(Registry.ITEM.getKey(stack.getItem()).toString());
    }
}
