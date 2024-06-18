package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.JewelCuttingStationHelper;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.JewelCuttingButtonElement;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.element.NineSliceElement;
import iskallia.vault.client.gui.framework.element.SlotsElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.block.VaultJewelCuttingStationScreen;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.tool.JewelItem;
import iskallia.vault.network.message.VaultJewelCuttingRequestModificationMessage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VaultJewelCuttingStationScreen.class, remap = false)
public abstract class VaultJewelCuttingStationScreenMixin extends AbstractElementContainerScreen<VaultJewelCuttingStationContainer>
{
    @Inject(method = "<init>", at = @At("RETURN"))
    private void init_impl(VaultJewelCuttingStationContainer container, Inventory inventory, Component title, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            this.removeAllElements();

            this.addElement((NineSliceElement) (new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((SlotsElement) (new SlotsElement(this)).layout((screen, gui, parent, world) -> {
                world.positionXY(gui);
            }));
            this.addElement((LabelElement) (new LabelElement(Spatials.positionXY(8, 7), ((VaultJewelCuttingStationContainer) this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            MutableComponent inventoryName = inventory.getDisplayName().copy();
            inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
            this.addElement((LabelElement) (new LabelElement(Spatials.positionXY(8, 77), inventoryName, LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            Slot slot = ((VaultJewelCuttingStationContainer) this.getMenu()).getJewelInputSlot();
            if (slot != null)
            {
                IMutableSpatial btnPosition = Spatials.positionXY(slot.x - 1, slot.y - 1).translateY(-10).translateX(40);
                JewelCuttingButtonElement<?> button = (JewelCuttingButtonElement) (new JewelCuttingButtonElement(btnPosition, () -> {
                    VaultJewelCuttingRequestModificationMessage msg = new VaultJewelCuttingRequestModificationMessage();
                    ModNetwork.CHANNEL.sendToServer(msg);
                }, (VaultJewelCuttingStationContainer) this.getMenu())).layout((screen, gui, parent, world) -> {
                    world.translateXY(gui);
                });
                button.setDisabled(() -> {
                    if (((VaultJewelCuttingStationContainer) this.getMenu()).getTileEntity() != null && !JewelCuttingStationHelper.canCraft(((VaultJewelCuttingStationContainer) this.getMenu()).getTileEntity(), playerInventory.player))
                    {
                        return true;
                    }
                    else if (((VaultJewelCuttingStationContainer) this.getMenu()).getJewelInputSlot().getItem().getItem() instanceof JewelItem)
                    {
                        VaultGearData data = VaultGearData.read(((VaultJewelCuttingStationContainer) this.getMenu()).getJewelInputSlot().getItem());
                        return (Integer) data.getFirstValue(ModGearAttributes.JEWEL_SIZE).orElse(0) <= 10;
                    }
                    else
                    {
                        return true;
                    }
                });
                this.addElement(button);
            }
        }
    }

    @Shadow
    @Final
    private Inventory playerInventory;

    public VaultJewelCuttingStationScreenMixin(VaultJewelCuttingStationContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<VaultJewelCuttingStationContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
