package com.shiftthedev.vaultcoinpouch.events;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.config.VCPConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.BiFunction;

@EventBusSubscriber(
        bus = EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ClientRegistryEvents
{
    @SubscribeEvent(
            priority = EventPriority.LOW
    )
    public static void setupClient(FMLClientSetupEvent event)
    {
        VCPRegistry.registerScreen();
        VCPRegistry.registerConfigScreen();
    }
}
