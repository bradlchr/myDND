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
import androidx.compose.material3.OutlinedButton
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
import com.droid.mydnd.model.CharacterSheet
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
fun CharactersScreen(
    characters: List<CharacterSheet>,
    activeCharacter: CharacterSheet?,
    onSelectCharacter: (String) -> Unit,
    onTogglePinAction: (String) -> Unit,
    onTogglePinSpell: (String) -> Unit,
    onOpenNewCharacterDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var charDetailTab by remember { mutableStateOf(0) } // 0=Actions, 1=Spells, 2=Skills/Stats, 3=Features, 4=Inventory

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Character List (28% width)
        Column(
            modifier = Modifier
                .weight(0.28f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CHARACTERS (${characters.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = DndGoldLight
                )

                Button(
                    onClick = onOpenNewCharacterDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ New", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                characters.forEach { char ->
                    val isSelected = (char.id == activeCharacter?.id)
                    val bg = if (isSelected) DndGoldPrimary.copy(alpha = 0.15f) else DndCardSurface
                    val border = if (isSelected) DndGoldPrimary else DndBorder

                    Surface(
                        color = bg,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCharacter(char.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = char.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) DndGoldLight else DndTextPrimary
                                )
                                Text(
                                    text = "Lvl ${char.level}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DndGoldLight
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${char.race} • ${char.characterClass}",
                                fontSize = 11.sp,
                                color = DndTextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("HP: ${char.currentHp}/${char.maxHp}", fontSize = 11.sp, color = DndHealthGreen)
                                Text("AC: ${char.armorClass}", fontSize = 11.sp, color = DndGoldLight)
                                Text("Speed: ${char.speed}ft", fontSize = 11.sp, color = DndTextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Full Character Details (72% width)
        if (activeCharacter != null) {
            Column(
                modifier = Modifier
                    .weight(0.72f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Main Header Banner
                Surface(
                    color = DndCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = activeCharacter.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = DndGoldLight
                                )
                                Text(
                                    text = "${activeCharacter.race} • ${activeCharacter.characterClass} • Background: ${activeCharacter.background.ifBlank { "Adventurer" }}",
                                    fontSize = 13.sp,
                                    color = DndTextMuted
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .background(DndCardSurfaceVariant, RoundedCornerShape(8.dp))
                                        .border(1.dp, DndBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("AC: ${activeCharacter.armorClass}", fontWeight = FontWeight.Bold, color = DndGoldLight)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(DndCardSurfaceVariant, RoundedCornerShape(8.dp))
                                        .border(1.dp, DndBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("HP: ${activeCharacter.currentHp}/${activeCharacter.maxHp}", fontWeight = FontWeight.Bold, color = DndHealthGreen)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Ability Scores Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("STR", "DEX", "CON", "INT", "WIS", "CHA").forEach { ability ->
                                val score = activeCharacter.abilityScores[ability] ?: 10
                                val mod = activeCharacter.getAbilityModifier(ability)
                                val modStr = if (mod >= 0) "+$mod" else "$mod"

                                Column(
                                    modifier = Modifier
                                        .background(DndCardSurfaceVariant, RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(ability, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DndTextMuted)
                                    Text(modStr, fontSize = 18.sp, fontWeight = FontWeight.Black, color = DndGoldLight)
                                    Text("$score", fontSize = 10.sp, color = DndTextMuted)
                                }
                            }
                        }
                    }
                }

                // Sub-navigation Tabs: Actions, Spells, Skills & Saves, Features, Inventory
                val tabTitles = listOf("Actions & Attacks", "Spells & Cantrips", "Skills & Saves", "Features & Traits", "Inventory")
                TabRow(
                    selectedTabIndex = charDetailTab,
                    containerColor = DndCardSurface,
                    contentColor = DndGoldLight
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = (charDetailTab == index),
                            onClick = { charDetailTab = index },
                            text = { Text(title, fontWeight = if (charDetailTab == index) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }

                when (charDetailTab) {
                    0 -> ActionsTabContent(activeCharacter, onTogglePinAction)
                    1 -> SpellsTabContent(activeCharacter, onTogglePinSpell)
                    2 -> SkillsAndSavesContent(activeCharacter)
                    3 -> FeaturesTabContent(activeCharacter)
                    4 -> InventoryTabContent(activeCharacter)
                }
            }
        }
    }
}

@Composable
fun ActionsTabContent(char: CharacterSheet, onTogglePin: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (char.actions.isEmpty()) {
            Text("No actions configured yet.", color = DndTextMuted, modifier = Modifier.padding(16.dp))
        } else {
            char.actions.forEach { action ->
                Surface(
                    color = DndCardSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(action.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DndGoldLight)
                                Spacer(modifier = Modifier.width(8.dp))
                                if (action.attackBonus != null) {
                                    val bStr = if (action.attackBonus >= 0) "+${action.attackBonus}" else "${action.attackBonus}"
                                    Text("Attack: $bStr", fontSize = 11.sp, color = DndTextPrimary, fontWeight = FontWeight.SemiBold)
                                }
                                if (action.damageFormula != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Damage: ${action.damageFormula} ${action.damageType ?: ""}", fontSize = 11.sp, color = DndHealthGreen)
                                }
                            }
                            if (action.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(action.description, fontSize = 11.sp, color = DndTextMuted)
                            }
                        }

                        // Pin Toggle Button
                        FilledTonalButton(
                            onClick = { onTogglePin(action.id) },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(if (action.isPinned) "📌 Pinned" else "Pin", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpellsTabContent(char: CharacterSheet, onTogglePin: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (char.spells.isEmpty()) {
            Text("No spells recorded for this character.", color = DndTextMuted, modifier = Modifier.padding(16.dp))
        } else {
            char.spells.forEach { spell ->
                Surface(
                    color = DndCardSurface,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(spell.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DndArcaneCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (spell.level == 0) "Cantrip" else "Level ${spell.level} ${spell.school}", fontSize = 11.sp, color = DndTextMuted)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("${spell.castingTime} • ${spell.range} • ${spell.duration}", fontSize = 11.sp, color = DndTextMuted)
                            if (spell.damageOrEffect.isNotBlank()) {
                                Text("Effect: ${spell.damageOrEffect}", fontSize = 11.sp, color = DndGoldLight)
                            }
                            if (spell.description.isNotBlank()) {
                                Text(spell.description, fontSize = 11.sp, color = DndTextPrimary, maxLines = 2)
                            }
                        }

                        FilledTonalButton(
                            onClick = { onTogglePin(spell.id) },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(if (spell.isPinned) "📌 Pinned" else "Pin", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkillsAndSavesContent(char: CharacterSheet) {
    val allSkills = listOf(
        Pair("Acrobatics", "DEX"), Pair("Animal Handling", "WIS"), Pair("Arcana", "INT"),
        Pair("Athletics", "STR"), Pair("Deception", "CHA"), Pair("History", "INT"),
        Pair("Insight", "WIS"), Pair("Intimidation", "CHA"), Pair("Investigation", "INT"),
        Pair("Medicine", "WIS"), Pair("Nature", "INT"), Pair("Perception", "WIS"),
        Pair("Performance", "CHA"), Pair("Persuasion", "CHA"), Pair("Religion", "INT"),
        Pair("Sleight of Hand", "DEX"), Pair("Stealth", "DEX"), Pair("Survival", "WIS")
    )

    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("SKILL PROFICIENCIES & MODIFIERS", fontWeight = FontWeight.Bold, color = DndGoldLight, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            allSkills.chunked(2).forEach { rowSkills ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                    rowSkills.forEach { (skillName, ability) ->
                        val bonus = char.getSkillBonus(skillName, ability)
                        val bonusStr = if (bonus >= 0) "+$bonus" else "$bonus"
                        val isProf = char.skillProficiencies.contains(skillName)
                        val isExpert = char.skillExpertise.contains(skillName)

                        Row(
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isExpert) "🌟" else if (isProf) "●" else "○",
                                color = if (isProf || isExpert) DndGoldLight else DndTextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(skillName, fontSize = 12.sp, color = DndTextPrimary, modifier = Modifier.weight(1f))
                            Text("($ability)", fontSize = 10.sp, color = DndTextMuted)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(bonusStr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isProf) DndGoldLight else DndTextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturesTabContent(char: CharacterSheet) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        char.features.forEach { feat ->
            Surface(
                color = DndCardSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(feat.name, fontWeight = FontWeight.Bold, color = DndGoldLight, fontSize = 13.sp)
                        Text(feat.source, fontSize = 10.sp, color = DndTextMuted)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(feat.description, fontSize = 11.sp, color = DndTextPrimary)
                }
            }
        }
    }
}

@Composable
fun InventoryTabContent(char: CharacterSheet) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        char.inventory.forEach { item ->
            Surface(
                color = DndCardSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, fontWeight = FontWeight.Bold, color = DndTextPrimary, fontSize = 13.sp)
                            if (item.isEquipped) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(DndHealthGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text("Equipped", fontSize = 9.sp, color = DndHealthGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        if (item.description.isNotBlank()) {
                            Text(item.description, fontSize = 11.sp, color = DndTextMuted)
                        }
                    }

                    Text("Qty: ${item.quantity} • ${item.weight} lbs", fontSize = 11.sp, color = DndTextMuted)
                }
            }
        }
    }
}
