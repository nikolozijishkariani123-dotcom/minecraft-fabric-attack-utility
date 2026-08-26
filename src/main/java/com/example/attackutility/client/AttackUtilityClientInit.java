package com.example.attackutility.client;

import com.example.attackutility.features.AutoAttackFeature;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import com.example.attackutility.config.ModConfig;

public class AttackUtilityClientInit implements ClientModInitializer {
    public static KeyBinding toggleBinding;
    public static KeyBinding openGuiBinding;

    @Override
    public void onInitializeClient() {
        toggleBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.attackutility.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.attackutility.keys"
        ));

        openGuiBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.attackutility.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.attackutility.keys"
        ));

        AutoAttackFeature.init();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleBinding.wasPressed()) {
                ModConfig.enabled = !ModConfig.enabled;
                if (client.player != null) {
                    client.player.sendMessage(
                            net.minecraft.text.Text.literal("Attack Utility: " + (ModConfig.enabled ? "§aON" : "§cOFF")),
                            true
                    );
                }
            }

            while (openGuiBinding.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new AttackUtilityGui());
                }
            }
        });
    }
}
