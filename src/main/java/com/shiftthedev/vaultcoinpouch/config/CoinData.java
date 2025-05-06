package com.shiftthedev.vaultcoinpouch.config;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public class CoinData {
    public int index;
    public String color;
    public String coin_id;
    public String next_coin_id;
    public String previous_coin_id;
    public int previous_coin_count_to_upgrade;

    public CoinData(int index, String color, String coin_id, String next_coin_id, String previous_coin_id, int previous_coin_count_to_upgrade) {
        this.index = index;
        this.color = color;
        this.coin_id = coin_id;
        this.next_coin_id = next_coin_id;
        this.previous_coin_id = previous_coin_id;
        this.previous_coin_count_to_upgrade = previous_coin_count_to_upgrade;
    }
    
    public CoinData()
    {}

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.index);
        buffer.writeUtf(this.color);
        buffer.writeUtf(this.coin_id);
        buffer.writeUtf(this.next_coin_id);
        buffer.writeUtf(this.previous_coin_id);
        buffer.writeInt(this.previous_coin_count_to_upgrade);
    }
    
    public CoinData read(FriendlyByteBuf buffer) {
        this.index = buffer.readInt();
        this.color = buffer.readUtf();
        this.coin_id = buffer.readUtf();
        this.next_coin_id = buffer.readUtf();
        this.previous_coin_id = buffer.readUtf();
        this.previous_coin_count_to_upgrade = buffer.readInt();
        
        return this;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        CoinData other = (CoinData) obj;
        return this.index == other.index &&
                Objects.equals(this.color, other.color) &&
                Objects.equals(this.coin_id, other.coin_id) &&
                Objects.equals(this.next_coin_id, other.next_coin_id) &&
                Objects.equals(this.previous_coin_id, other.previous_coin_id) &&
                this.previous_coin_count_to_upgrade == other.previous_coin_count_to_upgrade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, color, coin_id, next_coin_id, previous_coin_id, previous_coin_count_to_upgrade);
    }
}
