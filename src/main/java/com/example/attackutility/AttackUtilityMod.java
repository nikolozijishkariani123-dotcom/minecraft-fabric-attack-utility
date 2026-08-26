package com.example.attackutility;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackUtilityMod implements ModInitializer {
    public static final String MOD_ID = "attackutility";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Attack Utility Mod loaded!");
    }
}
