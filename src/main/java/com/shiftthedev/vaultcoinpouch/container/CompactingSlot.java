package com.shiftthedev.vaultcoinpouch.container;

import com.shiftthedev.vaultcoinpouch.config.CoinData;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.container.slot.ConditionalReadSlot;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CompactingSlot extends SlotItemHandler {
    private final ItemStack defaultStack;
    private CompactingSlot prevSlot;
    private CompactingSlot nextSlot;
    
    private CoinPouchContainer coinPouchContainer;
    public CoinData coinData;

    public CompactingSlot(IItemHandler inventory, int xPosition, int yPosition, CoinPouchContainer container, CoinData coinData) {
        super(inventory, coinData.index, xPosition, yPosition);
        this.coinPouchContainer = container;
        this.coinData = coinData;
        this.defaultStack = new ItemStack(Registry.ITEM.get(new ResourceLocation(coinData.coin_id)).asItem());
    }

    public void setupSlots(List<CompactingSlot> slots) {
        slots.forEach(compactingSlot -> {
            if (compactingSlot.coinData.coin_id.equals(this.coinData.next_coin_id)) {
                this.nextSlot = compactingSlot;
            }
            
            if (compactingSlot.coinData.coin_id.equals(this.coinData.previous_coin_id)) {
                this.prevSlot = compactingSlot;
            }
        });
    }

    public boolean canAccess(int slot, ItemStack stack) {
        return this.coinPouchContainer.canAccess(slot, stack) && stack.getItem() == defaultStack.getItem(); 
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.canAccess(this.getSlotIndex(), stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return this.canAccess(this.getSlotIndex(), this.getItem());
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return this.getItemHandler().getSlotLimit(this.getSlotIndex());
    }

    public ItemStack getDefaultCopy() {
        return defaultStack.copy();
    }
    
    @Override
    public void set(@NotNull ItemStack stack) {
        ((CoinPouchItem.Handler) this.getItemHandler()).setStackInSlotGUI(this.getSlotIndex(), stack);
        this.setChanged();
        compact(stack.getCount());
    }

    @NotNull
    @Override
    public ItemStack remove(int amount) {
        ItemStack stack = ((CoinPouchItem.Handler) this.getItemHandler()).extractItemGUI(getSlotIndex(), amount, false);
        this.setChanged();
        compact(getItem().getCount());
        return stack;
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.setChanged();
        ((CoinPouchItem.Handler) this.getItemHandler()).setStackInSlotGUI(this.getSlotIndex(), getItem());
        compact(getItem().getCount());
    }

    private void compact(int count) {
        if (prevSlot != null) {
            ItemStack stack = prevSlot.getItem();
            int prevCount;
            if (stack.is(Items.AIR)) {
                stack = prevSlot.getDefaultCopy();
                prevCount = count * coinData.previous_coin_count_to_upgrade;
            } else {
                prevCount = stack.getCount();
                prevCount -= Mth.intFloorDiv(prevCount, coinData.previous_coin_count_to_upgrade) * coinData.previous_coin_count_to_upgrade;
                prevCount += count * coinData.previous_coin_count_to_upgrade;
            }

            if (prevCount != prevSlot.getItem().getCount()) {
                prevSlot.set(ItemHandlerHelper.copyStackWithSize(stack, prevCount));
            }
        }

        if (nextSlot != null) {
            ItemStack stack = nextSlot.getItem();
            if (stack.is(Items.AIR)) {
                stack = nextSlot.getDefaultCopy();
            }

            int nextCount = Mth.intFloorDiv(count, nextSlot.coinData.previous_coin_count_to_upgrade);
            if (nextCount != nextSlot.getItem().getCount()) {
                nextSlot.set(ItemHandlerHelper.copyStackWithSize(stack, nextCount));
            }
        }
    }
}
