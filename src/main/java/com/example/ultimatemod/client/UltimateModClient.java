package com.example.ultimatemod.client;

import com.example.ultimatemod.client.hud.SurvivalHudRenderer;
import com.example.ultimatemod.client.network.ClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class UltimateModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register survival HUD bars (thirst, fatigue, temperature)
        SurvivalHudRenderer.register();

        // Register client-side packet handlers
        ClientPacketHandler.initialize();
    }
}
