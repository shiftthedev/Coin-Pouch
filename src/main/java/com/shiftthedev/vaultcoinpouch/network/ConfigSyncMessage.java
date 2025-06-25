package com.shiftthedev.vaultcoinpouch.network;

import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConfigSyncMessage {
    private boolean shardPouchSoulbound;
    private boolean shortCount;

    public ConfigSyncMessage() {
        shardPouchSoulbound = VCPConfig.shardPouchSoulboundEnabled();
        shortCount = VCPConfig.invShortEnabled();
    }

    public ConfigSyncMessage(boolean shardPouchSoulbound, boolean shortCount) {
        this.shardPouchSoulbound = shardPouchSoulbound;
        this.shortCount = shortCount;
    }

    public static void encode(ConfigSyncMessage message, FriendlyByteBuf buffer) {
            buffer.writeBoolean(message.shardPouchSoulbound);
            buffer.writeBoolean(message.shortCount);
    }

    public static ConfigSyncMessage decode(FriendlyByteBuf buffer) {
        boolean shardPouchSoulbound = buffer.readBoolean();
        boolean shortCount = buffer.readBoolean();

        return new ConfigSyncMessage(shardPouchSoulbound, shortCount);
    }

    public static void handle(ConfigSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            VCPConfig.updateFromServer(message.shardPouchSoulbound, message.shortCount);
            VaultCoinPouch.LOGGER.info("Received config from server.");
        });
        context.setPacketHandled(true);
    }
}
