package com.shiftthedev.vaultcoinpouch.helpers;

import com.shiftthedev.vaultcoinpouch.VCPRegistry;
import com.shiftthedev.vaultcoinpouch.item.CoinPouchItem;
import iskallia.vault.block.TransmogTableBlock;
import iskallia.vault.container.TransmogTableContainer;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.network.message.transmog.TransmogButtonMessage;
import iskallia.vault.world.data.DiscoveredModelsData;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

public class TransmogTableHelper
{
    public static void Handle(TransmogButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = (NetworkEvent.Context) contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            AbstractContainerMenu patt1481$temp = sender.containerMenu;
            if (patt1481$temp instanceof TransmogTableContainer container)
            {
                if (sender != null && !container.getPreviewItemStack().isEmpty() && container.priceFulfilled())
                {
                    Slot gearSlot = container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(0));
                    Slot bronzeSlot = container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(1));
                    Slot outputSlot = container.getSlot(container.getInternalInventoryIndexRange().getContainerIndex(container.getInternalInventory().outputSlotIndex()));
                    int copperCost = container.copperCost();
                    DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get((ServerLevel) sender.level);
                    Set<ResourceLocation> discoveredModels = discoveredModelsData.getDiscoveredModels(sender.getUUID());
                    if (!TransmogTableBlock.canTransmogModel(sender, discoveredModels, container.getSelectedModelId()))
                    {
                        return;
                    }

                    ItemStack resultingStack = gearSlot.getItem().copy();
                    VaultGearData gearData = VaultGearData.read(resultingStack);
                    gearData.updateAttribute(ModGearAttributes.GEAR_MODEL, container.getSelectedModelId());
                    gearData.write(resultingStack);
                    gearSlot.set(ItemStack.EMPTY);
                    ItemStack bronze = bronzeSlot.getItem().copy();

                    int deductedAmount = Math.min(copperCost, bronze.getCount());
                    bronze.shrink(deductedAmount);

                    // Coin Pouch remove
                    copperCost -= deductedAmount;
                    if (copperCost > 0)
                    {
                        NonNullList<ItemStack> pouchStacks = NonNullList.create();
                        NonNullList<ItemStack> playerItems = sender.getInventory().items;
                        Iterator it = playerItems.iterator();

                        while (it.hasNext())
                        {
                            ItemStack plStack = (ItemStack) it.next();
                            if (copperCost <= 0)
                            {
                                break;
                            }

                            if (plStack.is(ModBlocks.VAULT_BRONZE))
                            {
                                deductedAmount = Math.min(copperCost, plStack.getCount());
                                plStack.shrink(deductedAmount);
                                copperCost -= deductedAmount;
                            }

                            if (plStack.is(VCPRegistry.COIN_POUCH))
                            {
                                pouchStacks.add(plStack);
                            }
                        }

                        it = pouchStacks.iterator();
                        while (it.hasNext())
                        {
                            if (copperCost <= 0)
                            {
                                break;
                            }

                            ItemStack pouchStack = (ItemStack) it.next();
                            deductedAmount = Math.min(copperCost, CoinPouchItem.getCoinCount(pouchStack));
                            CoinPouchItem.extractCoins(pouchStack, deductedAmount);
                            copperCost -= deductedAmount;
                        }
                    }
                    // End of Coin Pouch remove

                    container.getInternalInventory().setItem(1, bronze);
                    outputSlot.set(resultingStack);
                    container.broadcastChanges();
                }
            }

        });
        context.setPacketHandled(true);
    }

    public static boolean PriceFulfilled(int remaining, Slot bronzeSlot, Player player)
    {
        if (bronzeSlot.hasItem() && bronzeSlot.getItem().is(ModBlocks.VAULT_BRONZE))
        {
            remaining -= bronzeSlot.getItem().getCount();
            if (remaining <= 0)
            {
                return true;
            }
        }

        NonNullList<ItemStack> playerItems = player.getInventory().items;
        Iterator it = playerItems.iterator();

        while (it.hasNext())
        {
            ItemStack plStack = (ItemStack) it.next();
            if (remaining <= 0)
            {
                return true;
            }

            if (plStack.is(ModBlocks.VAULT_BRONZE))
            {
                remaining -= plStack.getCount();
            }

            if (plStack.is(VCPRegistry.COIN_POUCH))
            {
                remaining -= CoinPouchItem.getCoinCount(plStack);
            }
        }

        return remaining <= 0;
    }
}
