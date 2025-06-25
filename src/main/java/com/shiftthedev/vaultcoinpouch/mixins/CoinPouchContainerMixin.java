package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.config.CoinData;
import com.shiftthedev.vaultcoinpouch.config.VCPData;
import iskallia.vault.container.inventory.CoinPouchContainer;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.slot.CompactingSlot;
import iskallia.vault.container.slot.ConditionalReadSlot;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;
import java.util.List;

@Mixin(value = CoinPouchContainer.class, remap = false)
public abstract class CoinPouchContainerMixin extends OverSizedSlotContainer {
    private HashMap<String, CompactingSlot> compactingSlots = new HashMap<>();
    
    @Shadow
    public abstract boolean canAccess(int slot, ItemStack slotStack);

    protected CoinPouchContainerMixin(MenuType<?> menuType, int id, Player player) {
        super(menuType, id, player);
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    private void initSlots(IItemHandler playerInvHandler, IItemHandler pouchHandler) {
        for(int row = 0; row < 3; ++row) {
            for(int column = 0; column < 9; ++column) {
                this.addSlot(new ConditionalReadSlot(playerInvHandler, column + row * 9 + 9, 8 + column * 18, 84 + row * 18, this::canAccess));
            }
        }

        for(int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new ConditionalReadSlot(playerInvHandler, hotbarSlot, 8 + hotbarSlot * 18, 142, this::canAccess));
        }

        // Pouch Slots
        List<CoinData> coinDataList = VCPData.getCoinDataList();

        for (int i = 0; i < coinDataList.size(); i++) {
            CoinData coinData = coinDataList.get(i);
            this.compactingSlots.put(coinData.coin_id, (CompactingSlot) this.addSlot(new CompactingSlot(pouchHandler, coinData.index, 9 + (i * 20), (i % 2 == 0 ? 25 : 33), (CoinPouchContainer)(Object)this, Registry.ITEM.get(new ResourceLocation(coinData.coin_id)).asItem())));
        }

        this.compactingSlots.forEach((id, compactingSlot) -> {
            CoinData data = VCPData.getCoinData(id);
            if (data != null) {
                compactingSlot.setupSlots(this.compactingSlots.get(data.previous_coin_id), this.compactingSlots.get(data.next_coin_id));
            }
        });
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public ItemStack quickMoveStack(Player playerIn, int index) {
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
