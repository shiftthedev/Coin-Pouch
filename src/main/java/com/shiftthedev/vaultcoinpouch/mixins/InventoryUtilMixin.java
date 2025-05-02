package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(InventoryUtil.class)
public class InventoryUtilMixin {
    @Inject(method = "getCoinPouchItemAccess", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
    private static void customCoinPouchAccess(InventoryUtil.ItemAccess access, CallbackInfoReturnable<List<InventoryUtil.ItemAccess>> cir, List<InventoryUtil.ItemAccess> accesses) {
        ItemStack itemStack = access.getStack();
        if (itemStack.getItem() instanceof CoinPouchItem) {
            ItemStack[] contents = CoinPouchItem.getContainedStacks(itemStack);
            for(int slot = 0; slot < contents.length; ++slot) {
                ItemStack stack = contents[slot];
                if (!stack.isEmpty()) {
                    final int coinSlot = slot;
                    accesses.add(access.chain(stack, (containerStack, newStack) -> {
                        ItemStack[] ctContents = CoinPouchItem.getContainedStacks(containerStack);
                        ctContents[coinSlot] = newStack;
                        CoinPouchItem.setContainedStack(containerStack, coinSlot, newStack.getCount());
                    }));
                }
            }
        }
    }
}
