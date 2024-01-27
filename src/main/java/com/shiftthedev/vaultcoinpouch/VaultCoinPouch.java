package com.shiftthedev.vaultcoinpouch;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(VaultCoinPouch.MOD_ID)
public class VaultCoinPouch {
    public static final String MOD_ID = "vaultcoinpouch";


    public VaultCoinPouch() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, VCPConfig.CONFIG);
    }
}
