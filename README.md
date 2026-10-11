# 🐉 MyDND — Desktop Tabletop Companion

**MyDND** is a desktop- and tablet-optimized Android app designed for active play during Dungeons & Dragons (5e) sessions. Built with Kotlin and Jetpack Compose, it features a wide multi-pane combat dashboard, active combat deck for favorite actions and spells, multiple character and campaign tracking, real-time session notes, and a D&D Beyond character importer.

---

## ⚔️ Key Features

- **Wide Multi-Pane Combat Dashboard**:
  - **Pinned Combat Deck**: Keep your favorite attacks, cantrips, spells, class powers, and tactical notes in front of you during combat.
  - **1-Click Rolls**: Instant `ROLL ATTACK (+X)` and `ROLL DAMAGE (XdY+Z)` buttons on every action card with critical hit (Natural 20) and fumble (Natural 1) detection.
  - **Vitals & Health Tracker**: Real-time HP bar with quick `±1`, `±5`, `±10` damage/heal buttons, temporary HP, and 3x3 Death Save tracker.
  - **Spell Slots Matrix**: Pip counters for Levels 1–9 with single-tap spend and restore.
  - **Conditions Bar**: Clickable status effect chips (*Poisoned*, *Prone*, *Stunned*, *Paralyzed*, etc.).
  - **Rest Mechanics**: One-click **Short Rest** (recharges short-rest abilities) and **Long Rest** (restores HP, hit dice, and spell slots).
  - **Encounter Initiative Order**: Party and monster combatants with turn order and `Next Turn` advancement.
  - **Live Session Scratchpad**: Fast note-taking with a *"Pin to Combat"* toggle.

- **Multiple Characters & Campaigns**:
  - Keep track of multiple characters with full ability scores, saving throws, skills, inventory, and features.
  - Pin or unpin any action or spell to/from the active combat dashboard.
  - Manage multiple campaigns with session notes journals, quest boards, and NPC directories.
  - Instant character & campaign switchers from the top bar dropdown.

- **D&D Beyond Character Importer**:
  - **URL / ID Fetch**: Import directly by entering a D&D Beyond share link (e.g. `https://www.dndbeyond.com/characters/142958192`) or numerical ID.
  - **Raw JSON Paste**: Paste exported D&D Beyond JSON.
  - **1-Click Preloaded Level 5 Archetypes**:
    - ⚔️ **Valeros Stormblade** — Human Battle Master Fighter (Plate AC 18, Greatsword +1, Action Surge, Trip Attack)
    - 🔮 **Lyra Nightbreeze** — High Elf Evocation Wizard (Fireball, Sculpt Spells, Shield, Misty Step)
    - 🗡️ **Theron Shadowcloak** — Wood Elf Arcane Trickster Rogue (Rapier + 3d6 Sneak Attack, Cunning Action, Uncanny Dodge)
    - 🛡️ **Brother Gareth** — Hill Dwarf Life Domain Cleric (Spirit Guardians, Spiritual Weapon, Preserve Life)

- **Polyhedral Dice Roller & History**:
  - Top-bar quick dice buttons (`d4`, `d6`, `d8`, `d10`, `d12`, `d20`, `d100`).
  - Advantage, Normal, and Disadvantage roll options.
  - Custom dice formula builder (e.g. `8d6 fire`, `2d6+5`).
  - Chronological roll history with full dice math and labels.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.x
- **UI Toolkit**: Jetpack Compose + Material 3 (Dark Fantasy Palette)
- **Architecture**: MVVM with Kotlin Coroutines & StateFlow
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14+ / 16 (API 36)
- **Build System**: Gradle 9.x with Android Gradle Plugin (AGP) 9.x

---

## 🧪 Building & Running Tests

Run unit tests via Gradle:
```bash
./gradlew testDebugUnitTest
```

Build debug APK:
```bash
./gradlew assembleDebug
```
The resulting APK is generated at `app/build/outputs/apk/debug/MyDND-debug.apk`.
