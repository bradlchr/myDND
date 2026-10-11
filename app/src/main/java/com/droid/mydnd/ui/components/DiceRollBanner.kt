package com.droid.mydnd.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droid.mydnd.model.DiceRollResult
import com.droid.mydnd.model.RollType
import com.droid.mydnd.theme.DndCardSurface
import com.droid.mydnd.theme.DndCrimsonLight
import com.droid.mydnd.theme.DndGoldLight
import com.droid.mydnd.theme.DndGoldPrimary
import com.droid.mydnd.theme.DndTextMuted
import com.droid.mydnd.theme.DndTextPrimary

@Composable
fun DiceRollBanner(
    roll: DiceRollResult?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = roll != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (roll == null) return@AnimatedVisibility

        val borderColor = when {
            roll.isCritical -> DndGoldLight
            roll.isFumble -> DndCrimsonLight
            else -> DndGoldPrimary.copy(alpha = 0.6f)
        }

        val badgeBg = when {
            roll.isCritical -> DndGoldPrimary.copy(alpha = 0.25f)
            roll.isFumble -> DndCrimsonLight.copy(alpha = 0.25f)
            else -> Color.White.copy(alpha = 0.08f)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(DndCardSurface, RoundedCornerShape(12.dp))
                .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon / Indicator
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(badgeBg, RoundedCornerShape(10.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (roll.isCritical) {
                            Text("⭐", fontSize = 22.sp)
                        } else if (roll.isFumble) {
                            Text("⚠️", fontSize = 22.sp)
                        } else {
                            Text("🎲", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = roll.label,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DndTextPrimary
                            )
                            if (roll.isCritical) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "NATURAL 20! CRITICAL HIT!",
                                    color = DndGoldLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            } else if (roll.isFumble) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "NATURAL 1! FUMBLE!",
                                    color = DndCrimsonLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        val rollsDisplay = if (roll.individualRolls.size > 1) {
                            val typeLabel = if (roll.rollType == RollType.ADVANTAGE) " (Advantage)" else if (roll.rollType == RollType.DISADVANTAGE) " (Disadvantage)" else ""
                            "Rolls: ${roll.individualRolls.joinToString(", ")}$typeLabel"
                        } else {
                            "Base: ${roll.individualRolls.firstOrNull() ?: 0} ${if (roll.modifier != 0) (if (roll.modifier > 0) "+ ${roll.modifier}" else "- ${-roll.modifier}") else ""}"
                        }

                        Text(
                            text = "${roll.formula} • $rollsDisplay",
                            color = DndTextMuted,
                            fontSize = 13.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${roll.total}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (roll.isCritical) DndGoldLight else if (roll.isFumble) DndCrimsonLight else DndGoldPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    IconButton(onClick = onDismiss) {
                        Text(
                            text = "✕",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = DndTextMuted
                        )
                    }
                }
            }
        }
    }
}
