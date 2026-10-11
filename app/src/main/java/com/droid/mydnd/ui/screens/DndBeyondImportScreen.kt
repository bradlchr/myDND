package com.droid.mydnd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.mydnd.theme.DndArcaneCyan
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
fun DndBeyondImportScreen(
    isLoading: Boolean,
    message: String?,
    onImportUrl: (String) -> Unit,
    onImportJson: (String) -> Unit,
    onImportArchetype: (String) -> Unit,
    onClearMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var urlOrIdInput by remember { mutableStateOf("") }
    var jsonInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column {
            Text(
                text = "📥 D&D BEYOND CHARACTER IMPORTER",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = DndGoldLight
            )
            Text(
                text = "Import any character sheet directly via URL/ID, paste raw JSON, or load ready-to-play archetypes",
                style = MaterialTheme.typography.bodyMedium,
                color = DndTextMuted
            )
        }

        // Status message banner
        if (message != null) {
            val isSuccess = message.contains("Successfully") || message.contains("Loaded")
            val bannerBg = if (isSuccess) DndHealthGreen.copy(alpha = 0.2f) else DndCrimsonLight.copy(alpha = 0.2f)
            val bannerBorder = if (isSuccess) DndHealthGreen else DndCrimsonLight

            Surface(
                color = bannerBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, bannerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(message, color = if (isSuccess) DndHealthGreen else DndCrimsonLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Button(
                        onClick = onClearMessage,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = DndTextMuted)
                    ) {
                        Text("Dismiss", fontSize = 11.sp)
                    }
                }
            }
        }

        // Section 1: Fetch via D&D Beyond Character URL or ID
        Surface(
            color = DndCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Method 1: D&D Beyond Share URL or Character ID",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DndGoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter your D&D Beyond share link (e.g. https://www.dndbeyond.com/characters/12345678) or just the number ID.",
                    fontSize = 12.sp,
                    color = DndTextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = urlOrIdInput,
                        onValueChange = { urlOrIdInput = it },
                        placeholder = { Text("https://www.dndbeyond.com/characters/142958192 or 142958192") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (urlOrIdInput.isNotBlank()) {
                                onImportUrl(urlOrIdInput)
                            }
                        },
                        enabled = !isLoading && urlOrIdInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(54.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.width(20.dp).height(20.dp), color = Color.Black, strokeWidth = 2.dp)
                        } else {
                            Text("Fetch & Import", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "💡 Tip: Make sure the character privacy in D&D Beyond is set to 'Public' so the API can read it.",
                    fontSize = 11.sp,
                    color = DndTextMuted
                )
            }
        }

        // Section 2: Paste Raw Character JSON
        Surface(
            color = DndCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Method 2: Paste Raw Character JSON",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DndArcaneCyan
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "If you have exported or copied JSON from D&D Beyond, paste it below to instantly parse stats, attacks, and spells.",
                    fontSize = 12.sp,
                    color = DndTextMuted
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = jsonInput,
                    onValueChange = { jsonInput = it },
                    placeholder = { Text("Paste JSON here ({ \"data\": { ... } } or { \"name\": \"...\" })") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            if (jsonInput.isNotBlank()) {
                                onImportJson(jsonInput)
                            }
                        },
                        enabled = !isLoading && jsonInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = DndArcaneCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Parse & Import JSON", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Section 3: 1-Click Preloaded Archetypes
        Surface(
            color = DndCardSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Method 3: Ready-to-Play Authentic Archetypes (Level 5)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DndGoldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Instantly load fully built, authentic 5e character sheets complete with weapons, spells, resources, and maneuvers.",
                    fontSize = 12.sp,
                    color = DndTextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ArchetypeCard(
                        name = "Valeros",
                        sub = "Human Fighter 5",
                        desc = "Greatsword +1, Plate AC 18, Action Surge, Trip Attack & Maneuvers",
                        emoji = "⚔️",
                        onImport = { onImportArchetype("valeros") },
                        modifier = Modifier.weight(1f)
                    )
                    ArchetypeCard(
                        name = "Lyra",
                        sub = "Elf Evocation Wizard 5",
                        desc = "Fireball, Sculpt Spells, Shield, Misty Step, Magic Missile",
                        emoji = "🔮",
                        onImport = { onImportArchetype("lyra") },
                        modifier = Modifier.weight(1f)
                    )
                    ArchetypeCard(
                        name = "Theron",
                        sub = "Elf Arcane Trickster 5",
                        desc = "Rapier + 3d6 Sneak Attack, Cunning Action, Uncanny Dodge",
                        emoji = "🗡️",
                        onImport = { onImportArchetype("theron") },
                        modifier = Modifier.weight(1f)
                    )
                    ArchetypeCard(
                        name = "Brother Gareth",
                        sub = "Dwarf Life Cleric 5",
                        desc = "AC 18, Spirit Guardians, Spiritual Weapon, Preserve Life",
                        emoji = "🛡️",
                        onImport = { onImportArchetype("gareth") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ArchetypeCard(
    name: String,
    sub: String,
    desc: String,
    emoji: String,
    onImport: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DndCardSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(name, fontWeight = FontWeight.Bold, color = DndTextPrimary, fontSize = 14.sp)
                }
                Text(sub, fontSize = 11.sp, color = DndGoldLight, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(desc, fontSize = 10.sp, color = DndTextMuted, lineHeight = 13.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onImport,
                colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth().height(32.dp)
            ) {
                Text("Load Hero", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
