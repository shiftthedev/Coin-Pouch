package com.shiftthedev.vaultcoinpouch.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "of", at = @At("HEAD"))
    private static void of_coinpouch(CompoundTag compoundTag, CallbackInfoReturnable<ItemStack> cir) {
        if (compoundTag.contains("id") && compoundTag.getString("id").equals("vaultcoinpouch:coin_pouch")) {
            if (compoundTag.contains("tag", 10)) {
                CompoundTag tag = compoundTag.getCompound("tag");
                if (tag.contains("Inventory", 10)) {
                    CompoundTag invTag = tag.getCompound("Inventory");
                    if (invTag.contains("BronzeStackSize")) {
                        int bronze = invTag.getInt("BronzeStackSize");
                        invTag.remove("BronzeStackSize");
                        invTag.putInt("the_vault:vault_bronze", bronze);
                    }

                    if (invTag.contains("SilverStackSize")) {
                        int silver = invTag.getInt("SilverStackSize");
                        invTag.remove("SilverStackSize");
                        invTag.putInt("the_vault:vault_silver", silver);
                    }

                    if (invTag.contains("GoldStackSize")) {
                        int gold = invTag.getInt("GoldStackSize");
                        invTag.remove("GoldStackSize");
                        invTag.putInt("the_vault:vault_gold", gold);
                    }

                    if (invTag.contains("PlatinumStackSize")) {
                        int platinum = invTag.getInt("PlatinumStackSize");
                        invTag.remove("PlatinumStackSize");
                        invTag.putInt("the_vault:vault_platinum", platinum);
                    }

                    compoundTag.put("Inventory", invTag);
                }
            }
        }
    }
}
