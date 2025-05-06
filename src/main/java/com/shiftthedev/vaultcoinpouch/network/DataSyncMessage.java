package com.shiftthedev.vaultcoinpouch.network;

import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.config.CoinData;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DataSyncMessage {
    List<CoinData> coinDataList = new ArrayList<>();
    
    public DataSyncMessage() {
        this.coinDataList = VCPConfig.getCoinDataList();
    }
    
    public DataSyncMessage(List<CoinData> coinData) {
        this.coinDataList = coinData;
    }
    
    public static void encode(DataSyncMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.coinDataList.size());
        message.coinDataList.forEach((data) -> {
            data.write(buffer);
        });
    }
    
    public static DataSyncMessage decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<CoinData> temp = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            CoinData data = new CoinData().read(buffer);
            temp.add(data);
        }
        
        return new DataSyncMessage(temp);
    }
    
    public static void handle(DataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            VCPConfig.applyServerCoinData(message.coinDataList);
            VaultCoinPouch.LOGGER.info("Received data from server.");
        });
        context.setPacketHandled(true);
    }
}
