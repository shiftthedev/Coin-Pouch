package com.shiftthedev.vaultcoinpouch.mixins.paradox;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.server_helpers.ParadoxServerHelper;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.event.Event;
import iskallia.vault.core.event.common.GateLockOpenEvent;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.objective.Objective;
import iskallia.vault.core.vault.objective.ParadoxObjective;
import iskallia.vault.core.world.storage.VirtualWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;
import java.util.function.Consumer;

@Mixin(value = ParadoxObjective.class, remap = false, priority = 1100)
public abstract class ParadoxObjectiveMixin extends Objective
{
    @Shadow
    @Final
    public static FieldKey<ParadoxObjective.Type> TYPE;

    @Shadow
    @Final
    public static FieldKey<UUID> PLAYER;

    @Shadow
    @Final
    public static FieldKey<Long> SEED;

    @Redirect(method = "initServer", at = @At(value = "INVOKE", target = "Liskallia/vault/core/event/common/GateLockOpenEvent;register(Ljava/lang/Object;Ljava/util/function/Consumer;)Liskallia/vault/core/event/Event;"))
    private Event gateLockOpenEvent_coinpouch(GateLockOpenEvent instance, Object o, Consumer consumer, VirtualWorld world, Vault vault)
    {
        if (VCPConfig.GENERAL.paradoxDoorsEnabled())
        {
            return CommonEvents.GATE_LOCK_OPEN.register(this, (data) -> ParadoxServerHelper.registerGateOpen_coinpouch((ParadoxObjective) (Object) this, data, world, vault));
        }
        else
        {
            return CommonEvents.GATE_LOCK_OPEN.register(this, (data) -> ParadoxServerHelper.registerGateOpen_vh((ParadoxObjective) (Object) this, data, world, vault));
        }
    }
}
