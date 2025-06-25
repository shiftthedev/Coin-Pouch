package com.shiftthedev.vaultcoinpouch.mixins;

import com.shiftthedev.vaultcoinpouch.config.CoinData;
import com.shiftthedev.vaultcoinpouch.config.VCPData;
import iskallia.vault.container.inventory.CoinPouchContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.CoinPouchItem;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = CoinPouchItem.class, remap = false)
public abstract class CoinPouchItemMixin {
    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static int getCoinCount(ItemStack pouch) {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        CoinData data = VCPData.getCoinData(0);
        if(data == null) {
            return 0;
        }

        return invTag.contains(data.coin_id) ? invTag.getInt(data.coin_id) : 0;
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static boolean extractCoins(ItemStack pouch, int coinAmount) {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        CoinData data = VCPData.getCoinData("the_vault:vault_bronze");
        if (data == null)
        {
            return false;
        }
        
        int bronzeCount = invTag.contains(data.coin_id) ? invTag.getInt(data.coin_id) : 0;
        int left = bronzeCount - coinAmount;
        if (left >= 0) {
            setContainedStack(pouch, data.index, left);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static ItemStack[] getContainedStacks(ItemStack pouch) {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        int count = VCPData.getCoinDataCount();
        if (count == 0) {
            return new ItemStack[] {};
        }

        ItemStack[] stacks = new ItemStack[count];
        for (int i = 0; i < count; i++)
        {
            CoinData data = VCPData.getCoinData(i);
            if (data == null) {
                continue;
            }

            stacks[i] = new ItemStack(Registry.ITEM.get(new ResourceLocation(data.coin_id)), invTag.contains(data.coin_id) ? invTag.getInt(data.coin_id) : 0);
        }

        return stacks;
    }
    
    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static void setContainedStack(ItemStack pouch, int slot, int count) {
        CoinData data = VCPData.getCoinData(slot);
        if (data == null) {
            return;
        }

        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        invTag.putInt(data.coin_id, count);
        if (!data.next_coin_id.isEmpty()) {
            compactUp(invTag, slot + 1, count);
        }

        if (!data.previous_coin_id.isEmpty()) {
            compactDown(data, invTag, slot - 1, count);
        }
    }

    private static void compactUp(CompoundTag invTag, int slot, int count) {
        CoinData data = VCPData.getCoinData(slot);
        if (data == null) {
            return;
        }

        int newCount = Mth.intFloorDiv(count, data.previous_coin_count_to_upgrade);
        invTag.putInt(data.coin_id, newCount);
        if (!data.next_coin_id.isEmpty()) {
            compactUp(invTag, slot + 1, newCount);
        }
    }

    private static void compactDown(CoinData prevData, CompoundTag invTag, int slot, int count) {
        CoinData data = VCPData.getCoinData(slot);
        if (data == null) {
            return;
        }

        int newCount;
        if (!invTag.contains(data.coin_id)) {
            newCount = count * prevData.previous_coin_count_to_upgrade;
        }
        else {
            newCount = invTag.getInt(data.coin_id);
            newCount -= Mth.intFloorDiv(newCount, prevData.previous_coin_count_to_upgrade) * prevData.previous_coin_count_to_upgrade;
            newCount += count * prevData.previous_coin_count_to_upgrade;
        }

        invTag.putInt(data.coin_id, newCount);
        if (!data.previous_coin_id.isEmpty()) {
            compactDown(data, invTag, slot - 1, newCount);
        }
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static void setContainedStackFromGUI(ItemStack pouch, int slot, ItemStack stack) {
        CoinData data = VCPData.getCoinData(slot);
        if (data == null) {
            return;
        }

        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        invTag.putInt(data.coin_id, stack.getCount());
    }
    
    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static int getTotalBronzeValue(Inventory playerInventory) {
        AtomicInteger totalBronzeValue = new AtomicInteger();
        Player player = playerInventory.player;
        CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.COIN_POUCH).ifPresent(slotResult -> {
            totalBronzeValue.addAndGet(getCoinCount(CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.COIN_POUCH).get().stack()));
        });

        for(int slot = 0; slot < playerInventory.getContainerSize(); ++slot) {
            ItemStack stack = playerInventory.getItem(slot);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (item == ModBlocks.VAULT_BRONZE.asItem()) {
                    totalBronzeValue.addAndGet(stack.getCount());
                }

                if (item instanceof CoinPouchItem) {
                    CompoundTag invTag = stack.getOrCreateTagElement("Inventory");
                    CoinData data = VCPData.getCoinData("the_vault:vault_bronze");
                    if (data != null) {
                        int bronzeCount = invTag.contains(data.coin_id) ? invTag.getInt(data.coin_id) : 0;
                        totalBronzeValue.addAndGet(bronzeCount);
                    }
                }
            }
        }

        return totalBronzeValue.get();
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static int getValueFromPouch(ItemStack stack, String tag) {
        int coinValue = 0;
        if (stack.getItem() instanceof CoinPouchItem) {
            CompoundTag invTag = stack.getOrCreateTagElement("Inventory");
            if (tag.equals("BronzeStackSize")) {
                tag = "the_vault:vault_bronze";
            }
            else if (tag.equals("SilverStackSize")) {
                tag = "the_vault:vault_silver";
            }
            else if (tag.equals("GoldStackSize")) {
                tag = "the_vault:vault_gold";
            }
            else if (tag.equals("PlatinumStackSize")) {
                tag = "the_vault:vault_platinum";
            }
            
            int count = invTag.contains(tag) ? invTag.getInt(tag) : 0;
            coinValue += count;
        }

        return coinValue;
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static int getGoldAmount(Inventory playerInventory) {
        AtomicInteger goldAmount = new AtomicInteger();
        Player player = playerInventory.player;

        CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.COIN_POUCH).ifPresent(slotResult -> {
            goldAmount.addAndGet(getCoinCount(CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.COIN_POUCH).get().stack()));
        });

        for(int slot = 0; slot < playerInventory.getContainerSize(); ++slot) {
            ItemStack stack = playerInventory.getItem(slot);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                if (stack.is(ModBlocks.VAULT_GOLD)) {
                    goldAmount.addAndGet(stack.getCount());
                }

                if (item instanceof CoinPouchItem) {
                    CompoundTag invTag = stack.getOrCreateTagElement("Inventory");
                    CoinData data = VCPData.getCoinData("the_vault:vault_gold");
                    if (data != null) {
                        int goldCount = invTag.contains(data.coin_id) ? invTag.getInt(data.coin_id) : 0;
                        goldAmount.addAndGet(goldCount);
                    }
                }
            }
        }

        return goldAmount.get();
    }

