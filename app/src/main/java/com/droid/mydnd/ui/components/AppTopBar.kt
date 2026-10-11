package com.droid.mydnd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.droid.mydnd.model.Campaign
import com.droid.mydnd.model.CharacterSheet
import com.droid.mydnd.model.RollType
import com.droid.mydnd.theme.DndCardSurface
import com.droid.mydnd.theme.DndCardSurfaceVariant
import com.droid.mydnd.theme.DndCrimsonLight
import com.droid.mydnd.theme.DndGoldLight
import com.droid.mydnd.theme.DndGoldPrimary
import com.droid.mydnd.theme.DndHealthGreen
import com.droid.mydnd.theme.DndTextMuted
import com.droid.mydnd.theme.DndTextPrimary

@Composable
fun AppTopBar(
    characters: List<CharacterSheet>,
    activeCharacter: CharacterSheet?,
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    advantageType: RollType,
    onSelectCharacter: (String) -> Unit,
    onSelectCampaign: (String) -> Unit,
    onRollDie: (Int) -> Unit,
    onSetAdvantage: (RollType) -> Unit,
    onShortRest: () -> Unit,
    onLongRest: () -> Unit,
    onOpenDamageHealDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var charMenuExpanded by remember { mutableStateOf(false) }
    var campMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = DndCardSurface,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Logo & Dropdowns
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Logo Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(DndGoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.5.dp, DndGoldPrimary, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚔️", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "MyDND",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = DndGoldLight
                    )
                    Text(
                        text = "Desktop Companion",
                        style = MaterialTheme.typography.labelSmall,
                        color = DndTextMuted
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Active Character Selector
                Box {
                    Row(
                        modifier = Modifier
                            .background(DndCardSurfaceVariant, RoundedCornerShape(8.dp))
                            .border(1.dp, DndGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { charMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🧙 ", fontSize = 14.sp)
                        Column {
                            Text(
                                text = activeCharacter?.name ?: "Select Character",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = DndTextPrimary
                            )
                            if (activeCharacter != null) {
                                Text(
                                    text = "Lvl ${activeCharacter.level} ${activeCharacter.characterClass}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DndGoldLight
                                )
                            }
                        }
                        Text(" ▾", color = DndTextMuted, fontSize = 12.sp)
                    }

                    DropdownMenu(
                        expanded = charMenuExpanded,
                        onDismissRequest = { charMenuExpanded = false }
                    ) {
                        characters.forEach { char ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(char.name, fontWeight = FontWeight.Bold)
                                        Text("Level ${char.level} ${char.characterClass}", style = MaterialTheme.typography.labelSmall)
                                    }
                                },
                                onClick = {
                                    onSelectCharacter(char.id)
                                    charMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Active Campaign Selector
                Box {
                    Row(
                        modifier = Modifier
                            .background(DndCardSurfaceVariant, RoundedCornerShape(8.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .clickable { campMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📜 ", fontSize = 14.sp)
                        Column {
                            Text(
                                text = activeCampaign?.title ?: "Select Campaign",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = DndTextPrimary,
                                maxLines = 1
                            )
                            if (activeCampaign != null) {
                                Text(
                                    text = "DM: ${activeCampaign.dungeonMaster}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DndTextMuted
                                )
                            }
                        }
                        Text(" ▾", color = DndTextMuted, fontSize = 12.sp)
                    }

                    DropdownMenu(
                        expanded = campMenuExpanded,
                        onDismissRequest = { campMenuExpanded = false }
                    ) {
                        campaigns.forEach { camp ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(camp.title, fontWeight = FontWeight.Bold)
                                        Text("DM: ${camp.dungeonMaster}", style = MaterialTheme.typography.labelSmall)
                                    }
                                },
                                onClick = {
                                    onSelectCampaign(camp.id)
                                    campMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Center: Polyhedral Quick-Roller Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(DndCardSurfaceVariant, RoundedCornerShape(10.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                // Advantage toggle chip
                val advLabel = when (advantageType) {
                    RollType.NORMAL -> "Normal"
                    RollType.ADVANTAGE -> "Adv"
                    RollType.DISADVANTAGE -> "Disadv"
                }
                val advColor = when (advantageType) {
                    RollType.NORMAL -> DndTextMuted
                    RollType.ADVANTAGE -> DndHealthGreen
                    RollType.DISADVANTAGE -> DndCrimsonLight
                }

                Box(
                    modifier = Modifier
                        .clickable {
                            val next = when (advantageType) {
                                RollType.NORMAL -> RollType.ADVANTAGE
                                RollType.ADVANTAGE -> RollType.DISADVANTAGE
                                RollType.DISADVANTAGE -> RollType.NORMAL
                            }
                            onSetAdvantage(next)
                        }
                        .background(advColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "[$advLabel]",
                        color = advColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                listOf(4, 6, 8, 10, 12, 20, 100).forEach { sides ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .clickable { onRollDie(sides) }
                            .background(
                                if (sides == 20) DndGoldPrimary.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                1.dp,
                                if (sides == 20) DndGoldPrimary else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "d$sides",
                            fontSize = 12.sp,
                            fontWeight = if (sides == 20) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (sides == 20) DndGoldLight else DndTextPrimary
                        )
                    }
                }
            }

            // Right: Rests & Damage/Heal quick tools
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = onOpenDamageHealDialog,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DndGoldLight),
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("± HP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(6.dp))

                FilledTonalButton(
                    onClick = onShortRest,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Short Rest", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(6.dp))

                ElevatedButton(
                    onClick = onLongRest,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = DndGoldPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Long Rest", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
