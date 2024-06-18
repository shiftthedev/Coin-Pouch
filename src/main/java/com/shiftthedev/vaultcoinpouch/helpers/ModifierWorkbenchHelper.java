package com.shiftthedev.vaultcoinpouch.helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.shiftthedev.vaultcoinpouch.network.ShiftModifierWorkbenchCraftMessage;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ModifierWorkbenchHelper
{
    public static void TryCraft(iskallia.vault.gear.crafting.ModifierWorkbenchHelper.CraftingOption selectedOption, ModifierWorkbenchContainer menu, Inventory playerInventory)
    {
        if (selectedOption != null)
        {
            ItemStack gear = menu.getInput();
            if (!gear.isEmpty())
            {
                ItemStack gearCopy = gear.copy();
                VaultGearWorkbenchConfig.CraftableModifierConfig cfg = selectedOption.cfg();
                if (cfg != null)
                {
                    if (VaultGearData.read(gearCopy).getItemLevel() < cfg.getMinLevel())
                    {
                        return;
                    }

                    VaultGearModifier<?> modifier = (VaultGearModifier) cfg.createModifier().orElse(null);
                    if (modifier != null)
                    {
                        iskallia.vault.gear.crafting.ModifierWorkbenchHelper.removeCraftedModifiers(gearCopy);
                        VaultGearData data = VaultGearData.read(gearCopy);
                        Set<String> modGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
                        if (modGroups.contains(modifier.getModifierGroup()))
                        {
                            return;
                        }
                    }
                }

                List<ItemStack> inputs = selectedOption.getCraftingCost(gear);
                List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
                if (missing.isEmpty())
                {
                    ResourceLocation craftKey = cfg == null ? null : cfg.getWorkbenchCraftIdentifier();
                    ModNetwork.CHANNEL.sendToServer(new ShiftModifierWorkbenchCraftMessage(menu.getTilePos(), craftKey));
                }
            }
        }
    }

    public static void Render(PoseStack poseStack, IMutableSpatial worldSpatial, List<ItemStack> inputs)
    {
        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
        Font font = Minecraft.getInstance().font;
        int offsetX = worldSpatial.x() + worldSpatial.width() - 18;
        int offsetY = worldSpatial.y() + worldSpatial.height() - 18;
        List<ItemStack> missingInputs = new ArrayList();
        if (Minecraft.getInstance().player != null)
        {
            missingInputs = ShiftInventoryUtils.getMissingInputs(inputs, Minecraft.getInstance().player.getInventory());
        }

        for (Iterator var12 = inputs.iterator(); var12.hasNext(); offsetX -= 17)
        {
            ItemStack stack = (ItemStack) var12.next();
            ir.renderGuiItem(stack, offsetX, offsetY);
            MutableComponent text = new TextComponent(String.valueOf(stack.getCount()));
            if (((List) missingInputs).contains(stack))
            {
                text.withStyle(ChatFormatting.RED);
            }

            poseStack.pushPose();
            poseStack.translate(0.0, 0.0, 200.0);
            MultiBufferSource.BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            font.drawInBatch(text, (float) (offsetX + 17 - font.width(text)), (float) (offsetY + 9), 16777215, true, poseStack.last().pose(), buffers, false, 0, LightmapHelper.getPackedFullbrightCoords());
            buffers.endBatch();
            poseStack.popPose();
        }
    }
}
