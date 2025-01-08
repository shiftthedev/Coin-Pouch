package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.ItemShardPouch;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.MOD_ID;

@Mixin(value = ItemShardPouch.class, priority = 1100)
public abstract class ItemShardPouchMixin
{
    @Inject(method = "appendHoverText", at = @At("RETURN"))
    private void appendHoverText_post_coinpouch(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci)
    {
        if (!VCPConfig.GENERAL.shardPouchSoulboundEnabled())
        {
            return;
        }
        
        tooltip.add(new TextComponent(" "));
        if (AttributeGearData.read(stack).hasAttribute(ModGearAttributes.SOULBOUND))
        {
            tooltip.add(new TextComponent(ModGearAttributes.SOULBOUND.getReader().getModifierName()).withStyle(ModGearAttributes.SOULBOUND.getReader().getColoredTextStyle()));
        }
        else
        {
            tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".soulbound").withStyle(ChatFormatting.GRAY));
        }
    }
}
