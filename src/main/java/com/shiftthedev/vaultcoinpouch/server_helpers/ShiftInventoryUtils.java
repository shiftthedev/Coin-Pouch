package com.shiftthedev.vaultcoinpouch.server_helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.*;

public class ShiftInventoryUtils
{
    private static final Set<Item> COINS_TYPE = new HashSet<>();

    /**
     * Called in utils/AlchemyTableHelper
     * Called in utils/ModifierWorkbenchHelper
     **/
    public static boolean consumeInputs(List<ItemStack> recipeInputs, Inventory playerInventory, boolean simulate)
    {
        return consumeInputs(recipeInputs, playerInventory, OverSizedInventory.EMPTY, simulate);
    }

    /**
     * Called in mixins/ForgeRecipeContainerScreenMixin
     **/
    public static boolean consumeInputs(List<ItemStack> recipeInputs, Inventory playerInventory, OverSizedInventory tileInv, boolean simulate)
    {
        return consumeInputs(recipeInputs, playerInventory, tileInv, simulate, new ArrayList());
    }

    /**
     * Called in mixins/ForgeRecipeContainerScreenMixin
     **/
    public static boolean consumeInputs(List<ItemStack> recipeInputs, Inventory playerInventory, OverSizedInventory tileInv, boolean simulate, List<OverSizedItemStack> consumed)
    {
        boolean success = true;

        NonNullList<ItemStack> pouchStacks = NonNullList.create();
        if (CuriosApi.getCuriosHelper().findFirstCurio(playerInventory.player, VCPRegistry.COIN_POUCH).isPresent())
        {
            pouchStacks.add(CuriosApi.getCuriosHelper().findFirstCurio(playerInventory.player, VCPRegistry.COIN_POUCH).get().stack());
        }

        Iterator var6 = recipeInputs.iterator();
        while (var6.hasNext())
        {
            ItemStack input = (ItemStack) var6.next();
            int neededCount = input.getCount();
            NonNullList<OverSizedItemStack> overSizedContents = tileInv.getOverSizedContents();

            for (int slot = 0; slot < overSizedContents.size(); ++slot)
            {
                OverSizedItemStack overSized = (OverSizedItemStack) overSizedContents.get(slot);
                if (neededCount <= 0)
                {
                    break;
                }

                if (isEqualCrafting(input, overSized.stack()))
                {
                    int deductedAmount = Math.min(neededCount, overSized.amount());
                    if (!simulate)
                    {
                        tileInv.setOverSizedStack(slot, overSized.addCopy(-deductedAmount));
                        consumed.add(overSized.copyAmount(deductedAmount));
                    }

                    neededCount -= overSized.amount();
                }
            }

            NonNullList<ItemStack> items = playerInventory.items;
            Iterator var16 = items.iterator();
            while (var16.hasNext())
            {
                ItemStack plStack = (ItemStack) var16.next();
                if (neededCount <= 0)
                {
                    break;
                }

                if (isEqualCrafting(input, plStack))
                {
                    int deductedAmount = Math.min(neededCount, plStack.getCount());
                    if (!simulate)
                    {
                        plStack.shrink(deductedAmount);
                        ItemStack deducted = plStack.copy();
                        deducted.setCount(deductedAmount);
                        consumed.add(OverSizedItemStack.of(deducted));
                    }

                    neededCount -= deductedAmount;
                }

                // Coin Pouch check
                if (COINS_TYPE.contains(input.getItem()) && plStack.is(VCPRegistry.COIN_POUCH))
                {
                    pouchStacks.add(plStack);
                }
                // End of Coin Pouch check
            }

            // Coin Pouch remove
            if (COINS_TYPE.contains(input.getItem()))
            {
                var16 = pouchStacks.iterator();
                while (var16.hasNext())
                {
                    ItemStack pouchStack = (ItemStack) var16.next();
                    int deductedAmount = Math.min(neededCount, CoinPouchItem.getCoinCount(pouchStack, input));
                    if (!simulate)
                    {
                        CoinPouchItem.extractCoins(pouchStack, input, deductedAmount);
                        ItemStack deducted = input.copy();
                        deducted.setCount(deductedAmount);
                        consumed.add(OverSizedItemStack.of(deducted));
                    }

                    neededCount -= deductedAmount;
                }
            }
            // End of Coin Pouch remove

            if (neededCount > 0)
            {
                success = false;
            }
        }

        return success;
    }

    /**
     * Called in mixins/AlchemyTableScreenMixin
     * Called in mixins/AlchemyWorkbenchListElementMixin
     **/
    public static List<ItemStack> getMissingInputs(List<ItemStack> recipeInputs, Inventory playerInventory)
    {
        return getMissingInputs(recipeInputs, playerInventory, OverSizedInventory.EMPTY);
    }

    /**
     * Called in mixins/ForgeRecipeContainerScreenMixin
     **/
    public static List<ItemStack> getMissingInputs(List<ItemStack> recipeInputs, Inventory playerInventory, OverSizedInventory containerInventory)
    {
        List<ItemStack> missing = new ArrayList();
        ItemStack curiosPouchStack = ItemStack.EMPTY;
        if (CuriosApi.getCuriosHelper().findFirstCurio(playerInventory.player, VCPRegistry.COIN_POUCH).isPresent())
        {
            curiosPouchStack = CuriosApi.getCuriosHelper().findFirstCurio(playerInventory.player, VCPRegistry.COIN_POUCH).get().stack();
        }

        Iterator var4 = recipeInputs.iterator();
        while (var4.hasNext())
        {
            ItemStack input = (ItemStack) var4.next();
            int neededCount = input.getCount();
            Iterator var7 = containerInventory.getOverSizedContents().iterator();

            while (var7.hasNext())
            {
                OverSizedItemStack overSized = (OverSizedItemStack) var7.next();
                if (isEqualCrafting(input, overSized.stack()))
                {
                    neededCount -= overSized.amount();
                }
            }

            if (COINS_TYPE.contains(input.getItem()) && !curiosPouchStack.isEmpty())
            {
                neededCount -= CoinPouchItem.getCoinCount(curiosPouchStack, input);
            }

            var7 = playerInventory.items.iterator();
            while (var7.hasNext())
            {
                ItemStack plStack = (ItemStack) var7.next();

                if (isEqualCrafting(input, plStack))
                {
                    neededCount -= plStack.getCount();
                }

                // Coin Pouch check
                if (COINS_TYPE.contains(input.getItem()) && plStack.is(VCPRegistry.COIN_POUCH))
                {
                    neededCount -= CoinPouchItem.getCoinCount(plStack, input);
                }
                // End of Coin Pouch check

                if (neededCount <= 0)
                {
                    break;
                }
            }

            if (neededCount > 0)
            {
                missing.add(input);
            }
        }

        return missing;
    }

    private static boolean isEqualCrafting(ItemStack thisStack, ItemStack thatStack)
    {
        return thisStack.getItem() == thatStack.getItem() && thisStack.getDamageValue() == thatStack.getDamageValue() && (thisStack.getTag() == null || thisStack.areShareTagsEqual(thatStack));
    }

    static
    {
        Collections.addAll(COINS_TYPE, ModBlocks.BRONZE_COIN_PILE.asItem(), ModBlocks.SILVER_COIN_PILE.asItem(), ModBlocks.GOLD_COIN_PILE.asItem(), ModBlocks.PLATINUM_COIN_PILE.asItem());
    }
}
