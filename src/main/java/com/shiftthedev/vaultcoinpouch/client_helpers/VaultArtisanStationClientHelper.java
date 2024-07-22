package com.shiftthedev.vaultcoinpouch.client_helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.gear.modification.GearModificationCost;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.*;

public class VaultArtisanStationClientHelper
{
    /**
     * Called in mixins/ModificationButtonElementMixin
     **/
    public static List<Component> tooltip(VaultArtisanStationContainer container, GearModification modification, Random rand)
    {
        GearModificationAction action = container.getModificationAction(modification);
        if (action == null)
        {
            return Collections.emptyList();
        }
        else
        {
            ItemStack inputItem = ItemStack.EMPTY;
            Slot inputSlot = action.getCorrespondingSlot(container);
            if (inputSlot != null && !inputSlot.getItem().isEmpty())
            {
                inputItem = inputSlot.getItem();
            }

            ItemStack gearStack = container.getGearInputSlot().getItem();
            AttributeGearData itemData = AttributeGearData.read(gearStack);
            int potential = (Integer) itemData.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL).orElse(Integer.MIN_VALUE);
            boolean hasInput = !gearStack.isEmpty() && potential != Integer.MIN_VALUE;
            boolean failedModification = false;
            List<Component> tooltip = new ArrayList(modification.getDescription(inputItem));
            if (hasInput && !itemData.isModifiable())
            {
                return List.of((new TranslatableComponent("the_vault.gear_modification.unmodifiable")).withStyle(ChatFormatting.RED));
            }
            else
            {
                if (hasInput && !inputItem.isEmpty() && !action.modification().canApply(gearStack, inputItem, container.getPlayer(), rand))
                {
                    tooltip.add(action.modification().getInvalidDescription(inputItem));
                    failedModification = true;
                }

                if (!failedModification && hasInput)
                {
                    MutableComponent focusCmp;
                    if (!inputItem.isEmpty())
                    {
                        focusCmp = (new TextComponent("- ")).append(modification.getDisplayStack().getHoverName()).append(" x1").append(" [%s]".formatted(inputItem.getCount()));
                    }
                    else
                    {
                        focusCmp = (new TextComponent("Requires ")).append(modification.getDisplayStack().getHoverName());
                    }

                    focusCmp.withStyle(inputItem.isEmpty() ? ChatFormatting.RED : ChatFormatting.GREEN);
                    tooltip.add(focusCmp);
                }

                if (hasInput)
                {
                    if (!failedModification && !inputItem.isEmpty())
                    {
                        VaultGearData data = VaultGearData.read(gearStack);
                        String rollType = (String) data.get(ModGearAttributes.GEAR_ROLL_TYPE, VaultGearAttributeTypeMerger.firstNonNull());
                        GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), rollType, data.getItemLevel(), potential, modification);
                        ItemStack plating = container.getPlatingSlot().getItem();
                        ItemStack bronze = container.getBronzeSlot().getItem();
                        MutableComponent var10001 = (new TextComponent("- ")).append((new ItemStack(ModItems.VAULT_PLATING)).getHoverName());
                        int var10002 = cost.costPlating();
                        tooltip.add(var10001.append(" x" + var10002).append(" [%s]".formatted(plating.getCount())).withStyle(cost.costPlating() > plating.getCount() ? ChatFormatting.RED : ChatFormatting.GREEN));
                        var10001 = (new TextComponent("- ")).append((new ItemStack(ModBlocks.VAULT_BRONZE)).getHoverName());
                        var10002 = cost.costBronze();

                        // Coin Pouch check
                        int bronzeAmount = bronze.getCount();

                        if (CuriosApi.getCuriosHelper().findFirstCurio(container.getPlayer(), VCPRegistry.COIN_POUCH).isPresent())
                        {
                            bronzeAmount += CoinPouchItem.getCoinCount(CuriosApi.getCuriosHelper().findFirstCurio(container.getPlayer(), VCPRegistry.COIN_POUCH).get().stack());
                        }

                        Iterator it = container.getPlayer().getInventory().items.iterator();
                        while (it.hasNext())
                        {
                            ItemStack plStack = (ItemStack) it.next();
                            if (plStack.is(VCPRegistry.COIN_POUCH))
                            {
                                bronzeAmount += CoinPouchItem.getCoinCount(plStack);
                            }
                            else if (plStack.is(ModBlocks.VAULT_BRONZE))
                            {
                                bronzeAmount += plStack.getCount();
                            }
                        }

                        tooltip.add(var10001.append(" x" + var10002).append(" [%s]".formatted(bronzeAmount)).withStyle(cost.costBronze() > bronzeAmount ? ChatFormatting.RED : ChatFormatting.GREEN));
                        // End of Coin Pouch check
                    }

                    tooltip.add(TextComponent.EMPTY);
                    tooltip.add(gearStack.getHoverName());
                    Item patt5642$temp = gearStack.getItem();
                    if (patt5642$temp instanceof VaultGearTooltipItem)
                    {
                        VaultGearTooltipItem gearTooltipItem = (VaultGearTooltipItem) patt5642$temp;
                        tooltip.addAll(gearTooltipItem.createTooltip(gearStack, GearTooltip.craftingView()));
                    }
                }

                return tooltip;
            }
        }
    }
}
