package com.shiftthedev.vaultcoinpouch.network;

import com.shiftthedev.vaultcoinpouch.utils.ShiftInventoryUtils;
import iskallia.vault.block.entity.AlchemyTableTileEntity;
import iskallia.vault.config.AlchemyTableConfig;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.bottle.BottleEffect;
import iskallia.vault.item.bottle.BottleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class ShiftAlchemyTableEffectCraftMessage
{
    private final BlockPos pos;
    private final String effectId;

    public ShiftAlchemyTableEffectCraftMessage(BlockPos pos, @Nullable String effectId)
    {
        this.pos = pos;
        this.effectId = effectId;
    }

    public static void encode(ShiftAlchemyTableEffectCraftMessage message, FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(message.pos);
        buffer.writeOptional(Optional.ofNullable(message.effectId), FriendlyByteBuf::writeUtf);
    }

    public static ShiftAlchemyTableEffectCraftMessage decode(FriendlyByteBuf buffer)
    {
        BlockPos pos = buffer.readBlockPos();
        String effectId = (String) buffer.readOptional(FriendlyByteBuf::readUtf).orElse(null);
        return new ShiftAlchemyTableEffectCraftMessage(pos, effectId);
    }

    public static void handle(ShiftAlchemyTableEffectCraftMessage message, Supplier<NetworkEvent.Context> contextSupplier)
    {
        NetworkEvent.Context context = (NetworkEvent.Context) contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            BlockPos pos = message.pos;
            BlockEntity tile = player.getLevel().getBlockEntity(pos);
            if (tile instanceof AlchemyTableTileEntity alchemyTableTile)
            {
                ItemStack input = alchemyTableTile.getInventory().getItem(0);
                if (!input.isEmpty())
                {
                    AlchemyTableConfig cfg = ModConfigs.VAULT_ALCHEMY_TABLE;
                    ItemStack inputCopy = input.copy();
                    List<ItemStack> cost = new ArrayList();
                    AlchemyTableConfig.CraftableEffectConfig effectConfig = cfg.getConfig(message.effectId);
                    if (effectConfig != null)
                    {
                        if (effectConfig.hasPrerequisites(player))
                        {
                            Optional<BottleItem.Type> var10000 = BottleItem.getType(input);
                            if (var10000.isPresent())
                            {
                                Objects.requireNonNull(effectConfig);
                                BottleEffect createdEffect = effectConfig.createEffect(var10000.get()).orElse(null);
                                if (createdEffect != null)
                                {
                                    cost.addAll(effectConfig.createCraftingCost(inputCopy));
                                    List<ItemStack> missing = ShiftInventoryUtils.getMissingInputs(cost, player.getInventory());
                                    if (missing.isEmpty())
                                    {
                                        if (ShiftInventoryUtils.consumeInputs(cost, player.getInventory(), true))
                                        {
                                            if (ShiftInventoryUtils.consumeInputs(cost, player.getInventory(), false))
                                            {
                                                alchemyTableTile.startCrafting();
                                                BottleItem.setEffect(input, createdEffect);
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
