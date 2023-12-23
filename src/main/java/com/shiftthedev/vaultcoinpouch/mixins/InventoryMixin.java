package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.CoinPileDecorBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.InventorySnapshotData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Inventory.class})
public abstract class InventoryMixin implements InventorySnapshotData.InventoryAccessor {

    @Shadow
    @Final
    public Player player;

    public InventoryMixin() {
    }

    @Inject(
            method = {"add(Lnet/minecraft/world/item/ItemStack;)Z"},
            at = {@At("HEAD")},
            cancellable = true
    )
    public void coinAdd(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (itemStack.getItem() instanceof BlockItem && ((BlockItem) itemStack.getItem()).getBlock() instanceof CoinPileDecorBlock) {
            if (!(this.player.containerMenu instanceof CoinPouchContainer)) {
                Inventory thisInventory = (Inventory) (Object) this;
                ItemStack pouchStack = ItemStack.EMPTY;

                for (int slot = 0; slot < thisInventory.getContainerSize(); ++slot) {
                    ItemStack invStack = thisInventory.getItem(slot);
                    if (invStack.getItem() instanceof CoinPouchItem) {
                        pouchStack = invStack;
                        break;
                    }
                }

                if (!pouchStack.isEmpty()) {
                    pouchStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(iItemHandler -> {
                        ItemStack remainder = ItemStack.EMPTY;
                        
                        if (itemStack.getItem().asItem() == ModBlocks.BRONZE_COIN_PILE.asItem()) {
                            remainder = iItemHandler.insertItem(0, itemStack, false);
                            //compactUp(1, 3, itemStack.getCount(), iItemHandler);
                        }
                        if (itemStack.getItem().asItem() == ModBlocks.SILVER_COIN_PILE.asItem()) {
                            remainder = iItemHandler.insertItem(1, itemStack, false);
                            //compactUp(2, 2, itemStack.getCount(), iItemHandler);
                            //compactDown(0, 1, itemStack.getCount(), iItemHandler);
                        }
                        if (itemStack.getItem().asItem() == ModBlocks.GOLD_COIN_PILE.asItem()) {
                            remainder = iItemHandler.insertItem(2, itemStack, false);
                            //compactUp(3, 1, itemStack.getCount(), iItemHandler);
                            //compactDown(1, 2, itemStack.getCount(), iItemHandler);
                        }
                        if (itemStack.getItem().asItem() == ModBlocks.PLATINUM_COIN_PILE.asItem()) {
                            remainder = iItemHandler.insertItem(3, itemStack, false);
                            //compactDown(2, 3, itemStack.getCount(), iItemHandler);
                        }

                        //System.out.println("StackCount: " + itemStack.getCount() + " Remainder: " + remainder.getCount());

                        itemStack.setCount(remainder.getCount());

                        //System.out.println("AFTER -> " + itemStack.getCount());
                        if (itemStack.isEmpty()) {
                            cir.setReturnValue(true);
                        }
                    });
                }
            }
        }
    }

    private void compactUp(int start, int range, int count, IItemHandler iItemHandler) {
        ItemStack stack = iItemHandler.getStackInSlot(start);
        if (stack.is(Items.AIR)) {
            stack = getItemBySlot(start);
        }

        int stackCount = Mth.intFloorDiv(count, 9);

        System.out.println("Compact Up -> Slot: " + start + " Stack: " + stack.getItem() + " Count: " + stackCount);

        if (stackCount != stack.getCount()) {
            iItemHandler.insertItem(start, ItemHandlerHelper.copyStackWithSize(stack, stackCount), false);
        }

        if (range > 1) {
            compactUp(start + 1, range - 1, stackCount, iItemHandler);
        }
    }

    private void compactDown(int start, int range, int count, IItemHandler iItemHandler) {
        ItemStack stack = iItemHandler.getStackInSlot(start);
        int stackCount = 0;
        if (stack.is(Items.AIR)) {
            stack = getItemBySlot(start);
            stackCount = count * 9;
        } else {
            stackCount = stack.getCount();
            stackCount -= Mth.intFloorDiv(stackCount, 9) * 9;
            stackCount += count * 9;
        }

        System.out.println("Compact Down -> Slot: " + start + " Stack: " + stack.getItem() + " Count: " + stackCount);

        if (stackCount != stack.getCount()) {
            iItemHandler.insertItem(start, ItemHandlerHelper.copyStackWithSize(stack, stackCount), false);
        }

        if (range > 1) {
            compactDown(start - 1, range - 1, stackCount, iItemHandler);
        }
    }

    private ItemStack getItemBySlot(int slot) {
        if (slot == 0)
            return new ItemStack(ModBlocks.BRONZE_COIN_PILE);
        if (slot == 1)
            return new ItemStack(ModBlocks.SILVER_COIN_PILE);
        if (slot == 2)
            return new ItemStack(ModBlocks.GOLD_COIN_PILE);
        if (slot == 3)
            return new ItemStack(ModBlocks.PLATINUM_COIN_PILE);
        return ItemStack.EMPTY;
    }
}
