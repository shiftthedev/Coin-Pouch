package com.shiftthedev.vaultcoinpouch.client_helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.SpiritExtractorContainer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpiritExtractorClientHelper
{
    /**
     * Called in mixins/SpiritExtractorScreenMixin
     **/
    public static boolean setDisabled_coinpouch(SpiritExtractorContainer menu, Inventory inventory)
    {
        return !menu.hasSpirit() ||
                !coinsCoverTotalCost_coinpouch(menu.getSlot(36), menu.getTotalCost(), inventory.player) ||
                menu.isSpewingItems();
    }

    /**
     * Called in mixins/SpiritExtractorScreenMixin
     **/
    public static boolean setDisabled_vh(SpiritExtractorContainer menu)
    {
        return !menu.coinsCoverTotalCost() || menu.isSpewingItems();
    }

    /**
     * Called in mixins/SpiritExtractorScreenMixin
     **/
    public static List<Component> getPurchaseButtonTooltipLines(SpiritExtractorContainer menu)
    {
        List<Component> purchaseButtonTooltips = new ArrayList();
        SpiritExtractorTileEntity.RecoveryCost recoveryCost = menu.getRecoveryCost();
        ItemStack totalCost = recoveryCost.getTotalCost();
        if (totalCost.getCount() > 0)
        {
            if (menu.hasSpirit())
            {
                purchaseButtonTooltips.add(new TextComponent("Cost for recovering items"));

                int paymentStackCount = getPouchCoinsCount(menu);

                ChatFormatting textColor = paymentStackCount < totalCost.getCount() ? ChatFormatting.RED : ChatFormatting.YELLOW;
                float var10000 = (float) paymentStackCount / (float) totalCost.getCount();
                String percentString = (int) (var10000 * 100.0F) + "%";
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.total_cost", new Object[]{percentString, totalCost.getItem().getName(totalCost).getString(), totalCost.getCount()})).withStyle(textColor));
            }
            else
            {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.would_be_cost", new Object[]{totalCost.getCount(), totalCost.getItem().getName(totalCost).getString()})).withStyle(ChatFormatting.GREEN));
            }

            purchaseButtonTooltips.add(net.minecraft.network.chat.TextComponent.EMPTY);
            float baseCostCount = recoveryCost.getBaseCount();
            int levels = Math.max(1, menu.getPlayerLevel());
            purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.base_cost", new Object[]{String.format("%.0f", baseCostCount * (float) levels), String.format("%.2f", baseCostCount), levels})).withStyle(ChatFormatting.GRAY));
            recoveryCost.getStackCost().forEach((t) -> {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.item_cost", new Object[]{t.getB(), ((ItemStack) t.getA()).getHoverName()})).withStyle(ChatFormatting.GRAY));
            });
            float multiplier = menu.getMultiplier();
            if (!Mth.equal(multiplier, 1.0F))
            {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.multiplier", new Object[]{String.format("%.2f", multiplier)})).withStyle(ChatFormatting.GRAY));
            }

            float heroDiscount = menu.getHeroDiscount();
            if ((double) heroDiscount >= 0.01)
            {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.hero_discount", new Object[]{String.format("%.0f%%", heroDiscount * 100.0F)})).withStyle(ChatFormatting.GRAY));
            }

            float rescuedBonus = menu.getRescuedBonus();
            if (rescuedBonus > 0.0F)
            {
                purchaseButtonTooltips.add((new TranslatableComponent("screen.the_vault.spirit_extractor.tooltip.rescued_bonus", new Object[]{String.format("%.0f%%", rescuedBonus * 100.0F)})).withStyle(ChatFormatting.GRAY));
            }
        }

        return purchaseButtonTooltips;
    }

    private static int getPouchCoinsCount(SpiritExtractorContainer menu)
    {
        Player player = menu.getPlayer();
        SpiritExtractorTileEntity.RecoveryCost recoveryCost = menu.getRecoveryCost();
        ItemStack totalCost = recoveryCost.getTotalCost();
        int paymentStackCount = menu.getSlot(36).getItem().getCount();

        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
        {
            paymentStackCount += CoinPouchItem.getCoinCount(CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack(), totalCost);
        }

        Iterator it = player.getInventory().items.iterator();
        while (it.hasNext())
        {
            ItemStack plStack = (ItemStack) it.next();
            if (plStack.is(ModBlocks.VAULT_GOLD))
            {
                paymentStackCount += plStack.getCount();
            }
            else if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                paymentStackCount += CoinPouchItem.getCoinCount(plStack, totalCost);
            }
        }

        return paymentStackCount;
    }

    private static boolean coinsCoverTotalCost_coinpouch(Slot slot, ItemStack costStack, Player player)
    {
        int totalCost = costStack.getCount();
        if (totalCost <= 0)
        {
            return true;
        }

        int toRemove = 0;
        if (canMerge(costStack, slot.getItem()))
        {
            if (slot.getItem().getCount() >= totalCost)
            {
                return true;
            }

            toRemove = Math.min(totalCost, slot.getItem().getCount());
            totalCost -= toRemove;
        }

        if (totalCost <= 0)
        {
            return true;
        }

        if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
        {
            ItemStack pouchStack = CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack();
            toRemove = Math.min(totalCost, CoinPouchItem.getCoinCount(pouchStack, costStack));
            totalCost -= toRemove;
        }

        Iterator it = player.getInventory().items.iterator();
        while (it.hasNext())
        {
            if (totalCost <= 0)
            {
                break;
            }

            ItemStack plStack = (ItemStack) it.next();
            if (canMerge(plStack, costStack))
            {
                toRemove = Math.min(totalCost, plStack.getCount());
                totalCost -= toRemove;
            }
            else if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                toRemove = Math.min(totalCost, CoinPouchItem.getCoinCount(plStack, costStack));
                totalCost -= toRemove;
            }
        }

        return totalCost <= 0;
    }

    private static boolean canMerge(ItemStack stack, ItemStack other)
    {
        return stack.getItem() == other.getItem() && ItemStack.tagMatches(stack, other);
    }
}
