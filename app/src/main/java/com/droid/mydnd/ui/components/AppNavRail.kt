package com.droid.mydnd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.mydnd.theme.DndCardSurface
import com.droid.mydnd.theme.DndCardSurfaceVariant
import com.droid.mydnd.theme.DndGoldLight
import com.droid.mydnd.theme.DndGoldPrimary
import com.droid.mydnd.theme.DndTextMuted
import com.droid.mydnd.theme.DndTextPrimary
import com.droid.mydnd.ui.NavigationTab

@Composable
fun AppNavRail(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DndCardSurface,
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "TABLETOP NAVIGATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = DndTextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )

                NavigationTab.values().forEach { tab ->
                    val isSelected = (tab == selectedTab)
                    val bg = if (isSelected) DndGoldPrimary.copy(alpha = 0.15f) else Color.Transparent
                    val border = if (isSelected) DndGoldPrimary else Color.Transparent
                    val textColor = if (isSelected) DndGoldLight else DndTextSecondary(isSelected)

                    val emoji = when (tab) {
                        NavigationTab.COMBAT -> "⚔️"
                        NavigationTab.CHARACTERS -> "🧙"
                        NavigationTab.CAMPAIGNS -> "📜"
                        NavigationTab.SPELLBOOK -> "📖"
                        NavigationTab.DND_BEYOND_IMPORT -> "📥"
                        NavigationTab.DICE_ROLLER -> "🎲"
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .background(bg, RoundedCornerShape(8.dp))
                            .border(1.dp, border, RoundedCornerShape(8.dp))
                            .clickable { onTabSelected(tab) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = emoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = textColor
                            )
                        }
                    }
                }
            }

            // Bottom info card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DndCardSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "Combat Session Mode",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DndGoldLight
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Desktop Wide View active. Pin actions, spells, and notes to your battle dashboard.",
                        fontSize = 10.sp,
                        color = DndTextMuted,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

private fun DndTextSecondary(selected: Boolean): Color {
    return if (selected) DndGoldLight else DndTextPrimary
}
