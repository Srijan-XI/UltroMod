package com.example.ultimatemod.client.hud;

import com.example.ultimatemod.client.ClientSurvivalData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SurvivalHudRenderer {

    // ARGB color constants
    private static final int COLOR_BG         = 0x88000000;
    private static final int COLOR_THIRST     = 0xFF3388FF;
    private static final int COLOR_THIRST_LOW = 0xFFFF4444;
    private static final int COLOR_FATIGUE    = 0xFFDDAA00;
    private static final int COLOR_FATIGUE_HI = 0xFFFF3300;
    private static final int COLOR_COLD       = 0xFF88CCFF;
    private static final int COLOR_NORMAL     = 0xFF55FF55;
    private static final int COLOR_HOT        = 0xFFFF8833;

    // HUD mode icons (16x16 textures)
    // Note: Minecraft's texture manager resolves paths under assets/<namespace>/textures/ automatically.
    // The path here must NOT include ".png" — the suffix is appended internally.
    private static final Identifier ICON_THIRST      = new Identifier("ultimatemod", "textures/hud/thirst.png");
    private static final Identifier ICON_FATIGUE     = new Identifier("ultimatemod", "textures/hud/fatigue.png");
    private static final Identifier ICON_TEMPERATURE = new Identifier("ultimatemod", "textures/hud/temperature.png");

    /** Icon size drawn on screen (pixels in GUI scale) */
    private static final int ICON_SIZE = 9;

    public static void register() {
        HudRenderCallback.EVENT.register(SurvivalHudRenderer::render);
    }

    private static void render(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (client.options.hudHidden) return;
        // Don't render when a screen is open
        if (client.currentScreen != null) return;

        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        int barW  = 82;
        int barH  = 5;
        // Leave ICON_SIZE+2 px on the left for the icon
        int iconPad = ICON_SIZE + 2;
        int baseX = sw - barW - 4;
        int iconX = baseX - iconPad;
        int baseY = sh - 62; // Just above the vanilla hotbar/XP bar area

        int thirst  = ClientSurvivalData.thirst;
        int fatigue = ClientSurvivalData.fatigue;
        float temp  = ClientSurvivalData.temperature;

        // ── Thirst bar ──────────────────────────────────────────────────
        int thirstColor = (thirst <= 5) ? COLOR_THIRST_LOW : COLOR_THIRST;
        int iconY1 = baseY - 2;
        drawIcon(context, ICON_THIRST, iconX, iconY1);
        drawBar(context, baseX, baseY, barW, barH, thirst, 20, thirstColor, "Thirst");

        // ── Fatigue bar ─────────────────────────────────────────────────
        int baseY2 = baseY + barH + 7;
        int fatigueColor = (fatigue >= 80) ? COLOR_FATIGUE_HI : COLOR_FATIGUE;
        int iconY2 = baseY2 - 2;
        drawIcon(context, ICON_FATIGUE, iconX, iconY2);
        drawBar(context, baseX, baseY2, barW, barH, fatigue, 100, fatigueColor, "Fatigue");

        // ── Temperature label ────────────────────────────────────────────
        int baseY3 = baseY2 + barH + 7;
        String tempLabel;
        int tempColor;
        if (temp < 0.15f) {
            tempLabel = "Temp: Freezing";
            tempColor = COLOR_COLD;
        } else if (temp < 0.8f) {
            tempLabel = "Temp: Cold";
            tempColor = 0xFFAADDFF;
        } else if (temp < 1.5f) {
            tempLabel = "Temp: Normal";
            tempColor = COLOR_NORMAL;
        } else {
            tempLabel = "Temp: Hot";
            tempColor = COLOR_HOT;
        }

        // Draw temperature icon
        drawIcon(context, ICON_TEMPERATURE, iconX, baseY3 - 1);

        // Measure text to right-align with bar
        int textW = client.textRenderer.getWidth(tempLabel);
        context.drawTextWithShadow(client.textRenderer, tempLabel,
                baseX + barW - textW, baseY3, tempColor);
    }

    /**
     * Renders a 16x16 HUD texture, scaled to ICON_SIZE x ICON_SIZE pixels.
     */
    private static void drawIcon(DrawContext ctx, Identifier texture, int x, int y) {
        ctx.drawTexture(texture, x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    private static void drawBar(DrawContext ctx, int x, int y, int barW, int barH,
                                int value, int max, int fillColor, String label) {
        MinecraftClient client = MinecraftClient.getInstance();

        int filled = (int) ((value / (float) max) * barW);
        filled = Math.max(0, Math.min(filled, barW));

        // Dark background
        ctx.fill(x - 1, y - 1, x + barW + 1, y + barH + 1, COLOR_BG);
        // Filled portion
        if (filled > 0) ctx.fill(x, y, x + filled, y + barH, fillColor);
        // Empty portion (darker)
        if (filled < barW) ctx.fill(x + filled, y, x + barW, y + barH, 0xFF333333);

        // Label text (right-aligned to bar)
        int textW = client.textRenderer.getWidth(label);
        ctx.drawTextWithShadow(client.textRenderer, label,
                x + barW - textW, y - 9, 0xFFFFFFFF);
    }
}
