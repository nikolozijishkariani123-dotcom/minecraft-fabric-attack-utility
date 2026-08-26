# Attack Utility Mod

A Minecraft Fabric mod for version 1.21.1 that automatically attacks hostile entities in view.

## Features

- **Auto-Attack**: Automatically attacks hostile mobs that come into view
- **Configurable Delay**: Set custom attack speed delay (in milliseconds)
- **Crit Preference**: Option to perform critical hits when attacking
- **Toggle Keybind**: Press `V` to toggle the mod on/off
- **GUI Settings**: Press `B` to open the settings GUI

## Installation

1. Download the latest release `.jar` file
2. Place it in your `.minecraft/mods` folder
3. Make sure you have Fabric Loader and Fabric API installed
4. Launch Minecraft with the Fabric profile

## Controls

- **V**: Toggle the mod on/off
- **B**: Open the GUI to adjust settings

## Configuration

Inside the GUI, you can configure:
- **Attack Delay**: Delay between attacks in milliseconds (default: 100ms)
- **Prefer Crits**: Toggle critical hit attacks on/off (default: ON)

## How It Works

The mod raycasts from your player position to find the nearest hostile entity in view. When enabled, it will automatically attack that entity with the configured delay between attacks. If crit preference is enabled, it will jump while attacking to perform critical hits.

## Building

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`

## License

MIT License
