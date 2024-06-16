package com.shiftthedev.vaultcoinpouch;

import com.shiftthedev.vaultcoinpouch.config.VCPConfigScreen;
import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import com.shiftthedev.vaultcoinpouch.container.CoinPouchScreen;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.function.BiFunction;

public class VCPRegistry {
    public static final CoinPouchItem COIN_POUCH = new CoinPouchItem("coin_pouch");

    public static MenuType<CoinPouchContainer> COIN_POUCH_CONTAINER;

    public static VCPConfigScreen CONFIG_SCREEN = new VCPConfigScreen();

    public VCPRegistry() {
    }

    public static void register(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(COIN_POUCH);
    }

    public static void registerMenu(RegistryEvent.Register<MenuType<?>> event) {
        COIN_POUCH_CONTAINER = IForgeMenuType.create((windowId, inv, data) -> {
            int pouchSlot = data.readInt();
            return new CoinPouchContainer(windowId, inv, pouchSlot);
        });

        event.getRegistry().registerAll(new MenuType[]{(MenuType) COIN_POUCH_CONTAINER.setRegistryName("coin_pouch_container")});
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerScreen() 
    {
        MenuScreens.register(COIN_POUCH_CONTAINER, CoinPouchScreen::new);
    }
    
    @OnlyIn(Dist.CLIENT)
    public static void registerConfigScreen()
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
}
