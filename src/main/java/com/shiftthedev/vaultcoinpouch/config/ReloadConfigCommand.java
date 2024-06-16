package com.shiftthedev.vaultcoinpouch.config;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class ReloadConfigCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("coinpouch")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(0))
                .then(Commands.literal("reload")
                        .executes(commandContext -> {
                            VCPConfig.loadConfig();
                            commandContext.getSource().sendSuccess(new TextComponent("Coin Pouch configs reloaded!"), true);
                            return 1;
                        })));
    }
}
