package com.droid.mydnd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.mydnd.model.ActionItem
import com.droid.mydnd.model.Campaign
import com.droid.mydnd.model.CharacterSheet
import com.droid.mydnd.model.Combatant
import com.droid.mydnd.model.SpellItem
import com.droid.mydnd.theme.DndArcaneCyan
import com.droid.mydnd.theme.DndBorder
import com.droid.mydnd.theme.DndCardSurface
import com.droid.mydnd.theme.DndCardSurfaceVariant
import com.droid.mydnd.theme.DndCrimsonLight
import com.droid.mydnd.theme.DndDamageRed
import com.droid.mydnd.theme.DndGoldDark
import com.droid.mydnd.theme.DndGoldLight
import com.droid.mydnd.theme.DndGoldPrimary
import com.droid.mydnd.theme.DndHealthGreen
import com.droid.mydnd.theme.DndTempHpBlue
import com.droid.mydnd.theme.DndTextMuted
import com.droid.mydnd.theme.DndTextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CombatDashboardScreen(
    character: CharacterSheet?,
    campaign: Campaign?,
    combatants: List<Combatant>,
    onRollAttack: (action: ActionItem) -> Unit,
    onRollDamage: (action: ActionItem) -> Unit,
    onCastSpell: (spell: SpellItem) -> Unit,
    onTakeDamage: (Int) -> Unit,
    onHeal: (Int) -> Unit,
    onUseSpellSlot: (Int) -> Unit,
    onRestoreSpellSlot: (Int) -> Unit,
    onToggleCondition: (String) -> Unit,
    onToggleDeathSaveSuccess: (Int) -> Unit,
    onToggleDeathSaveFailure: (Int) -> Unit,
    onAdjustResource: (resourceId: String, delta: Int) -> Unit,
    onNextCombatTurn: () -> Unit,
    onResetEncounter: () -> Unit,
    onSaveQuickNote: (title: String, content: String, isPinned: Boolean) -> Unit,
    onOpenAddCustomAction: () -> Unit,
    onOpenDamageHealDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (character == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No active character selected. Please select or import a character.", color = DndTextMuted)
        }
        return
    }

    // Wide 3-column desktop layout
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // COLUMN 1: Vitals, AC, Spell Slots & Resources (32% width)
        Column(
            modifier = Modifier
                .weight(0.32f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header stats badge (AC, Speed, Init, Prof)
            StatBadgesCard(character)

            // Hit Points & Death Saves
            HitPointsCard(
                character = character,
                onTakeDamage = onTakeDamage,
                onHeal = onHeal,
                onToggleSuccess = onToggleDeathSaveSuccess,
                onToggleFailure = onToggleDeathSaveFailure,
                onOpenDamageHeal = onOpenDamageHealDialog
            )

            // Conditions Bar
            ConditionsCard(
                activeConditions = character.conditions,
                onToggleCondition = onToggleCondition
            )

            // Spell Slots Card (if has slots)
            if (character.spellSlotsTotal.isNotEmpty()) {
                SpellSlotsCard(
                    totalSlots = character.spellSlotsTotal,
                    usedSlots = character.spellSlotsUsed,
                    onUseSlot = onUseSpellSlot,
                    onRestoreSlot = onRestoreSpellSlot
                )
            }

            // Resource Trackers Card (Action Surge, Second Wind, Ki, etc.)
            if (character.resources.isNotEmpty()) {
                ResourcesCard(
                    resources = character.resources,
                    onAdjustResource = onAdjustResource
                )
            }
        }

        // COLUMN 2: The Pinned Combat Deck (42% width)
        Column(
            modifier = Modifier
                .weight(0.42f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Deck Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "⚔️ PINNED COMBAT DECK",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = DndGoldLight
                    )
                    Text(
                        text = "Quick-access actions, spells, and tactical session notes",
                        style = MaterialTheme.typography.labelSmall,
                        color = DndTextMuted
                    )
                }

                Button(
                    onClick = onOpenAddCustomAction,
                    colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ Add Action", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Pinned Tactical Notes (from active campaign)
            val pinnedNotes = campaign?.sessionNotes?.filter { it.isPinnedToDashboard } ?: emptyList()
            if (pinnedNotes.isNotEmpty()) {
                pinnedNotes.forEach { note ->
                    PinnedNoteCard(note)
                }
            }

            // Pinned Attacks & Actions
            val pinnedActions = character.actions.filter { it.isPinned }
            if (pinnedActions.isNotEmpty()) {
                Text(
                    text = "FAVORITE ATTACKS & ACTIONS (${pinnedActions.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DndTextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
                pinnedActions.forEach { action ->
                    ActionDeckCard(
                        action = action,
                        onRollAttack = { onRollAttack(action) },
                        onRollDamage = { onRollDamage(action) }
                    )
                }
            }

            // Pinned Spells
            val pinnedSpells = character.spells.filter { it.isPinned }
            if (pinnedSpells.isNotEmpty()) {
                Text(
                    text = "PINNED SPELLS (${pinnedSpells.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DndArcaneCyan,
                    modifier = Modifier.padding(top = 6.dp)
                )
                pinnedSpells.forEach { spell ->
                    SpellDeckCard(
                        spell = spell,
                        onCast = { onCastSpell(spell) }
                    )
                }
            }

            if (pinnedActions.isEmpty() && pinnedSpells.isEmpty() && pinnedNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DndCardSurface, RoundedCornerShape(10.dp))
                        .border(1.dp, DndBorder, RoundedCornerShape(10.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No Pinned Items Yet", fontWeight = FontWeight.Bold, color = DndTextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Pin actions or spells from your Character Sheet, or pin tactical notes from your Campaign Journal!",
                            color = DndTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // COLUMN 3: Encounter & Initiative Order + Session Scratchpad (26% width)
        Column(
            modifier = Modifier
                .weight(0.26f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Encounter & Turn Tracker
            EncounterTrackerCard(
                combatants = combatants,
                onNextTurn = onNextCombatTurn,
                onReset = onResetEncounter
            )

            // Live Session Scratchpad
            LiveScratchpadCard(
                onSaveNote = onSaveQuickNote
            )
        }
    }
}

@Composable
fun StatBadgesCard(char: CharacterSheet) {
    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = char.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = DndGoldLight
                    )
                    Text(
                        text = "${char.race} • ${char.characterClass}",
                        fontSize = 12.sp,
                        color = DndTextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .background(DndGoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .border(1.dp, DndGoldPrimary, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Lvl ${char.level}", fontWeight = FontWeight.Bold, color = DndGoldLight, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stat Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBadge("AC", "${char.armorClass}", "🛡️", DndGoldLight)
                val initModStr = if (char.initiativeModifier >= 0) "+${char.initiativeModifier}" else "${char.initiativeModifier}"
                StatBadge("INIT", initModStr, "⚡", DndArcaneCyan)
                StatBadge("SPEED", "${char.speed}ft", "🏃", DndTextPrimary)
                StatBadge("PROF", "+${char.proficiencyBonus}", "⭐", DndHealthGreen)
            }
        }
    }
}

@Composable
fun StatBadge(label: String, value: String, emoji: String, color: Color) {
    Column(
        modifier = Modifier
            .background(DndCardSurfaceVariant, RoundedCornerShape(8.dp))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$emoji $label", fontSize = 10.sp, color = DndTextMuted, fontWeight = FontWeight.Bold)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
fun HitPointsCard(
    character: CharacterSheet,
    onTakeDamage: (Int) -> Unit,
    onHeal: (Int) -> Unit,
    onToggleSuccess: (Int) -> Unit,
    onToggleFailure: (Int) -> Unit,
    onOpenDamageHeal: () -> Unit
) {
    val hpPercent = if (character.maxHp > 0) (character.currentHp.toFloat() / character.maxHp).coerceIn(0f, 1f) else 0f
    val hpBarColor = when {
        hpPercent > 0.5f -> DndHealthGreen
        hpPercent > 0.2f -> DndGoldPrimary
        else -> DndCrimsonLight
    }

    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("HIT POINTS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = DndTextMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (character.tempHp > 0) {
                        Box(
                            modifier = Modifier
                                .background(DndTempHpBlue.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text("+${character.tempHp} Temp", fontSize = 11.sp, color = DndTempHpBlue, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = "${character.currentHp} / ${character.maxHp} HP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = hpBarColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // HP Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = hpPercent)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(hpBarColor)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Damage / Heal adjustment buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Damage quick buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalButton(onClick = { onTakeDamage(1) }, shape = RoundedCornerShape(6.dp)) { Text("-1", fontSize = 11.sp) }
                    FilledTonalButton(onClick = { onTakeDamage(5) }, shape = RoundedCornerShape(6.dp)) { Text("-5", fontSize = 11.sp) }
                    FilledTonalButton(onClick = { onTakeDamage(10) }, shape = RoundedCornerShape(6.dp)) { Text("-10", fontSize = 11.sp) }
                }

                // Heal quick buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalButton(onClick = { onHeal(1) }, shape = RoundedCornerShape(6.dp)) { Text("+1", fontSize = 11.sp) }
                    FilledTonalButton(onClick = { onHeal(5) }, shape = RoundedCornerShape(6.dp)) { Text("+5", fontSize = 11.sp) }
                    FilledTonalButton(onClick = { onHeal(10) }, shape = RoundedCornerShape(6.dp)) { Text("+10", fontSize = 11.sp) }
                }
            }

            // Death saves (show if 0 HP)
            if (character.currentHp == 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DndDamageRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, DndDamageRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text("UNCONSCIOUS • DEATH SAVES", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = DndDamageRed)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Successes
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Success: ", fontSize = 11.sp, color = DndHealthGreen)
                            for (i in 0 until 3) {
                                val isChecked = character.deathSavesSuccesses > i
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(if (isChecked) DndHealthGreen else Color.Transparent)
                                        .border(1.dp, DndHealthGreen, CircleShape)
                                        .clickable { onToggleSuccess(i) }
                                )
                            }
                        }

                        // Failures
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Failure: ", fontSize = 11.sp, color = DndDamageRed)
                            for (i in 0 until 3) {
                                val isChecked = character.deathSavesFailures > i
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(if (isChecked) DndDamageRed else Color.Transparent)
                                        .border(1.dp, DndDamageRed, CircleShape)
                                        .clickable { onToggleFailure(i) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConditionsCard(
    activeConditions: Set<String>,
    onToggleCondition: (String) -> Unit
) {
    val commonConditions = listOf(
        "Blinded", "Charmed", "Frightened", "Grappled", "Incapacitated",
        "Invisible", "Paralyzed", "Petrified", "Poisoned", "Prone",
        "Restrained", "Stunned", "Unconscious", "Exhaustion"
    )

    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "CONDITIONS & STATUS EFFECTS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = DndTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                commonConditions.forEach { condition ->
                    val isActive = activeConditions.contains(condition)
                    Box(
                        modifier = Modifier
                            .clickable { onToggleCondition(condition) }
                            .background(
                                if (isActive) DndDamageRed.copy(alpha = 0.3f) else DndCardSurfaceVariant,
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                1.dp,
                                if (isActive) DndDamageRed else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = condition,
                            fontSize = 11.sp,
                            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isActive) DndCrimsonLight else DndTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpellSlotsCard(
    totalSlots: Map<Int, Int>,
    usedSlots: Map<Int, Int>,
    onUseSlot: (Int) -> Unit,
    onRestoreSlot: (Int) -> Unit
) {
    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPELL SLOTS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = DndArcaneCyan
                )
                Text(
                    text = "Tap circle to spend / restore",
                    fontSize = 10.sp,
                    color = DndTextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            totalSlots.keys.sorted().forEach { level ->
                val total = totalSlots[level] ?: 0
                val used = usedSlots[level] ?: 0
                val available = total - used

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Level $level",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DndTextPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (i in 0 until total) {
                            val isSpent = i < used
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(if (!isSpent) DndArcaneCyan else Color.White.copy(alpha = 0.1f))
                                    .border(1.5.dp, DndArcaneCyan, CircleShape)
                                    .clickable {
                                        if (isSpent) onRestoreSlot(level) else onUseSlot(level)
                                    }
                            )
                        }

                        Text(
                            text = "$available / $total",
                            fontSize = 11.sp,
                            color = DndTextMuted,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResourcesCard(
    resources: List<com.droid.mydnd.model.ResourceCounter>,
    onAdjustResource: (id: String, delta: Int) -> Unit
) {
    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("CLASS RESOURCES & POWERS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = DndGoldLight)
            Spacer(modifier = Modifier.height(6.dp))

            resources.forEach { res ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(res.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DndTextPrimary)
                        Text("Recharges on ${res.recharge.name.replace("_", " ").lowercase()}", fontSize = 10.sp, color = DndTextMuted)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledTonalButton(
                            onClick = { onAdjustResource(res.id, -1) },
                            modifier = Modifier.size(28.dp),
                            shape = CircleShape,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("-", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "${res.current} / ${res.max}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (res.current > 0) DndGoldLight else DndTextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        FilledTonalButton(
                            onClick = { onAdjustResource(res.id, 1) },
                            modifier = Modifier.size(28.dp),
                            shape = CircleShape,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("+", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionDeckCard(
    action: ActionItem,
    onRollAttack: () -> Unit,
    onRollDamage: () -> Unit
) {
    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndGoldPrimary.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = action.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DndGoldLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = action.type.name.replace("_", " "),
                            fontSize = 9.sp,
                            color = DndTextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (action.range != null) {
                    Text(text = "Range: ${action.range}", fontSize = 11.sp, color = DndTextMuted)
                }
            }

            if (action.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = action.description,
                    fontSize = 11.sp,
                    color = DndTextSecondary(false),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (action.attackBonus != null) {
                    val bonusText = if (action.attackBonus >= 0) "+${action.attackBonus}" else "${action.attackBonus}"
                    ElevatedButton(
                        onClick = onRollAttack,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = DndGoldPrimary,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("ROLL ATTACK ($bonusText)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                if (action.damageFormula != null) {
                    OutlinedButton(
                        onClick = onRollDamage,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DndCrimsonLight),
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        val dmgType = action.damageType?.let { " $it" } ?: ""
                        Text("DAMAGE (${action.damageFormula}$dmgType)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun SpellDeckCard(
    spell: SpellItem,
    onCast: () -> Unit
) {
    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndArcaneCyan.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = spell.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = DndArcaneCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(DndArcaneCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (spell.level == 0) "Cantrip" else "Level ${spell.level}",
                            fontSize = 9.sp,
                            color = DndArcaneCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "${spell.castingTime} • ${spell.range}",
                    fontSize = 11.sp,
                    color = DndTextMuted
                )
            }

            if (spell.damageOrEffect.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Effect: ${spell.damageOrEffect} ${spell.saveDcType?.let { "• $it" } ?: ""}",
                    fontSize = 11.sp,
                    color = DndGoldLight,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (spell.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = spell.description,
                    fontSize = 11.sp,
                    color = DndTextSecondary(false),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onCast,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DndArcaneCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(30.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("CAST & ROLL", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PinnedNoteCard(note: com.droid.mydnd.model.SessionNote) {
    Surface(
        color = DndCardSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, DndGoldPrimary),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📌 ${note.title}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = DndGoldLight
                )
                Text(note.date, fontSize = 10.sp, color = DndTextMuted)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                fontSize = 12.sp,
                color = DndTextPrimary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun EncounterTrackerCard(
    combatants: List<Combatant>,
    onNextTurn: () -> Unit,
    onReset: () -> Unit
) {
    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INITIATIVE ORDER",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = DndGoldLight
                )

                Row {
                    OutlinedButton(
                        onClick = onReset,
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Reset", fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(
                        onClick = onNextTurn,
                        colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("Next Turn ❯", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            combatants.forEach { comb ->
                val bg = if (comb.isCurrentTurn) DndGoldPrimary.copy(alpha = 0.2f) else Color.Transparent
                val border = if (comb.isCurrentTurn) DndGoldPrimary else Color.White.copy(alpha = 0.05f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(bg, RoundedCornerShape(6.dp))
                        .border(1.dp, border, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${comb.initiative}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DndGoldLight,
                            modifier = Modifier.width(24.dp)
                        )
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = comb.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (comb.isCurrentTurn) FontWeight.Bold else FontWeight.Medium,
                                    color = if (comb.isPlayer) DndTextPrimary else DndCrimsonLight
                                )
                                if (comb.isCurrentTurn) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("◀ TURN", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = DndGoldLight)
                                }
                            }
                            if (comb.notes.isNotBlank()) {
                                Text(comb.notes, fontSize = 10.sp, color = DndTextMuted)
                            }
                        }
                    }

                    Text(
                        text = "AC ${comb.armorClass} • ${comb.currentHp} HP",
                        fontSize = 11.sp,
                        color = DndTextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun LiveScratchpadCard(
    onSaveNote: (title: String, content: String, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(true) }

    Surface(
        color = DndCardSurface,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "LIVE SESSION SCRATCHPAD",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = DndTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Note title (e.g. Boss clue)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                placeholder = { Text("Write tactical or lore notes here...") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = CheckboxDefaults.colors(checkedColor = DndGoldPrimary)
                    )
                    Text("Pin to Combat", fontSize = 11.sp, color = DndTextPrimary)
                }

                Button(
                    onClick = {
                        if (content.isNotBlank()) {
                            onSaveNote(title, content, isPinned)
                            title = ""
                            content = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun DndTextSecondary(selected: Boolean): Color {
    return if (selected) DndGoldLight else DndTextPrimary
}
