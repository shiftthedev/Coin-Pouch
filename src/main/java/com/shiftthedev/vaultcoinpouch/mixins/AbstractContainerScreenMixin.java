package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>
{
    @Shadow
    public abstract T getMenu();

    protected AbstractContainerScreenMixin(Component p_96550_)
    {
        super(p_96550_);
    }

    @Redirect(method = "renderSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"))
    private void renderSlot_coinpouch(ItemRenderer itemRenderer, Font font, ItemStack itemStack, int x, int y, String count)
    {
        if (this.getMenu() instanceof CoinPouchContainer && itemStack.getCount() > 1 && !Screen.hasShiftDown())
        {
            itemRenderer.renderGuiItemDecorations(font, itemStack, x, y, getCount(itemStack.getCount()));
        }
        else
        {
            itemRenderer.renderGuiItemDecorations(font, itemStack, x, y, count);
        }
    }

    private String getCount(int count)
    {
        if (count > 1000000000)
        {
            return Math.floorDiv(count, 1000000000) + "B";
        }

        if (count > 1000000)
        {
            return Math.floorDiv(count, 1000000) + "M";
        }

        if (count > 1000)
        {
            return Math.floorDiv(count, 1000) + "K";
        }

        return String.valueOf(count);
    }
}
