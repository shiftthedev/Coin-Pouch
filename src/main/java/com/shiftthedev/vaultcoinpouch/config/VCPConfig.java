package com.shiftthedev.vaultcoinpouch.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class VCPConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec COMMON_SPEC;

    private static ForgeConfigSpec.ConfigValue<Boolean> enableShardPouchSoulbound;
    private static ForgeConfigSpec.ConfigValue<Boolean> useShortCoinCountInInventoryHud;
    
    public VCPConfig() 
    {}

    public static void updateFromServer(boolean serverShardPouchSoulbound, boolean serverShortCount) {
        enableShardPouchSoulbound.set(serverShardPouchSoulbound);
        useShortCoinCountInInventoryHud.set(serverShortCount);
    }

    public static boolean shardPouchSoulboundEnabled() {
        return enableShardPouchSoulbound.get();
    }

    public static boolean invShortEnabled() {
        return useShortCoinCountInInventoryHud.get();
    }

    static {
        BUILDER.push("General");

        enableShardPouchSoulbound = BUILDER
                .comment("Enable / Disable the possibility to add Soulbound to Shard Pouch.")
                .define("enableShardPouchSoulbound", true);

        useShortCoinCountInInventoryHud = BUILDER
                .comment("Display 1k instead of 1000.")
                .define("useShortCoinCountInInventoryHud", true);

        BUILDER.pop();
        
        COMMON_SPEC = BUILDER.build();
    }
}
