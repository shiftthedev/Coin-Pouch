package com.shiftthedev.vaultcoinpouch.item;

import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.config.CoinData;
import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import iskallia.vault.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.MOD_ID;

public class CoinPouchItem extends Item {
    public CoinPouchItem(String id) {
        super(new Item.Properties().stacksTo(1).tab(ModItems.VAULT_MOD_GROUP));
        this.setRegistryName(id);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag p_41424_) {
        super.appendHoverText(stack, level, tooltip, p_41424_);
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".info").withStyle(ChatFormatting.GRAY));

        if (!VCPConfig.getCoinDataList().isEmpty()) {
            String[] counts = getCounts(stack);
            int size = Math.min(VCPConfig.getCoinDataCount(), counts.length);
            for (int i = 0; i < size; i++)
            {
                CoinData data = VCPConfig.getCoinData(i);
                if (data == null) {
                    continue;
                }
                
                String nameKey = "block." + data.coin_id.replace(":", ".");
                if (!I18n.exists(nameKey)) {
                    nameKey = "item." + data.coin_id.replace(":", ".");
                }

                tooltip.add(new TextComponent(counts[i]).append(" ").append(new TranslatableComponent(nameKey)).append(new TextComponent(" Coin(s)")).withStyle(Style.EMPTY.withColor(TextColor.parseColor(data.color))));
                
            }
        }

        if (VCPConfig.GENERAL.soulboundEnabled()) {
            VaultCoinPouch.addSoulboundTooltip(stack, tooltip);
        }
    }

    public static int getCoinCount(Inventory inventory) {
        int coins = 0;
        for (ItemStack itemStack : inventory.items) {
            if (itemStack.getItem() instanceof CoinPouchItem) {
                coins += getCoinCount(itemStack);
            }
        }
        return coins;
    }

    public static int getCoinCount(ItemStack pouch) {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        CoinData data = VCPConfig.getCoinData(0);
        if(data == null) {
            return 0;
        }
        
        return invTag.contains(data.coin_id) ? invTag.getInt(data.coin_id) : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static String[] getCounts(ItemStack pouch) {
        ItemStack[] stacks = getContainedStacks(pouch);
        String[] amounts = new String[stacks.length];
        for (int i = 0; i < stacks.length; i++) {
            int count = stacks[i].getCount();
            amounts[i] = Screen.hasShiftDown() ? String.valueOf(count) : VaultCoinPouch.formatCount(count);
        }
        return amounts;
    }

    public static ItemStack[] getContainedStacks(ItemStack pouch) {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        int count = VCPConfig.getCoinDataCount();
        if (count == 0) {
            return new ItemStack[] {};
        }

        ItemStack[] stacks = new ItemStack[count];
        for (int i = 0; i < count; i++)
        {
            CoinData data = VCPConfig.getCoinData(i);
            if (data == null) {
                continue;
            }

            stacks[i] = new ItemStack(Registry.ITEM.get(new ResourceLocation(data.coin_id)), invTag.contains(data.coin_id) ? invTag.getInt(data.coin_id) : 0);
        }
        
        return stacks;
    }

    public static void setContainedStack(ItemStack pouch, int slot, int count) {
        CoinData data = VCPConfig.getCoinData(slot);
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
        CoinData data = VCPConfig.getCoinData(slot);
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
        CoinData data = VCPConfig.getCoinData(slot);
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

    public static void setContainedStackFromGUI(ItemStack pouch, int slot, ItemStack stack) {
        CoinData data = VCPConfig.getCoinData(slot);
        if (data == null) {
            return;
        }
        
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        invTag.putInt(data.coin_id, stack.getCount());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            openGUI(serverPlayer, hand == InteractionHand.OFF_HAND ? 40 : player.getInventory().selected);
        }
        return InteractionResultHolder.pass(stack);
    }

    public static void openGUI(ServerPlayer player, int pouchSlot) {
        NetworkHooks.openGui(player, new MenuProvider() {
            @Override
            public @NotNull Component getDisplayName() {
                return new TranslatableComponent("item." + MOD_ID + ".coin_pouch");
            }
            @Override
            public @NotNull AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
                return new CoinPouchContainer(windowId, inventory, pouchSlot);
            }
        }, friendlyByteBuf -> friendlyByteBuf.writeInt(pouchSlot));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    public static NonNullSupplier<IItemHandler> getInventorySupplier(final ItemStack itemStack) {
        return () -> new Handler(itemStack);
    }

    @Override
    public @NotNull ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                        ? LazyOptional.of(CoinPouchItem.getInventorySupplier(stack)).cast()
                        : LazyOptional.empty();
            }
        };
    }

    public static class Handler extends ItemStackHandler {
        protected final ItemStack delegate;

        public Handler(ItemStack delegate) {
            this.delegate = delegate;
            ItemStack[] containedStacks = CoinPouchItem.getContainedStacks(this.delegate);
            setSize(containedStacks.length);
            for (int i = 0; i < containedStacks.length; i++) {
                this.stacks.set(i, containedStacks[i]);
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            CoinPouchItem.setContainedStack(this.delegate, slot, this.getStackInSlot(slot).getCount());
        }

        protected void onGuiContentChanged(int slot) {
            CoinPouchItem.setContainedStackFromGUI(this.delegate, slot, this.getStackInSlot(slot));
        }

        public void setStackInSlotGUI(int slot, @NotNull ItemStack stack) {
            validateSlotIndex(slot);
            this.stacks.set(slot, stack);
            this.onGuiContentChanged(slot);
        }

        public ItemStack extractItemGUI(int slot, int amount, boolean simulate) {
            if (amount == 0) {
                return ItemStack.EMPTY;
            }

            validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            }

            int toExtract = Math.min(amount, existing.getMaxStackSize());
            if (existing.getCount() <= toExtract) {
                if (!simulate) {
                    this.stacks.set(slot, ItemStack.EMPTY);
                    onGuiContentChanged(slot);
                    return existing;
                } else {
                    return existing.copy();
                }
            } else {
                if (!simulate) {
                    this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                    onGuiContentChanged(slot);
                }
                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            if(slot >= VCPConfig.getCoinDataCount()) {
                return 0;
            }
            
            int size = 2147483097;
            for (int i = 0; i <= slot; i++)
            {
                CoinData data = VCPConfig.getCoinData(i);
                if (data.previous_coin_count_to_upgrade == 0) {
                    continue;
                }
                
                size = Mth.intFloorDiv(size, data.previous_coin_count_to_upgrade);
            }

            return size;
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return this.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            CoinData data = VCPConfig.getCoinData(slot);
            if (data == null) {
                return false;
            }

            return data.coin_id.equals(Registry.ITEM.getKey(stack.getItem()).toString());
        }
    }
}