    /**
     * @author ShiftTheDev
     * @reason Adding support to extra coin types
     */
    @Overwrite
    public static boolean interceptPlayerInventoryItemAddition(Inventory playerInventory, ItemStack toAdd) {
        if (!isValidItem(toAdd.getItem().getRegistryName().toString())) {
            return false;
        } else {
            Player player = playerInventory.player;
            if (player.containerMenu instanceof CoinPouchContainer) {
                return false;
            } else {
                ItemStack pouchStack = ItemStack.EMPTY;
                if (CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.COIN_POUCH).isPresent())
                {
                    pouchStack = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.COIN_POUCH).get().stack();
                }
                else
                {
                    for (int slot = 0; slot < playerInventory.getContainerSize(); ++slot)
                    {
                        ItemStack invStack = playerInventory.getItem(slot);
                        if (invStack.getItem() instanceof CoinPouchItem)
                        {
                            pouchStack = invStack;
                            break;
                        }
                    }
                }

                return pouchStack.isEmpty() ? false : (Boolean)pouchStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map((handler) -> {
                    ItemStack remainder = toAdd.copy();
                    Optional<CoinData> coinData = VCPData.getCoinDataList().stream().filter(data -> data.coin_id.equals(Registry.ITEM.getKey(toAdd.getItem()).toString())).findFirst();
                    if (coinData.isPresent())
                    {
                        remainder = handler.insertItem(coinData.get().index, toAdd, false);
                    }
                    
                    toAdd.setCount(remainder.getCount());
                    return toAdd.isEmpty();
                }).orElse(false);
            }
        }
    }

    private static boolean isValidItem(String regName) {
        for (CoinData coinData : VCPData.getCoinDataList())
        {
            if (coinData.coin_id.equals(regName)) {
                return true;
            }
        }

        return false;
    }
}
