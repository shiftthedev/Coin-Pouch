package com.shiftthedev.vaultcoinpouch.config;

import com.mojang.brigadier.CommandDispatcher;
import com.shiftthedev.vaultcoinpouch.VaultCoinPouch;
import com.shiftthedev.vaultcoinpouch.network.ConfigSyncMessage;
import iskallia.vault.init.ModNetwork;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

public class ReloadConfigCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("coinpouch")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("reload")
                        .executes(commandContext -> {
                            reloadConfig(commandContext.getSource().getEntity());
                            commandContext.getSource().sendSuccess(new TextComponent("Coin Pouch configs reloaded!"), true);
                            return 1;
                        })));
    }

    private static void reloadConfig(Entity sourceEntity)
    {
        if (sourceEntity == null || sourceEntity instanceof ServerPlayer)
        {
            VCPConfig.reloadConfig();
            VaultCoinPouch.LOGGER.info("Syncing reloaded configs to all players");
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ConfigSyncMessage(VCPConfig.GENERAL));
        }
        else
        {
            VCPConfig.reloadConfig();
        }
    }
}
