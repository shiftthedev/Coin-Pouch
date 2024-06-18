package com.shiftthedev.vaultcoinpouch;

import com.mojang.logging.LogUtils;
import com.shiftthedev.vaultcoinpouch.config.ReloadConfigCommand;
import com.shiftthedev.vaultcoinpouch.config.ShowConfigCommand;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(VaultCoinPouch.MOD_ID)
public class VaultCoinPouch
{
    public static final String MOD_ID = "vaultcoinpouch";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VaultCoinPouch()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::registerCommand);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::registerClientCommand));
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        VCPConfig.initConfig();
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
