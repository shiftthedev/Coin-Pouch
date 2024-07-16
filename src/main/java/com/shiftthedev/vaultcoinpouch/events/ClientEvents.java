package com.shiftthedev.vaultcoinpouch.events;

import com.shiftthedev.vaultcoinpouch.config.VCPConfigScreen;
import com.shiftthedev.vaultcoinpouch.container.CoinPouchScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.BiFunction;

import static com.shiftthedev.vaultcoinpouch.VCPRegistry.COIN_POUCH_CONTAINER;

@EventBusSubscriber(
        bus = EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ClientEvents
{
    @OnlyIn(Dist.CLIENT)
    public static VCPConfigScreen CONFIG_SCREEN = new VCPConfigScreen();

    @SubscribeEvent(
            priority = EventPriority.LOW
    )
    public static void setupClient(FMLClientSetupEvent event)
    {
        registerScreen();
        registerConfigScreen();
    }


    @OnlyIn(Dist.CLIENT)
    private static void registerConfigScreen()
    {
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory(new BiFunction<Minecraft, Screen, Screen>()
                {
                    @Override
                    public Screen apply(Minecraft minecraft, Screen screen)
                    {
                        CONFIG_SCREEN.setup(minecraft, screen);
                        return CONFIG_SCREEN;
                    }
                }));
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreen()
    {
        MenuScreens.register(COIN_POUCH_CONTAINER, CoinPouchScreen::new);
    }
}
