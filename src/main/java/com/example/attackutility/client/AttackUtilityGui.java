package com.example.attackutility.client;

import com.example.attackutility.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AttackUtilityGui extends Screen {
    private TextFieldWidget delayField;
    private TextFieldWidget distanceField;
    private TextFieldWidget fovField;
    private TextFieldWidget maxAngleField;
    private ButtonWidget critToggle;
    private ButtonWidget toggleBindButton;
    private ButtonWidget aimAssistToggleBindButton;
    private int delayValue;
    private double distanceValue;
    private double fovValue;
    private double maxAngleValue;
    private boolean bindingToggleKey = false;
    private boolean bindingAimAssistKey = false;
    private String toggleKeyDisplay = "NOT SET";
    private String aimAssistKeyDisplay = "NOT SET";
    private int scrollY = 0;

    public AttackUtilityGui() {
        super(Text.literal("Attack Utility Settings"));
        this.delayValue = ModConfig.attackDelay;
        this.distanceValue = ModConfig.aimAssistDistance;
        this.fovValue = ModConfig.aimAssistFOV;
        this.maxAngleValue = ModConfig.aimAssistMaxAngle;
        updateToggleKeyDisplay();
        updateAimAssistKeyDisplay();
    }

    private void updateToggleKeyDisplay() {
        if (ModConfig.toggleKey == 0) {
            toggleKeyDisplay = "NOT SET";
        } else {
            toggleKeyDisplay = InputUtil.keyCodeToString(ModConfig.toggleKey);
        }
    }

    private void updateAimAssistKeyDisplay() {
        if (ModConfig.aimAssistToggleKey == 0) {
            aimAssistKeyDisplay = "NOT SET";
        } else {
            aimAssistKeyDisplay = InputUtil.keyCodeToString(ModConfig.aimAssistToggleKey);
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 50 - scrollY;

        // Title for Auto Attack section
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§6=== AUTO ATTACK ==="), button -> {})
                .dimensions(centerX - 150, startY, 300, 20)
                .build());

        // Delay label
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Attack Delay (ms): " + delayValue), button -> {})
                .dimensions(centerX - 150, startY + 30, 300, 20)
                .build());

        // Delay input field
        this.delayField = new TextFieldWidget(this.textRenderer, centerX - 150, startY + 55, 300, 20, Text.literal(""));
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
        ).dimensions(centerX - 150, startY + 80, 300, 20).build());

        // Toggle keybind button
        this.toggleBindButton = this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Toggle Key: " + toggleKeyDisplay + (bindingToggleKey ? " (PRESS KEY)" : "")),
                button -> {
                    bindingToggleKey = !bindingToggleKey;
                    bindingAimAssistKey = false;
                    updateToggleKeyDisplay();
                    if (bindingToggleKey) {
                        button.setMessage(Text.literal("Toggle Key: (PRESS KEY)"));
                    } else {
                        button.setMessage(Text.literal("Toggle Key: " + toggleKeyDisplay));
                    }
                }
        ).dimensions(centerX - 150, startY + 105, 300, 20).build());

        // Title for Aim Assist section
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§5=== AIM ASSIST ==="), button -> {})
                .dimensions(centerX - 150, startY + 135, 300, 20)
                .build());

        // Distance label
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Max Distance: " + distanceValue), button -> {})
                .dimensions(centerX - 150, startY + 160, 300, 20)
                .build());

        // Distance input field
        this.distanceField = new TextFieldWidget(this.textRenderer, centerX - 150, startY + 185, 300, 20, Text.literal(""));
        this.distanceField.setMaxLength(5);
        this.distanceField.setText(String.valueOf((int) distanceValue));
        this.addDrawableChild(this.distanceField);

        // FOV label
        this.addDrawableChild(ButtonWidget.builder(Text.literal("FOV: " + fovValue + "°"), button -> {})
                .dimensions(centerX - 150, startY + 210, 300, 20)
                .build());

        // FOV input field
        this.fovField = new TextFieldWidget(this.textRenderer, centerX - 150, startY + 235, 300, 20, Text.literal(""));
        this.fovField.setMaxLength(3);
        this.fovField.setText(String.valueOf((int) fovValue));
        this.addDrawableChild(this.fovField);

        // Max Angle label
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Max Angle: " + maxAngleValue + "°"), button -> {})
                .dimensions(centerX - 150, startY + 260, 300, 20)
                .build());

        // Max Angle input field
        this.maxAngleField = new TextFieldWidget(this.textRenderer, centerX - 150, startY + 285, 300, 20, Text.literal(""));
        this.maxAngleField.setMaxLength(3);
        this.maxAngleField.setText(String.valueOf((int) maxAngleValue));
        this.addDrawableChild(this.maxAngleField);

        // Aim Assist toggle keybind button
        this.aimAssistToggleBindButton = this.addDrawableChild(ButtonWidget.builder(
                Text.literal("Aim Assist Key: " + aimAssistKeyDisplay + (bindingAimAssistKey ? " (PRESS KEY)" : "")),
                button -> {
                    bindingAimAssistKey = !bindingAimAssistKey;
                    bindingToggleKey = false;
                    updateAimAssistKeyDisplay();
                    if (bindingAimAssistKey) {
                        button.setMessage(Text.literal("Aim Assist Key: (PRESS KEY)"));
                    } else {
                        button.setMessage(Text.literal("Aim Assist Key: " + aimAssistKeyDisplay));
                    }
                }
        ).dimensions(centerX - 150, startY + 310, 300, 20).build());

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

            try {
                double distance = Double.parseDouble(this.distanceField.getText());
                if (distance > 0 && distance <= 100) {
                    ModConfig.aimAssistDistance = distance;
                }
            } catch (NumberFormatException e) {
                // Invalid input, keep old value
            }

            try {
                double fov = Double.parseDouble(this.fovField.getText());
                if (fov > 0 && fov <= 360) {
                    ModConfig.aimAssistFOV = fov;
                }
            } catch (NumberFormatException e) {
                // Invalid input, keep old value
            }

            try {
                double maxAngle = Double.parseDouble(this.maxAngleField.getText());
                if (maxAngle > 0 && maxAngle <= 180) {
                    ModConfig.aimAssistMaxAngle = maxAngle;
                }
            } catch (NumberFormatException e) {
                // Invalid input, keep old value
            }

            bindingToggleKey = false;
            bindingAimAssistKey = false;
            this.close();
        }).dimensions(centerX - 150, startY + 340, 300, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Press \\ to open GUI"), 10, 10, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (bindingToggleKey && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            ModConfig.toggleKey = keyCode;
            bindingToggleKey = false;
            updateToggleKeyDisplay();
            if (toggleBindButton != null) {
                toggleBindButton.setMessage(Text.literal("Toggle Key: " + toggleKeyDisplay));
            }
            return true;
        }

        if (bindingAimAssistKey && keyCode != GLFW.GLFW_KEY_ESCAPE) {
            ModConfig.aimAssistToggleKey = keyCode;
            bindingAimAssistKey = false;
            updateAimAssistKeyDisplay();
            if (aimAssistToggleBindButton != null) {
                aimAssistToggleBindButton.setMessage(Text.literal("Aim Assist Key: " + aimAssistKeyDisplay));
            }
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (bindingToggleKey) {
            bindingToggleKey = false;
            if (toggleBindButton != null) {
                toggleBindButton.setMessage(Text.literal("Toggle Key: " + toggleKeyDisplay));
            }
            return false;
        }
        if (bindingAimAssistKey) {
            bindingAimAssistKey = false;
            if (aimAssistToggleBindButton != null) {
                aimAssistToggleBindButton.setMessage(Text.literal("Aim Assist Key: " + aimAssistKeyDisplay));
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
