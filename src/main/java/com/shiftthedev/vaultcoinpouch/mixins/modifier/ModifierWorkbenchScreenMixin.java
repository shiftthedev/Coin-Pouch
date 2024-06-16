package com.shiftthedev.vaultcoinpouch.mixins.modifier;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.network.ShiftModifierWorkbenchCraftMessage;
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
import iskallia.vault.client.gui.screen.block.ModifierWorkbenchScreen;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.container.ModifierWorkbenchContainer;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.crafting.ModifierWorkbenchHelper;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.tooltip.GearTooltip;
import iskallia.vault.gear.tooltip.VaultGearTooltipItem;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.util.function.ObservableSupplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
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
import java.util.Set;

@Mixin(value = ModifierWorkbenchScreen.class, remap = false)
public abstract class ModifierWorkbenchScreenMixin extends AbstractElementContainerScreen<ModifierWorkbenchContainer>
{
    @Mutable
    @Shadow
    @Final
    private WorkbenchCraftSelectorElement<?, ?> selectorElement;

    @Mutable
    @Shadow
    @Final
    private TextInputElement<?> searchInput;

    @Shadow
    private ModifierWorkbenchHelper.CraftingOption selectedOption;

    @Shadow
    @Final
    private Inventory playerInventory;

    @Shadow
    protected abstract void tryCraft();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init_impl(ModifierWorkbenchContainer container, Inventory inventory, Component title, CallbackInfo ci)
    {
        if(VCPConfig.GENERAL.modifierWorkbenchEnabled())
        {
            this.removeAllElements();

            this.setGuiSize(Spatials.size(176, 212));
            this.addElement((NineSliceElement)(new NineSliceElement(this.getGuiSpatial(), ScreenTextures.DEFAULT_WINDOW_BACKGROUND)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui).size(Spatials.copy(gui));
            }));
            this.addElement((LabelElement)(new LabelElement(Spatials.positionXY(8, 7), ((ModifierWorkbenchContainer)this.getMenu()).getTileEntity().getDisplayName().copy().withStyle(Style.EMPTY.withColor(-12632257)), LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            MutableComponent inventoryName = inventory.getDisplayName().copy();
            inventoryName.withStyle(Style.EMPTY.withColor(-12632257));
            this.addElement((LabelElement)(new LabelElement(Spatials.positionXY(8, 120), inventoryName, LabelTextStyle.defaultStyle())).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            this.addElement((SlotsElement)(new SlotsElement(this)).layout((screen, gui, parent, world) -> {
                world.positionXY(gui);
            }));
            this.addElement(this.searchInput = (TextInputElement)(new TextInputElement(Spatials.positionXY(110, 5).size(60, 12), Minecraft.getInstance().font)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            IMutableSpatial var10004 = Spatials.positionXY(8, 19).height(97);
            ObservableSupplier var10005 = ObservableSupplier.ofIdentity(() -> {
                return ((ModifierWorkbenchContainer)this.getMenu()).getInput();
            });
            TextInputElement var10006 = this.searchInput;
            Objects.requireNonNull(var10006);
            this.addElement(this.selectorElement = (WorkbenchCraftSelectorElement)(new WorkbenchCraftSelectorElement(var10004, var10005, var10006::getInput)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            ButtonElement craftButton;
            this.addElement(craftButton = (ButtonElement)(new ButtonElement(Spatials.positionXY(142, 48), ScreenTextures.BUTTON_CRAFT_TEXTURES, this::tryCraft)).layout((screen, gui, parent, world) -> {
                world.translateXY(gui);
            }));
            craftButton.tooltip((tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag) -> {
                if (this.selectedOption == null) {
                    return false;
                } else {
                    ItemStack gear = ((ModifierWorkbenchContainer)this.getMenu()).getInput();
                    if (gear.isEmpty()) {
                        return false;
                    } else if (AttributeGearData.hasData(gear) && !AttributeGearData.read(gear).isModifiable()) {
                        Component cmp = (new TranslatableComponent("the_vault.gear_modification.unmodifiable")).withStyle(ChatFormatting.RED);
                        tooltipRenderer.renderTooltip(poseStack, cmp, mouseX, mouseY, TooltipDirection.RIGHT);
                        return true;
                    } else {
                        List<ItemStack> inputs = this.selectedOption.getCraftingCost(gear);
                        List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, this.playerInventory);
                        if (missing.isEmpty()) {
                            List<Component> tooltip = new ArrayList();
                            tooltip.add(gear.getHoverName());
                            Item patt5771$temp = gear.getItem();
                            if (patt5771$temp instanceof VaultGearTooltipItem) {
                                VaultGearTooltipItem gearTooltipItem = (VaultGearTooltipItem)patt5771$temp;
                                tooltip.addAll(gearTooltipItem.createTooltip(gear, GearTooltip.craftingView()));
                            }

                            tooltipRenderer.renderComponentTooltip(poseStack, tooltip, mouseX, mouseY, TooltipDirection.RIGHT);
                            return true;
                        } else {
                            Component cmpx = (new TranslatableComponent("the_vault.gear_workbench.missing_inputs")).withStyle(ChatFormatting.RED);
                            tooltipRenderer.renderTooltip(poseStack, cmpx, mouseX, mouseY, TooltipDirection.RIGHT);
                            return true;
                        }
                    }
                }
            });
            craftButton.setDisabled(() -> {
                ItemStack gear = ((ModifierWorkbenchContainer)this.getMenu()).getInput();
                if (gear.isEmpty()) {
                    return true;
                } else if (this.selectedOption != null) {
                    List<ItemStack> inputs = this.selectedOption.getCraftingCost(gear);
                    List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, this.playerInventory);
                    return !missing.isEmpty();
                } else {
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
        if(VCPConfig.GENERAL.modifierWorkbenchEnabled())
        {
            shift_tryCraft();
            ci.cancel();
            return;
        }
    }
    
    private void shift_tryCraft()
    {
        if (this.selectedOption != null) {
            ItemStack gear = ((ModifierWorkbenchContainer)this.getMenu()).getInput();
            if (!gear.isEmpty()) {
                ItemStack gearCopy = gear.copy();
                VaultGearWorkbenchConfig.CraftableModifierConfig cfg = this.selectedOption.cfg();
                if (cfg != null) {
                    if (VaultGearData.read(gearCopy).getItemLevel() < cfg.getMinLevel()) {
                        return;
                    }

                    VaultGearModifier<?> modifier = (VaultGearModifier)cfg.createModifier().orElse(null);
                    if (modifier != null) {
                        ModifierWorkbenchHelper.removeCraftedModifiers(gearCopy);
                        VaultGearData data = VaultGearData.read(gearCopy);
                        Set<String> modGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
                        if (modGroups.contains(modifier.getModifierGroup())) {
                            return;
                        }
                    }
                }

                List<ItemStack> inputs = this.selectedOption.getCraftingCost(gear);
                List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(inputs, this.playerInventory);
                if (missing.isEmpty()) {
                    ResourceLocation craftKey = cfg == null ? null : cfg.getWorkbenchCraftIdentifier();
                    ModNetwork.CHANNEL.sendToServer(new ShiftModifierWorkbenchCraftMessage(((ModifierWorkbenchContainer)this.getMenu()).getTilePos(), craftKey));
                }
            }
        }
    }

    public ModifierWorkbenchScreenMixin(ModifierWorkbenchContainer container, Inventory inventory, Component title, IElementRenderer elementRenderer, ITooltipRendererFactory<AbstractElementContainerScreen<ModifierWorkbenchContainer>> tooltipRendererFactory)
    {
        super(container, inventory, title, elementRenderer, tooltipRendererFactory);
    }
}
