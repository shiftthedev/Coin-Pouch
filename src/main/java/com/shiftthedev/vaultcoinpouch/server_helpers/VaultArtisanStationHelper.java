package com.shiftthedev.vaultcoinpouch.server_helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationCost;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

public class VaultArtisanStationHelper
{
    /**
     * Called in mixins/GearModificationActionMixin
     **/
    public static void apply(VaultArtisanStationContainer container, ServerPlayer player, Slot correspondingSlot, GearModification modification, Random rand)
    {
        if (canApply(container, player, correspondingSlot, modification, rand))
        {
            ItemStack gear = container.getGearInputSlot().getItem();
            VaultGearData data = VaultGearData.read(gear);
            Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);
            if (!potential.isEmpty())
            {
                Slot inSlot = correspondingSlot;
                if (inSlot != null)
                {
                    ItemStack input = inSlot.getItem();
                    ItemStack material = input.copy();
                    input.shrink(1);
                    inSlot.set(input);
                    String rollType = (String) data.get(ModGearAttributes.GEAR_ROLL_TYPE, VaultGearAttributeTypeMerger.firstNonNull());
                    GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), rollType, data.getItemLevel(), (Integer) potential.get(), modification);
                    ItemStack plating = container.getPlatingSlot().getItem();
                    plating.shrink(cost.costPlating());
                    container.getPlatingSlot().set(plating);

                    ItemStack bronze = container.getBronzeSlot().getItem();
                    int bronzeRemaining = cost.costBronze();
                    int bronzeToTake = Math.min(bronzeRemaining, bronze.getCount());
                    bronze.shrink(bronzeToTake);
                    container.getBronzeSlot().set(bronze);

                    // Coin Pouch remove
                    bronzeRemaining -= bronzeToTake;

                    if (bronzeRemaining > 0)
                    {
                        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
                        {
                            ItemStack pouchStack = CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack();
                            bronzeToTake = Math.min(bronzeRemaining, CoinPouchItem.getCoinCount(pouchStack));
                            CoinPouchItem.extractCoins(pouchStack, bronzeToTake);
                            bronzeRemaining -= bronzeToTake;
                        }
                    }

                    NonNullList<ItemStack> pouchStacks = NonNullList.create();
                    if (bronzeRemaining > 0)
                    {
                        Iterator it = player.getInventory().items.iterator();
                        while (it.hasNext())
                        {
                            if (bronzeRemaining <= 0)
                            {
                                break;
                            }

                            ItemStack plStack = (ItemStack) it.next();
                            if (plStack.is(ModBlocks.VAULT_BRONZE))
                            {
                                bronzeToTake = Math.min(bronzeRemaining, plStack.getCount());
                                plStack.shrink(bronzeToTake);
                                bronzeRemaining -= bronzeToTake;
                            }

                            if (plStack.is(VCPRegistry.COIN_POUCH))
                            {
                                pouchStacks.add(plStack);
                            }
                        }
                    }

                    if (bronzeRemaining > 0 && !pouchStacks.isEmpty())
                    {
                        Iterator it = pouchStacks.iterator();
                        while (it.hasNext())
                        {
                            if (bronzeRemaining <= 0)
                            {
                                break;
                            }

                            ItemStack pouchStack = (ItemStack) it.next();
                            bronzeToTake = Math.min(bronzeRemaining, CoinPouchItem.getCoinCount(pouchStack));
                            CoinPouchItem.extractCoins(pouchStack, bronzeToTake);
                            bronzeRemaining -= bronzeToTake;
                        }
                    }
                    // End of Coin Pouch remove

                    modification.apply(gear, material, player, rand);
                }
            }
        }
    }

    /**
     * Called in mixins/GearModificationActionMixin
     **/
    public static boolean canApply(VaultArtisanStationContainer container, Player player, Slot correspondingSlot, GearModification modification, Random rand)
    {
        Slot inSlot = correspondingSlot;
        if (inSlot == null)
        {
            return false;
        }
        else
        {
            ItemStack gear = container.getGearInputSlot().getItem();
            ItemStack in = inSlot.getItem();
            if (!in.isEmpty() && !gear.isEmpty() && modification.getStackFilter().test(in))
            {
                VaultGearData data = VaultGearData.read(gear);
                Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);
                if (potential.isEmpty())
                {
                    return false;
                }
                else
                {
                    String rollType = (String) data.get(ModGearAttributes.GEAR_ROLL_TYPE, VaultGearAttributeTypeMerger.firstNonNull());
                    GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), rollType, data.getItemLevel(), (Integer) potential.get(), modification);

                    // Coin Pouch check
                    ItemStack plating = container.getPlatingSlot().getItem();
                    if (plating.getCount() < cost.costPlating())
                    {
                        return false;
                    }

                    int bronzeMissing = cost.costBronze();
                    ItemStack bronze = container.getBronzeSlot().getItem();
                    bronzeMissing -= bronze.getCount();
                    if (bronzeMissing <= 0)
                    {
                        return modification.canApply(gear, in, player, rand).success();
                    }

                    if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
                    {
                        bronzeMissing -= CoinPouchItem.getCoinCount(CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack());
                    }

                    if (bronzeMissing <= 0)
                    {
                        return modification.canApply(gear, in, player, rand).success();
                    }

                    Iterator it = player.getInventory().items.iterator();
                    while (it.hasNext())
                    {
                        ItemStack plStack = (ItemStack) it.next();
                        if (plStack.is(VCPRegistry.COIN_POUCH))
                        {
                            bronzeMissing -= CoinPouchItem.getCoinCount(plStack);
                        }
                        else if (plStack.is(ModBlocks.VAULT_BRONZE))
                        {
                            bronzeMissing -= plStack.getCount();
                        }

                        if (bronzeMissing <= 0)
                        {
                            break;
                        }
                    }

                    return bronzeMissing <= 0 && modification.canApply(gear, in, player, rand).success();
                    // End of Coin Pouch check
                }
            }
            else
            {
                return false;
            }
        }
    }
}
