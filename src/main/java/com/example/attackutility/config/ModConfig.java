package com.example.attackutility.config;

public class ModConfig {
    public static int attackDelay = 100; // milliseconds
    public static boolean preferCrits = true;
    public static boolean enabled = false;
    public static int toggleKey = 0; // 0 = not set, will be set to actual key code

    // Aim assist settings
    public static boolean aimAssistEnabled = false;
    public static double aimAssistDistance = 20.0; // Max distance to track entities
    public static double aimAssistFOV = 90.0; // Field of view (in degrees)
    public static double aimAssistMaxAngle = 45.0; // Max angle to rotate
    public static int aimAssistToggleKey = 0; // Keybind for aim assist toggle

    // Anti-cheat evasion settings
    public static double jitterAmount = 0.5; // Add random noise to rotations
    public static int reactionTimeMin = 50; // Min ms delay before acting
    public static int reactionTimeMax = 150; // Max ms delay before acting
    public static boolean enableRandomPauses = true; // Random pause intervals
    public static double rotationSmoothness = 0.7; // Slower rotation acceleration (0-1)
}
