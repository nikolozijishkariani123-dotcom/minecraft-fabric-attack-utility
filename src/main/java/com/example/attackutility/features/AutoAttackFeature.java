package com.example.attackutility.features;

import com.example.attackutility.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;
import java.util.Random;

public class AutoAttackFeature {
    private static long lastAttackTime = 0;
    private static long lastToggleTime = 0;
    private static long lastAimUpdateTime = 0;
    private static long lastPauseTime = 0;
    private static boolean inPauseMode = false;
    private static Random random = new Random();
    private static double reactionTimeDelay = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null && ModConfig.toggleKey != 0) {
                // Check if toggle key is pressed with reaction time delay
                if (InputUtil.isKeyPressed(client.getWindow().getHandle(), ModConfig.toggleKey)) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastToggleTime > 300) { // Increased debounce for natural feeling
                        ModConfig.enabled = !ModConfig.enabled;
                        if (client.player != null) {
                            // Don't always show message (sometimes humans are subtle)
                            if (random.nextDouble() > 0.2) {
                                client.player.sendMessage(
                                        net.minecraft.text.Text.literal("Attack Utility: " + (ModConfig.enabled ? "§aON" : "§cOFF")),
                                        true
                                );
                            }
                        }
                        lastToggleTime = currentTime + random.nextInt(50); // Add random delay
                    }
                }
            }

            if (client.player != null && client.world != null && ModConfig.aimAssistToggleKey != 0) {
                // Check if aim assist toggle key is pressed
                if (InputUtil.isKeyPressed(client.getWindow().getHandle(), ModConfig.aimAssistToggleKey)) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastToggleTime > 300) {
                        ModConfig.aimAssistEnabled = !ModConfig.aimAssistEnabled;
                        if (client.player != null && random.nextDouble() > 0.2) {
                            client.player.sendMessage(
                                    net.minecraft.text.Text.literal("Aim Assist: " + (ModConfig.aimAssistEnabled ? "§aON" : "§cOFF")),
                                    true
                            );
                        }
                        lastToggleTime = currentTime + random.nextInt(50);
                    }
                }
            }

            // Random pause intervals (human behavior)
            if (ModConfig.enableRandomPauses) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPauseTime > random.nextInt(2000) + 1000) {
                    inPauseMode = !inPauseMode;
                    lastPauseTime = currentTime;
                }
            }

            if (!inPauseMode) {
                if (ModConfig.aimAssistEnabled && client.player != null && client.world != null) {
                    performAimAssist(client);
                }

                if (ModConfig.enabled && client.player != null && client.world != null) {
                    tick(client);
                }
            }
        });
    }

    private static void performAimAssist(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return;

        long currentTime = System.currentTimeMillis();
        // Add reaction time delay
        int reactionTime = ModConfig.reactionTimeMin + random.nextInt(ModConfig.reactionTimeMax - ModConfig.reactionTimeMin);
        
        if (currentTime - lastAimUpdateTime > reactionTime) {
            Entity target = findAimTarget(client);
            if (target != null && random.nextDouble() > 0.1) { // 10% chance to "miss" target temporarily
                aimAtEntity(player, target);
            }
            lastAimUpdateTime = currentTime;
        }
    }

    private static Entity findAimTarget(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return null;

        Entity closestEntity = null;
        double closestDistance = ModConfig.aimAssistDistance;
        double closestAngle = ModConfig.aimAssistMaxAngle;

        for (Entity entity : client.world.getEntities()) {
            if (entity == player || entity.isSpectator()) continue;
            if (!(entity instanceof HostileEntity)) continue;
            if (!entity.isAlive()) continue;

            double distance = player.distanceTo(entity);
            // Add jitter to distance calculations
            distance += (random.nextDouble() - 0.5) * ModConfig.jitterAmount;
            
            if (distance > ModConfig.aimAssistDistance) continue;

            // Check if entity is within FOV with jitter
            double angle = getAngleToEntity(player, entity);
            double fovWithJitter = ModConfig.aimAssistFOV + (random.nextDouble() - 0.5) * 10;
            if (angle > fovWithJitter / 2.0) continue;

            if (distance < closestDistance || (distance == closestDistance && angle < closestAngle)) {
                closestEntity = entity;
                closestDistance = distance;
                closestAngle = angle;
            }
        }

        return closestEntity;
    }

    private static double getAngleToEntity(PlayerEntity player, Entity entity) {
        Vec3d playerPos = player.getEyePos();
        Vec3d entityPos = entity.getEyePos();
        Vec3d direction = entityPos.subtract(playerPos).normalize();

        Vec3d lookDirection = player.getRotationVector();

        double dotProduct = direction.x * lookDirection.x + direction.y * lookDirection.y + direction.z * lookDirection.z;
        dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));

        return Math.acos(dotProduct) * (180.0 / Math.PI);
    }

    private static void aimAtEntity(PlayerEntity player, Entity target) {
        Vec3d playerPos = player.getEyePos();
        Vec3d targetPos = target.getEyePos();
        Vec3d direction = targetPos.subtract(playerPos);

        double distance = direction.length();
        if (distance == 0) return;

        // Calculate yaw and pitch
        double yaw = Math.atan2(direction.z, direction.x) * (180.0 / Math.PI) - 90.0;
        double pitch = Math.asin(-direction.y / distance) * (180.0 / Math.PI);

        // Apply max angle constraint
        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();

        float yawDifference = (float) yaw - currentYaw;
        float pitchDifference = (float) pitch - currentPitch;

        // Normalize angles to -180 to 180 range
        while (yawDifference > 180) yawDifference -= 360;
        while (yawDifference < -180) yawDifference += 360;

        // Apply smoothness (slower, more human-like rotation)
        float smoothness = (float) ModConfig.rotationSmoothness;
        yawDifference *= smoothness;
        pitchDifference *= smoothness;

        // Limit rotation to max angle
        float maxAngle = (float) ModConfig.aimAssistMaxAngle;
        yawDifference = Math.max(-maxAngle, Math.min(maxAngle, yawDifference));
        pitchDifference = Math.max(-maxAngle, Math.min(maxAngle, pitchDifference));

        // Add jitter to rotations (human-like tremor)
        yawDifference += (random.nextFloat() - 0.5f) * (float) ModConfig.jitterAmount;
        pitchDifference += (random.nextFloat() - 0.5f) * (float) ModConfig.jitterAmount;

        player.setYaw(currentYaw + yawDifference);
        player.setPitch(currentPitch + pitchDifference);
    }

    private static void tick(MinecraftClient client) {
        long currentTime = System.currentTimeMillis();
        // Add random variance to attack delay
        int delayVariance = ModConfig.attackDelay + random.nextInt(50) - 25;
        
        if (currentTime - lastAttackTime < delayVariance) {
            return;
        }

        Entity target = findTarget(client);
        if (target != null && random.nextDouble() > 0.15) { // 15% chance to "miss" occasionally
            attackEntity(client, target);
            lastAttackTime = currentTime;
        }
    }

    private static Entity findTarget(MinecraftClient client) {
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return null;

        double range = 6.0;
        Entity closestEntity = null;
        double closestDistance = range;

        for (Entity entity : client.world.getEntities()) {
            if (entity == player || entity.isSpectator()) continue;
            if (!(entity instanceof HostileEntity)) continue;
            if (!entity.isAlive()) continue;

            double distance = player.distanceTo(entity);
            // Add jitter to distance
            distance += (random.nextDouble() - 0.5) * ModConfig.jitterAmount;
            
            if (distance < closestDistance) {
                if (isEntityInView(player, entity)) {
                    closestEntity = entity;
                    closestDistance = distance;
                }
            }
        }

        return closestEntity;
    }

    private static boolean isEntityInView(PlayerEntity player, Entity entity) {
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

        if (ModConfig.preferCrits && random.nextDouble() > 0.2) { // Don't always crit
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
