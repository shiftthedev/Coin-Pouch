package com.shiftthedev.vaultcoinpouch.config;

import java.util.Objects;

public class CoinData {
    public String coin_id;
    public String next_coin_id;
    public String previous_coin_id;
    public int previous_coin_count_to_upgrade;

    public CoinData(String coin_id, String next_coin_id, String previous_coin_id, int previous_coin_count_to_upgrade) {
        this.coin_id = coin_id;
        this.next_coin_id = next_coin_id;
        this.previous_coin_id = previous_coin_id;
        this.previous_coin_count_to_upgrade = previous_coin_count_to_upgrade;
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
        return Objects.equals(this.coin_id, other.coin_id) &&
                Objects.equals(this.next_coin_id, other.next_coin_id) &&
                Objects.equals(this.previous_coin_id, other.previous_coin_id) &&
                this.previous_coin_count_to_upgrade == other.previous_coin_count_to_upgrade;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coin_id, next_coin_id, previous_coin_id, previous_coin_count_to_upgrade);
    }
}
