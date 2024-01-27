package com.shiftthedev.vaultcoinpouch.events;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import iskallia.vault.event.InputEvents;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.MOD_ID;

@EventBusSubscriber(
        bus = EventBusSubscriber.Bus.MOD,
        value = {Dist.CLIENT}
)
public class ClientRegistryEvents {
    public static KeyMapping OPEN_POUCH;

    @SubscribeEvent(
            priority = EventPriority.LOW
    )
    public static void setupClient(FMLClientSetupEvent event) {
        VCPRegistry.registerScreen();

        OPEN_POUCH = new KeyMapping("key." + MOD_ID + ".open", GLFW.GLFW_KEY_B, "key.categories." + MOD_ID);
        ClientRegistry.registerKeyBinding(OPEN_POUCH);

        MinecraftForge.EVENT_BUS.register(InputEvents.class);
    }
}
