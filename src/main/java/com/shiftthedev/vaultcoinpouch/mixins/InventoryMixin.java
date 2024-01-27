package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.CoinPileDecorBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.world.data.InventorySnapshotData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
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
                        }
                        if (itemStack.getItem().asItem() == ModBlocks.SILVER_COIN_PILE.asItem()) {
                            remainder = iItemHandler.insertItem(1, itemStack, false);
                        }
                        if (itemStack.getItem().asItem() == ModBlocks.GOLD_COIN_PILE.asItem()) {
                            remainder = iItemHandler.insertItem(2, itemStack, false);
                        }
                        if (itemStack.getItem().asItem() == ModBlocks.PLATINUM_COIN_PILE.asItem()) {
                            remainder = iItemHandler.insertItem(3, itemStack, false);
                        }

                        itemStack.setCount(remainder.getCount());

                        if (itemStack.isEmpty()) {
                            cir.setReturnValue(true);
                        }
                    });
                }
            }
        }
    }
}
