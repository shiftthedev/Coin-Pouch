package com.shiftthedev.vaultcoinpouch.events;

import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class AnvilEvents {

    @SubscribeEvent
    public static void applySoulbound(AnvilUpdateEvent event) {
        if (VaultCoinPouch.CONFIG.soulboundEnabled()) {
            if (event.getLeft().getItem() instanceof CoinPouchItem) {
                if (event.getRight().getItem() == ModItems.ECHO_POG) {
                    ItemStack result = event.getLeft().copy();
                    AttributeGearData data = AttributeGearData.empty();
                    data.updateAttribute(ModGearAttributes.SOULBOUND, true);
                    data.write(result);

                    event.setOutput(result);
                    event.setCost(10);
                    event.setMaterialCost(1);
                }
            }
        }
    }
}
