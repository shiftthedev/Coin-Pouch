package com.shiftthedev.vaultcoinpouch.item;

import com.shiftthedev.vaultcoinpouch.config.VCPConfig;
import com.shiftthedev.vaultcoinpouch.container.CoinPouchContainer;
import iskallia.vault.block.CoinPileDecorBlock;
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
import net.minecraft.world.item.BlockItem;
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

import java.util.List;

import static com.shiftthedev.vaultcoinpouch.VaultCoinPouch.MOD_ID;

public class CoinPouchItem extends Item
{

    public CoinPouchItem(String id)
    {
        super(new Item.Properties().stacksTo(1).tab(ModItems.VAULT_MOD_GROUP));
        this.setRegistryName(id);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level p_41422_, List<Component> tooltip, TooltipFlag p_41424_)
    {
        super.appendHoverText(stack, p_41422_, tooltip, p_41424_);
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".info").withStyle(ChatFormatting.GRAY));

        ItemStack[] contained = getContainedStacks(stack);

        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".bronze", contained[0].getCount()).withStyle(Style.EMPTY.withColor(14712607)));
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".silver", contained[1].getCount()).withStyle(Style.EMPTY.withColor(12632256)));
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".gold", contained[2].getCount()).withStyle(ChatFormatting.GOLD));
        tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".platinum", contained[3].getCount()).withStyle(Style.EMPTY.withColor(16119285)));

        if (!VCPConfig.GENERAL.soulboundEnabled())
        {
            return;
        }
        if (AttributeGearData.read(stack).has(ModGearAttributes.SOULBOUND))
        {
            tooltip.add(new TextComponent("\n" + ModGearAttributes.SOULBOUND.getReader().getModifierName()).withStyle(ModGearAttributes.SOULBOUND.getReader().getColoredTextStyle()));
        }
        else
        {
            tooltip.add(new TranslatableComponent("tooltip." + MOD_ID + ".soulbound").withStyle(ChatFormatting.GRAY));
        }
    }

    public static int getCoinCount(ItemStack pouch)
    {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        return invTag.contains("BronzeStackSize") ? invTag.getInt("BronzeStackSize") : 0;
    }

    public static int extractCoins(ItemStack pouch, int currency)
    {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        int bronzeCount = invTag.contains("BronzeStackSize") ? invTag.getInt("BronzeStackSize") : 0;
        int left = bronzeCount - currency;
        if (left >= 0)
        {
            setContainedStack(pouch, 0, left);
            return currency;
        }

        setContainedStack(pouch, 0, 0);
        return currency - bronzeCount;
    }

    public static int getCoinCount(ItemStack pouch, ItemStack coin)
    {
        if (!(coin.getItem() instanceof BlockItem blockItem))
        {
            return 0;
        }
        if (!(blockItem.getBlock() instanceof CoinPileDecorBlock))
        {
            return 0;
        }

        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        if (blockItem.getBlock() == ModBlocks.BRONZE_COIN_PILE)
        {
            return invTag.contains("BronzeStackSize") ? invTag.getInt("BronzeStackSize") : 0;
        }
        if (blockItem.getBlock() == ModBlocks.SILVER_COIN_PILE)
        {
            return invTag.contains("SilverStackSize") ? invTag.getInt("SilverStackSize") : 0;
        }
        if (blockItem.getBlock() == ModBlocks.GOLD_COIN_PILE)
        {
            return invTag.contains("GoldStackSize") ? invTag.getInt("GoldStackSize") : 0;
        }
        if (blockItem.getBlock() == ModBlocks.PLATINUM_COIN_PILE)
        {
            return invTag.contains("PlatinumStackSize") ? invTag.getInt("PlatinumStackSize") : 0;
        }

        return 0;
    }

    public static void extractCoins(ItemStack pouch, ItemStack coin, int currency)
    {
        if (!(coin.getItem() instanceof BlockItem blockItem))
        {
            return;
        }
        if (!(blockItem.getBlock() instanceof CoinPileDecorBlock))
        {
            return;
        }

        int coinAmount = blockItem.getBlock() == ModBlocks.BRONZE_COIN_PILE ? currency :
                blockItem.getBlock() == ModBlocks.SILVER_COIN_PILE ? currency * 9 :
                        blockItem.getBlock() == ModBlocks.GOLD_COIN_PILE ? currency * 81 :
                                blockItem.getBlock() == ModBlocks.PLATINUM_COIN_PILE ? currency * 729 : 0;

        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        int bronzeCount = invTag.contains("BronzeStackSize") ? invTag.getInt("BronzeStackSize") : 0;
        int left = bronzeCount - coinAmount;

        if (left >= 0)
        {
            setContainedStack(pouch, 0, left);
            return;
        }

        setContainedStack(pouch, 0, 0);
    }

    public static ItemStack[] getContainedStacks(ItemStack pouch)
    {
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

    public static void setContainedStack(ItemStack pouch, int slot, int count)
    {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");

        switch (slot)
        {
            case 0 ->
            {
                invTag.putInt("BronzeStackSize", count);
                invTag.putInt("SilverStackSize", Mth.intFloorDiv(count, 9));
                invTag.putInt("GoldStackSize", Mth.intFloorDiv(count, 81));
                invTag.putInt("PlatinumStackSize", Mth.intFloorDiv(count, 729));
            }
            case 1 ->
            {
                int newCount = invTag.getInt("BronzeStackSize");

                newCount -= Mth.intFloorDiv(newCount, 9) * 9;
                newCount += count * 9;

                setContainedStack(pouch, 0, newCount);
            }
            case 2 ->
            {
                int newCount = invTag.getInt("BronzeStackSize");

                newCount -= Mth.intFloorDiv(newCount, 81) * 81;
                newCount += count * 81;

                setContainedStack(pouch, 0, newCount);
            }
            case 3 ->
            {
                int newCount = invTag.getInt("BronzeStackSize");

                newCount -= Mth.intFloorDiv(newCount, 729) * 729;
                newCount += count * 729;

                setContainedStack(pouch, 0, newCount);
            }
        }
    }

    public static void setContainedStackFromGUI(ItemStack pouch, int slot, ItemStack stack)
    {
        CompoundTag invTag = pouch.getOrCreateTagElement("Inventory");
        switch (slot)
        {
            case 0 ->
            {
                invTag.putInt("BronzeStackSize", stack.getCount());
            }
            case 1 ->
            {
                invTag.putInt("SilverStackSize", stack.getCount());
            }
            case 2 ->
            {
                invTag.putInt("GoldStackSize", stack.getCount());
            }
            case 3 ->
            {
                invTag.putInt("PlatinumStackSize", stack.getCount());
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer)
        {
            int pouchSlot;
            if (hand == InteractionHand.OFF_HAND)
            {
                pouchSlot = 40;
            }
            else
            {
                pouchSlot = player.getInventory().selected;
            }

            openGUI(player, pouchSlot);
        }

        return InteractionResultHolder.pass(stack);
    }

    public static void openGUI(Player player, int pouchSlot)
    {
        NetworkHooks.openGui((ServerPlayer) player, new MenuProvider()
        {
            @Override
            public Component getDisplayName()
            {
                return new TranslatableComponent("item." + MOD_ID + ".coin_pouch");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
            {
                return new CoinPouchContainer(windowId, inventory, pouchSlot);
            }
        }, friendlyByteBuf -> {
            friendlyByteBuf.writeInt(pouchSlot);
        });
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return oldStack.getItem() != newStack.getItem();
    }

    public static NonNullSupplier<IItemHandler> getInventorySupplier(final ItemStack itemStack)
    {
        return new NonNullSupplier<IItemHandler>()
        {
            @NotNull
            @Override
            public IItemHandler get()
            {
                return new Handler(itemStack);
            }
        };
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new ICapabilityProvider()
        {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
            {
                return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? LazyOptional.of(CoinPouchItem.getInventorySupplier(stack)).cast() : LazyOptional.empty();
            }
        };
    }

    public static class Handler extends ItemStackHandler
    {
        protected final ItemStack delegate;

        public Handler(ItemStack delegate)
        {
            super();
            this.delegate = delegate;

            ItemStack[] containedStacks = CoinPouchItem.getContainedStacks(this.delegate);
            setSize(containedStacks.length);
            for (int i = 0; i < containedStacks.length; i++)
            {
                this.stacks.set(i, containedStacks[i]);
            }
        }

        @Override
        protected void onContentsChanged(int slot)
        {
            //super.onContentsChanged(slot);
            CoinPouchItem.setContainedStack(this.delegate, slot, this.getStackInSlot(slot).getCount());
        }

        protected void onGuiContentChanged(int slot)
        {
            //super.onContentsChanged(slot);
            CoinPouchItem.setContainedStackFromGUI(this.delegate, slot, this.getStackInSlot(slot));
        }

        public void setStackInSlotGUI(int slot, @NotNull ItemStack stack)
        {
            validateSlotIndex(slot);
            this.stacks.set(slot, stack);
            this.onGuiContentChanged(slot);
        }

        public ItemStack extractItemGUI(int slot, int amount, boolean simulate)
        {
            if (amount == 0)
            {
                return ItemStack.EMPTY;
            }

            validateSlotIndex(slot);

            ItemStack existing = this.stacks.get(slot);

            if (existing.isEmpty())
            {
                return ItemStack.EMPTY;
            }

            int toExtract = Math.min(amount, existing.getMaxStackSize());

            if (existing.getCount() <= toExtract)
            {
                if (!simulate)
                {
                    this.stacks.set(slot, ItemStack.EMPTY);
                    onGuiContentChanged(slot);
                    return existing;
                }
                else
                {
                    return existing.copy();
                }
            }
            else
            {
                if (!simulate)
                {
                    this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                    onGuiContentChanged(slot);
                }

                return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
            }
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return switch (slot)
            {
                case 0 -> 2147483582;
                case 1 -> 238609286;
                case 2 -> 26512142;
                case 3 -> 2945793;
                default -> 0;
            };
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack)
        {
            return this.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack)
        {
            return (stack.getItem().asItem() == ModBlocks.BRONZE_COIN_PILE.asItem() && slot == 0) ||
                    (stack.getItem().asItem() == ModBlocks.SILVER_COIN_PILE.asItem() && slot == 1) ||
                    (stack.getItem().asItem() == ModBlocks.GOLD_COIN_PILE.asItem() && slot == 2) ||
                    (stack.getItem().asItem() == ModBlocks.PLATINUM_COIN_PILE.asItem() && slot == 3);
        }
    }
}
