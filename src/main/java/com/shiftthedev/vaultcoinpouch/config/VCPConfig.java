package com.shiftthedev.vaultcoinpouch.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.LOGGER;

public class VCPConfig {
    // <editor-fold desc="Common Configs Fields">
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final String COMMON_CONFIG_PATH = "config/shift_mods/coinpouch/coinpouch-common.shift";
    private static final String OLD_COMMON_PATH = "config/vaultcoinpouch-common.toml";

    private static CommentedFileConfig CONFIG_FILE;
    public static ForgeConfigSpec COMMON_CONFIG;

    public static final General GENERAL;
    // </editor-fold>

    // <editor-fold desc="Coin Data Fields">
    private static final Path COIN_DATA_PATH = Path.of("config/shift_mods/coinpouch/coins.shift");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Map<String, CoinData> coinDataMap = new HashMap<>();
    // </editor-fold>

    public VCPConfig() {
    }

    public static void initConfig() {
        // load coin data
        //loadCoinData();

        // load common config
        loadCommonConfigs();
    }

    // <editor-fold desc="Coin Data Methods">
    private static void loadCoinData() {
        if (!Files.exists(COIN_DATA_PATH)) {
            genDefaultCoinData();
        }

        try (Reader reader = Files.newBufferedReader(COIN_DATA_PATH)) {
            Type type = new TypeToken<List<CoinData>>() {
            }.getType();
            List<CoinData> coinDataList = GSON.fromJson(reader, type);
            coinDataList.forEach(coinData -> coinDataMap.put(coinData.coin_id, coinData));
        } catch (IOException ex) {
            LOGGER.warn("[CoinPouch] Failed to load coin data");
            ex.printStackTrace();
        }
    }

    private static void genDefaultCoinData() {
        try (Writer writer = Files.newBufferedWriter(COIN_DATA_PATH)) {
            List<CoinData> coinDataList = List.of(
                    new CoinData("the_vault:vault_bronze", "the_vault:vault_silver", "", 0),
                    new CoinData("the_vault:vault_silver", "the_vault:vault_gold", "the_vault:vault_bronze", 9),
                    new CoinData("the_vault:vault_gold", "the_vault:vault_platinum", "the_vault:vault_silver", 9),
                    new CoinData("the_vault:vault_platinum", "", "the_vault:vault_gold", 9)
            );

            GSON.toJson(coinDataList, writer);
        } catch (IOException ex) {
            LOGGER.warn("[CoinPouch] Failed to generate default coin data");
            ex.printStackTrace();
        }
    }

    //public static void reloadCoins()
    //{
    //    loadCoinData();
    //}
    // </editor-fold>

    // <editor-fold desc="Common Configs Methods">
    private static void loadCommonConfigs() {
        Path path = Paths.get(COMMON_CONFIG_PATH);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);

