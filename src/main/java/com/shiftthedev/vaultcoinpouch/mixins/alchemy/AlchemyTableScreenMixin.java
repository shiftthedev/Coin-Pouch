package com.shiftthedev.vaultcoinpouch.mixins.alchemy;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.helpers.AlchemyTableHelper;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.*;
import iskallia.vault.client.gui.framework.render.TooltipDirection;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.AbstractElementContainerScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.block.AlchemyTableScreen;
import iskallia.vault.container.AlchemyTableContainer;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleItem;
import iskallia.vault.util.function.ObservableSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(value = AlchemyTableScreen.class, remap = false)
public abstract class AlchemyTableScreenMixin extends AbstractElementContainerScreen<AlchemyTableContainer>
{
    @Mutable
    @Shadow
    @Final
    private TextInputElement<?> searchInput;

    @Mutable
    @Shadow
    @Final
    private AlchemyCraftSelectorElement<?, ?> selectorElement;

    @Shadow
    private AlchemyCraftSelectorElement.CraftingOption selectedOption;

    @Shadow
    protected abstract void tryCraft();

    @Shadow
    @Final
    private Inventory playerInventory;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init_impl(AlchemyTableContainer container, Inventory inventory, Component title, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.alchemyTableEnabled())
        {
            this.removeAllElements();

            this.setGuiSize(Spatials.size(176, 221));
            this.addElement((NineSliceElement) (new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui).size(Spatials.copy(gui));
            }));
            this.addElement((LabelElement) (new LabelElement(Spatials.positionXY(8, 7), ((AlchemyTableContainer) this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            MutableComponent inventoryName = inventory.getDisplayName().copy();
            inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
            this.addElement((LabelElement) (new LabelElement(Spatials.positionXY(8, 129), inventoryName, LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((SlotsElement) (new SlotsElement(this)).layout((screen, gui, parent, world) -> {
                world.positionXY(gui);
            }));

            this.addElement(this.searchInput = (TextInputElement) (new TextInputElement(Spatials.positionXY(108, 5).size(60, 12), Minecraft.getInstance().font)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));

            IMutableSpatial var10004 = Spatials.positionXY(7, 19).height(97);
            ObservableSupplier var10005 = ObservableSupplier.ofIdentity(() -> {
                return ((AlchemyTableContainer) this.getMenu()).getInput();
            });
            TextInputElement var10006 = this.searchInput;
            Objects.requireNonNull(var10006);

            this.addElement(this.selectorElement = (AlchemyCraftSelectorElement) (new AlchemyCraftSelectorElement(var10004, var10005, var10006::getInput)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));

            ButtonElement craftButton;
            this.addElement(craftButton = (ButtonElement) (new ButtonElement(Spatials.positionXY(131, 118), ScreenTextures.BUTTON_ALCHEMY_CRAFT_TEXTURES, this::tryCraft)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            craftButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                if (this.selectedOption == null)
                {
                    return false;
                }
                else
                {
                    ItemStack bottle = ((AlchemyTableContainer) this.getMenu()).getInput();
                    if (bottle.isEmpty())
                    {
                        return false;
                    }
                    else
                    {
                        List<ItemStack> inputs = this.selectedOption.getCraftingCost(bottle);
                        List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, this.playerInventory);
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
            });
            craftButton.setDisabled(() -> {
                ItemStack potion = ((AlchemyTableContainer) this.getMenu()).getInput();
                if (potion.isEmpty())
                {
                    return true;
                }
                else if (this.selectedOption != null)
                {
                    List<ItemStack> inputs = this.selectedOption.getCraftingCost(potion);
                    List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, this.playerInventory);
                    return !missing.isEmpty();
                }
                else
                {
                    return true;
                }
            });

            this.selectorElement.onSelect((option) -> {
                this.selectedOption = option;
            });

            this.searchInput.onTextChanged((text) -> {
                this.selectorElement.refreshElements();
            });
        }
    }

    @Inject(method = "tryCraft", at = @At("HEAD"), cancellable = true)
    private void tryCraft_impl(CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.alchemyTableEnabled())
        {
            AlchemyTableHelper.TryCraft(this.selectedOption, this.getMenu(), this.playerInventory);
            ci.cancel();
            return;
        }
    }

    public AlchemyTableScreenMixin(AlchemyTableContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<AlchemyTableContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
