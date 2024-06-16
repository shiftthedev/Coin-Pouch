package com.shiftthedev.vaultcoinpouch.ui;

import iskallia.vault.client.gui.framework.element.FakeItemSlotElement;
import iskallia.vault.client.gui.framework.element.ScrollableItemStackSelectorElement;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import net.minecraft.world.item.ItemStack;

public class ShiftSpiritItemSelectorWithTooltipEntry extends ScrollableItemStackSelectorElement.ItemSelectorEntry
{
    public ShiftSpiritItemSelectorWithTooltipEntry(ItemStack displayStack) {
        super(displayStack, false);
    }

    public void adjustSlot(FakeItemSlotElement<?> slot) {
        slot.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
            if (!this.getDisplayStack().isEmpty()) {
                tooltipRenderer.renderTooltip(poseStack, this.getDisplayStack(), mouseX, mouseY, TooltipDirection.RIGHT);
            }

            return true;
        });
        slot.setLabelStackCount();
    }
}
