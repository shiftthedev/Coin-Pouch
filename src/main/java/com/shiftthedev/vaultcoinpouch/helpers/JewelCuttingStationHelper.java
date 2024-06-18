package com.shiftthedev.vaultcoinpouch.helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import iskallia.vault.config.VaultJewelCuttingConfig;
import iskallia.vault.container.VaultJewelCuttingStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.init.ModSounds;
import iskallia.vault.network.message.JewelCuttingParticleMessage;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.world.data.PlayerExpertisesData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class JewelCuttingStationHelper
{
    public static void CutJewel(VaultJewelCuttingStationContainer container, ServerPlayer player, int sizeToRemove, OverSizedInventory inventory, BlockPos blockPos,
                                VaultJewelCuttingConfig.JewelCuttingOutput recipeOutput, VaultJewelCuttingConfig.JewelCuttingInput recipeInput, Level level)
    {
        if (container.getJewelInputSlot() != null)
        {
            ItemStack stack = container.getJewelInputSlot().getItem();
            if (!stack.isEmpty())
            {
                VaultGearData data = VaultGearData.read(stack);
                Random random = new Random();
                boolean broken = false;
                boolean chipped = false;
                int freeCuts = 0;
                ExpertiseTree expertises = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);

                JewelExpertise expertise;
                for (Iterator var10 = expertises.getAll(JewelExpertise.class, Skill::isUnlocked).iterator(); var10.hasNext(); freeCuts = expertise.getNumberOfFreeCuts())
                {
                    expertise = (JewelExpertise) var10.next();
                }

                Iterator var18 = data.getModifiers(ModGearAttributes.JEWEL_SIZE, VaultGearData.Type.ALL_MODIFIERS).iterator();

                while (var18.hasNext())
                {
                    VaultGearAttributeInstance<Integer> sizeAttribute = (VaultGearAttributeInstance) var18.next();
                    sizeAttribute.setValue(Math.max(10, (Integer) sizeAttribute.getValue() - sizeToRemove));
                    data.write(stack);
                }

                if ((freeCuts <= 0 || !stack.getOrCreateTag().contains("freeCuts") || stack.getOrCreateTag().getInt("freeCuts") < freeCuts) && freeCuts != 0)
                {
                    if (freeCuts > 0 && (!stack.getOrCreateTag().contains("freeCuts") || stack.getOrCreateTag().getInt("freeCuts") < freeCuts))
                    {
                        stack.getOrCreateTag().putInt("freeCuts", stack.getOrCreateTag().getInt("freeCuts") + 1);
                    }
                }
                else
                {
                    List<VaultGearModifier<?>> prefix = new ArrayList(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
                    List<VaultGearModifier<?>> suffix = new ArrayList(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
                    int affixSize = prefix.size() + suffix.size();
                    if (affixSize <= 1)
                    {
                        breakJewel(blockPos, recipeOutput, level, inventory);
                        container.getJewelInputSlot().set(ItemStack.EMPTY);
                        broken = true;
                    }
                    else
                    {
                        Collections.shuffle(prefix, random);
                        Collections.shuffle(suffix, random);
                        if (suffix.size() > 0 && prefix.size() > 0)
                        {
                            boolean removedAffix = false;
                            VaultGearModifier modifier;
                            Iterator var25;
                            if (random.nextBoolean())
                            {
                                var25 = prefix.iterator();

                                while (var25.hasNext())
                                {
                                    modifier = (VaultGearModifier) var25.next();
                                    if (data.removeModifier(modifier))
                                    {
                                        data.updateAttribute(ModGearAttributes.PREFIXES, Math.max(0, (Integer) data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - 1));
                                        data.setRarity(VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1));
                                        removedAffix = true;
                                        break;
                                    }
                                }
                            }

                            if (!removedAffix)
                            {
                                var25 = suffix.iterator();

                                while (var25.hasNext())
                                {
                                    modifier = (VaultGearModifier) var25.next();
                                    if (data.removeModifier(modifier))
                                    {
                                        data.updateAttribute(ModGearAttributes.SUFFIXES, Math.max(0, (Integer) data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - 1));
                                        data.setRarity(VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1));
                                        break;
                                    }
                                }
                            }
                        }
                        else
                        {
                            Iterator var14;
                            VaultGearModifier modifier;
                            if (suffix.size() > 0)
                            {
                                var14 = suffix.iterator();

                                while (var14.hasNext())
                                {
                                    modifier = (VaultGearModifier) var14.next();
                                    if (data.removeModifier(modifier))
                                    {
                                        data.updateAttribute(ModGearAttributes.SUFFIXES, Math.max(0, (Integer) data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - 1));
                                        data.setRarity(VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1));
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                var14 = prefix.iterator();

                                while (var14.hasNext())
                                {
                                    modifier = (VaultGearModifier) var14.next();
                                    if (data.removeModifier(modifier))
                                    {
                                        data.updateAttribute(ModGearAttributes.PREFIXES, Math.max(0, (Integer) data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - 1));
                                        data.setRarity(VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1));
                                        break;
                                    }
                                }
                            }
                        }

                        chipped = true;
                    }
                }

                ItemStack scrap = container.getScrapSlot().getItem();
                scrap.shrink(recipeInput.getMainInput().getCount());
                container.getScrapSlot().set(scrap);

                ItemStack gold = container.getBronzeSlot().getItem();
                ItemStack secondInput = recipeInput.getSecondInput();
                int goldMissing = secondInput.getCount();
                int goldToRemove = Math.min(goldMissing, gold.getCount());
                gold.shrink(goldToRemove);
                container.getBronzeSlot().set(gold);

                // Coin Pouch remove
                goldMissing -= goldToRemove;
                if (goldMissing > 0)
                {
                    NonNullList<ItemStack> pouchStacks = NonNullList.create();
                    Iterator it = player.getInventory().items.iterator();
                    while (it.hasNext())
                    {
                        if (goldMissing <= 0)
                        {
                            break;
                        }

                        ItemStack plStack = (ItemStack) it.next();
                        if (VaultJewelCuttingStationTileEntity.canMerge(plStack, secondInput))
                        {
                            goldToRemove = Math.min(goldMissing, plStack.getCount());
                            plStack.shrink(goldToRemove);
                            goldMissing -= goldToRemove;
                        }

                        if (plStack.is(VCPRegistry.COIN_POUCH))
                        {
                            pouchStacks.add(plStack);
                        }
                    }

                    it = pouchStacks.iterator();
                    while (it.hasNext())
                    {
                        if (goldMissing <= 0)
                        {
                            break;
                        }

                        ItemStack pouchStack = (ItemStack) it.next();
                        goldToRemove = Math.min(goldMissing, CoinPouchItem.getCoinCount(pouchStack, secondInput));
                        CoinPouchItem.extractCoins(pouchStack, secondInput, goldToRemove);
                        goldMissing -= goldToRemove;
                    }
                }
                // End of Coin Pouch remove

                if (level != null)
                {
                    if (broken)
                    {
                        level.playSound((Player) null, container.getTilePos(), ModSounds.JEWEL_CUT, SoundSource.BLOCKS, 0.8F, level.random.nextFloat() * 0.1F + 0.9F);
                    }
                    else
                    {
                        if (chipped)
                        {
                            level.playSound((Player) null, container.getTilePos(), ModSounds.JEWEL_CUT, SoundSource.BLOCKS, 0.3F, level.random.nextFloat() * 0.1F + 0.7F);
                        }
                        else
                        {
                            level.playSound((Player) null, container.getTilePos(), ModSounds.JEWEL_CUT, SoundSource.BLOCKS, 0.3F, level.random.nextFloat() * 0.1F + 0.7F);
                            level.playSound((Player) null, container.getTilePos(), ModSounds.JEWEL_CUT_SUCCESS, SoundSource.BLOCKS, 0.2F, level.random.nextFloat() * 0.1F + 0.9F);
                        }

                        data.write(stack);
                        container.getJewelInputSlot().set(stack);
                    }
                }
            }
        }
    }

    public static boolean canCraft(VaultJewelCuttingStationTileEntity tileEntity, Player player)
    {
        VaultJewelCuttingConfig.JewelCuttingOutput output = tileEntity.getRecipeOutput();
        VaultJewelCuttingConfig.JewelCuttingInput input = tileEntity.getRecipeInput();
        OverSizedInventory inventory = tileEntity.getInventory();

        if (input == null || output == null)
        {
            return false;
        }

        if (!VaultJewelCuttingStationTileEntity.canMerge(inventory.getItem(0), input.getMainInput()))
        {
            return false;
        }
        if (inventory.getItem(0).getCount() < input.getMainInput().getCount())
        {
            return false;
        }

        if (!hasGold(input.getSecondInput(), inventory.getItem(1), player))
        {
            return false;
        }

        if (!MiscUtils.canFullyMergeIntoSlot(inventory, 2, output.getMainOutputMatching()))
        {
            return false;
        }
        if (!MiscUtils.canFullyMergeIntoSlot(inventory, 3, output.getExtraOutput1Matching()))
        {
            return false;
        }

        return MiscUtils.canFullyMergeIntoSlot(inventory, 4, output.getExtraOutput2Matching());
    }

    private static boolean hasGold(ItemStack goldInput, ItemStack goldInventory, Player player)
    {
        int goldMissing = goldInput.getCount();
        if (VaultJewelCuttingStationTileEntity.canMerge(goldInventory, goldInput))
        {
            if (goldInventory.getCount() >= goldMissing)
            {
                return true;
            }

            goldMissing -= goldInput.getCount();
        }

        Iterator it = player.getInventory().items.iterator();
        int toRemove = 0;
        while (it.hasNext())
        {
            if (goldMissing <= 0)
            {
                break;
            }

            ItemStack plStack = (ItemStack) it.next();
            if (VaultJewelCuttingStationTileEntity.canMerge(plStack, goldInput))
            {
                toRemove = Math.min(goldMissing, plStack.getCount());
                goldMissing -= toRemove;
            }
            else if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                toRemove = Math.min(goldMissing, CoinPouchItem.getCoinCount(plStack, goldInput));
                goldMissing -= toRemove;
            }
        }

        return goldMissing <= 0;
    }

    private static void breakJewel(BlockPos blockPos, VaultJewelCuttingConfig.JewelCuttingOutput output, Level level, OverSizedInventory inventory)
    {
        if (output != null)
        {
            if (level != null)
            {
                level.playSound((Player) null, blockPos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5F + (new Random()).nextFloat() * 0.25F, 0.75F + (new Random()).nextFloat() * 0.25F);
            }

            ItemStack input = inventory.getItem(0).copy();
            VaultJewelCuttingStationTileEntity.addStackToSlot(inventory, 2, getUseRelatedOutput(output.generateMainOutput()));
            VaultJewelCuttingStationTileEntity.addStackToSlot(inventory, 3, getUseRelatedOutput(output.generateExtraOutput1()));
            VaultJewelCuttingStationTileEntity.addStackToSlot(inventory, 4, getUseRelatedOutput(output.generateExtraOutput2()));
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new JewelCuttingParticleMessage(blockPos, inventory.getItem(5)));
        }
    }

    private static ItemStack getUseRelatedOutput(ItemStack output)
    {
        float out = (float) output.getCount();
        int resultCount = Mth.floor(out);
        if (resultCount < 1 && out > 0.0F && (new Random()).nextFloat() < out)
        {
            ++resultCount;
        }

        ItemStack copyOut = output.copy();
        copyOut.setCount(resultCount);
        return copyOut;
    }
}
