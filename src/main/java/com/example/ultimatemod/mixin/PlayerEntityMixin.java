package com.example.ultimatemod.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Mixin into PlayerEntity.
 * Reserved for future hooks; current survival logic is handled via Fabric events.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    // No-op: all player-related logic in this mod uses Fabric event callbacks.
}
