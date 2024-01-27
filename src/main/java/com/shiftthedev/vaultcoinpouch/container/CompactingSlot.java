package com.shiftthedev.vaultcoinpouch.container;

import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.container.slot.ConditionalReadSlot;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CompactingSlot extends ConditionalReadSlot {
    private final ItemStack defaultStack;
    private Slot prevSlot;
    private Slot nextSlot;
    
    private CoinPouchContainer coinContainer;

    public CompactingSlot(IItemHandler inventory, int index, int xPosition, int yPosition, CoinPouchContainer container, Item defaultItem) {
        super(inventory, index, xPosition, yPosition, (slot, stack) -> container.canAccess(slot, stack) && stack.getItem() == defaultItem);
        this.defaultStack = new ItemStack(defaultItem);
        this.coinContainer = container;
    }

    public void setupSlots(@Nullable Slot prevSlot, @Nullable Slot nextSlot) {
        this.prevSlot = prevSlot;
        this.nextSlot = nextSlot;
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
    public void onTake(Player p_150645_, ItemStack stack) {
        this.setChanged();

        ((CoinPouchItem.Handler) this.getItemHandler()).setStackInSlotGUI(this.getSlotIndex(), getItem());
        compact(getItem().getCount());
    }

    private void compact(int count) {
        if (prevSlot != null) {
            ItemStack stack = prevSlot.getItem();
            int prevCount = 0;
            if (stack.is(Items.AIR)) {
                stack = ((CompactingSlot) prevSlot).getDefaultCopy();
                prevCount = count * 9;
            } else {
                prevCount = stack.getCount();
                prevCount -= Mth.intFloorDiv(prevCount, 9) * 9;
                prevCount += count * 9;
            }

            if (prevCount != prevSlot.getItem().getCount()) {
                prevSlot.set(ItemHandlerHelper.copyStackWithSize(stack, prevCount));
            }
        }

        if (nextSlot != null) {
            ItemStack stack = nextSlot.getItem();
            if (stack.is(Items.AIR)) {
                stack = ((CompactingSlot) nextSlot).getDefaultCopy();
            }

            int nextCount = Mth.intFloorDiv(count, 9);

            if (nextCount != nextSlot.getItem().getCount()) {
                nextSlot.set(ItemHandlerHelper.copyStackWithSize(stack, nextCount));
            }
        }
    }
}
