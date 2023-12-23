package com.shiftthedev.vaultcoinpouch.item;

import iskallia.vault.init.ModBlocks;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CoinHandler extends ItemStackHandler {
    protected final ItemStack delegate;

    public CoinHandler(ItemStack delegate) {
        super();
        this.delegate = delegate;

        ItemStack[] containedStacks = CoinPouchItem.getContainedStacks(this.delegate);
        setSize(containedStacks.length);
        for (int i = 0; i < containedStacks.length; i++) {
            this.stacks.set(i, containedStacks[i]);
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        CoinPouchItem.setContainedStack(this.delegate, slot, this.getStackInSlot(slot));
    }

    @Override
    public int getSlotLimit(int slot) {
        return switch (slot) {
            case 0 -> 2147483582;
            case 1 -> 238609286;
            case 2 -> 26512142;
            case 3 -> 2945793;
            default -> 0;
        };
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        ItemStack newstack = super.insertItem(slot, stack, simulate);

        System.out.println("INSERT");
        if (!simulate)
            compact(slot);

        return newstack;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack newstack = super.extractItem(slot, amount, simulate);

        System.out.println("EXTRACT");
        if (!simulate)
            compact(slot);

        return newstack;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return this.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return (stack.getItem().asItem() == ModBlocks.BRONZE_COIN_PILE.asItem() && slot == 0) ||
                (stack.getItem().asItem() == ModBlocks.SILVER_COIN_PILE.asItem() && slot == 1) ||
                (stack.getItem().asItem() == ModBlocks.GOLD_COIN_PILE.asItem() && slot == 2) ||
                (stack.getItem().asItem() == ModBlocks.PLATINUM_COIN_PILE.asItem() && slot == 3);
    }

    private void compact(int slot) {
        if (slot == 0) {
            compactUp(1, 3, this.stacks.get(slot).getCount());
        } else if (slot == 1) {
            int prevSlotCount = this.stacks.get(slot).getCount();
            compactUp(2, 2, prevSlotCount);
            compactDown(0, 1, prevSlotCount);
        } else if (slot == 2) {
            int prevSlotCount = this.stacks.get(slot).getCount();
            compactUp(3, 1, prevSlotCount);
            compactDown(1, 2, prevSlotCount);
        } else if (slot == 3) {
            compactDown(2, 3, this.stacks.get(slot).getCount());
        }
    }

    private void compactUp(int slot, int range, int prevSlotCount) {
        ItemStack stack = this.stacks.get(slot);
        if (stack.is(Items.AIR)) {
            stack = getDefaultItem(slot);
        }

        int count = Mth.intFloorDiv(prevSlotCount, 9);
        if (stack.getCount() == count) return;

        this.insertItem(slot, ItemHandlerHelper.copyStackWithSize(stack, count), false);
        
        if(range > 1)
            compactUp(slot + 1, range - 1, count);
    }

    private void compactDown(int slot, int range, int prevSlotCount) {
        ItemStack stack = this.stacks.get(slot);
        int count = 0;
        if (stack.is(Items.AIR)) {
            stack = getDefaultItem(slot);
            count = prevSlotCount * 9;
        } else {
            count -= Mth.intFloorDiv(count, 9) * 9;
            count += prevSlotCount * 9;
        }
        
        if(stack.getCount() == count) return;
        
        this.insertItem(slot, ItemHandlerHelper.copyStackWithSize(stack, count), false);
        
        if(range > 1)
            compactDown(slot - 1, range - 1, count);
    }

    private ItemStack getDefaultItem(int slot) {
        if (slot == 0) {
            return new ItemStack(ModBlocks.BRONZE_COIN_PILE);
        }
        if (slot == 1) {
            return new ItemStack(ModBlocks.SILVER_COIN_PILE);
        }
        if (slot == 2) {
            return new ItemStack(ModBlocks.GOLD_COIN_PILE);
        }
        if (slot == 3) {
            return new ItemStack(ModBlocks.PLATINUM_COIN_PILE);
        }

        return ItemStack.EMPTY;
    }
}
