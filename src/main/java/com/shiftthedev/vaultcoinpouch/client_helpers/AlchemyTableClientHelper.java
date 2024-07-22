package com.shiftthedev.vaultcoinpouch.client_helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.server_helpers.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.element.AlchemyCraftSelectorElement;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.container.AlchemyTableContainer;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlchemyTableClientHelper
{
    /**
     * Called in mixins/AlchemyTableScreenMixin
     **/
    public static boolean tooltip_coinpouch(AlchemyCraftSelectorElement.CraftingOption selectedOption, Inventory playerInventory, AlchemyTableContainer menu, ITooltipRenderer tooltipRenderer, @NotNull PoseStack poseStack, int mouseX, int mouseY)
    {
        if (selectedOption == null)
        {
            return false;
        }
        else
        {
            ItemStack bottle = menu.getInput();
            if (bottle.isEmpty())
            {
                return false;
            }
            else
            {
                List<ItemStack> inputs = selectedOption.getCraftingCost(bottle);
                List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
                if (missing.isEmpty())
                {
                    List<Component> tooltip = new ArrayList();
                    tooltip.add(bottle.getHoverName());
                    BottleItem.getEffect(bottle).ifPresent(BottleEffect::getTooltip);
                    tooltipRenderer.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, TooltipDirection.RIGHT);
                    return true;
                }
                else
                {
                    Component cmp = (new TranslatableComponent("the_vault.gear_workbench.missing_inputs")).withStyle(ChatFormatting.RED);
                    tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                    return true;
                }
            }
        }
    }

    /**
     * Called in mixins/AlchemyTableScreenMixin
     **/
    public static boolean setDisabled_coinpouch(AlchemyTableContainer menu, AlchemyCraftSelectorElement.CraftingOption selectedOption, Inventory playerInventory)
    {
        ItemStack potion = menu.getInput();
        if (potion.isEmpty())
        {
            return true;
        }
        else if (selectedOption != null)
        {
            List<ItemStack> inputs = selectedOption.getCraftingCost(potion);
            List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
            return !missing.isEmpty();
        }
        else
        {
            return true;
        }
    }

    /**
     * Called in mixins/AlchemyTableScreenMixin
     **/
    public static boolean setDisabled_vh(AlchemyTableContainer menu, AlchemyCraftSelectorElement.CraftingOption selectedOption, Inventory playerInventory)
    {
        ItemStack potion = menu.getInput();
        if (potion.isEmpty())
        {
            return true;
        }
        else if (selectedOption != null)
        {
            List<ItemStack> inputs = selectedOption.getCraftingCost(potion);
            List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, playerInventory);
            return !missing.isEmpty();
        }
        else
        {
            return true;
        }
    }
}
