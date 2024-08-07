package com.shiftthedev.vaultcoinpouch.config;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.shiftthedev.vaultcoinpouch.events.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class ShowConfigCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("coinpouch")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                .then(Commands.literal("open")
                        .executes(commandContext -> {
                            Minecraft.getInstance().tell(() -> {
                                try
                                {
                                    openConfigScreen();
                                    commandContext.getSource().sendSuccess(new TextComponent("Opening Coin Pouch Configs"), true);
                                }
                                catch (Exception e2)
                                {
                                    commandContext.getSource().sendFailure(new TextComponent("Failed to run coin pouch command with error: " + e2.getMessage()));
                                }
                            });
                            return 1;
                        })));
    }

    private static void openConfigScreen() throws CommandSyntaxException
    {
        if (Minecraft.getInstance().player == null)
        {
            throw new CommandSyntaxException(null, new TextComponent("Not in single-player!"));
        }

        Minecraft mc = Minecraft.getInstance();
        mc.mouseHandler.releaseMouse();
        ClientEvents.CONFIG_SCREEN.setup(mc, null);
        mc.setScreen(ClientEvents.CONFIG_SCREEN);
    }
}
