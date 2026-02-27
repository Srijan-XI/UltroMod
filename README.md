# ⚔️ Ultimate Survival Mod - [Ultro]

> **A Fabric 1.20.1 all-in-one survival overhaul.**  
> Utility items, smarter mob drops, a thirst/fatigue/temperature system, and a portable backpack — all in one mod.

---

## 📦 Features

### 🔦 Utility Items
| Item | How to Use |
|------|-----------|
| **Auto Torch Placer** | Keep it anywhere in your inventory. When block light drops below 7, it automatically places a torch from your inventory. No clicks needed. |
| **Portable Crafting Table** | Right-click to open a full 3×3 crafting grid anywhere, anytime. |
| **Instant Smelting Stick** | Hold the stick in your main hand, put a smeltable item in your off-hand, then right-click. Instantly smelts the entire stack. Consumes 1 durability per use. |
| **Tree Cutter Axe** | Mine any log block and the entire connected tree (up to 64 logs) falls at once. Each log costs 1 durability. |

### 💀 Better Drops
| Change | Details |
|--------|---------|
| **Zombie coins** | Zombies have a 50% chance to drop 1–3 Gold Coins on death. |
| **Extra saplings** | Every leaves block has a 30% bonus chance to drop its matching sapling. |
| **Ore XP boost** | Breaking ores gives extra XP: coal +2, iron/redstone +3, gold/lapis +4, copper +2, emerald +6, diamond +7. |

### 🌡️ Survival Expansion
A passive HUD appears in the top-right corner of your screen showing three new stats:

| Stat | Range | What happens when it's bad |
|------|-------|---------------------------|
| **Thirst** | 0–20 | Drains 1 point every 60 s. Below 5 → Slowness. At 0 → Slowness + Weakness + Mining Fatigue. **Drink** by right-clicking still or flowing water. |
| **Fatigue** | 0–100 | Builds 1 point every 90 s. At 60 → tired warning. At 80 → Slowness + Weakness + Mining Fatigue. **Resets** on sleeping. |
| **Temperature** | Biome-based | In freezing biomes (< 0.15), outdoor exposure deals 1.5 ♥ damage every 30 s. In scorching biomes (> 1.5) during the day, outdoor exposure deals 1 ♥ damage every 30 s. |

### 🎒 Backpack
- Right-click the Backpack item to open **27 slots** (3×9 rows) of extra storage.
- Items are saved directly to the backpack's NBT — they persist through deaths (drop the item) and reloads.
- Uses the vanilla chest UI — no extra client-side setup needed.

---

## 🛠️ Requirements

| Requirement | Version |
|-------------|---------|
| Minecraft | 1.20.1 |
| Fabric Loader | ≥ 0.15.11 |
| Fabric API | 0.92.7+1.20.1 |
| Java | 17 |

---

## 🚀 Installation (Prism Launcher)

1. **Build the mod** (see [Building from Source](#-building-from-source)) or download a release JAR.
2. Open **Prism Launcher** → select or create a **Fabric 1.20.1** instance.
3. In the instance, go to **Edit → Mods → Download Mods** and install **Fabric API**.
4. Click **Add File** and select `ultimatemod-1.0.0.jar`.
5. Launch — your items appear in the **Tools** creative tab.

---

## 🔨 Building from Source

### Prerequisites
- JDK 17 (e.g. [Eclipse Adoptium](https://adoptium.net/))
- Git

### Steps

```bash
# Clone the repo
git clone <your-repo-url>
cd Mod1

# Build (downloads dependencies automatically on first run)
./gradlew build          # Linux / macOS
gradlew.bat build        # Windows
```

The compiled mod JAR is output to:
```
build/libs/ultimatemod-1.0.0.jar
```

> The `-dev` and `-sources` JARs in the same folder are for development only; use the plain JAR for playing.

### Quick rebuild & copy workflow (Windows)

```powershell
.\gradlew.bat build
# Then copy to your Prism instance mods folder:
Copy-Item build\libs\ultimatemod-1.0.0.jar "C:\Users\<you>\AppData\Roaming\PrismLauncher\instances\<instance>\.minecraft\mods\" -Force
```

---

## 📁 Project Structure

```
src/main/java/com/example/ultimatemod/
├── UltimateMod.java               # Main mod initializer
├── ModItems.java                  # Item registration
├── ModPackets.java                # Network packet IDs
├── ModScreenHandlers.java         # Screen handler registry
├── client/
│   ├── UltimateModClient.java     # Client entry point
│   ├── ClientSurvivalData.java    # Client-side stat cache
│   ├── hud/SurvivalHudRenderer   # Thirst/Fatigue/Temp HUD
│   ├── network/ClientPacketHandler# Receives server->client sync
│   └── screen/BackpackScreen.java # (placeholder — uses vanilla UI)
├── drops/
│   └── BetterDropsHandler.java   # Loot table modifications + XP
├── inventory/
│   └── BackpackInventory.java    # 27-slot NBT-backed inventory
├── items/
│   ├── AutoTorchPlacerItem.java
│   ├── BackpackItem.java
│   ├── InstantSmeltingStickItem.java
│   ├── PortableCraftingTableItem.java
│   └── TreeCutterAxeItem.java
├── mixin/
│   └── PlayerEntityMixin.java    # Reserved for future hooks
├── screen/
│   └── BackpackScreenHandler.java# Saves inventory to NBT on close
└── survival/
    ├── SurvivalData.java          # Per-player stat model
    └── SurvivalManager.java       # Server-side tick logic + networking
```

---

## 🧪 Testing Checklist

- [ ] Auto Torch Placer places torch in a dark cave (light ≤ 6), consumes torch from inventory
- [ ] Portable Crafting Table opens crafting grid on right-click
- [ ] Instant Smelting Stick smelts off-hand item, awards XP, damages stick
- [ ] Tree Cutter Axe fells an entire tree in one swing
- [ ] Zombies drop Gold Coins (~50% of kills)
- [ ] Leaves drop extra saplings when broken
- [ ] Breaking ores gives bonus XP
- [ ] Thirst bar drains over time; right-clicking water restores it
- [ ] Fatigue bar builds over time; sleeping resets it
- [ ] Temperature shows correct label for biome type
- [ ] Backpack opens 27-slot GUI; items persist after close/reopen
- [ ] HUD bars are visible in survival, hidden when a screen is open

---

## 📝 Known Limitations

- **Survival data resets on relog** — thirst, fatigue, and temperature are not persisted to disk between sessions yet. This is a planned feature.
- **No custom item textures** — items use vanilla textures as placeholders (torch for auto-torch placer, gold nugget for coin, etc.).
- **Tree Cutter Axe cap** — limited to 64 connected logs per swing to protect server performance.

---

## 📜 License

This project is licensed under the **MIT License** — see the [`LICENSE`](LICENSE) file for the full text.

### What this means for you

| You can… | You cannot… |
|----------|------------|
| ✅ Use this mod freely in any modpack | ❌ Hold the author liable for damages |
| ✅ Modify the source code | |
| ✅ Distribute original or modified versions | |
| ✅ Use it privately or commercially | |
| ✅ Sub-license it | |

> **One requirement:** Keep the original copyright notice and MIT license text in any copy or substantial portion of the mod.

```
MIT License  Copyright (c) 2026 Srijan-xi
```
