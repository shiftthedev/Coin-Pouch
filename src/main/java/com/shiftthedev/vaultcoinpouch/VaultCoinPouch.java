package com.shiftthedev.vaultcoinpouch;

import com.electronwill.nightconfig.core.file.FormatDetector;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.logging.LogUtils;
import com.shiftthedev.vaultcoinpouch.config.ReloadConfigCommand;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.config.VCPData;
import com.shiftthedev.vaultcoinpouch.network.NetworkManager;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.List;

@Mod(VaultCoinPouch.MOD_ID)
public class VaultCoinPouch {
    public static final String MOD_ID = "vaultcoinpouch";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation EMPTY_COIN_POUCH_SLOT = new ResourceLocation(CuriosApi.MODID, "slot/empty_coin_pouch_slot");

    public VaultCoinPouch() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imc);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::registerCommand);

        FormatDetector.registerExtension("shift", TomlFormat::instance);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, VCPConfig.COMMON_SPEC, "shift_mods/" + MOD_ID + "/coinpouch-common.shift");

        VCPData.loadData();
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkManager.initializeNetwork();
    }

    private void imc(final InterModEnqueueEvent event) {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("coin_pouch")
                        .size(1)
                        .priority(780)
                        .icon(EMPTY_COIN_POUCH_SLOT)
                        .build());
    }

    private void registerCommand(RegisterCommandsEvent event) {
        ReloadConfigCommand.registerCommand(event.getDispatcher());
    }

    public static void addSoulboundTooltip(ItemStack stack, List<Component> tooltip) {
        tooltip.add(new TextComponent(" "));
        if (AttributeGearData.read(stack).hasAttribute(ModGearAttributes.SOULBOUND)) {
            tooltip.add(new TextComponent(ModGearAttributes.SOULBOUND.getReader().getModifierName()).withStyle(ModGearAttributes.SOULBOUND.getReader().getColoredTextStyle()));
        } else {
            tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".soulbound").withStyle(ChatFormatting.GRAY));
        }
    }

    public static String formatCount(int count) {
        if (count > 1000000000) {
            return Math.floorDiv(count, 1000000000) + "B";
        } else if (count > 1000000) {
            return Math.floorDiv(count, 1000000) + "M";
        } else if (count > 1000) {
            return Math.floorDiv(count, 1000) + "K";
        }
        return String.valueOf(count);
    }
}