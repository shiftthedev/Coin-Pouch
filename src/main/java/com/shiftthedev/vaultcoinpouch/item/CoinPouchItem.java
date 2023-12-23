package com.shiftthedev.vaultcoinpouch.item;

import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.MOD_ID;

public class CoinPouchItem extends Item {

    public CoinPouchItem(String id) {
        super(new Item.Properties().stacksTo(1).tab(ModItems.VAULT_MOD_GROUP));
        this.setRegistryName(id);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> tooltip, TooltipFlag p_41424_) {
        super.appendHoverText(stack, p_41422_, tooltip, p_41424_);
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".info").withStyle(ChatFormatting.GRAY));

        ItemStack[] contained = getContainedStacks(stack);

        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".bronze", contained[0].getCount()).withStyle(Style.EMPTY.withColor(14712607)));
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".silver", contained[1].getCount()).withStyle(Style.EMPTY.withColor(12632256)));
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".gold", contained[2].getCount()).withStyle(ChatFormatting.GOLD));
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".platinum", contained[3].getCount()).withStyle(Style.EMPTY.withColor(16119285)));

        if (AttributeGearData.read(stack).has(ModGearAttributes.SOULBOUND)) {
            tooltip.add(new TextComponent(""));
            tooltip.add(new TextComponent(ModGearAttributes.SOULBOUND.getReader().getModifierName()).withStyle(ModGearAttributes.SOULBOUND.getReader().getColoredTextStyle()));
        }
    }

    public static boolean hasEnoughCoins(ItemStack pouch, ItemStack currency) {
        ItemStack[] contained = getContainedStacks(pouch);
        for (ItemStack itemStack : contained) {
            if (itemStack.getItem().asItem() == currency.getItem().asItem()) {
                return itemStack.getCount() >= currency.getCount();
            }
        }

        return false;
    }

    
    public static int extractCoins(ItemStack pouch, ItemStack currency) {
        ItemStack[] contained = getContainedStacks(pouch);
        int required = currency.getCount();

        for (int i = 0; i < contained.length; i++) {
            ItemStack itemStack = contained[i];
            if (itemStack.getItem().asItem() == currency.getItem().asItem()) {

                int toReduce = Math.min(required, itemStack.getCount());
                itemStack.setCount(itemStack.getCount() - toReduce);
                setContainedStack(pouch, i, itemStack);

                if (i == 0) {
                    compactUp(1, 3, itemStack.getCount(), pouch, contained);
                } else if (i == 1) {
                    compactUp(2, 2, itemStack.getCount(), pouch, contained);
                    compactDown(0, 1, itemStack.getCount(), pouch, contained);
                } else if (i == 2) {
                    compactUp(3, 1, itemStack.getCount(), pouch, contained);
                    compactDown(1, 2, itemStack.getCount(), pouch, contained);
                } else if (i == 3) {
                    compactDown(2, 3, itemStack.getCount(), pouch, contained);
                }

                required -= toReduce;
                break;
            }
        }

        return required;
    }

    private static void compactUp(int start, int range, int count, ItemStack pouch, ItemStack[] contained) {
        ItemStack stack = contained[start];
        int stackCount = Mth.intFloorDiv(count, 9);
        
        if(stackCount != stack.getCount()){
            stack.setCount(stackCount);
            setContainedStack(pouch, start, stack);
        }
        
        if(range > 1){
            compactUp(start + 1, range - 1, stackCount, pouch, contained);
        }
    }

    private static void compactDown(int start, int range, int count, ItemStack pouch, ItemStack[] contained) {
        ItemStack stack = contained[start];
        int stackCount = stack.getCount();
        stackCount -= Mth.intFloorDiv(stackCount, 9) * 9;
        stackCount += count * 9;

        if (stackCount != stack.getCount()) {
            stack.setCount(stackCount);
            setContainedStack(pouch, start, stack);
        }

        if (range > 1) {
            compactDown(start - 1, range - 1, stackCount, pouch, contained);
        }
    }

    
    public static ItemStack[] getContainedStacks(ItemStack pouch) {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");

        int bronzeCount = invTag.contains("BronzeStackSize") ? invTag.getInt("BronzeStackSize") : 0;
        int silverCount = invTag.contains("SilverStackSize") ? invTag.getInt("SilverStackSize") : 0;
        int goldCount = invTag.contains("GoldStackSize") ? invTag.getInt("GoldStackSize") : 0;
        int platinumCount = invTag.contains("PlatinumStackSize") ? invTag.getInt("PlatinumStackSize") : 0;
        return new ItemStack[]{
                new ItemStack(ModBlocks.BRONZE_COIN_PILE, bronzeCount),
                new ItemStack(ModBlocks.SILVER_COIN_PILE, silverCount),
                new ItemStack(ModBlocks.GOLD_COIN_PILE, goldCount),
                new ItemStack(ModBlocks.PLATINUM_COIN_PILE, platinumCount)
        };
    }

    public static void setContainedStack(ItemStack pouch, int slot, ItemStack contained) {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");

        switch (slot) {
            case 0 -> {
                invTag.putInt("BronzeStackSize", contained.getCount());
            }
            case 1 -> {
                invTag.putInt("SilverStackSize", contained.getCount());
            }
            case 2 -> {
                invTag.putInt("GoldStackSize", contained.getCount());
            }
            case 3 -> {
                invTag.putInt("PlatinumStackSize", contained.getCount());
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer) {
            int pouchSlot;
            if (hand == InteractionHand.OFF_HAND) {
                pouchSlot = 40;
            } else {
                pouchSlot = player.getInventory().selected;
            }

            NetworkHooks.openGui((ServerPlayer) player, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return new TranslatableComponent("item." + MOD_ID + ".coin_pouch");
                }

                @Nullable
                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
                    return new CoinPouchContainer(windowId, inventory, pouchSlot);
                }
            }, friendlyByteBuf -> {
                friendlyByteBuf.writeInt(pouchSlot);
            });
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem();
    }

    public static NonNullSupplier<IItemHandler> getInventorySupplier(final ItemStack itemStack) {
        return new NonNullSupplier<IItemHandler>() {
            @NotNull
            @Override
            public IItemHandler get() {
                return new CoinHandler(itemStack);
            }
        };
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? LazyOptional.of(CoinPouchItem.getInventorySupplier(stack)).cast() : LazyOptional.empty();
            }
        };
    }
}
