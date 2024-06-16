package com.shiftthedev.vaultcoinpouch.mixins.jewelcutting;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
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
import iskallia.vault.init.ModSounds;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.expertise.type.JewelExpertise;
import iskallia.vault.skill.tree.ExpertiseTree;
import iskallia.vault.world.data.PlayerExpertisesData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = VaultJewelCuttingStationTileEntity.class, remap = false)
public abstract class VaultJewelCuttingStationTileEntityMixin extends BlockEntity implements MenuProvider
{
    @Inject(method = "cutJewel", at = @At("HEAD"), cancellable = true)
    private void cutJewel_impl(VaultJewelCuttingStationContainer container, ServerPlayer player, CallbackInfo ci)
    {
        if(VCPConfig.GENERAL.jewelCuttingStationEnabled())
        {
            shift_cutJewel(container, player);
            ci.cancel();
            return;
        }
    }
    
    private void shift_cutJewel(VaultJewelCuttingStationContainer container, ServerPlayer player) {
        if (container.getJewelInputSlot() != null) {
            ItemStack stack = container.getJewelInputSlot().getItem();
            if (!stack.isEmpty()) {
                VaultGearData data = VaultGearData.read(stack);
                Random random = new Random();
                boolean broken = false;
                boolean chipped = false;
                int freeCuts = 0;
                ExpertiseTree expertises = PlayerExpertisesData.get(player.getLevel()).getExpertises(player);

                JewelExpertise expertise;
                for(Iterator var10 = expertises.getAll(JewelExpertise.class, Skill::isUnlocked).iterator(); var10.hasNext(); freeCuts = expertise.getNumberOfFreeCuts()) {
                    expertise = (JewelExpertise)var10.next();
                }

                int sizeToRemove = this.getJewelCuttingRange().getRandom();
                Iterator var18 = data.getModifiers(ModGearAttributes.JEWEL_SIZE, VaultGearData.Type.ALL_MODIFIERS).iterator();

                while(var18.hasNext()) {
                    VaultGearAttributeInstance<Integer> sizeAttribute = (VaultGearAttributeInstance)var18.next();
                    sizeAttribute.setValue(Math.max(10, (Integer)sizeAttribute.getValue() - sizeToRemove));
                    data.write(stack);
                }

                if ((freeCuts <= 0 || !stack.getOrCreateTag().contains("freeCuts") || stack.getOrCreateTag().getInt("freeCuts") < freeCuts) && freeCuts != 0) {
                    if (freeCuts > 0 && (!stack.getOrCreateTag().contains("freeCuts") || stack.getOrCreateTag().getInt("freeCuts") < freeCuts)) {
                        stack.getOrCreateTag().putInt("freeCuts", stack.getOrCreateTag().getInt("freeCuts") + 1);
                    }
                } else {
                    List<VaultGearModifier<?>> prefix = new ArrayList(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
                    List<VaultGearModifier<?>> suffix = new ArrayList(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
                    int affixSize = prefix.size() + suffix.size();
                    if (affixSize <= 1) {
                        this.breakJewel();
                        container.getJewelInputSlot().set(ItemStack.EMPTY);
                        broken = true;
                    } else {
                        Collections.shuffle(prefix, random);
                        Collections.shuffle(suffix, random);
                        if (suffix.size() > 0 && prefix.size() > 0) {
                            boolean removedAffix = false;
                            VaultGearModifier modifier;
                            Iterator var25;
                            if (random.nextBoolean()) {
                                var25 = prefix.iterator();

                                while(var25.hasNext()) {
                                    modifier = (VaultGearModifier)var25.next();
                                    if (data.removeModifier(modifier)) {
                                        data.updateAttribute(ModGearAttributes.PREFIXES, Math.max(0, (Integer)data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - 1));
                                        data.setRarity(VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1));
                                        removedAffix = true;
                                        break;
                                    }
                                }
                            }

                            if (!removedAffix) {
                                var25 = suffix.iterator();

                                while(var25.hasNext()) {
                                    modifier = (VaultGearModifier)var25.next();
                                    if (data.removeModifier(modifier)) {
                                        data.updateAttribute(ModGearAttributes.SUFFIXES, Math.max(0, (Integer)data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - 1));
                                        data.setRarity(VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1));
                                        break;
                                    }
                                }
                            }
                        } else {
                            Iterator var14;
                            VaultGearModifier modifier;
                            if (suffix.size() > 0) {
                                var14 = suffix.iterator();

                                while(var14.hasNext()) {
                                    modifier = (VaultGearModifier)var14.next();
                                    if (data.removeModifier(modifier)) {
                                        data.updateAttribute(ModGearAttributes.SUFFIXES, Math.max(0, (Integer)data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - 1));
                                        data.setRarity(VaultJewelCuttingStationTileEntity.getNewRarity(affixSize - 1));
                                        break;
                                    }
                                }
                            } else {
                                var14 = prefix.iterator();

                                while(var14.hasNext()) {
                                    modifier = (VaultGearModifier)var14.next();
                                    if (data.removeModifier(modifier)) {
                                        data.updateAttribute(ModGearAttributes.PREFIXES, Math.max(0, (Integer)data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - 1));
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
                scrap.shrink(this.getRecipeInput().getMainInput().getCount());
                container.getScrapSlot().set(scrap);
                
                ItemStack gold = container.getBronzeSlot().getItem();
                ItemStack secondInput = this.getRecipeInput().getSecondInput();
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
                
                Level level = this.getLevel();
                if (level != null) {
                    if (broken) {
                        level.playSound((Player)null, container.getTilePos(), ModSounds.JEWEL_CUT, SoundSource.BLOCKS, 0.8F, level.random.nextFloat() * 0.1F + 0.9F);
                    } else {
                        if (chipped) {
                            level.playSound((Player)null, container.getTilePos(), ModSounds.JEWEL_CUT, SoundSource.BLOCKS, 0.3F, level.random.nextFloat() * 0.1F + 0.7F);
                        } else {
                            level.playSound((Player)null, container.getTilePos(), ModSounds.JEWEL_CUT, SoundSource.BLOCKS, 0.3F, level.random.nextFloat() * 0.1F + 0.7F);
                            level.playSound((Player)null, container.getTilePos(), ModSounds.JEWEL_CUT_SUCCESS, SoundSource.BLOCKS, 0.2F, level.random.nextFloat() * 0.1F + 0.9F);
                        }

                        data.write(stack);
                        container.getJewelInputSlot().set(stack);
                    }
                }
            }
        }
    }

    @Shadow
    public abstract VaultJewelCuttingConfig.JewelCuttingRange getJewelCuttingRange();

    @Shadow
    protected abstract void breakJewel();

    @Shadow
    public abstract VaultJewelCuttingConfig.JewelCuttingInput getRecipeInput();

    @Shadow
    public abstract VaultJewelCuttingConfig.JewelCuttingOutput getRecipeOutput();

    @Shadow
    @Final
    private OverSizedInventory inventory;

    public VaultJewelCuttingStationTileEntityMixin(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_)
    {
        super(p_155228_, p_155229_, p_155230_);
    }
    
}
