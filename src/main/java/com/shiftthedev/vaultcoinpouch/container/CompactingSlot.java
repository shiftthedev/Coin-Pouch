package com.shiftthedev.vaultcoinpouch.container;

import iskallia.vault.container.slot.ConditionalReadSlot;
import net.minecraft.util.Mth;
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

    public CompactingSlot(IItemHandler inventory, int index, int xPosition, int yPosition, CoinPouchContainer container, Item defaultItem) {
        super(inventory, index, xPosition, yPosition, (slot, stack) -> container.canAccess(slot, stack) && stack.getItem() == defaultItem);
        this.defaultStack = new ItemStack(defaultItem);
    }

    public void setupSlots(@Nullable Slot prevSlot, @Nullable Slot nextSlot) {
        this.prevSlot = prevSlot;
        this.nextSlot = nextSlot;
    }

    @Override
    public int getMaxStackSize() {
        return this.getItemHandler().getSlotLimit(this.getSlotIndex());
    }

    public ItemStack getDefaultCopy() {
        return defaultStack.copy();
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(this.getSlotIndex(), stack);
        //compact(stack.getCount());
        this.setChanged();
    }

    @NotNull
    @Override
    public ItemStack remove(int amount) {
        ItemStack stack = super.remove(amount);
        //compact(getItem().getCount());
        return stack;
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
