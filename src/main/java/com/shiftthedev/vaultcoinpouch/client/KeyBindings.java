package com.shiftthedev.vaultcoinpouch.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings
{
    public static final KeyMapping OPEN_POUCH = new KeyMapping(
            "key.vaultcoinpouch.showpouch",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "key.categories.vaultcoinpouch"
    );
    
    public static void init()
    {
        ClientRegistry.registerKeyBinding(OPEN_POUCH);
    }
}
