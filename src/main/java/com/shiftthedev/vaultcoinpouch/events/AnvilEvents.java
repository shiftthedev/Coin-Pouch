package com.shiftthedev.vaultcoinpouch.events;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
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
public class AnvilEvents
{

    @SubscribeEvent
    public static void applySoulbound(AnvilUpdateEvent event)
    {
        if (VCPConfig.GENERAL.soulboundEnabled())
        {
            if (event.getLeft().getItem() instanceof CoinPouchItem)
            {
                ItemStack right = event.getRight();
                if (right.getItem() == ModItems.JEWEL)
                {
                    if(AttributeGearData.hasData(right) && AttributeGearData.read(right).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue()))
                    {
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
        }
        else if (VCPConfig.GENERAL.soulboundEnabled())
        {
            if (event.getLeft().getItem() instanceof CoinPouchItem)
            {
                ItemStack right = event.getRight();
                if (right.getItem() == ModItems.JEWEL)
                {
                    if(AttributeGearData.hasData(right) && AttributeGearData.read(right).get(ModGearAttributes.SOULBOUND, VaultGearAttributeTypeMerger.anyTrue()))
                    {
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
        }
    }
}
