package com.shiftthedev.vaultcoinpouch.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.LOGGER;

public class VCPData {
    private static final Path COIN_DATA_PATH = Path.of("config/shift_mods/coinpouch/coins.shift");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static List<CoinData> coinDataList = new ArrayList<>();
    
    public VCPData()
    {}
    
    public static void loadData() {
        if (!Files.exists(COIN_DATA_PATH)) {
            genDefaultCoinData();
        }

        try (Reader reader = Files.newBufferedReader(COIN_DATA_PATH)) {
            Type type = new TypeToken<List<CoinData>>() {
            }.getType();
            List<CoinData> coinDataList = GSON.fromJson(reader, type);

            validateCoinData(coinDataList);
        } catch (IOException ex) {
            LOGGER.warn("[CoinPouch] Failed to load coin data");
            ex.printStackTrace();
        }
    }

    private static void genDefaultCoinData() {
        try (Writer writer = Files.newBufferedWriter(COIN_DATA_PATH)) {
            List<CoinData> coinDataList = List.of(
                    new CoinData(0, "#E07F1F", "the_vault:vault_bronze", "the_vault:vault_silver", "", 0),
                    new CoinData(1, "#C0C0C0", "the_vault:vault_silver", "the_vault:vault_gold", "the_vault:vault_bronze", 9),
                    new CoinData(2, "#FFAA00", "the_vault:vault_gold", "the_vault:vault_platinum", "the_vault:vault_silver", 9),
                    new CoinData(3, "#F5F5F5", "the_vault:vault_platinum", "", "the_vault:vault_gold", 9)
            );

            GSON.toJson(coinDataList, writer);
        } catch (IOException ex) {
            LOGGER.warn("[CoinPouch] Failed to generate default coin data");
            ex.printStackTrace();
        }
    }

    private static void validateCoinData(List<CoinData> temp) {
        temp.sort((coinA, coinB) -> {
            if (coinA.index < coinB.index)
                return -1;

            if (coinA.index > coinB.index)
                return 1;

            return 0;
        });

        coinDataList = temp;
    }

    public static void reloadCoins() {
        loadData();
    }

    public static void applyServerCoinData(List<CoinData> serverCoinDataList) {
        coinDataList = serverCoinDataList;
    }

    public static List<CoinData> getCoinDataList() {
        return coinDataList;
    }

    public static int getCoinDataCount() {
        return coinDataList.size();
    }

    public static CoinData getCoinData(int index) {
        if(index < getCoinDataCount() && index >= 0) {
            return coinDataList.get(index);
        }

        return null;
    }

    public static CoinData getCoinData(String id) {
        int count = getCoinDataCount();
        if (count <= 0) {
            return null;
        }

        for (int i = 0; i < count; i++)
        {
            CoinData data = getCoinData(i);
            if (data.coin_id.equals(id)) {
                return data;
            }
        }

        return null;
    }
}
