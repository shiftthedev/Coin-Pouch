package com.shiftthedev.vaultcoinpouch;

import net.minecraftforge.common.ForgeConfigSpec;

public class VCPConfig {
    private final ForgeConfigSpec.ConfigValue<Boolean> enableSoulbound;

    public static ForgeConfigSpec CONFIG;

    public VCPConfig() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        this.enableSoulbound = builder
                .comment("Enable / Disable the possibility to add Soulbound to the pouch.")
                .define("enableSoulbound", true);

        VCPConfig.CONFIG = builder.build();
    }

    public boolean soulboundEnabled() {
        return this.enableSoulbound.get();
    }
}
