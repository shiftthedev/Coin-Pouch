package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.mojang.blaze3d.platform.InputConstants;
import com.shiftthedev.vaultcoinpouch.client_helpers.JewelCuttingStationClientHelper;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.client.ClientExpertiseData;
import iskallia.vault.client.gui.framework.element.ButtonElement;
import iskallia.vault.client.gui.framework.element.JewelCuttingButtonElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.skill.base.LearnableSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Mixin(value = JewelCuttingButtonElement.class, remap = false, priority = 1100)
public abstract class JewelCuttingButtonElementMixin extends ButtonElement
{
    @Shadow
    protected abstract Component addTooltipDots(int amount, ChatFormatting formatting);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void inti_coinpouch(IPosition position, Runnable onClick, VaultJewelCuttingStationContainer container, CallbackInfo ci)
    {
        if (VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            this.tooltip(Tooltips.multi(() -> JewelCuttingStationClientHelper.tooltip_coinpouch(container)));
        }
    }

    public List<Component> tooltip_coinpouch(VaultJewelCuttingStationContainer container)
    {
        Player player = Minecraft.getInstance().player;
        if (player == null)
        {
            return List.of();
        }
        else
        {
            long window = Minecraft.getInstance().getWindow().getWindow();
            boolean shiftDown = InputConstants.isKeyDown(window, 340) || InputConstants.isKeyDown(window, 344);
            ItemStack inputItem = ItemStack.EMPTY;
            Slot inputSlot = container.getJewelInputSlot();
            if (inputSlot != null && !inputSlot.getItem().isEmpty())
            {
                inputItem = inputSlot.getItem();
            }

            boolean hasInput = !inputItem.isEmpty();
            List<Component> tooltip = new ArrayList();
            VaultJewelCuttingConfig.JewelCuttingInput input = container.getTileEntity().getRecipeInput();
            VaultJewelCuttingConfig.JewelCuttingRange range = container.getTileEntity().getJewelCuttingRange();
            float chance = container.getTileEntity().getJewelCuttingModifierRemovalChance();
            int numberOfFreeCuts = 0;
            Iterator var14 = ClientExpertiseData.getLearnedTalentNodes().iterator();

            while (var14.hasNext())
            {
                TieredSkill learnedTalentNode = (TieredSkill) var14.next();
                LearnableSkill patt3197$temp = learnedTalentNode.getChild();
                if (patt3197$temp instanceof JewelExpertise)
                {
                    JewelExpertise jewelExpertise = (JewelExpertise) patt3197$temp;
                    numberOfFreeCuts = jewelExpertise.getNumberOfFreeCuts();
                }
            }

            ItemStack scrap = container.getScrapSlot().getItem();
            ItemStack bronze = container.getBronzeSlot().getItem();
            if (hasInput)
            {
                VaultGearData data = VaultGearData.read(inputItem);
                List<VaultGearModifier<?>> prefix = new ArrayList(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
                List<VaultGearModifier<?>> suffix = new ArrayList(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
                int affixSize = prefix.size() + suffix.size();
                VaultGearRarity lowerRarity = VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1);
                String var10000 = lowerRarity.name();
                String jewelLowerRarity = "item.the_vault.jewel." + var10000.toLowerCase(Locale.ROOT);
                MutableComponent lowerRarityComponent = (new TranslatableComponent(jewelLowerRarity)).withStyle(ChatFormatting.YELLOW);
                VaultGearRarity rarity = VaultJewelCuttingStationTileEntity.getNewRarity(affixSize);
                var10000 = rarity.name();
                String jewelRarity = "item.the_vault.jewel." + var10000.toLowerCase(Locale.ROOT);
                MutableComponent rarityComponent = (new TranslatableComponent(jewelRarity)).withStyle(ChatFormatting.YELLOW);
                if (affixSize < 2)
                {
                    tooltip.add(new TextComponent("Cut the Jewel into a Gemstone"));
                }
                else
                {
                    int var10003 = range.getMin();
                    tooltip.add((new TextComponent("Cut the jewel down in size (" + var10003 + "-" + range.getMax() + "), making it ")).append(lowerRarityComponent).append(new TextComponent(".")));
                    tooltip.add(new TextComponent("This will make it lose a random affix."));
                }

                if (numberOfFreeCuts > 0)
                {
                    tooltip.add(TextComponent.EMPTY);
                    tooltip.add((new TextComponent("")).append((new TextComponent("* ")).withStyle(ChatFormatting.GOLD)).append(new TextComponent("Your ")).append((new TextComponent("Jeweler Expertise")).withStyle(ChatFormatting.LIGHT_PURPLE)).append(new TextComponent(" gives you ")).append((new TextComponent(String.valueOf(numberOfFreeCuts))).withStyle(ChatFormatting.YELLOW)).append(new TextComponent(" free cut" + (numberOfFreeCuts == 1 ? "" : "s"))));
                    tooltip.add(new TextComponent("retaining its current grade."));
                    int usedFreeCuts = !inputItem.getOrCreateTag().contains("freeCuts") ? 0 : inputItem.getOrCreateTag().getInt("freeCuts");
                    int remaining = numberOfFreeCuts - usedFreeCuts;
                    tooltip.add((new TextComponent("Expertise Cuts: ")).append(this.addTooltipDots(usedFreeCuts, ChatFormatting.YELLOW)).append(addTooltipDots(remaining, ChatFormatting.GRAY)));
                }

                tooltip.add(TextComponent.EMPTY);
                tooltip.add(new TextComponent("Cost"));
                MutableComponent var10001 = (new TextComponent("- ")).append(input.getMainInput().getHoverName());
                int var10002 = input.getMainInput().getCount();
                tooltip.add(var10001.append(" x" + var10002).append(" [%s]".formatted(scrap.getCount())).withStyle(input.getMainInput().getCount() > scrap.getCount() ? ChatFormatting.RED : ChatFormatting.GREEN));
                var10001 = (new TextComponent("- ")).append(input.getSecondInput().getHoverName());
                var10002 = input.getSecondInput().getCount();

                // Coin Pouch check
                int goldMAmount = bronze.getCount();
                if (CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).isPresent())
                {
                    goldMAmount += CoinPouchItem.getCoinCount(CuriosApi.getCuriosHelper().findFirstCurio(player, VCPRegistry.COIN_POUCH).get().stack(), input.getSecondInput());
                }

                Iterator it = container.getPlayer().getInventory().items.iterator();
                while (it.hasNext())
                {
                    ItemStack plStack = (ItemStack) it.next();
                    if (VaultJewelCuttingStationTileEntity.canMerge(plStack, input.getSecondInput()))
                    {
                        goldMAmount += plStack.getCount();
                    }
                    else if (plStack.is(VCPRegistry.COIN_POUCH))
                    {
                        goldMAmount += CoinPouchItem.getCoinCount(plStack, input.getSecondInput());
                    }
                }

                tooltip.add(var10001.append(" x" + var10002).append(" [%s]".formatted(goldMAmount)).withStyle(input.getSecondInput().getCount() > goldMAmount ? ChatFormatting.RED : ChatFormatting.GREEN));
                // End of Coin Pouch check

                tooltip.add(new TextComponent(""));
                if (shiftDown)
                {
                    tooltip.addAll(inputItem.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.ADVANCED));
                }
                else
                {
                    tooltip.addAll(inputItem.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL));
                }

                Iterator var32 = data.getModifiers(ModGearAttributes.JEWEL_SIZE, VaultGearData.Type.ALL_MODIFIERS).iterator();

                while (var32.hasNext())
                {
                    VaultGearAttributeInstance<Integer> sizeAttribute = (VaultGearAttributeInstance) var32.next();
                    if ((Integer) sizeAttribute.getValue() <= 10)
                    {
                        tooltip.add(new TextComponent(""));
                        tooltip.add((new TextComponent("Cannot cut size to lower than 10")).withStyle(ChatFormatting.RED));
                    }
                }
            }
            else
            {
                tooltip.add((new TextComponent("Requires Jewel")).withStyle(ChatFormatting.RED));
            }

            return tooltip;
        }
    }

    public JewelCuttingButtonElementMixin(IPosition position, ButtonTextures textures, Runnable onClick)
    {
        super(position, textures, onClick);
    }
}
