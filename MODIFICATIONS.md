# Modifications to GT: Not Leisure (Science Not Leisure)

This repository is an **unofficial modified fork** of GT: Not Leisure. It is not
affiliated with, endorsed by, or supported by the upstream authors.

- Upstream project: <https://github.com/ABKQPO/GT-Not-Leisure>
- Upstream base revision: `082483e468942eb1949975f08bc900510c91d930`
- Upstream version string: `0.2.7-pre3-dev-290.37+082483e468`
- Target modpack: GT: New Horizons `2.9.0-beta-3` (Minecraft 1.7.10, Forge 10.13.4.1614)
- Modification date: **2026-10-18**
- Modified by: `KurumiMyWaifu` (`https://github.com/KurumiMyWaifu`)
- License of the modifications: **GNU Lesser General Public License v3.0
  (LGPL-3.0)**, the same license as the upstream project. See `LICENSE.txt`.

The upstream project is licensed under **LGPL-3.0**, not GPL-3.0. All original
copyright and license notices are retained. Third-party code and assets keep
their own licenses; see the "Credits" table in `README.md`.

## Purpose of the modifications

The mod used the deprecated IndustrialCraft 2 block `IC2:blockAlloyGlass`
(block ID `183:0`) as a structural block, a recipe input, and a quest material.
This fork replaces every such use with GregTech's `gregtech:gt.blockglass1`
with metadata `10` (block ID `3028:10`).

In GT5-Unofficial, `gt.blockglass1` meta `10` is `ItemList.ReinforcedGlass`
(ore dictionary entry `glassReinforced`), which is the intended replacement for
IC2 alloy glass.

## Detailed changes

All changes are in `src/`. No other functional code was modified.

### 1. Multiblock structure elements (5 files)

Before:

```java
StructureUtility.ofBlockAnyMeta(GameRegistry.findBlock(Mods.IndustrialCraft2.ID, "blockAlloyGlass"))
```

After:

```java
StructureUtility.ofBlock(GregTechAPI.sBlockGlass1, 10)
```

Files:

- `src/main/java/com/science/gtnl/common/machine/multiblock/FuelRefiningComplex.java`
- `src/main/java/com/science/gtnl/common/machine/multiblock/structuralReconstructionPlan/LargeForming.java`
- `src/main/java/com/science/gtnl/common/machine/multiblock/structuralReconstructionPlan/MolecularTransformer.java`
- `src/main/java/com/science/gtnl/common/machine/multiblock/wireless/MegaCanner.java`
- `src/main/java/com/science/gtnl/common/machine/multiblock/wireless/NanoAssemblerMarkL.java`

### 2. Recipe inputs (7 references)

Before:

```java
GTModHandler.getModItem(Mods.IndustrialCraft2.ID, "blockAlloyGlass", <count>, 0)
```

After:

```java
ItemList.ReinforcedGlass.get(<count>)
```

Files:

- `src/main/java/com/science/gtnl/common/recipe/gregtech/AssemblerRecipes.java` (6 references)
- `src/main/java/com/science/gtnl/common/recipe/gtnl/CraftingTableRecipes.java` (1 reference)

### 3. Quest material requirements (7 JSON files)

Each quest item requirement that asked for the deprecated IC2 block now asks
for the GregTech block at metadata 10:

```diff
-          "Damage:2": 0,
+          "Damage:2": 10,
           "OreDict:8": "",
-          "id:8": "IC2:blockAlloyGlass"
+          "id:8": "gregtech:gt.blockglass1"
```

Files:

- `src/main/resources/assets/sciencenotleisure/quest/Quests/Tier075Superheat-GTNotLeisure75SteamAge==/-I97UUD3oRdWn8Wk-hr6nIg==.json`
- `src/main/resources/assets/sciencenotleisure/quest/Quests/Tier075Superheat-GTNotLeisure75SteamAge==/-PAEAFa5xShavUK6dZgq45Q==.json`
- `src/main/resources/assets/sciencenotleisure/quest/Quests/Tier075Superheat-GTNotLeisure75SteamAge==/-Thn62VlIRL2opp_oKxQanw==.json`
- `src/main/resources/assets/sciencenotleisure/quest/Quests/Tier075Superheat-GTNotLeisure75SteamAge==/-Wl2Do2ocSTamZzrRADtxgw==.json`
- `src/main/resources/assets/sciencenotleisure/quest/Quests/Tier075Superheat-GTNotLeisure75SteamAge==/-mC6_hr7yTOKllBrvuYzNpg==.json`
- `src/main/resources/assets/sciencenotleisure/quest/Quests/Tier075Superheat-GTNotLeisure75SteamAge==/-ryXkmJDfRKuEZdjrMQfcfw==.json`
- `src/main/resources/assets/sciencenotleisure/quest/Quests/Tier0999Supercri-GTNotLeisure99SteamAge==/-ftAZh6RtRQ-EQ3depF4aGw==.json`

### 4. Repository notice

- Added this `MODIFICATIONS.md`.
- Added a short "unofficial modified fork" notice at the top of `README.md`
  pointing to this file.

## Verification

- `grep -r "blockAlloyGlass" src/` returns no matches.
- The built jar contains no `blockAlloyGlass` references.
- `StructureUtility.ofBlock(GregTechAPI.sBlockGlass1, 10)` and
  `ItemList.ReinforcedGlass.get(...)` are present in the compiled bytecode.
- All modified quest JSON files parse as valid JSON.

## Building

Requirements:

- Gradle 9.7.0 (the version pinned by `gradle/wrapper/gradle-wrapper.properties`)
- JDK 25 to run Gradle, plus Azul Zulu JDK 8, 17, and 21 toolchains for
  RetroFuturaGradle/JvmDowngrader.

Build the production jar with:

```bash
./gradlew assemble
```

The output jar is written to `build/libs/`.

## License and warranty

The original work and these modifications are distributed under the
**GNU Lesser General Public License v3.0**. See `LICENSE.txt` for the full
license text. There is **no warranty**, to the extent permitted by law.

If you redistribute a compiled jar, you must also make the corresponding source
code available under LGPL-3.0. Publishing this repository and the source archive
alongside the jar satisfies that requirement.
