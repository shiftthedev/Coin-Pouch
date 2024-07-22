package com.shiftthedev.vaultcoinpouch.client_helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.shiftthedev.vaultcoinpouch.server_helpers.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.util.InventoryUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ModifierWorkbenchClientHelper
{
    /**
     * Called in mixins/ModifierWorkbenchScreenMixin
     **/
    public static boolean tooltip(ModifierWorkbenchContainer menu, iskallia.vault.gear.crafting.ModifierWorkbenchHelper.CraftingOption selectedOption, Inventory playerInventory, ITooltipRenderer tooltipRenderer, @NotNull PoseStack poseStack, int mouseX, int mouseY)
    {
        if (selectedOption == null)
        {
            return false;
        }
        else
        {
            ItemStack gear = menu.getInput();
            if (gear.isEmpty())
            {
                return false;
            }
            else if (AttributeGearData.hasData(gear) && !AttributeGearData.read(gear).isModifiable())
            {
                Component cmp = (new TranslatableComponent("the_vault.gear_modification.unmodifiable")).withStyle(ChatFormatting.RED);
                tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                return true;
            }
            else
            {
                List<ItemStack> inputs = selectedOption.getCraftingCost(gear);
                List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
                if (missing.isEmpty())
                {
                    List<Component> tooltip = new ArrayList();
                    tooltip.add(gear.getHoverName());
                    Item patt5771$temp = gear.getItem();
                    if (patt5771$temp instanceof VaultGearTooltipItem)
                    {
                        VaultGearTooltipItem gearTooltipItem = (VaultGearTooltipItem) patt5771$temp;
                        tooltip.addAll(gearTooltipItem.createTooltip(gear, GearTooltip.craftingView()));
                    }

                    tooltipRenderer.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, TooltipDirection.RIGHT);
                    return true;
                }
                else
                {
                    Component cmpx = (new TranslatableComponent("the_vault.gear_workbench.missing_inputs")).withStyle(ChatFormatting.RED);
                    tooltipRenderer.renderTooltip(poseStack, cmpx, mouseX, mouseY, TooltipDirection.RIGHT);
                    return true;
                }
            }
        }
    }

    /**
     * Called in mixins/ModifierWorkbenchScreenMixin
     **/
    public static boolean setDisabled_coinpouch(ModifierWorkbenchContainer menu, iskallia.vault.gear.crafting.ModifierWorkbenchHelper.CraftingOption selectedOption, Inventory playerInventory)
    {
        ItemStack gear = menu.getInput();
        if (gear.isEmpty())
        {
            return true;
        }
        else if (selectedOption != null)
        {
            List<ItemStack> inputs = selectedOption.getCraftingCost(gear);
            List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
            return !missing.isEmpty();
        }
        else
        {
            return true;
        }
    }

    /**
     * Called in mixins/ModifierWorkbenchScreenMixin
     **/
    public static boolean setDisabled_vh(ModifierWorkbenchContainer menu, iskallia.vault.gear.crafting.ModifierWorkbenchHelper.CraftingOption selectedOption, Inventory playerInventory)
    {
        ItemStack gear = menu.getInput();
        if (gear.isEmpty())
        {
            return true;
        }
        else if (selectedOption != null)
        {
            List<ItemStack> inputs = selectedOption.getCraftingCost(gear);
            List<ItemStack> missing = InventoryUtil.getMissingInputs(inputs, playerInventory);
            return !missing.isEmpty();
        }
        else
        {
            return true;
        }
    }
}
