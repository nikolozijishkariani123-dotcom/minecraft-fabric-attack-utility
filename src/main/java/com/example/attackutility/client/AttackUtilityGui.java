package com.example.attackutility.client;

import com.example.attackutility.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AttackUtilityGui extends Screen {
    private TextFieldWidget delayField;
    private ButtonWidget critToggle;
    private int delayValue;

    public AttackUtilityGui() {
        super(Text.literal("Attack Utility Settings"));
        this.delayValue = ModConfig.attackDelay;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Delay label
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Attack Delay (ms): " + delayValue), button -> {})
                .dimensions(centerX - 150, centerY - 60, 300, 20)
                .build());

        // Delay input field
        this.delayField = new TextFieldWidget(this.textRenderer, centerX - 150, centerY - 35, 300, 20, Text.literal(""));
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
        ).dimensions(centerX - 150, centerY + 10, 300, 20).build());

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
            this.close();
        }).dimensions(centerX - 150, centerY + 50, 300, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.literal("Press V to toggle mod | Press B to open GUI"), 10, 10, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        this.client.setScreen(null);
    }
}
