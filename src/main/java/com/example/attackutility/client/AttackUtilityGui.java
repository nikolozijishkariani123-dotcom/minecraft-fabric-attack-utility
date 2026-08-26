package com.example.attackutility.client;

import com.example.attackutility.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AttackUtilityGui extends Screen {
    private TextFieldWidget delayField;
    private ButtonWidget critToggle;
    private ButtonWidget toggleBindButton;
    private ButtonWidget bindingKeyButton;
    private int delayValue;
    private boolean bindingToggleKey = false;
    private String toggleKeyDisplay = "NOT SET";

    public AttackUtilityGui() {
        super(Text.literal("Attack Utility Settings"));
        this.delayValue = ModConfig.attackDelay;
        updateToggleKeyDisplay();
    }

    private void updateToggleKeyDisplay() {
        if (ModConfig.toggleKey == 0) {
            toggleKeyDisplay = "NOT SET";
        } else {
            toggleKeyDisplay = InputUtil.keyCodeToString(ModConfig.toggleKey);
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Delay label
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Attack Delay (ms): " + delayValue), button -> {})
                .dimensions(centerX - 150, centerY - 80, 300, 20)
                .build());

        // Delay input field
        this.delayField = new TextFieldWidget(this.textRenderer, centerX - 150, centerY - 55, 300, 20, Text.literal(""));
        this.delayField.setMaxLength(5);
        this.delayField.setText(String.valueOf(delayValue));
        this.addDrawableChild(this.delayField);

        // Crit toggle button
        this.critToggle = this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Prefer Crits: " + (ModConfig.preferCrits ? "§aON" : "§cOFF")),
                button -> {
                    ModConfig.preferCrits = !ModConfig.preferCrits;
                    button.setMessage(Text.literal("Prefer Crits: " + (ModConfig.preferCrits ? "§aON" : "§cOFF")));
                }
        ).dimensions(centerX - 150, centerY - 10, 300, 20).build());

        // Toggle keybind button
        this.bindingKeyButton = this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Toggle Key: " + toggleKeyDisplay + (bindingToggleKey ? " (PRESS KEY)" : "")),
                button -> {
                    bindingToggleKey = !bindingToggleKey;
                    updateToggleKeyDisplay();
                    if (bindingToggleKey) {
                        button.setMessage(Text.literal("Toggle Key: (PRESS KEY)"));
                    } else {
                        button.setMessage(Text.literal("Toggle Key: " + toggleKeyDisplay));
                    }
                }
        ).dimensions(centerX - 150, centerY + 30, 300, 20).build());

        // Save button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), button -> {
            try {
                int delay = Integer.parseInt(this.delayField.getText());
                if (delay > 0 && delay <= 10000) {
                    ModConfig.attackDelay = delay;
                }
            } catch (NumberFormatException e) {
                // Invalid input, keep old value
            }
            bindingToggleKey = false;
            this.close();
        }).dimensions(centerX - 150, centerY + 70, 300, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Press \\ to open GUI | Click 'Toggle Key' to set custom keybind"), 10, 10, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindingToggleKey && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            ModConfig.toggleKey = keyCode;
            bindingToggleKey = false;
            updateToggleKeyDisplay();
            if (bindingKeyButton != null) {
                bindingKeyButton.setMessage(Text.literal("Toggle Key: " + toggleKeyDisplay));
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (bindingToggleKey) {
            bindingToggleKey = false;
            if (bindingKeyButton != null) {
                bindingKeyButton.setMessage(Text.literal("Toggle Key: " + toggleKeyDisplay));
            }
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }
}
