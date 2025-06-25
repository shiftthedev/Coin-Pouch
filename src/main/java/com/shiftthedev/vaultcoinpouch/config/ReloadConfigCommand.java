package com.shiftthedev.vaultcoinpouch.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.network.ConfigSyncMessage;
import com.shiftthedev.vaultcoinpouch.network.DataSyncMessage;
import com.shiftthedev.vaultcoinpouch.network.NetworkManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

public class ReloadConfigCommand {
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("reload");
        builder.requires(sender -> sender.hasPermission(2));
        register(builder);
        dispatcher.register(Commands.literal("coinpouch").then(builder));
    }

    private static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
        builder.then(Commands.literal("data").executes(ReloadConfigCommand::reloadData));
    }
    
    private static int reloadData(CommandContext<CommandSourceStack> context) {
        Entity sourceEntity = context.getSource().getEntity();
        VCPData.reloadCoins();
        if (sourceEntity == null || sourceEntity instanceof ServerPlayer) {
            VaultCoinPouch.LOGGER.info("Syncing reloaded data to all players");
            NetworkManager.CHANNEL.send(PacketDistributor.ALL.noArg(), new DataSyncMessage());
        }

        context.getSource().sendSuccess(new TextComponent("Coin Pouch configs reloaded!"), true);
        return 1;
    }
}
