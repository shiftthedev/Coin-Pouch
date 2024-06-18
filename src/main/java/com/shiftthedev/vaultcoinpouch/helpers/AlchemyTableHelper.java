package com.shiftthedev.vaultcoinpouch.helpers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.shiftthedev.vaultcoinpouch.network.ShiftAlchemyTableEffectCraftMessage;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.element.AlchemyCraftSelectorElement;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.container.AlchemyTableContainer;
import iskallia.vault.init.ModNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AlchemyTableHelper
{
    public static void TryCraft(AlchemyCraftSelectorElement.CraftingOption option, AlchemyTableContainer menu, Inventory playerInventory)
    {
        if (option != null)
        {
            ItemStack potion = menu.getInput();
            if (!potion.isEmpty())
            {
                AlchemyTableConfig.CraftableEffectConfig cfg = option.cfg();
                List<ItemStack> inputs = option.getCraftingCost(potion);
                List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, playerInventory);
                if (missing.isEmpty())
                {
                    String craftId = cfg == null ? null : cfg.getEffectId();
                    ModNetwork.CHANNEL.sendToServer(new ShiftAlchemyTableEffectCraftMessage(menu.getTilePos(), craftId));
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
