package com.shiftthedev.vaultcoinpouch;

import net.minecraftforge.common.ForgeConfigSpec;

public class VCPConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL;
    public static ForgeConfigSpec CONFIG;
    
    public VCPConfig() {
    }
   
    static {
        GENERAL = new General(BUILDER);
        CONFIG = BUILDER.build();
    }
    
    public static class General {
        private final ForgeConfigSpec.ConfigValue<Boolean> enableSoulbound;

        public General(ForgeConfigSpec.Builder builder){
            builder.push("General");
            
            this.enableSoulbound = builder
                    .comment("Enable / Disable the possibility to add Soulbound to the pouch.")
                    .define("enableSoulbound", true);

            builder.pop();
        }
        
        public boolean soulboundEnabled() {
            return this.enableSoulbound.get();
        }
    }
}
