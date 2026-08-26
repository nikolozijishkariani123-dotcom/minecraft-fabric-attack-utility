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
}
