package com.shiftthedev.vaultcoinpouch.mixins.artisan;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.gear.modification.GearModificationAction;
import iskallia.vault.gear.modification.GearModificationCost;
import iskallia.vault.init.ModGearAttributes;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;

@Mixin(value = GearModificationAction.class, remap = false)
public abstract class GearModificationActionMixin
{
    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void apply_impl(VaultArtisanStationContainer container, ServerPlayer player, CallbackInfo ci)
    {
        if(VCPConfig.GENERAL.vaultArtisanStationEnabled())
        {
            shift_apply(container, player);
            ci.cancel();
            return;
        }
    }
    
    @Inject(method = "canApply", at = @At("HEAD"), cancellable = true)
    private void canApply_impl(VaultArtisanStationContainer container, Player player, CallbackInfoReturnable<Boolean> cir)
    {
        if(VCPConfig.GENERAL.vaultArtisanStationEnabled())
        {
            cir.setReturnValue(shift_canApply(container, player));
            cir.cancel();
            return;
        }
    }
  
    private void shift_apply(VaultArtisanStationContainer container, ServerPlayer player)
    {
        if (shift_canApply(container, player))
        {
            ItemStack gear = container.getGearInputSlot().getItem();
            VaultGearData data = VaultGearData.read(gear);
            Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);
            if (!potential.isEmpty())
            {
                Slot inSlot = this.getCorrespondingSlot(container);
                if (inSlot != null)
                {
                    ItemStack input = inSlot.getItem();
                    ItemStack material = input.copy();
                    input.shrink(1);
                    inSlot.set(input);
                    String rollType = (String) data.get(ModGearAttributes.GEAR_ROLL_TYPE, VaultGearAttributeTypeMerger.firstNonNull());
                    GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), rollType, data.getItemLevel(), (Integer) potential.get(), this.modification());
                    ItemStack plating = container.getPlatingSlot().getItem();
                    plating.shrink(cost.costPlating());
                    container.getPlatingSlot().set(plating);

                    ItemStack bronze = container.getBronzeSlot().getItem();
                    int bronzeRemaining = cost.costBronze();
                    int bronzeToTake = Math.min(bronzeRemaining, bronze.getCount());
                    bronze.shrink(bronzeToTake);
                    container.getBronzeSlot().set(bronze);

                    // Coin Pouch remove
                    bronzeRemaining -= bronzeToTake;
                    if (bronzeRemaining > 0)
                    {
                        NonNullList<ItemStack> pouchStacks = NonNullList.create();
                        Iterator it = player.getInventory().items.iterator();
                        while (it.hasNext())
                        {
                            if (bronzeRemaining <= 0)
                            {
                                break;
                            }

                            ItemStack plStack = (ItemStack) it.next();
                            if (ShiftInventoryUtils.isEqualCrafting(plStack, bronze))//plStack.is(ModBlocks.VAULT_BRONZE))
                            {
                                bronzeToTake = Math.min(bronzeRemaining, plStack.getCount());
                                plStack.shrink(bronzeToTake);
                                bronzeRemaining -= bronzeToTake;
                            }

                            if (plStack.is(VCPRegistry.COIN_POUCH))
                            {
                                pouchStacks.add(plStack);
                            }
                        }

                        it = pouchStacks.iterator();
                        while (it.hasNext())
                        {
                            if (bronzeRemaining <= 0)
                            {
                                break;
                            }

                            ItemStack pouchStack = (ItemStack) it.next();
                            bronzeToTake = Math.min(bronzeRemaining, CoinPouchItem.getCoinCount(pouchStack));
                            CoinPouchItem.extractCoins(pouchStack, bronzeToTake);
                            bronzeRemaining -= bronzeToTake;
                        }
                    }
                    // End of Coin Pouch remove

                    this.modification().apply(gear, material, player, rand);
                }
            }
        }
    }

    public boolean shift_canApply(VaultArtisanStationContainer container, Player player)
    {
        Slot inSlot = this.getCorrespondingSlot(container);
        if (inSlot == null)
        {
            return false;
        }
        else
        {
            ItemStack gear = container.getGearInputSlot().getItem();
            ItemStack in = inSlot.getItem();
            if (!in.isEmpty() && !gear.isEmpty())
            {
                VaultGearData data = VaultGearData.read(gear);
                Optional<Integer> potential = data.getFirstValue(ModGearAttributes.CRAFTING_POTENTIAL);
                if (potential.isEmpty())
                {
                    return false;
                }
                else
                {
                    String rollType = (String) data.get(ModGearAttributes.GEAR_ROLL_TYPE, VaultGearAttributeTypeMerger.firstNonNull());
                    GearModificationCost cost = GearModificationCost.getCost(data.getRarity(), rollType, data.getItemLevel(), (Integer) potential.get(), this.modification());

                    // Coin Pouch check
                    ItemStack plating = container.getPlatingSlot().getItem();
                    if (plating.getCount() < cost.costPlating())
                    {
                        return false;
                    }

                    int bronzeMissing = cost.costBronze();
                    ItemStack bronze = container.getBronzeSlot().getItem();
                    bronzeMissing -= bronze.getCount();
                    if (bronzeMissing <= 0)
                    {
                        return this.modification().canApply(gear, in, player, rand);
                    }

                    Iterator it = player.getInventory().items.iterator();
                    while (it.hasNext())
                    {
                        ItemStack plStack = (ItemStack) it.next();
                        if (plStack.is(VCPRegistry.COIN_POUCH))
                        {
                            bronzeMissing -= CoinPouchItem.getCoinCount(plStack);
                        }
                        else if (ShiftInventoryUtils.isEqualCrafting(plStack, bronze))//plStack.is(ModBlocks.BRONZE_COIN_PILE.asItem()))
                        {
                            bronzeMissing -= plStack.getCount();
                        }

                        if (bronzeMissing <= 0)
                        {
                            break;
                        }
                    }

                    return bronzeMissing <= 0 && this.modification().canApply(gear, in, player, rand);
                    // End of Coin Pouch check
                }
            }
            else
            {
                return false;
            }
        }
    }

    @Shadow
    @Nullable
    public abstract Slot getCorrespondingSlot(VaultArtisanStationContainer container);

    @Shadow
    public abstract GearModification modification();

    @Shadow
    @Final
    private static Random rand;
}
