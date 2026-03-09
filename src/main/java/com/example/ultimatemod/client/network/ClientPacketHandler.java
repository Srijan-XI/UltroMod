package com.example.ultimatemod.client.network;

import com.example.ultimatemod.ModPackets;
import com.example.ultimatemod.client.ClientSurvivalData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientPacketHandler {

    public static void initialize() {
        // Receive survival stats from the server and cache them for HUD rendering
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SURVIVAL_SYNC, (client, handler, buf, responseSender) -> {
            int   thirst      = buf.readInt();
            int   fatigue     = buf.readInt();
            float temperature = buf.readFloat();

            client.execute(() -> {
                ClientSurvivalData.thirst      = thirst;
                ClientSurvivalData.fatigue     = fatigue;
                ClientSurvivalData.temperature = temperature;
            });
        });
    }
}
