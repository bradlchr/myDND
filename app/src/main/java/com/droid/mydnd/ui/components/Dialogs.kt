package com.droid.mydnd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.mydnd.model.ActionType
import com.droid.mydnd.theme.DndCardSurface
import com.droid.mydnd.theme.DndCrimsonLight
import com.droid.mydnd.theme.DndGoldLight
import com.droid.mydnd.theme.DndGoldPrimary
import com.droid.mydnd.theme.DndHealthGreen
import com.droid.mydnd.theme.DndTempHpBlue
import com.droid.mydnd.theme.DndTextMuted
import com.droid.mydnd.theme.DndTextPrimary

@Composable
fun DamageHealDialog(
    onDismiss: () -> Unit,
    onApplyDamage: (Int) -> Unit,
    onApplyHeal: (Int) -> Unit,
    onApplyTempHp: (Int) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.toIntOrNull() ?: 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Adjust Hit Points", fontWeight = FontWeight.Bold, color = DndTextPrimary)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { if (it.all { ch -> ch.isDigit() }) amountText = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick buttons
                Text("Quick Presets", fontSize = 12.sp, color = DndTextMuted)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(1, 5, 10, 20).forEach { preset ->
                        FilledTonalButton(
                            onClick = { amountText = preset.toString() },
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        ) {
                            Text("$preset")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (amount > 0) {
                            onApplyDamage(amount)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DndCrimsonLight)
                ) {
                    Text("Damage", color = Color.White)
                }

                Button(
                    onClick = {
                        if (amount > 0) {
                            onApplyHeal(amount)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DndHealthGreen)
                ) {
                    Text("Heal", color = Color.White)
                }

                Button(
                    onClick = {
                        if (amount >= 0) {
                            onApplyTempHp(amount)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DndTempHpBlue)
                ) {
                    Text("Temp HP", color = Color.Black)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DndTextMuted)
            }
        }
    )
}

@Composable
fun NewSessionNoteDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, content: String, isPinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New Session / Combat Note", fontWeight = FontWeight.Bold, color = DndTextPrimary)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title (e.g. Troll Weakness, Crypt Clue)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Note Content") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pin to Combat Dashboard for easy reference",
                        fontSize = 13.sp,
                        color = DndTextPrimary
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(title, content, isPinned)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black)
            ) {
                Text("Save Note", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DndTextMuted)
            }
        }
    )
}

@Composable
fun NewCharacterDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, className: String, race: String, level: Int, maxHp: Int, ac: Int, speed: Int, str: Int, dex: Int, con: Int, int: Int, wis: Int, cha: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("Fighter 5") }
    var race by remember { mutableStateOf("Human") }
    var levelText by remember { mutableStateOf("5") }
    var maxHpText by remember { mutableStateOf("45") }
    var acText by remember { mutableStateOf("18") }
    var speedText by remember { mutableStateOf("30") }

    var strText by remember { mutableStateOf("16") }
    var dexText by remember { mutableStateOf("14") }
    var conText by remember { mutableStateOf("16") }
    var intText by remember { mutableStateOf("10") }
    var wisText by remember { mutableStateOf("12") }
    var chaText by remember { mutableStateOf("10") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Character", fontWeight = FontWeight.Bold, color = DndTextPrimary)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Character Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Class & Subclass") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = race,
                        onValueChange = { race = it },
                        label = { Text("Race") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = levelText,
                        onValueChange = { levelText = it },
                        label = { Text("Level") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = maxHpText,
                        onValueChange = { maxHpText = it },
                        label = { Text("Max HP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = acText,
                        onValueChange = { acText = it },
                        label = { Text("AC") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Ability Scores", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DndGoldLight)
                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = strText, onValueChange = { strText = it }, label = { Text("STR") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(value = dexText, onValueChange = { dexText = it }, label = { Text("DEX") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(value = conText, onValueChange = { conText = it }, label = { Text("CON") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = intText, onValueChange = { intText = it }, label = { Text("INT") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(value = wisText, onValueChange = { wisText = it }, label = { Text("WIS") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(value = chaText, onValueChange = { chaText = it }, label = { Text("CHA") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        name,
                        className,
                        race,
                        levelText.toIntOrNull() ?: 1,
                        maxHpText.toIntOrNull() ?: 10,
                        acText.toIntOrNull() ?: 10,
                        speedText.toIntOrNull() ?: 30,
                        strText.toIntOrNull() ?: 10,
                        dexText.toIntOrNull() ?: 10,
                        conText.toIntOrNull() ?: 10,
                        intText.toIntOrNull() ?: 10,
                        wisText.toIntOrNull() ?: 10,
                        chaText.toIntOrNull() ?: 10
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black)
            ) {
                Text("Create Character", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = DndTextMuted)
            }
        }
    )
}

@Composable
fun NewCampaignDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, dm: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var dm by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Campaign", fontWeight = FontWeight.Bold, color = DndTextPrimary) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Campaign Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = dm,
                    onValueChange = { dm = it },
                    label = { Text("Dungeon Master") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Setting") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(title, dm, description)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black)
            ) {
                Text("Create Campaign", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DndTextMuted) }
        }
    )
}

@Composable
fun AddCustomActionDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, type: ActionType, attackBonus: Int?, damageFormula: String?, damageType: String?, range: String?, description: String, isPinned: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(ActionType.MELEE_ATTACK) }
    var attackBonusText by remember { mutableStateOf("+7") }
    var damageFormula by remember { mutableStateOf("1d8+4") }
    var damageType by remember { mutableStateOf("Slashing") }
    var range by remember { mutableStateOf("5 ft") }
    var description by remember { mutableStateOf("") }
    var isPinned by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Action / Attack", fontWeight = FontWeight.Bold, color = DndTextPrimary) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Action Name (e.g. Longsword, Fireball, Shield)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = attackBonusText,
                        onValueChange = { attackBonusText = it },
                        label = { Text("Attack Bonus (e.g. +7)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = range,
                        onValueChange = { range = it },
                        label = { Text("Range") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = damageFormula,
                        onValueChange = { damageFormula = it },
                        label = { Text("Damage Formula (e.g. 2d6+5)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = damageType,
                        onValueChange = { damageType = it },
                        label = { Text("Damage Type") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Effects") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isPinned, onCheckedChange = { isPinned = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pin to Combat Deck immediately", fontSize = 13.sp, color = DndTextPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bonusInt = attackBonusText.replace("+", "").trim().toIntOrNull()
                    onSave(name, type, bonusInt, damageFormula, damageType, range, description, isPinned)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black)
            ) {
                Text("Add Action", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = DndTextMuted) }
        }
    )
}
