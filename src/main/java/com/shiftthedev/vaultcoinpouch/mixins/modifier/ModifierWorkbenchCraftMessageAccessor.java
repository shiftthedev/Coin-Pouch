package com.shiftthedev.vaultcoinpouch.mixins.modifier;

import iskallia.vault.network.message.ModifierWorkbenchCraftMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModifierWorkbenchCraftMessage.class, remap = false)
public interface ModifierWorkbenchCraftMessageAccessor
{
    @Accessor("pos")
    public BlockPos getPos();

    @Accessor("craftModifierIdentifier")
    public ResourceLocation getCraftModifierIdentifier();
}
