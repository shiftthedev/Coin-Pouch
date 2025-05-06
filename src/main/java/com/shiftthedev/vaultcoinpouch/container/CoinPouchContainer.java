package com.shiftthedev.vaultcoinpouch.container;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.config.CoinData;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.ConditionalReadSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

public class CoinPouchContainer extends OverSizedSlotContainer {
    private final int pouchSlot;
    private final boolean fromCurios;
    private final Inventory inventory;
    
    private List<CompactingSlot> compactingSlots = new ArrayList<>();

    public CoinPouchContainer(int id, Inventory playerInventory, int pouchSlot) {
        super(VCPRegistry.COIN_POUCH_CONTAINER, id, playerInventory.player);
        this.inventory = playerInventory;
        this.pouchSlot = pouchSlot;
        this.fromCurios = pouchSlot == -1;

        if (!this.hasPouch(playerInventory.player)) {
            return;
        }

        playerInventory.player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(playerHandler -> {
            ItemStack pouch;
            if (this.fromCurios) {
                pouch = CuriosApi.getCuriosHelper().findFirstCurio(playerInventory.player, VCPRegistry.COIN_POUCH).get().stack();
            } else {
                pouch = this.inventory.getItem(this.pouchSlot);
            }

            if (!pouch.isEmpty()) {
                pouch.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                        .ifPresent(pouchHandler -> initSlots(playerHandler, pouchHandler));
            }
        });
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
        List<CoinData> coinData = VCPConfig.getCoinDataList();

        for (int i = 0; i < coinData.size(); i++) {
            this.compactingSlots.add((CompactingSlot) this.addSlot(new CompactingSlot(pouchHandler, 9 + (i * 20), (i % 2 == 0 ? 17 : 25), this, coinData.get(i))));
        }
       
        for (int i = 0; i < this.compactingSlots.size(); i++) {
            this.compactingSlots.get(i).setupSlots(this.compactingSlots);
        }
    }
    
    public int coinSlotCount()
    {
        return this.compactingSlots.size();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.hasPouch(player);
    }

    public boolean canAccess(int slot, ItemStack slotStack) {
        return !(slotStack.getItem() instanceof CoinPouchItem);
    }

    public boolean hasPouch(Player player) {
        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent()) {
            return true;
        } else if (this.pouchSlot == -1) {
            return false;
        }

        ItemStack pouchStack = this.inventory.getItem(this.pouchSlot);
        return !pouchStack.isEmpty() && pouchStack.getItem() instanceof CoinPouchItem;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return itemStack;
        }

        ItemStack slotStack = slot.getItem();
        itemStack = slotStack.copy();
        if (index >= 0 && index < 36 && this.moveItemStackTo(slotStack, 36, 36 + this.compactingSlots.size(), false)) {
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
        return itemStack;
    }
}
