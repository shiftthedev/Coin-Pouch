package com.shiftthedev.vaultcoinpouch.network;

import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.event.event.ForgeGearEvent;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ForgeParticleMessage;
import iskallia.vault.util.SidedHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ShiftVaultForgeRequestCraftMessage
{
    private final ResourceLocation recipe;
    private final int level;

    public ShiftVaultForgeRequestCraftMessage(ResourceLocation recipe, int level) {
        this.recipe = recipe;
        this.level = level;
    }

    public static void encode(ShiftVaultForgeRequestCraftMessage message, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(message.recipe);
        buffer.writeInt(message.level);
    }

    public static ShiftVaultForgeRequestCraftMessage decode(FriendlyByteBuf buffer) {
        return new ShiftVaultForgeRequestCraftMessage(buffer.readResourceLocation(), buffer.readInt());
    }

    public static void handle(ShiftVaultForgeRequestCraftMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = (NetworkEvent.Context)contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer requester = context.getSender();
            if (requester != null) {
                AbstractContainerMenu patt2079$temp = requester.containerMenu;
                if (patt2079$temp instanceof ForgeRecipeContainer) {
                    ForgeRecipeContainer container = (ForgeRecipeContainer)patt2079$temp;
                    if (!container.getResultSlot().getItem().isEmpty()) {
                        return;
                    }

                    ForgeRecipeTileEntity tile = container.getTile();
                    if (tile == null) {
                        return;
                    }

                    VaultForgeRecipe recipe = null;
                    ForgeRecipeType[] var6 = tile.getSupportedRecipeTypes();
                    int var7 = var6.length;

                    for(int var8 = 0; var8 < var7; ++var8) {
                        ForgeRecipeType type = var6[var8];
                        VaultForgeRecipe found = type.getRecipe(message.recipe);
                        if (found != null && found.canCraft(requester)) {
                            recipe = found;
                            break;
                        }
                    }

                    if (recipe == null) {
                        return;
                    }

                    Inventory playerInventory = requester.getInventory();
                    OverSizedInventory tileInventory = tile.getInventory();
                    List<OverSizedItemStack> consumed = new ArrayList();
                    if (ShiftInventoryUtils.consumeInputs(recipe.getInputs(), playerInventory, tileInventory, true) && ShiftInventoryUtils.consumeInputs(recipe.getInputs(), playerInventory, tileInventory, false, consumed)) {
                        int level = Mth.clamp(message.level, 0, Math.min(ModConfigs.LEVELS_META.getMaxLevel(), SidedHelper.getVaultLevel(requester)));
                        container.getResultSlot().set(recipe.createOutput(consumed, requester, level));
                        requester.level.levelEvent(1030, tile.getBlockPos(), 0);
                        container.broadcastChanges();
                        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ForgeParticleMessage(tile.getBlockPos()));
                        MinecraftForge.EVENT_BUS.post(new ForgeGearEvent(requester, recipe));
                    }

                    return;
                }
            }

        });
        context.setPacketHandled(true);
    }
}
