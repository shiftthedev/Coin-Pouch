package com.shiftthedev.vaultcoinpouch.helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import com.shiftthedev.vaultcoinpouch.utils.ShiftCoinDefinition;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;

public class ShopPedestalHelper
{
    private static Map<Item, ShiftCoinDefinition> COIN_DEFINITIONS;

    public static boolean hasEnoughCurrency(List<InventoryUtil.ItemAccess> allItems, ItemStack currency)
    {
        return (Boolean) getCoinDefinition(currency.getItem()).map((priceCoinDefinition) -> hasEnoughCoin(priceCoinDefinition, currency, allItems)).orElse(false);
    }

    private static boolean hasEnoughCoin(ShiftCoinDefinition priceCoinDefinition, ItemStack currency, List<InventoryUtil.ItemAccess> allItems)
    {
        int priceValue = priceCoinDefinition.coinValue() * currency.getCount();
        Iterator var4 = allItems.iterator();

        do
        {
            if (!var4.hasNext())
            {
                return false;
            }

            InventoryUtil.ItemAccess itemAccess = (InventoryUtil.ItemAccess) var4.next();
            if (itemAccess.getStack().is(VCPRegistry.COIN_POUCH))
            {
                priceValue -= CoinPouchItem.getCoinCount(itemAccess.getStack());
            }
            else
            {
                priceValue -= (Integer) getCoinDefinition(itemAccess.getStack().getItem()).map((coinDefinition) -> {
                    return coinDefinition.coinValue() * itemAccess.getStack().getCount();
                }).orElse(0);
            }
        }
        while (priceValue > 0);

        return true;
    }

    public static boolean extractCurrency(Player player, List<InventoryUtil.ItemAccess> allItems, ItemStack price)
    {
        getCoinDefinition(price.getItem()).ifPresent((priceCoinDefinition) -> extractCoin(priceCoinDefinition, price, allItems, player));
        return true;
    }

    private static void extractCoin(ShiftCoinDefinition priceCoinDefinition, ItemStack price, List<InventoryUtil.ItemAccess> allItems, Player player)
    {
        int priceValue = priceCoinDefinition.coinValue() * price.getCount();
        priceValue = deductCoins(allItems, priceValue, priceCoinDefinition);
        if (priceValue > 0)
        {
            priceValue = payUsingLowerDenominations(allItems, priceValue, priceCoinDefinition);
            priceValue = payUsingHigherDenominations(allItems, priceValue, priceCoinDefinition);
        }

        if (priceValue < 0)
        {
            int change = -priceValue;
            returnChangeToPlayer(player, change);
        }
    }

    private static void returnChangeToPlayer(Player player, int change)
    {
        label21:
        while (true)
        {
            if (change > 0)
            {
                Iterator var2 = COIN_DEFINITIONS.values().iterator();

                while (true)
                {
                    if (!var2.hasNext())
                    {
                        continue label21;
                    }

                    ShiftCoinDefinition definition = (ShiftCoinDefinition) var2.next();
                    if (definition.coinValue() <= change && change / definition.coinValue() < 9)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(definition.coinItem(), change / definition.coinValue()));
                        change -= definition.coinValue() * (change / definition.coinValue());
                    }
                }
            }

            return;
        }
    }

    private static int payUsingHigherDenominations(List<InventoryUtil.ItemAccess> allItems, int priceValue, ShiftCoinDefinition coinDefinition)
    {
        while (priceValue > 0 && coinDefinition.previousHigherDenomination() != null)
        {
            Optional<ShiftCoinDefinition> higherCoinDefinition = getCoinDefinition(coinDefinition.previousHigherDenomination());
            if (higherCoinDefinition.isPresent())
            {
                coinDefinition = (ShiftCoinDefinition) higherCoinDefinition.get();
                priceValue = deductCoins(allItems, priceValue, coinDefinition);
            }
        }

        return priceValue;
    }

    private static int payUsingLowerDenominations(List<InventoryUtil.ItemAccess> allItems, int priceValue, ShiftCoinDefinition coinDefinition)
    {
        while (priceValue > 0 && coinDefinition.nextLowerDenomination() != null)
        {
            Optional<ShiftCoinDefinition> lowerCoinDefinition = getCoinDefinition(coinDefinition.nextLowerDenomination());
            if (lowerCoinDefinition.isPresent())
            {
                coinDefinition = (ShiftCoinDefinition) lowerCoinDefinition.get();
                priceValue = deductCoins(allItems, priceValue, coinDefinition);
            }
        }

        return priceValue;
    }

    private static int deductCoins(List<InventoryUtil.ItemAccess> allItems, int priceValue, ShiftCoinDefinition coinDefinition)
    {
        Iterator var4 = allItems.iterator();
        NonNullList<InventoryUtil.ItemAccess> pouchStacks = NonNullList.create();

        while (var4.hasNext())
        {
            InventoryUtil.ItemAccess itemAccess = (InventoryUtil.ItemAccess) var4.next();
            ItemStack stack = itemAccess.getStack();
            if (stack.getItem() == coinDefinition.coinItem())
            {
                int countToRemove = (int) Math.ceil((double) Math.min(priceValue, stack.getCount() * coinDefinition.coinValue()) / (double) coinDefinition.coinValue());
                if (countToRemove > 0)
                {
                    itemAccess.setStack(ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - countToRemove));
                    priceValue -= countToRemove * coinDefinition.coinValue();
                    if (priceValue <= 0)
                    {
                        return priceValue;
                    }
                }
            }

            if (stack.is(VCPRegistry.COIN_POUCH))
            {
                pouchStacks.add(itemAccess);
            }
        }

        var4 = pouchStacks.iterator();
        while (var4.hasNext())
        {
            InventoryUtil.ItemAccess pouch = (InventoryUtil.ItemAccess) var4.next();
            ItemStack pouchStack = pouch.getStack();
            priceValue -= CoinPouchItem.extractCoins(pouchStack, priceValue);
            pouch.setStack(pouchStack.copy());
            if (priceValue <= 0)
            {
                break;
            }
        }

        return priceValue;
    }

    private static Optional<ShiftCoinDefinition> getCoinDefinition(Item coin)
    {
        if (COIN_DEFINITIONS == null)
        {
            COIN_DEFINITIONS = new LinkedHashMap();
            COIN_DEFINITIONS.put(ModBlocks.VAULT_BRONZE, new ShiftCoinDefinition(ModBlocks.VAULT_BRONZE, ModBlocks.VAULT_SILVER, (Item) null, 1));
            COIN_DEFINITIONS.put(ModBlocks.VAULT_SILVER, new ShiftCoinDefinition(ModBlocks.VAULT_SILVER, ModBlocks.VAULT_GOLD, ModBlocks.VAULT_BRONZE, 9));
            COIN_DEFINITIONS.put(ModBlocks.VAULT_GOLD, new ShiftCoinDefinition(ModBlocks.VAULT_GOLD, ModBlocks.VAULT_PLATINUM, ModBlocks.VAULT_SILVER, 81));
            COIN_DEFINITIONS.put(ModBlocks.VAULT_PLATINUM, new ShiftCoinDefinition(ModBlocks.VAULT_PLATINUM, (Item) null, ModBlocks.VAULT_GOLD, 729));
        }

        return Optional.ofNullable((ShiftCoinDefinition) COIN_DEFINITIONS.get(coin));
    }
}
