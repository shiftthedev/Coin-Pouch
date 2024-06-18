package com.shiftthedev.vaultcoinpouch.events;

import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.network.ConfigSyncMessage;
import iskallia.vault.init.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class PlayerEvents
{
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        VaultCoinPouch.LOGGER.info("Syncing config to {} ({})", event.getPlayer().getGameProfile().getName(), event.getPlayer().getGameProfile().getId());
        ModNetwork.CHANNEL.sendTo(new ConfigSyncMessage(VCPConfig.GENERAL), ((ServerPlayer) event.getPlayer()).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