                CommentedFileConfig defaults = CommentedFileConfig.builder(path, TomlFormat.instance()).writingMode(WritingMode.REPLACE).build();
                defaults.set("version.versionvalue", 1);
                defaults.save();
                defaults.close();
            } catch (IOException ex) {
                throw new RuntimeException("Failed to create coin pouch config file", ex);
            }
        }

        CONFIG_FILE = CommentedFileConfig.builder(path, TomlFormat.instance()).writingMode(WritingMode.REPLACE).build();
        CONFIG_FILE.load();

        Path oldPath = Paths.get(OLD_COMMON_PATH);
        if (Files.exists(oldPath)) {
            CommentedFileConfig old_config = CommentedFileConfig.builder(OLD_COMMON_PATH).writingMode(WritingMode.REPLACE).build();
            old_config.load();

            CONFIG_FILE.set("General.enableSoulbound", old_config.get("General.enableSoulbound"));
            saveCommonConfigs();

            old_config.close();
            try {
                Files.delete(oldPath);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to delete old coin pouch config file", ex);
            }
        }

        COMMON_CONFIG.setConfig(CONFIG_FILE);
    }

    public static void reloadCommonConfig() {
        CONFIG_FILE.load();
        COMMON_CONFIG.setConfig(CONFIG_FILE);
    }

    public static void applyCommonServerConfigs(Map<String, Boolean> serverConfigs) {
        serverConfigs.forEach(CONFIG_FILE::set);
        COMMON_CONFIG.setConfig(CONFIG_FILE);
    }

    public static void saveCommonConfigs() {
        CONFIG_FILE.save();
    }

    static {
        GENERAL = new General(BUILDER);
        COMMON_CONFIG = BUILDER.build();
    }
    // </editor-fold>

    public static class General {
        // BLACKLIST (TEMP)
        private ForgeConfigSpec.ConfigValue<List<String>> coinBlacklist;

        // SOULBOUND
        private ForgeConfigSpec.ConfigValue<Boolean> enableSoulbound;
        private ForgeConfigSpec.ConfigValue<Boolean> enableShardPouchSoulbound;

        // INTERACTIONS
        private ForgeConfigSpec.ConfigValue<Boolean> shopPedestalInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> vaultForgeInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> toolStationInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> inscriptionTableInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> modifierWorkbenchInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> alchemyTableInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> transmogTableInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> vaultArtisanStationInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> jewelCraftingTableInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> spiritExtractorInteraction;
        private ForgeConfigSpec.ConfigValue<Boolean> paradoxDoorInteraction;

        // HUD OVERLAY
        private ForgeConfigSpec.ConfigValue<Boolean> showCoinCountInInventoryHud;
        private ForgeConfigSpec.ConfigValue<Boolean> useShortCoinCountInInventoryHud;
        private ForgeConfigSpec.ConfigValue<Boolean> horizontalAlignInInventoryHud;

        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");

            this.enableSoulbound = builder
                    .comment("Enable / Disable the possibility to add Soulbound to the Coin Pouch.")
                    .define("enableSoulbound", true);

            this.enableShardPouchSoulbound = builder
                    .comment("Enable / Disable the possibility to add Soulbound to Shard Pouch.")
                    .define("enableShardPouchSoulbound", true);

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

            this.jewelCraftingTableInteraction = builder
                    .comment("Enable / Disable interaction with Jewel Crafting Station without taking coins out of the pouch.")
                    .define("jewelCraftingTableInteraction", true);

            this.spiritExtractorInteraction = builder
                    .comment("Enable / Disable interaction with Spirit Extractor without taking coins out of the pouch.")
                    .define("spiritExtractorInteraction", true);

            this.paradoxDoorInteraction = builder
                    .comment("Enable / Disable interaction with Paradox Doors without taking coins out of the pouch.")
                    .define("paradoxDoorInteraction", true);

            this.showCoinCountInInventoryHud = builder
                    .comment("Enable / Disable coin display in the Inventory HUD overlay.")
                    .define("showCoinCountInInventoryHud", true);

            this.useShortCoinCountInInventoryHud = builder
                    .comment("Display 1k instead of 1000.")
                    .define("useShortCoinCountInInventoryHud", true);

            this.horizontalAlignInInventoryHud = builder
                    .comment("How should coin display be align.")
                    .define("horizontalAlignInInventoryHud", false);

            // BLACKLIST (TEMP)
            ArrayList<String> a = new ArrayList<>();
            this.coinBlacklist = builder
                    .comment("List of coins to be ignore by Coin Pouch.")
                    .define("coinBlacklist", a);

            builder.pop();
        }

        // BLACKLIST (TEMP)
        public boolean isBlacklisted(String itemID) {
            return this.coinBlacklist.get().contains(itemID);
        }

        // SOULBOUND
        public boolean soulboundEnabled() {
            return this.enableSoulbound.get();
        }

        public boolean shardPouchSoulboundEnabled() {
            return this.enableShardPouchSoulbound.get();
        }

        public void cycleSoulbound() {
            this.enableSoulbound.set(!this.enableSoulbound.get());
        }

        public void cycleShardPouchSoulbound() {
            this.enableShardPouchSoulbound.set(!this.enableShardPouchSoulbound.get());
        }

        // INTERACTIONS
        public boolean vaultForgeEnabled() {
            return this.vaultForgeInteraction.get();
        }

        public boolean toolStationEnabled() {
            return this.toolStationInteraction.get();
        }

        public boolean inscriptionTableEnabled() {
            return this.inscriptionTableInteraction.get();
        }

        public boolean modifierWorkbenchEnabled() {
            return this.modifierWorkbenchInteraction.get();
        }

        public boolean alchemyTableEnabled() {
            return this.alchemyTableInteraction.get();
        }

        public boolean shopPedestalEnabled() {
            return this.shopPedestalInteraction.get();
        }

        public boolean transmogTableEnabled() {
            return this.transmogTableInteraction.get();
        }

        public boolean vaultArtisanStationEnabled() {
            return this.vaultArtisanStationInteraction.get();
        }

        public boolean jewelCraftingTableEnabled() {
            return this.jewelCraftingTableInteraction.get();
        }

        public boolean spiritExtractorEnabled() {
            return this.spiritExtractorInteraction.get();
        }

        public boolean paradoxDoorsEnabled() {
            return this.paradoxDoorInteraction.get();
        }


        public void cycleVaultForge() {
            this.vaultForgeInteraction.set(!this.vaultForgeInteraction.get());
        }

        public void cycleToolStation() {
            this.toolStationInteraction.set(!this.toolStationInteraction.get());
        }

        public void cycleInscriptionTable() {
            this.inscriptionTableInteraction.set(!this.inscriptionTableInteraction.get());
        }

        public void cycleModifierWorkbench() {
            this.modifierWorkbenchInteraction.set(!this.modifierWorkbenchInteraction.get());
        }

        public void cycleAlchemyTable() {
            this.alchemyTableInteraction.set(!this.alchemyTableInteraction.get());
        }

        public void cycleShopPedestal() {
            this.shopPedestalInteraction.set(!this.shopPedestalInteraction.get());
        }

        public void cycleTransmogTable() {
            this.transmogTableInteraction.set(!this.transmogTableInteraction.get());
        }

        public void cycleVaultArtisanStation() {
            this.vaultArtisanStationInteraction.set(!this.vaultArtisanStationInteraction.get());
        }

        public void cycleJewelCraftingStation() {
            this.jewelCraftingTableInteraction.set(!this.jewelCraftingTableInteraction.get());
        }

        public void cycleSpiritExtractor() {
            this.spiritExtractorInteraction.set(!this.spiritExtractorInteraction.get());
        }

        public void cycleParadoxDoors() {
            this.paradoxDoorInteraction.set(!this.paradoxDoorInteraction.get());
        }


        // HUD OVERLAY
        public boolean invCoinsEnabled() {
            return this.showCoinCountInInventoryHud.get();
        }

        public boolean invShortEnabled() {
            return this.useShortCoinCountInInventoryHud.get();
        }

        public boolean invHorizontalEnabled() {
            return this.horizontalAlignInInventoryHud.get();
        }

        public void cycleInvCoins() {
            this.showCoinCountInInventoryHud.set(!this.showCoinCountInInventoryHud.get());
        }

        public void cycleInvShort() {
            this.useShortCoinCountInInventoryHud.set(!this.useShortCoinCountInInventoryHud.get());
        }

        public void cycleHorizontal() {
            this.horizontalAlignInInventoryHud.set(!this.horizontalAlignInInventoryHud.get());
        }
    }
}
