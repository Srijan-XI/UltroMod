package com.example.ultimatemod;

import com.example.ultimatemod.drops.BetterDropsHandler;
import com.example.ultimatemod.survival.SurvivalManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UltimateMod implements ModInitializer {

    public static final String MOD_ID = "ultimatemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.initialize();
        ModScreenHandlers.initialize();
        ModPackets.initialize();
        BetterDropsHandler.initialize();
        SurvivalManager.initialize();
        LOGGER.info("[UltimateMod] Loaded - Utility Items, Better Drops, Survival Expansion & Backpack!");
    }
}
