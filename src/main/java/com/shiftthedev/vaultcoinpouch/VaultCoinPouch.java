package com.shiftthedev.vaultcoinpouch;

import com.mojang.logging.LogUtils;
import com.shiftthedev.vaultcoinpouch.config.ReloadConfigCommand;
import com.shiftthedev.vaultcoinpouch.config.ShowConfigCommand;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod(VaultCoinPouch.MOD_ID)
public class VaultCoinPouch
{
    public static final String MOD_ID = "vaultcoinpouch";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation EMPTY_COIN_POUCH_SLOT = new ResourceLocation(CuriosApi.MODID, "slot/empty_coin_pouch_slot");

    public VaultCoinPouch()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imc);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::registerCommand);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::registerClientCommand));
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        VCPConfig.initConfig();
    }

    private void imc(final InterModEnqueueEvent event)
    {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("coin_pouch")
                        .size(1)
                        .priority(780)
                        .icon(EMPTY_COIN_POUCH_SLOT)
                        .build());
    }

    private void registerCommand(RegisterCommandsEvent event)
    {
        ReloadConfigCommand.register(event.getDispatcher());
    }

    @OnlyIn(Dist.CLIENT)
    private void registerClientCommand(RegisterCommandsEvent event)
    {
        ShowConfigCommand.register(event.getDispatcher());
    }
}