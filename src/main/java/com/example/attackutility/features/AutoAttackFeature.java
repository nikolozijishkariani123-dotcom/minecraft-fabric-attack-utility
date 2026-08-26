package com.example.attackutility.features;

import com.example.attackutility.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

public class AutoAttackFeature {
    private static long lastAttackTime = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null && ModConfig.toggleKey != 0) {
                // Check if toggle key is pressed
                if (InputUtil.isKeyPressed(client.getWindow().getHandle(), ModConfig.toggleKey)) {
                    ModConfig.enabled = !ModConfig.enabled;
                    if (client.player != null) {
                        client.player.sendMessage(
                                net.minecraft.text.Text.literal("Attack Utility: " + (ModConfig.enabled ? "§aON" : "§cOFF")),
                                true
                        );
                    }
                    try {
                        Thread.sleep(200); // Debounce
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (ModConfig.enabled && client.player != null && client.world != null) {
                tick(client);
            }
        });
    }

    private static void tick(MinecraftClient client) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < ModConfig.attackDelay) {
            return;
        }

        Entity target = findTarget(client);
        if (target != null) {
            attackEntity(client, target);
            lastAttackTime = currentTime;
        }
    }

    private static Entity findTarget(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return null;

        double range = 6.0; // Attack range
        Entity closestEntity = null;
        double closestDistance = range;

        for (Entity entity : client.world.getEntities()) {
            if (entity == player || entity.isSpectator()) continue;
            if (!(entity instanceof HostileEntity)) continue;
            if (!entity.isAlive()) continue;

            double distance = player.distanceTo(entity);
            if (distance < closestDistance) {
                // Check if entity is in sight
                if (isEntityInView(player, entity)) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity;
    }

    private static boolean isEntityInView(PlayerEntity player, Entity entity) {
        double x = entity.getX();
        double y = entity.getY() + entity.getHeight() / 2.0;
        double z = entity.getZ();

        HitResult result = player.raycast(6.0, 0.0f, false);
        if (result instanceof EntityHitResult entityHit) {
            return entityHit.getEntity() == entity;
        }
        return false;
    }

    private static void attackEntity(MinecraftClient client, Entity target) {
        PlayerEntity player = client.player;
        if (player == null) return;

        // Attack the entity
        client.interactionManager.attackEntity(player, target);

        if (ModConfig.preferCrits) {
            // Perform crit attack (jump while attacking)
            player.jump();
        }
    }
}

class InputUtil {
    public static boolean isKeyPressed(long window, int keyCode) {
        return GLFW.glfwGetKey(window, keyCode) == GLFW.GLFW_PRESS;
    }
}
