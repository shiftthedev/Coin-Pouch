package com.shiftthedev.vaultcoinpouch;

import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;

public class VCPRegistry
{
    public static final CoinPouchItem COIN_POUCH = new CoinPouchItem("coin_pouch");

    public static MenuType<CoinPouchContainer> COIN_POUCH_CONTAINER;

    public VCPRegistry()
    {
    }

    public static void register(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(COIN_POUCH);
    }

    public static void registerMenu(RegistryEvent.Register<MenuType<?>> event)
    {
        COIN_POUCH_CONTAINER = IForgeMenuType.create((windowId, inv, data) -> createPouch(windowId, inv, data));

        event.getRegistry().registerAll(new MenuType[]{(MenuType) COIN_POUCH_CONTAINER.setRegistryName("coin_pouch_container")});
    }

    private static CoinPouchContainer createPouch(int windowId, Inventory inv, FriendlyByteBuf data)
    {
        int pouchSlot = data.readInt();
        return new CoinPouchContainer(windowId, inv, pouchSlot);
    }
}
