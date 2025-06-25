package com.shiftthedev.vaultcoinpouch.events;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemShardPouch;
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
        if (!VCPConfig.shardPouchSoulboundEnabled()) {
            return;
        }

        ItemStack jewel = event.getRight();
        if (jewel.getItem() != ModItems.JEWEL || !GearDataCache.of(jewel).hasAttribute(ModGearAttributes.SOULBOUND)) {
            return;
        }

        ItemStack pouch = event.getLeft();
        boolean shard = VCPConfig.shardPouchSoulboundEnabled() && pouch.getItem() instanceof ItemShardPouch;
        if (shard) {
            ItemStack result = event.getLeft().copy();
            AttributeGearData data = AttributeGearData.empty();
            data.createOrReplaceAttributeValue(ModGearAttributes.SOULBOUND, true);
            data.write(result);

            event.setOutput(result);
            event.setCost(10);
            event.setMaterialCost(1);
        }
    }
}
