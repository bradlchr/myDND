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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.mydnd.model.DiceRollResult
import com.droid.mydnd.model.RollType
import com.droid.mydnd.theme.DndBorder
import com.droid.mydnd.theme.DndCardSurface
import com.droid.mydnd.theme.DndCardSurfaceVariant
import com.droid.mydnd.theme.DndCrimsonLight
import com.droid.mydnd.theme.DndGoldLight
import com.droid.mydnd.theme.DndGoldPrimary
import com.droid.mydnd.theme.DndHealthGreen
import com.droid.mydnd.theme.DndTextMuted
import com.droid.mydnd.theme.DndTextPrimary

@Composable
fun DiceRollerScreen(
    diceHistory: List<DiceRollResult>,
    advantageType: RollType,
    onSetAdvantage: (RollType) -> Unit,
    onRollD20: (modifier: Int, label: String) -> Unit,
    onRollFormula: (formula: String, label: String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var customFormula by remember { mutableStateOf("2d6+3") }
    var customLabel by remember { mutableStateOf("Custom Roll") }
    var d20Modifier by remember { mutableStateOf(5) }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Interactive Polyhedral Dice Board (45% width)
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = DndCardSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🎲 POLYHEDRAL DICE ROLLER",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = DndGoldLight
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Advantage / Disadvantage Selector
                    Text("ROLL TYPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DndTextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            RollType.NORMAL to "Normal",
                            RollType.ADVANTAGE to "Advantage",
                            RollType.DISADVANTAGE to "Disadvantage"
                        ).forEach { (type, label) ->
                            val isSel = (advantageType == type)
                            val col = when (type) {
                                RollType.NORMAL -> DndGoldPrimary
                                RollType.ADVANTAGE -> DndHealthGreen
                                RollType.DISADVANTAGE -> DndCrimsonLight
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSetAdvantage(type) }
                                    .background(if (isSel) col else DndCardSurfaceVariant, RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isSel) col else DndBorder, RoundedCornerShape(8.dp))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.Black else DndTextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Big d20 roll button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("D20 Check / Attack", fontWeight = FontWeight.Bold, color = DndTextPrimary)
                            Text("Mod: ${if (d20Modifier >= 0) "+$d20Modifier" else "$d20Modifier"}", fontSize = 12.sp, color = DndGoldLight)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FilledTonalButton(onClick = { d20Modifier-- }, shape = CircleShape, modifier = Modifier.size(32.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                                Text("-", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            FilledTonalButton(onClick = { d20Modifier++ }, shape = CircleShape, modifier = Modifier.size(32.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                                Text("+", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = { onRollD20(d20Modifier, "d20 Check") },
                                colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Text("ROLL d20", fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Polyhedral dice grid (d4, d6, d8, d10, d12, d100)
                    Text("STANDARD DICE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DndTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(4, 6, 8, 10, 12, 100).forEach { sides ->
                            Surface(
                                color = DndCardSurfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onRollFormula("1d$sides", "d$sides Roll") }
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("d$sides", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DndGoldLight)
                                    Text("Roll", fontSize = 9.sp, color = DndTextMuted)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom Formula Builder
                    Text("CUSTOM FORMULA BUILDER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DndTextMuted)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customFormula,
                            onValueChange = { customFormula = it },
                            label = { Text("Formula (e.g. 4d6+3, 8d6)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (customFormula.isNotBlank()) {
                                    onRollFormula(customFormula, customLabel)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("ROLL", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Right Column: Dice Roll History (55% width)
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📜 COMBAT ROLL HISTORY (${diceHistory.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = DndGoldLight
                )

                if (diceHistory.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onClearHistory,
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Clear Log", fontSize = 11.sp)
                    }
                }
            }

            if (diceHistory.isEmpty()) {
                Surface(
                    color = DndCardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No rolls yet. Click any die to roll!", color = DndTextMuted)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    diceHistory.forEach { roll ->
                        val isCrit = roll.isCritical
                        val isFumble = roll.isFumble
                        val borderColor = when {
                            isCrit -> DndGoldLight
                            isFumble -> DndCrimsonLight
                            else -> DndBorder
                        }

                        Surface(
                            color = DndCardSurface,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(roll.label, fontWeight = FontWeight.Bold, color = DndTextPrimary, fontSize = 13.sp)
                                        if (isCrit) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("CRIT!", color = DndGoldLight, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                        } else if (isFumble) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("FUMBLE!", color = DndCrimsonLight, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                                        }
                                    }

                                    val rollsText = if (roll.individualRolls.size > 1) {
                                        "Rolls: [${roll.individualRolls.joinToString(", ")}]"
                                    } else {
                                        "Base: ${roll.individualRolls.firstOrNull() ?: 0}"
                                    }
                                    val modText = if (roll.modifier != 0) (if (roll.modifier > 0) " + ${roll.modifier}" else " - ${-roll.modifier}") else ""
                                    Text(
                                        text = "${roll.formula} • $rollsText$modText",
                                        fontSize = 11.sp,
                                        color = DndTextMuted
                                    )
                                }

                                Text(
                                    text = "${roll.total}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isCrit) DndGoldLight else if (isFumble) DndCrimsonLight else DndGoldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
