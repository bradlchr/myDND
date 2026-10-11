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
import com.droid.mydnd.model.Campaign
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
fun CampaignsScreen(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    onSelectCampaign: (String) -> Unit,
    onTogglePinNote: (String) -> Unit,
    onDeleteNote: (String) -> Unit,
    onOpenNewCampaignDialog: () -> Unit,
    onOpenNewNoteDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var campTab by remember { mutableStateOf(0) } // 0=Session Notes, 1=Quests, 2=NPCs

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Campaign List (28% width)
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
                    text = "CAMPAIGNS (${campaigns.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = DndGoldLight
                )

                Button(
                    onClick = onOpenNewCampaignDialog,
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
                campaigns.forEach { camp ->
                    val isSelected = (camp.id == activeCampaign?.id)
                    val bg = if (isSelected) DndGoldPrimary.copy(alpha = 0.15f) else DndCardSurface
                    val border = if (isSelected) DndGoldPrimary else DndBorder

                    Surface(
                        color = bg,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, border),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCampaign(camp.id) }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = camp.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) DndGoldLight else DndTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Dungeon Master: ${camp.dungeonMaster}",
                                fontSize = 11.sp,
                                color = DndTextMuted
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${camp.sessionNotes.size} Notes", fontSize = 11.sp, color = DndGoldLight)
                                Text("${camp.quests.size} Quests", fontSize = 11.sp, color = DndHealthGreen)
                                Text("${camp.npcs.size} NPCs", fontSize = 11.sp, color = DndTextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Campaign Details & Tabs (72% width)
        if (activeCampaign != null) {
            Column(
                modifier = Modifier
                    .weight(0.72f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Banner
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
                                    text = activeCampaign.title,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = DndGoldLight
                                )
                                Text(
                                    text = "Dungeon Master: ${activeCampaign.dungeonMaster}",
                                    fontSize = 13.sp,
                                    color = DndTextMuted
                                )
                            }

                            Button(
                                onClick = onOpenNewNoteDialog,
                                colors = ButtonDefaults.buttonColors(containerColor = DndGoldPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+ Add Session Note", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (activeCampaign.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = activeCampaign.description,
                                fontSize = 12.sp,
                                color = DndTextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Sub-navigation tabs: Session Notes, Quests, NPCs
                TabRow(
                    selectedTabIndex = campTab,
                    containerColor = DndCardSurface,
                    contentColor = DndGoldLight
                ) {
                    Tab(
                        selected = (campTab == 0),
                        onClick = { campTab = 0 },
                        text = { Text("Session Notes (${activeCampaign.sessionNotes.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = (campTab == 1),
                        onClick = { campTab = 1 },
                        text = { Text("Quests (${activeCampaign.quests.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = (campTab == 2),
                        onClick = { campTab = 2 },
                        text = { Text("Key NPCs (${activeCampaign.npcs.size})", fontWeight = FontWeight.Bold) }
                    )
                }

                // Tab Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (campTab) {
                        0 -> SessionNotesTabContent(activeCampaign, onTogglePinNote, onDeleteNote)
                        1 -> QuestsTabContent(activeCampaign)
                        2 -> NpcsTabContent(activeCampaign)
                    }
                }
            }
        }
    }
}

@Composable
fun SessionNotesTabContent(
    campaign: Campaign,
    onTogglePin: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    if (campaign.sessionNotes.isEmpty()) {
        Text("No session notes recorded yet. Click '+ Add Session Note' to begin journaling!", color = DndTextMuted, modifier = Modifier.padding(16.dp))
    } else {
        campaign.sessionNotes.forEach { note ->
            Surface(
                color = DndCardSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    if (note.isPinnedToDashboard) 1.5.dp else 1.dp,
                    if (note.isPinnedToDashboard) DndGoldPrimary else DndBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (note.isPinnedToDashboard) "📌 ${note.title}" else note.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (note.isPinnedToDashboard) DndGoldLight else DndTextPrimary
                            )
                            if (note.isPinnedToDashboard) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(DndGoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Pinned to Combat", fontSize = 10.sp, color = DndGoldLight, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(note.date, fontSize = 11.sp, color = DndTextMuted)
                            Spacer(modifier = Modifier.width(10.dp))

                            FilledTonalButton(
                                onClick = { onTogglePin(note.id) },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(if (note.isPinnedToDashboard) "Unpin" else "Pin to Combat", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = note.content,
                        fontSize = 12.sp,
                        color = DndTextPrimary,
                        lineHeight = 17.sp
                    )

                    if (note.tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            note.tags.forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("#$tag", fontSize = 10.sp, color = DndTextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestsTabContent(campaign: Campaign) {
    if (campaign.quests.isEmpty()) {
        Text("No active quests listed for this campaign.", color = DndTextMuted, modifier = Modifier.padding(16.dp))
    } else {
        campaign.quests.forEach { quest ->
            Surface(
                color = DndCardSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (quest.isCompleted) "✓ ${quest.title}" else "⚔️ ${quest.title}",
                                fontWeight = FontWeight.Bold,
                                color = if (quest.isCompleted) DndHealthGreen else DndGoldLight,
                                fontSize = 14.sp
                            )
                        }
                        if (quest.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(quest.description, fontSize = 12.sp, color = DndTextPrimary)
                        }
                        if (quest.reward.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Reward: ${quest.reward}", fontSize = 11.sp, color = DndGoldLight)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(if (quest.isCompleted) DndHealthGreen.copy(alpha = 0.2f) else DndGoldPrimary.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (quest.isCompleted) "Completed" else "In Progress",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quest.isCompleted) DndHealthGreen else DndGoldLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NpcsTabContent(campaign: Campaign) {
    if (campaign.npcs.isEmpty()) {
        Text("No NPCs noted yet.", color = DndTextMuted, modifier = Modifier.padding(16.dp))
    } else {
        campaign.npcs.forEach { npc ->
            Surface(
                color = DndCardSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DndBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(npc.name, fontWeight = FontWeight.Bold, color = DndTextPrimary, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("• ${npc.role}", fontSize = 12.sp, color = DndGoldLight)
                        }
                        if (npc.location.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Location: ${npc.location}", fontSize = 11.sp, color = DndTextMuted)
                        }
                        if (npc.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(npc.notes, fontSize = 12.sp, color = DndTextPrimary)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(if (npc.isHostile) DndCrimsonLight.copy(alpha = 0.2f) else DndHealthGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (npc.isHostile) "Hostile" else "Ally / Neutral",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (npc.isHostile) DndCrimsonLight else DndHealthGreen
                        )
                    }
                }
            }
        }
    }
}
