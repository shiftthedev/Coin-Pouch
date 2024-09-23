package com.shiftthedev.vaultcoinpouch.events;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.client.KeyBindings;
import com.shiftthedev.vaultcoinpouch.config.VCPConfigScreen;
import com.shiftthedev.vaultcoinpouch.container.CoinPouchScreen;
import com.shiftthedev.vaultcoinpouch.network.KeyPressMessage;
import com.shiftthedev.vaultcoinpouch.network.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.function.BiFunction;

import static com.shiftthedev.vaultcoinpouch.VCPRegistry.COIN_POUCH_CONTAINER;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
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

        KeyBindings.init();
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


    @EventBusSubscriber(modid = VaultCoinPouch.MOD_ID, value = {Dist.CLIENT})
    static class ClientForgeEvents
    {
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void onKeyInput(InputEvent.KeyInputEvent event)
        {
            if (KeyBindings.OPEN_POUCH.consumeClick())
            {
                Minecraft mc = Minecraft.getInstance();
                Player player = mc.player;
                if (player == null)
                {
                    return;
                }

                int slot = getPouchSlot(player.getInventory());
                if (slot == -1 && !CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
                {
                    return;
                }

                NetworkManager.CHANNEL.sendToServer(new KeyPressMessage(slot));
            }
        }

        private static int getPouchSlot(Inventory player)
        {
            for (int i = 0; i < player.items.size(); ++i)
            {
                ItemStack stack = player.items.get(i);
                if (!stack.isEmpty() && stack.is(VCPRegistry.COIN_POUCH))
                {
                    return i;
                }
            }

            return -1;
        }
    }
}
