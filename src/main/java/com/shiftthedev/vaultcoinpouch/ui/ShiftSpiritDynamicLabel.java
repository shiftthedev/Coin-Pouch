package com.shiftthedev.vaultcoinpouch.ui;

import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.function.Supplier;

public class ShiftSpiritDynamicLabel extends LabelElement<ShiftSpiritDynamicLabel>
{
    private final Supplier<Component> textSupplier;
    
    public ShiftSpiritDynamicLabel(IPosition position, Supplier<Component> textSupplier, LabelTextStyle.Builder labelTextStyle, TextComponent defaultText) {
        super(position, defaultText, labelTextStyle);
        this.textSupplier = textSupplier;
    }

    public Component getComponent() {
        return (Component)this.textSupplier.get();
    }
}
