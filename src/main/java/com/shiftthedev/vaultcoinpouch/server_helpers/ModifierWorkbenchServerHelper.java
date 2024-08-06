package com.shiftthedev.vaultcoinpouch.server_helpers;

import com.shiftthedev.vaultcoinpouch.mixins.modifier.ModifierWorkbenchCraftMessageAccessor;
import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import iskallia.vault.config.gear.VaultGearWorkbenchConfig;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.network.message.ModifierWorkbenchCraftMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModifierWorkbenchServerHelper
{
    /**
     * Called in mixins/ModifierWorkbenchCraftMessageMixin
     **/
    public static void enqueueWork(NetworkEvent.Context context, ModifierWorkbenchCraftMessage message)
    {
        ServerPlayer player = context.getSender();
        BlockPos pos = ((ModifierWorkbenchCraftMessageAccessor) message).getPos();
        BlockEntity tile = player.getLevel().getBlockEntity(pos);
        if (tile instanceof ModifierWorkbenchTileEntity workbenchTile)
        {
            ItemStack input = workbenchTile.getInventory().getItem(0);
            if (!input.isEmpty() && input.getItem() instanceof VaultGearItem && AttributeGearData.hasData(input))
            {
                if (VaultGearData.read(input).isModifiable())
                {
                    VaultGearWorkbenchConfig.getConfig(input.getItem()).ifPresent((cfg) -> getConfig(input, message, cfg, player, tile));
                }
            }
        }
    }

    private static void getConfig(ItemStack input, ModifierWorkbenchCraftMessage message, VaultGearWorkbenchConfig cfg, ServerPlayer player, BlockEntity tile)
    {
        ItemStack inputCopy = input.copy();
        VaultGearModifier.AffixType targetAffix = null;
        VaultGearModifier<?> createdModifier = null;
        List<ItemStack> cost = new ArrayList();
        if (((ModifierWorkbenchCraftMessageAccessor) message).getCraftModifierIdentifier() == null)
        {
            if (!iskallia.vault.gear.crafting.ModifierWorkbenchHelper.hasCraftedModifier(inputCopy))
            {
                return;
            }

            cost.addAll(cfg.getCostRemoveCraftedModifiers());
        }
        else
        {
            VaultGearWorkbenchConfig.CraftableModifierConfig modifierConfig = cfg.getConfig(((ModifierWorkbenchCraftMessageAccessor) message).getCraftModifierIdentifier());
            if (modifierConfig == null)
            {
                return;
            }

            if (!modifierConfig.hasPrerequisites(player))
            {
                return;
            }

            boolean hadCraftedModifiers = iskallia.vault.gear.crafting.ModifierWorkbenchHelper.hasCraftedModifier(inputCopy);
            iskallia.vault.gear.crafting.ModifierWorkbenchHelper.removeCraftedModifiers(inputCopy);
            VaultGearData data = VaultGearData.read(inputCopy);
            if (data.getItemLevel() < modifierConfig.getMinLevel())
            {
                return;
            }

            targetAffix = modifierConfig.getAffixGroup().getTargetAffixType();
            if (targetAffix == VaultGearModifier.AffixType.PREFIX)
            {
                if (!VaultGearModifierHelper.hasOpenPrefix(inputCopy))
                {
                    return;
                }
            }
            else if (!VaultGearModifierHelper.hasOpenSuffix(inputCopy))
            {
                return;
            }

            createdModifier = (VaultGearModifier) modifierConfig.createModifier().orElse(null);
            if (createdModifier == null)
            {
                return;
            }

            Set<String> existingModGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
            if (existingModGroups.contains(createdModifier.getModifierGroup()))
            {
                return;
            }

            cost.addAll(modifierConfig.createCraftingCost(inputCopy));
            if (hadCraftedModifiers)
            {
                cost.addAll(cfg.getCostRemoveCraftedModifiers());
            }
        }

        List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(cost, player.getInventory());
        if (missing.isEmpty())
        {
            if (ShiftInventoryUtils.consumeInputs(cost, player.getInventory(), true))
            {
                if (ShiftInventoryUtils.consumeInputs(cost, player.getInventory(), false))
                {
                    if (createdModifier == null)
                    {
                        iskallia.vault.gear.crafting.ModifierWorkbenchHelper.removeCraftedModifiers(input);
                    }
                    else
                    {
                        createdModifier.addCategory(VaultGearModifier.AffixCategory.CRAFTED);
                        createdModifier.setGameTimeAdded(player.getLevel().getGameTime());
                        iskallia.vault.gear.crafting.ModifierWorkbenchHelper.removeCraftedModifiers(input);
                        VaultGearData datax = VaultGearData.read(input);
                        datax.addModifier(targetAffix, createdModifier);
                        datax.write(input);
                    }

                    player.getLevel().levelEvent(1030, tile.getBlockPos(), 0);
                }

            }
        }
    }
}
