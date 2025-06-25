package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import iskallia.vault.item.ItemShardPouch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ItemShardPouch.class, priority = 1100)
public abstract class ItemShardPouchMixin {
    @Inject(method = "appendHoverText", at = @At("RETURN"))
    private void appendSoulbound(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        if (VCPConfig.shardPouchSoulboundEnabled()) {
            VaultCoinPouch.addSoulboundTooltip(stack, tooltip);
        }
    }
}
