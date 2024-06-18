package com.shiftthedev.vaultcoinpouch.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class VCPConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final String CONFIG_PATH = "config/shift_mods/coinpouch/coinpouch-common.shift";
    private static final String OLD_PATH = "config/vaultcoinpouch-common.toml";

    private static CommentedFileConfig CONFIG_FILE;
    public static ForgeConfigSpec CONFIG;

    public static final General GENERAL;

    public VCPConfig()
    {
    }

    public static void initConfig()
    {
        Path path = Paths.get(CONFIG_PATH);
        if (!Files.exists(path))
        {
            try
            {
                Files.createDirectories(path.getParent());
                Files.createFile(path);

                CommentedFileConfig defaults = CommentedFileConfig.builder(path, TomlFormat.instance()).writingMode(WritingMode.REPLACE).build();
                defaults.set("version.versionvalue", 1);
                defaults.save();
                defaults.close();
            }
            catch (IOException ex)
            {
                throw new RuntimeException("Failed to create coin pouch config file", ex);
            }
        }

        CONFIG_FILE = CommentedFileConfig.builder(path, TomlFormat.instance()).writingMode(WritingMode.REPLACE).build();
        CONFIG_FILE.load();

        Path oldPath = Paths.get(OLD_PATH);
        if (Files.exists(oldPath))
        {
            CommentedFileConfig old_config = CommentedFileConfig.builder(OLD_PATH).writingMode(WritingMode.REPLACE).build();
            old_config.load();

            CONFIG_FILE.set("General.enableSoulbound", old_config.get("General.enableSoulbound"));
            saveConfig();

            old_config.close();
            try
            {
                Files.delete(oldPath);
            }
            catch (IOException ex)
            {
                throw new RuntimeException("Failed to delete old coin pouch config file", ex);
            }
        }

        CONFIG.setConfig(CONFIG_FILE);
    }

    public static void reloadConfig()
    {
        CONFIG_FILE.load();
        CONFIG.setConfig(CONFIG_FILE);
    }

    public static void updateFromServer(Map<String, Boolean> serverConfigs)
    {
        serverConfigs.forEach(CONFIG_FILE::set);
        CONFIG.setConfig(CONFIG_FILE);
    }

    public static void saveConfig()
    {
        CONFIG_FILE.save();
    }

    static
    {
        GENERAL = new General(BUILDER);
        CONFIG = BUILDER.build();
    }

    public static class General
    {
        private ForgeConfigSpec.ConfigValue<Boolean> enableSoulbound;
        private ForgeConfigSpec.ConfigValue<Boolean> shopPedestalInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> vaultForgeInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> toolStationInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> inscriptionTableInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> modifierWorkbenchInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> alchemyTableInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> transmogTableInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> vaultArtisanStationInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> jewelCuttingStationInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> spiritExtractorInteraction;

        public General(ForgeConfigSpec.Builder builder)
        {
            builder.push("General");

            this.enableSoulbound = builder
                    .comment("Enable / Disable the possibility to add Soulbound to the pouch.")
                    .define("enableSoulbound", true);

            this.vaultForgeInteraction = builder
                    .comment("Enable / Disable interaction with Vault Forge without taking coins out of the pouch.")
                    .define("vaultForgeInteraction", true);

            this.toolStationInteraction = builder
                    .comment("Enable / Disable interaction with Tool Station without taking coins out of the pouch.")
                    .define("toolStationInteraction", true);

            this.inscriptionTableInteraction = builder
                    .comment("Enable / Disable interaction with Inscription Table without taking coins out of the pouch.")
                    .define("inscriptionTableInteraction", true);

            this.modifierWorkbenchInteraction = builder
                    .comment("Enable / Disable interaction with Modifier Workbench without taking coins out of the pouch.")
                    .define("modifierWorkbenchInteraction", true);

            this.alchemyTableInteraction = builder
                    .comment("Enable / Disable interaction with Alchemy Table without taking coins out of the pouch.")
                    .define("alchemyTableInteraction", true);

            this.shopPedestalInteraction = builder
                    .comment("Enable / Disable interaction with Shop Pedestal without taking coins out of the pouch.")
                    .define("shopPedestalInteraction", true);

            this.transmogTableInteraction = builder
                    .comment("Enable / Disable interaction with Transmog Table without taking coins out of the pouch.")
                    .define("transmogTableInteraction", true);

            this.vaultArtisanStationInteraction = builder
                    .comment("Enable / Disable interaction with Vault Artisan Station without taking coins out of the pouch.")
                    .define("vaultArtisanStationInteraction", true);

            this.jewelCuttingStationInteraction = builder
                    .comment("Enable / Disable interaction with Jewel Cutting Station without taking coins out of the pouch.")
                    .define("jewelCuttingStationInteraction", true);

            this.spiritExtractorInteraction = builder
                    .comment("Enable / Disable interaction with Spirit Extractor without taking coins out of the pouch.")
                    .define("spiritExtractorInteraction", true);

            builder.pop();
        }

        public boolean soulboundEnabled()
        {
            return this.enableSoulbound.get();
        }

        public boolean vaultForgeEnabled() {return this.vaultForgeInteraction.get();}

        public boolean toolStationEnabled() {return this.toolStationInteraction.get();}

        public boolean inscriptionTableEnabled() {return this.inscriptionTableInteraction.get();}

        public boolean modifierWorkbenchEnabled() {return this.modifierWorkbenchInteraction.get();}

        public boolean alchemyTableEnabled() {return this.alchemyTableInteraction.get();}

        public boolean shopPedestalEnabled() {return this.shopPedestalInteraction.get();}

        public boolean transmogTableEnabled() {return this.transmogTableInteraction.get();}

        public boolean vaultArtisanStationEnabled() {return this.vaultArtisanStationInteraction.get();}

        public boolean jewelCuttingStationEnabled() {return this.jewelCuttingStationInteraction.get();}

        public boolean spiritExtractorEnabled() {return this.spiritExtractorInteraction.get();}

        public void cycleSoulbound()
        {
            this.enableSoulbound.set(!this.enableSoulbound.get());
        }

        public void cycleVaultForge()
        {
            this.vaultForgeInteraction.set(!this.vaultForgeInteraction.get());
        }

        public void cycleToolStation()
        {
            this.toolStationInteraction.set(!this.toolStationInteraction.get());
        }

        public void cycleInscriptionTable()
        {
            this.inscriptionTableInteraction.set(!this.inscriptionTableInteraction.get());
        }

        public void cycleModifierWorkbench()
        {
            this.modifierWorkbenchInteraction.set(!this.modifierWorkbenchInteraction.get());
        }

        public void cycleAlchemyTable()
        {
            this.alchemyTableInteraction.set(!this.alchemyTableInteraction.get());
        }

        public void cycleShopPedestal()
        {
            this.shopPedestalInteraction.set(!this.shopPedestalInteraction.get());
        }

        public void cycleTransmogTable()
        {
            this.transmogTableInteraction.set(!this.transmogTableInteraction.get());
        }

        public void cycleVaultArtisanStation()
        {
            this.vaultArtisanStationInteraction.set(!this.vaultArtisanStationInteraction.get());
        }

        public void cycleJewelCuttingStation()
        {
            this.jewelCuttingStationInteraction.set(!this.jewelCuttingStationInteraction.get());
        }

        public void cycleSpiritExtractor()
        {
            this.spiritExtractorInteraction.set(!this.spiritExtractorInteraction.get());
        }
    }
}
