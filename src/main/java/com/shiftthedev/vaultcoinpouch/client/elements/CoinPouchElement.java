package com.shiftthedev.vaultcoinpouch.client.elements;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class CoinPouchElement<E extends CoinPouchElement<E>> extends ElasticContainerElement<E>
{
    private final Supplier<ISpatial> backgroundSpatialOffset;
    private final int visibleSlotCount;

    public CoinPouchElement(ISpatial position, Supplier<ISpatial> backgroundSpatialOffset, Player player)
    {
        super(position);
        this.backgroundSpatialOffset = backgroundSpatialOffset;
        this.visibleSlotCount = 4;

        this.addElement((NineSliceElement) (new NineSliceElement(Spatials.width(28), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout(this::layoutBackground));
        StacksElement element = this.addElement((StacksElement) (new StacksElement(Spatials.zero(), player, getPouchSlot(player.getInventory()))).layout(this::layoutStacks));
        this.setVisible(element.isVisible());
    }

    private void layoutBackground(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world)
    {
        world.translateX(gui);
        world.translateY(backgroundSpatialOffset.get().bottom());
        world.height(this.visibleSlotCount * 18 + 10);
    }

    private void layoutStacks(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world)
    {
        world.translateX(gui);
        world.translateY(backgroundSpatialOffset.get().bottom());
    }
    
    private int getPouchSlot(Inventory player)
    {
        for(int i = 0; i < player.items.size(); ++i)
        {
            ItemStack stack = player.items.get(i);
            if(!stack.isEmpty() && stack.is(VCPRegistry.COIN_POUCH))
            {
                return i;
            }
        }
        
        return -1;
    }
}
