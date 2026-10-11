package com.droid.mydnd.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.droid.mydnd.model.ActionItem
import com.droid.mydnd.model.SpellItem
import com.droid.mydnd.theme.DndObsidianBg
import com.droid.mydnd.ui.DndViewModel
import com.droid.mydnd.ui.NavigationTab
import com.droid.mydnd.ui.components.AddCustomActionDialog
import com.droid.mydnd.ui.components.AppNavRail
import com.droid.mydnd.ui.components.AppTopBar
import com.droid.mydnd.ui.components.DamageHealDialog
import com.droid.mydnd.ui.components.DiceRollBanner
import com.droid.mydnd.ui.components.NewCampaignDialog
import com.droid.mydnd.ui.components.NewCharacterDialog
import com.droid.mydnd.ui.components.NewSessionNoteDialog
import com.droid.mydnd.ui.screens.CampaignsScreen
import com.droid.mydnd.ui.screens.CharactersScreen
import com.droid.mydnd.ui.screens.CombatDashboardScreen
import com.droid.mydnd.ui.screens.DiceRollerScreen
import com.droid.mydnd.ui.screens.DndBeyondImportScreen
import com.droid.mydnd.ui.screens.SpellbookScreen

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: DndViewModel = viewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val characters by viewModel.characters.collectAsStateWithLifecycle()
    val activeCharacter by viewModel.activeCharacter.collectAsStateWithLifecycle()
    val campaigns by viewModel.campaigns.collectAsStateWithLifecycle()
    val activeCampaign by viewModel.activeCampaign.collectAsStateWithLifecycle()
    val combatants by viewModel.combatants.collectAsStateWithLifecycle()
    val diceHistory by viewModel.diceHistory.collectAsStateWithLifecycle()
    val lastRoll by viewModel.lastRoll.collectAsStateWithLifecycle()
    val advantageType by viewModel.advantageType.collectAsStateWithLifecycle()
    val importLoading by viewModel.importLoading.collectAsStateWithLifecycle()
    val importMessage by viewModel.importMessage.collectAsStateWithLifecycle()

    // Dialog visibility states
    var showDamageHealDialog by remember { mutableStateOf(false) }
    var showNewNoteDialog by remember { mutableStateOf(false) }
    var showNewCharacterDialog by remember { mutableStateOf(false) }
    var showNewCampaignDialog by remember { mutableStateOf(false) }
    var showAddCustomActionDialog by remember { mutableStateOf(false) }

    Surface(
        color = DndObsidianBg,
        modifier = modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
            AppTopBar(
                characters = characters,
                activeCharacter = activeCharacter,
                campaigns = campaigns,
                activeCampaign = activeCampaign,
                advantageType = advantageType,
                onSelectCharacter = { viewModel.selectCharacter(it) },
                onSelectCampaign = { viewModel.selectCampaign(it) },
                onRollDie = { sides -> viewModel.rollFormula("1d$sides", "d$sides Roll") },
                onSetAdvantage = { viewModel.setAdvantage(it) },
                onShortRest = { viewModel.applyShortRest() },
                onLongRest = { viewModel.applyLongRest() },
                onOpenDamageHealDialog = { showDamageHealDialog = true }
            )

            // Roll Outcome Banner
            DiceRollBanner(
                roll = lastRoll,
                onDismiss = { viewModel.dismissLastRoll() }
            )

            // Main Desktop Body (Nav Rail on Left + Active Tab Screen on Right)
            Row(modifier = Modifier.fillMaxSize()) {
                AppNavRail(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.selectTab(it) }
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DndObsidianBg)
                ) {
                    when (selectedTab) {
                        NavigationTab.COMBAT -> {
                            CombatDashboardScreen(
                                character = activeCharacter,
                                campaign = activeCampaign,
                                combatants = combatants,
                                onRollAttack = { action: ActionItem ->
                                    val bonus = action.attackBonus ?: 0
                                    viewModel.rollD20(bonus, "${action.name} Attack")
                                },
                                onRollDamage = { action: ActionItem ->
                                    val formula = action.damageFormula ?: "1d8"
                                    viewModel.rollFormula(formula, "${action.name} Damage")
                                },
                                onCastSpell = { spell: SpellItem ->
                                    if (spell.level > 0) {
                                        viewModel.useSpellSlot(spell.level)
                                    }
                                    if (spell.damageOrEffect.isNotBlank() && spell.damageOrEffect.contains("d")) {
                                        val firstDice = spell.damageOrEffect.split(" ").firstOrNull { it.contains("d") } ?: "2d6"
                                        viewModel.rollFormula(firstDice, "${spell.name} Cast")
                                    } else {
                                        val atkBonus = activeCharacter?.spellAttackBonus ?: 5
                                        viewModel.rollD20(atkBonus, "${spell.name} Spell Attack")
                                    }
                                },
                                onTakeDamage = { viewModel.takeDamage(it) },
                                onHeal = { viewModel.heal(it) },
                                onUseSpellSlot = { viewModel.useSpellSlot(it) },
                                onRestoreSpellSlot = { viewModel.restoreSpellSlot(it) },
                                onToggleCondition = { viewModel.toggleCondition(it) },
                                onToggleDeathSaveSuccess = { viewModel.toggleDeathSaveSuccess(it) },
                                onToggleDeathSaveFailure = { viewModel.toggleDeathSaveFailure(it) },
                                onAdjustResource = { id, delta -> viewModel.adjustResource(id, delta) },
                                onNextCombatTurn = { viewModel.nextCombatTurn() },
                                onResetEncounter = { viewModel.resetCombatEncounter() },
                                onSaveQuickNote = { title, content, isPinned -> viewModel.addSessionNote(title, content, isPinned) },
                                onOpenAddCustomAction = { showAddCustomActionDialog = true },
                                onOpenDamageHealDialog = { showDamageHealDialog = true }
                            )
                        }

                        NavigationTab.CHARACTERS -> {
                            CharactersScreen(
                                characters = characters,
                                activeCharacter = activeCharacter,
                                onSelectCharacter = { viewModel.selectCharacter(it) },
                                onTogglePinAction = { viewModel.togglePinAction(it) },
                                onTogglePinSpell = { viewModel.togglePinSpell(it) },
                                onOpenNewCharacterDialog = { showNewCharacterDialog = true }
                            )
                        }

                        NavigationTab.CAMPAIGNS -> {
                            CampaignsScreen(
                                campaigns = campaigns,
                                activeCampaign = activeCampaign,
                                onSelectCampaign = { viewModel.selectCampaign(it) },
                                onTogglePinNote = { viewModel.togglePinSessionNote(it) },
                                onDeleteNote = { viewModel.deleteSessionNote(it) },
                                onOpenNewCampaignDialog = { showNewCampaignDialog = true },
                                onOpenNewNoteDialog = { showNewNoteDialog = true }
                            )
                        }

                        NavigationTab.SPELLBOOK -> {
                            SpellbookScreen(
                                onAddSpellToCharacter = { spell ->
                                    val char = activeCharacter
                                    if (char != null) {
                                        val pinnedSpell = spell.copy(isPinned = true)
                                        val updatedSpells = char.spells + pinnedSpell
                                        viewModel.selectTab(NavigationTab.COMBAT)
                                    }
                                }
                            )
                        }

                        NavigationTab.DND_BEYOND_IMPORT -> {
                            DndBeyondImportScreen(
                                isLoading = importLoading,
                                message = importMessage,
                                onImportUrl = { viewModel.importDndBeyondUrl(it) },
                                onImportJson = { viewModel.importDndBeyondJson(it) },
                                onImportArchetype = { viewModel.importArchetype(it) },
                                onClearMessage = { viewModel.clearImportMessage() }
                            )
                        }

                        NavigationTab.DICE_ROLLER -> {
                            DiceRollerScreen(
                                diceHistory = diceHistory,
                                advantageType = advantageType,
                                onSetAdvantage = { viewModel.setAdvantage(it) },
                                onRollD20 = { mod, label -> viewModel.rollD20(mod, label) },
                                onRollFormula = { formula, label -> viewModel.rollFormula(formula, label) },
                                onClearHistory = { viewModel.clearDiceHistory() }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (showDamageHealDialog) {
        DamageHealDialog(
            onDismiss = { showDamageHealDialog = false },
            onApplyDamage = { viewModel.takeDamage(it) },
            onApplyHeal = { viewModel.heal(it) },
            onApplyTempHp = { viewModel.setTempHp(it) }
        )
    }

    if (showNewNoteDialog) {
        NewSessionNoteDialog(
            onDismiss = { showNewNoteDialog = false },
            onSave = { title, content, isPinned -> viewModel.addSessionNote(title, content, isPinned) }
        )
    }

    if (showNewCharacterDialog) {
        NewCharacterDialog(
            onDismiss = { showNewCharacterDialog = false },
            onSave = { name, className, race, level, maxHp, ac, speed, str, dex, con, int, wis, cha ->
                viewModel.saveNewCharacter(name, className, race, level, maxHp, ac, speed, str, dex, con, int, wis, cha)
            }
        )
    }

    if (showNewCampaignDialog) {
        NewCampaignDialog(
            onDismiss = { showNewCampaignDialog = false },
            onSave = { title, dm, desc -> viewModel.saveNewCampaign(title, dm, desc) }
        )
    }

    if (showAddCustomActionDialog) {
        AddCustomActionDialog(
            onDismiss = { showAddCustomActionDialog = false },
            onSave = { name, type, attackBonus, damageFormula, damageType, range, desc, isPinned ->
                viewModel.addCustomAction(name, type, attackBonus, damageFormula, damageType, range, desc, isPinned)
            }
        )
    }
}
