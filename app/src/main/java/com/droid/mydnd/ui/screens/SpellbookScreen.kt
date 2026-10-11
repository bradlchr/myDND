package com.droid.mydnd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.mydnd.model.SpellItem
import com.droid.mydnd.theme.DndArcaneCyan
import com.droid.mydnd.theme.DndBorder
import com.droid.mydnd.theme.DndCardSurface
import com.droid.mydnd.theme.DndCardSurfaceVariant
import com.droid.mydnd.theme.DndGoldLight
import com.droid.mydnd.theme.DndGoldPrimary
import com.droid.mydnd.theme.DndHealthGreen
import com.droid.mydnd.theme.DndTextMuted
import com.droid.mydnd.theme.DndTextPrimary

@Composable
fun SpellbookScreen(
    onAddSpellToCharacter: (SpellItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevelFilter by remember { mutableStateOf<Int?>(null) } // null = All
    var compendiumTab by remember { mutableStateOf(0) } // 0=Spellbook, 1=Conditions, 2=Combat Actions

    val allSpells = remember {
        listOf(
            SpellItem(name = "Eldritch Blast", level = 0, school = "Evocation", castingTime = "1 Action", range = "120 ft", damageOrEffect = "1d10 Force", description = "A beam of crackling energy streaks toward a creature within range. Make a ranged spell attack."),
            SpellItem(name = "Fire Bolt", level = 0, school = "Evocation", castingTime = "1 Action", range = "120 ft", damageOrEffect = "1d10 Fire", description = "Hurl a mote of fire. On hit, deals fire damage. Flammable objects ignite if not being worn or carried."),
            SpellItem(name = "Mage Hand", level = 0, school = "Conjuration", castingTime = "1 Action", range = "30 ft", description = "A spectral, floating hand appears. Manipulates an object, opens unlocked doors/containers, or pours out vial."),
            SpellItem(name = "Minor Illusion", level = 0, school = "Illusion", castingTime = "1 Action", range = "30 ft", duration = "1 min", description = "Create a sound or an image of an object that fits within a 5-ft cube."),
            SpellItem(name = "Prestidigitation", level = 0, school = "Transmutation", castingTime = "1 Action", range = "10 ft", description = "Create harmless sensory effects, light/snuff candles, clean or soil items, chill or warm material."),
            SpellItem(name = "Sacred Flame", level = 0, school = "Evocation", castingTime = "1 Action", range = "60 ft", damageOrEffect = "1d8 Radiant", saveDcType = "DEX Save", description = "Flame-like radiance descends on creature. Target gains no benefit from cover for this save."),
            SpellItem(name = "Vicious Mockery", level = 0, school = "Enchantment", castingTime = "1 Action", range = "60 ft", damageOrEffect = "1d4 Psychic", saveDcType = "WIS Save", description = "Unleash string of insults. On failed save, takes 1d4 psychic damage and has disadvantage on next attack roll."),
            SpellItem(name = "Bless", level = 1, school = "Enchantment", castingTime = "1 Action", range = "30 ft", duration = "1 min", isConcentration = true, damageOrEffect = "+1d4 Attack & Saves", description = "Bless up to 3 creatures. Whenever a target makes an attack roll or saving throw, add 1d4 to the roll."),
            SpellItem(name = "Cure Wounds", level = 1, school = "Evocation", castingTime = "1 Action", range = "Touch", damageOrEffect = "1d8 + Mod Healing", description = "A creature you touch regains hit points equal to 1d8 + your spellcasting ability modifier."),
            SpellItem(name = "Healing Word", level = 1, school = "Evocation", castingTime = "1 Bonus Action", range = "60 ft", damageOrEffect = "1d4 + Mod Healing", description = "A creature of your choice that you can see within range regains 1d4 + mod hit points as a bonus action."),
            SpellItem(name = "Magic Missile", level = 1, school = "Evocation", castingTime = "1 Action", range = "120 ft", damageOrEffect = "3x (1d4+1) Force", description = "Create 3 glowing darts of magical force. Each dart hits automatically for 1d4+1 force damage."),
            SpellItem(name = "Shield", level = 1, school = "Abjuration", castingTime = "1 Reaction", range = "Self", duration = "1 round", damageOrEffect = "+5 AC", description = "Invisible barrier protects you. Until the start of your next turn, you have a +5 bonus to AC and take no damage from Magic Missile."),
            SpellItem(name = "Hold Person", level = 2, school = "Enchantment", castingTime = "1 Action", range = "60 ft", duration = "1 min", isConcentration = true, saveDcType = "WIS Save", description = "Choose a humanoid that you can see. Target must succeed on a WIS saving throw or be Paralyzed for the duration."),
            SpellItem(name = "Misty Step", level = 2, school = "Conjuration", castingTime = "1 Bonus Action", range = "Self (30 ft)", description = "Briefly surrounded by silvery mist, you teleport up to 30 feet to an unoccupied space that you can see."),
            SpellItem(name = "Scorching Ray", level = 2, school = "Evocation", castingTime = "1 Action", range = "120 ft", damageOrEffect = "3x 2d6 Fire", description = "Create three rays of fire and hurl them at targets within range. Make a ranged spell attack for each ray."),
            SpellItem(name = "Counterspell", level = 3, school = "Abjuration", castingTime = "1 Reaction", range = "60 ft", description = "Attempt to interrupt a creature in the process of casting a spell. If 3rd level or lower, spell fails automatically."),
            SpellItem(name = "Fireball", level = 3, school = "Evocation", castingTime = "1 Action", range = "150 ft (20-ft radius)", damageOrEffect = "8d6 Fire", saveDcType = "DEX Save (DC 15)", description = "A bright streak flashes to a point you choose within range and blossoms into an explosion of flame. 8d6 fire damage on failed save, half on success."),
            SpellItem(name = "Revivify", level = 3, school = "Necromancy", castingTime = "1 Action", range = "Touch", components = "V, S, M (Diamonds worth 300 gp)", description = "Touch a creature that has died within the last minute. The creature returns to life with 1 hit point."),
            SpellItem(name = "Spirit Guardians", level = 3, school = "Conjuration", castingTime = "1 Action", range = "Self (15 ft)", duration = "10 min", isConcentration = true, damageOrEffect = "3d8 Radiant", saveDcType = "WIS Save", description = "Spirits flit around you in a 15-foot radius. Affected creatures move at half speed and take 3d8 radiant damage on failed save.")
        )
    }

    val filteredSpells = allSpells.filter { spell ->
        val matchesQuery = searchQuery.isBlank() || spell.name.contains(searchQuery, ignoreCase = true) || spell.school.contains(searchQuery, ignoreCase = true) || spell.description.contains(searchQuery, ignoreCase = true)
        val matchesLevel = (selectedLevelFilter == null || spell.level == selectedLevelFilter)
        matchesQuery && matchesLevel
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📖 5e SPELLBOOK & COMPENDIUM",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = DndGoldLight
                )
                Text(
                    text = "Searchable SRD spells, status condition rules, and combat actions reference",
                    style = MaterialTheme.typography.labelSmall,
                    color = DndTextMuted
                )
            }
        }

        // Tab Row
        TabRow(
            selectedTabIndex = compendiumTab,
            containerColor = DndCardSurface,
            contentColor = DndGoldLight
        ) {
            Tab(selected = (compendiumTab == 0), onClick = { compendiumTab = 0 }, text = { Text("Spells (${filteredSpells.size})", fontWeight = FontWeight.Bold) })
            Tab(selected = (compendiumTab == 1), onClick = { compendiumTab = 1 }, text = { Text("Condition Rules", fontWeight = FontWeight.Bold) })
            Tab(selected = (compendiumTab == 2), onClick = { compendiumTab = 2 }, text = { Text("Combat Actions", fontWeight = FontWeight.Bold) })
        }

        when (compendiumTab) {
            0 -> {
                // Search & Filter Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search spells by name, school, effect...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    // Level Filter Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(null to "All", 0 to "Cantrip", 1 to "1st", 2 to "2nd", 3 to "3rd").forEach { (lvl, label) ->
                            val isSel = (selectedLevelFilter == lvl)
                            Box(
                                modifier = Modifier
                                    .clickable { selectedLevelFilter = lvl }
                                    .background(if (isSel) DndGoldPrimary else DndCardSurfaceVariant, RoundedCornerShape(6.dp))
                                    .border(1.dp, if (isSel) DndGoldPrimary else DndBorder, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else DndTextPrimary
                                )
                            }
                        }
                    }
                }

                // Spell list
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredSpells.forEach { spell ->
                        Surface(
                            color = DndCardSurface,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(spell.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = DndArcaneCyan)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(DndArcaneCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (spell.level == 0) "Cantrip" else "Level ${spell.level} ${spell.school}",
                                                fontSize = 10.sp,
                                                color = DndArcaneCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (spell.isConcentration) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(DndGoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("Concentration", fontSize = 10.sp, color = DndGoldLight, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { onAddSpellToCharacter(spell) },
                                        colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("+ Pin to Combat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Casting: ${spell.castingTime} • Range: ${spell.range} • Components: ${spell.components} • Duration: ${spell.duration}",
                                    fontSize = 11.sp,
                                    color = DndTextMuted
                                )

                                if (spell.damageOrEffect.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Effect: ${spell.damageOrEffect} ${spell.saveDcType?.let { "• $it" } ?: ""}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DndGoldLight
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = spell.description,
                                    fontSize = 12.sp,
                                    color = DndTextPrimary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            1 -> ConditionsCompendiumContent()
            2 -> CombatActionsCompendiumContent()
        }
    }
}

@Composable
fun ConditionsCompendiumContent() {
    val conditions = listOf(
        Pair("Blinded", "Can't see and automatically fails any ability check requiring sight. Attack rolls against the creature have advantage, and creature's attack rolls have disadvantage."),
        Pair("Charmed", "Can't attack the charmer or target them with harmful abilities/magical effects. Charmer has advantage on social ability checks against the creature."),
        Pair("Frightened", "Disadvantage on ability checks and attack rolls while source of fear is in line of sight. Creature can't willingly move closer to the source."),
        Pair("Grappled", "Speed becomes 0. Ends if grappler is incapacitated or removed by effect (such as Thunderwave)."),
        Pair("Incapacitated", "Creature cannot take actions or reactions."),
        Pair("Invisible", "Impossible to see without magic or special senses. Creature's attack rolls have advantage, and attacks against it have disadvantage."),
        Pair("Paralyzed", "Incapacitated and can't move or speak. Automatically fails STR and DEX saves. Attacks against creature have advantage, and hits from within 5 ft are critical hits!"),
        Pair("Petrified", "Transformed into solid stone. Incapacitated, can't move or speak, unaware of surroundings. Resistant to all damage, immune to poison/disease."),
        Pair("Poisoned", "Disadvantage on attack rolls and ability checks."),
        Pair("Prone", "Only movement option is crawling (half speed). Creature has disadvantage on attack rolls. Attack rolls against it have advantage if within 5 ft, otherwise disadvantage."),
        Pair("Restrained", "Speed becomes 0. Attack rolls against creature have advantage, creature's attacks have disadvantage. Disadvantage on DEX saving throws."),
        Pair("Stunned", "Incapacitated, can't move, can speak only falteringly. Automatically fails STR and DEX saving throws. Attacks against it have advantage."),
        Pair("Unconscious", "Incapacitated, can't move or speak, unaware. Drops whatever it's holding and falls prone. Attacks against have advantage, hits within 5 ft are criticals!")
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        conditions.forEach { (name, desc) ->
            Surface(
                color = DndCardSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(name, fontWeight = FontWeight.Bold, color = DndGoldLight, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(desc, fontSize = 12.sp, color = DndTextPrimary, lineHeight = 16.sp)
                }
            }
        }
    }
}

@Composable
fun CombatActionsCompendiumContent() {
    val actions = listOf(
        Pair("Attack", "Make one melee or ranged attack, or multiple attacks if your class grants Extra Attack."),
        Pair("Cast a Spell", "Cast a cantrip or spell with a casting time of 1 action. If you cast a bonus action spell, you can only cast a cantrip with your action."),
        Pair("Dash", "Gain extra movement equal to your speed for the current turn."),
        Pair("Disengage", "Your movement doesn't provoke opportunity attacks for the rest of the turn."),
        Pair("Dodge", "Until the start of your next turn, any attack roll made against you has disadvantage if you can see the attacker, and you make DEX saves with advantage."),
        Pair("Help", "Lend aid to an ally. The ally gets advantage on the next ability check or attack roll made before the start of your next turn."),
        Pair("Hide", "Make a DEX (Stealth) check in an attempt to hide from enemies."),
        Pair("Ready", "Prepare an action in response to a specific perceptible trigger using your reaction.")
    )

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        actions.forEach { (name, desc) ->
            Surface(
                color = DndCardSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("⚔️ $name", fontWeight = FontWeight.Bold, color = DndGoldLight, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(desc, fontSize = 12.sp, color = DndTextPrimary, lineHeight = 16.sp)
                }
            }
        }
    }
}
