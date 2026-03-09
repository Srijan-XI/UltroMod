package com.example.ultimatemod;

import net.minecraft.util.Identifier;

public class ModPackets {

    // Server -> Client: sync survival stats to HUD
    public static final Identifier SURVIVAL_SYNC = new Identifier(UltimateMod.MOD_ID, "survival_sync");

    public static void initialize() {
        // Packet channels are registered by ID reference; no server-side init needed here.
        // Client registers receive handlers in ClientPacketHandler.
    }
}
