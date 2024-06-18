package com.shiftthedev.vaultcoinpouch.network;

import com.google.common.collect.Maps;
import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class ConfigSyncMessage
{
    private Map<String, Boolean> configs = Maps.newHashMap();

    public ConfigSyncMessage(VCPConfig.General server)
    {
        configs.put("General.enableSoulbound", server.soulboundEnabled());
        configs.put("General.shopPedestalInteraction", server.shopPedestalEnabled());
        configs.put("General.vaultForgeInteraction", server.vaultForgeEnabled());
        configs.put("General.toolStationInteraction", server.toolStationEnabled());
        configs.put("General.inscriptionTableInteraction", server.inscriptionTableEnabled());
        configs.put("General.modifierWorkbenchInteraction", server.modifierWorkbenchEnabled());
        configs.put("General.alchemyTableInteraction", server.alchemyTableEnabled());
        configs.put("General.transmogTableInteraction", server.transmogTableEnabled());
        configs.put("General.vaultArtisanStationInteraction", server.vaultArtisanStationEnabled());
        configs.put("General.jewelCuttingStationInteraction", server.jewelCuttingStationEnabled());
        configs.put("General.spiritExtractorInteraction", server.spiritExtractorEnabled());
    }

    public ConfigSyncMessage(Map<String, Boolean> configs)
    {
        this.configs = configs;
    }

    public static void encode(ConfigSyncMessage message, FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.configs.size());
        message.configs.forEach((k, v) -> {
            buffer.writeUtf(k);
            buffer.writeBoolean(v);
        });
    }

    public static ConfigSyncMessage decode(FriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        Map<String, Boolean> temp = Maps.newHashMap();
        for (int i = 0; i < size; i++)
        {
            String id = buffer.readUtf(128);
            boolean value = buffer.readBoolean();
            temp.put(id, value);
        }

        return new ConfigSyncMessage(temp);
    }

    public static void handle(ConfigSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() ->
        {
            VCPConfig.updateFromServer(message.configs);
            VaultCoinPouch.LOGGER.info("Received config from server.");
        });
        context.setPacketHandled(true);
    }
}
