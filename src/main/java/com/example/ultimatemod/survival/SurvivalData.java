package com.example.ultimatemod.survival;

/**
 * Stores per-player survival stats: thirst, fatigue, and temperature.
 */
public class SurvivalData {

    // Thirst: 0 (parched) – 20 (hydrated). Drains over time; refilled by drinking water.
    public int thirst = 20;

    // Fatigue: 0 (rested) – 100 (exhausted). Builds over time; reset upon sleeping.
    public int fatigue = 0;

    // Temperature: float sourced from Minecraft biome temperature.
    // Roughly: <0.15 = freezing, 0.15-0.8 = cold, 0.8-1.5 = normal, >1.5 = hot.
    // Default to 1.0 (temperate / normal biome) so new players don't start "Hot".
    public float temperature = 1.0f;

    // Internal tick counters
    public int thirstTick  = 0;  // counts toward next thirst drain
    public int fatigueTick = 0;  // counts toward next fatigue increase
    public int damageTick  = 0;  // counts toward next environment damage tick

    public boolean wasSleeping = false; // tracks sleep-state transitions

    public SurvivalData() {}
}
