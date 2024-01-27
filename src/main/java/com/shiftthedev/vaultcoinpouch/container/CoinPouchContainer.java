package com.shiftthedev.vaultcoinpouch.container;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.ConditionalReadSlot;
import iskallia.vault.init.ModBlocks;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CoinPouchContainer extends OverSizedSlotContainer {
    private final int pouchSlot;
    private final Inventory inventory;

    public CoinPouchContainer(int id, Inventory playerInventory, int pouchSlot) {
        super(VCPRegistry.COIN_POUCH_CONTAINER, id, playerInventory.player);
        this.inventory = playerInventory;
        this.pouchSlot = pouchSlot;
        if (this.hasPouch()) {
            playerInventory.player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(playerHandler -> {
                ItemStack pouch = this.inventory.getItem(this.pouchSlot);
                pouch.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(pouchHandler -> {
                    this.initSlots(playerHandler, pouchHandler);
                });
            });
        }
    }

    private void initSlots(IItemHandler playerHandler, final IItemHandler pouchHandler) {
        int hotbarSlot;
        // Player Inventory
        for (hotbarSlot = 0; hotbarSlot < 3; ++hotbarSlot) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new ConditionalReadSlot(playerHandler, column + hotbarSlot * 9 + 9, 8 + column * 18, 55 + hotbarSlot * 18, this::canAccess));
            }
        }

        // Player Hotbar
        for (hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new ConditionalReadSlot(playerHandler, hotbarSlot, 8 + hotbarSlot * 18, 113, this::canAccess));
        }

        // Pouch Slots
        Slot bronze = this.addSlot(new CompactingSlot(pouchHandler, 0, 50, 16, this, ModBlocks.BRONZE_COIN_PILE.asItem()));
        Slot silver = this.addSlot(new CompactingSlot(pouchHandler, 1, 70, 24, this, ModBlocks.SILVER_COIN_PILE.asItem()));
        Slot gold = this.addSlot(new CompactingSlot(pouchHandler, 2, 90, 16, this, ModBlocks.GOLD_COIN_PILE.asItem()));
        Slot plat = this.addSlot(new CompactingSlot(pouchHandler, 3, 110, 24, this, ModBlocks.PLATINUM_COIN_PILE.asItem()));
        ((CompactingSlot) bronze).setupSlots(null, silver);
        ((CompactingSlot) silver).setupSlots(bronze, gold);
        ((CompactingSlot) gold).setupSlots(silver, plat);
        ((CompactingSlot) plat).setupSlots(gold, null);
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return this.hasPouch();
    }

    public boolean canAccess(int slot, ItemStack slotStack) {
        return this.hasPouch() && !(slotStack.getItem() instanceof CoinPouchItem);
    }

    public boolean hasPouch() {
        ItemStack pouchStack = this.inventory.getItem(this.pouchSlot);
        return !pouchStack.isEmpty() && pouchStack.getItem() instanceof CoinPouchItem;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            if (index >= 0 && index < 36 && this.moveItemStackTo(slotStack, 36, 40, false)) {
                return itemStack;
            }

            if (index >= 0 && index < 27) {
                if (!this.moveItemStackTo(slotStack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 && index < 36) {
                if (!this.moveItemStackTo(slotStack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return itemStack;
    }
}
