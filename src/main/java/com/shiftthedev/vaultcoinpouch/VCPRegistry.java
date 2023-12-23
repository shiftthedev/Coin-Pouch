package com.shiftthedev.vaultcoinpouch;

import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import com.shiftthedev.vaultcoinpouch.container.CoinPouchScreen;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;

public class VCPRegistry {
    public static final CoinPouchItem COIN_POUCH = new CoinPouchItem("coin_pouch");

    public static MenuType<CoinPouchContainer> COIN_POUCH_CONTAINER;

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
    public static void registerScreen() {
        MenuScreens.register(COIN_POUCH_CONTAINER, CoinPouchScreen::new);
    }
}
