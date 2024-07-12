package com.shiftthedev.vaultcoinpouch.mixins.alchemy;

import iskallia.vault.network.message.AlchemyTableEffectCraftMessage;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AlchemyTableEffectCraftMessage.class, remap = false)
public interface AlchemyTableEffectCraftMessageAccessor
{
    @Accessor("pos")
    public BlockPos getPos();

    @Accessor("effectId")
    public String getEffectId();
}
