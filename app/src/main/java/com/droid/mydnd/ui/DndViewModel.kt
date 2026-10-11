package com.droid.mydnd.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droid.mydnd.data.DndRepository
import com.droid.mydnd.data.DndRepositoryImpl
import com.droid.mydnd.model.ActionItem
import com.droid.mydnd.model.ActionType
import com.droid.mydnd.model.Campaign
import com.droid.mydnd.model.CharacterSheet
import com.droid.mydnd.model.Combatant
import com.droid.mydnd.model.DiceRollResult
import com.droid.mydnd.model.RollType
import com.droid.mydnd.model.SessionNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class NavigationTab(val label: String, val subtitle: String) {
    COMBAT("Combat Dashboard", "Active Battle & Pinned Quick-Bar"),
    CHARACTERS("Characters", "Character Sheets & Stats"),
    CAMPAIGNS("Campaigns", "Session Notes, Quests & NPCs"),
    SPELLBOOK("Spellbook & Rules", "5e Spells & Condition Compendium"),
    DND_BEYOND_IMPORT("D&D Beyond Import", "Import from URL/ID or JSON"),
    DICE_ROLLER("Dice & History", "Polyhedral Dice & Roll Log")
}

class DndViewModel(
    private val repository: DndRepository = DndRepositoryImpl()
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(NavigationTab.COMBAT)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()

    val characters: StateFlow<List<CharacterSheet>> = repository.characters
    val activeCharacter: StateFlow<CharacterSheet?> = repository.activeCharacter

    val campaigns: StateFlow<List<Campaign>> = repository.campaigns
    val activeCampaign: StateFlow<Campaign?> = repository.activeCampaign

    val combatants: StateFlow<List<Combatant>> = repository.combatants
    val diceHistory: StateFlow<List<DiceRollResult>> = repository.diceHistory

    private val _lastRoll = MutableStateFlow<DiceRollResult?>(null)
    val lastRoll: StateFlow<DiceRollResult?> = _lastRoll.asStateFlow()

    private val _advantageType = MutableStateFlow(RollType.NORMAL)
    val advantageType: StateFlow<RollType> = _advantageType.asStateFlow()

    private val _importLoading = MutableStateFlow(false)
    val importLoading: StateFlow<Boolean> = _importLoading.asStateFlow()

    private val _importMessage = MutableStateFlow<String?>(null)
    val importMessage: StateFlow<String?> = _importMessage.asStateFlow()

    fun selectTab(tab: NavigationTab) {
        _selectedTab.value = tab
    }

    fun selectCharacter(id: String) {
        repository.selectCharacter(id)
    }

    fun selectCampaign(id: String) {
        repository.selectCampaign(id)
    }

    fun setAdvantage(type: RollType) {
        _advantageType.value = type
    }

    fun rollD20(modifier: Int, label: String) {
        val result = repository.rollD20(_advantageType.value, modifier, label)
        _lastRoll.value = result
    }

    fun rollFormula(formula: String, label: String) {
        val result = repository.rollFormula(formula, label)
        _lastRoll.value = result
    }

    fun dismissLastRoll() {
        _lastRoll.value = null
    }

    fun clearDiceHistory() {
        repository.clearDiceHistory()
    }

    fun takeDamage(amount: Int) {
        repository.takeDamage(amount)
    }

    fun heal(amount: Int) {
        repository.heal(amount)
    }

    fun setTempHp(amount: Int) {
        repository.setTempHp(amount)
    }

    fun toggleDeathSaveSuccess(index: Int) {
        repository.toggleDeathSaveSuccess(index)
    }

    fun toggleDeathSaveFailure(index: Int) {
        repository.toggleDeathSaveFailure(index)
    }

    fun applyShortRest() {
        repository.applyShortRest()
    }

    fun applyLongRest() {
        repository.applyLongRest()
    }

    fun useSpellSlot(level: Int) {
        repository.useSpellSlot(level)
    }

    fun restoreSpellSlot(level: Int) {
        repository.restoreSpellSlot(level)
    }

    fun toggleCondition(condition: String) {
        repository.toggleCondition(condition)
    }

    fun togglePinAction(actionId: String) {
        repository.togglePinAction(actionId)
    }

    fun togglePinSpell(spellId: String) {
        repository.togglePinSpell(spellId)
    }

    fun adjustResource(resourceId: String, delta: Int) {
        repository.adjustResource(resourceId, delta)
    }

    fun addSessionNote(title: String, content: String, isPinned: Boolean) {
        val campId = activeCampaign.value?.id ?: ""
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val note = SessionNote(
            campaignId = campId,
            title = title.ifBlank { "Session Note" },
            date = dateFormat.format(Date()),
            content = content,
            isPinnedToDashboard = isPinned
        )
        repository.addSessionNote(note)
    }

    fun togglePinSessionNote(noteId: String) {
        repository.togglePinSessionNote(noteId)
    }

    fun deleteSessionNote(noteId: String) {
        repository.deleteSessionNote(noteId)
    }

    fun addCombatant(name: String, init: Int, hp: Int, ac: Int, isPlayer: Boolean) {
        val combatant = Combatant(
            name = name,
            initiative = init,
            currentHp = hp,
            maxHp = hp,
            armorClass = ac,
            isPlayer = isPlayer
        )
        repository.addCombatant(combatant)
    }

    fun removeCombatant(id: String) {
        repository.removeCombatant(id)
    }

    fun nextCombatTurn() {
        repository.nextCombatTurn()
    }

    fun resetCombatEncounter() {
        repository.resetCombatEncounter()
    }

    fun importDndBeyondUrl(idOrUrl: String) {
        viewModelScope.launch {
            _importLoading.value = true
            _importMessage.value = "Fetching character from D&D Beyond..."
            val result = repository.fetchDndBeyondCharacter(idOrUrl)
            _importLoading.value = false
            result.fold(
                onSuccess = { char ->
                    _importMessage.value = "Successfully imported '${char.name}' (Level ${char.level} ${char.characterClass})!"
                    _selectedTab.value = NavigationTab.COMBAT
                },
                onFailure = { err ->
                    _importMessage.value = "Import failed: ${err.message}"
                }
            )
        }
    }

    fun importDndBeyondJson(json: String) {
        _importLoading.value = true
        val result = repository.importDndBeyondJson(json)
        _importLoading.value = false
        result.fold(
            onSuccess = { char ->
                _importMessage.value = "Successfully imported '${char.name}' from JSON!"
                _selectedTab.value = NavigationTab.COMBAT
            },
            onFailure = { err ->
                _importMessage.value = "Invalid JSON format: ${err.message}"
            }
        )
    }

    fun importArchetype(archetypeId: String) {
        val imported = repository.importArchetype(archetypeId)
        _importMessage.value = "Loaded '${imported.name}'!"
        _selectedTab.value = NavigationTab.COMBAT
    }

    fun clearImportMessage() {
        _importMessage.value = null
    }

    fun saveNewCharacter(
        name: String,
        className: String,
        race: String,
        level: Int,
        maxHp: Int,
        ac: Int,
        speed: Int,
        str: Int,
        dex: Int,
        con: Int,
        int: Int,
        wis: Int,
        cha: Int
    ) {
        val abilityScores = mapOf(
            "STR" to str,
            "DEX" to dex,
            "CON" to con,
            "INT" to int,
            "WIS" to wis,
            "CHA" to cha
        )
        val dexMod = Math.floorDiv(dex - 10, 2)
        val strMod = Math.floorDiv(str - 10, 2)
        val prof = 2 + ((level - 1) / 4)

        val defaultWeapon = ActionItem(
            name = "Melee Attack",
            type = ActionType.MELEE_ATTACK,
            attackBonus = strMod + prof,
            damageFormula = if (strMod >= 0) "1d8+$strMod" else "1d8$strMod",
            damageType = "Slashing",
            isPinned = true
        )

        val newSheet = CharacterSheet(
            name = name.ifBlank { "New Adventurer" },
            characterClass = className.ifBlank { "Fighter 1" },
            race = race.ifBlank { "Human" },
            level = level,
            maxHp = maxHp,
            currentHp = maxHp,
            armorClass = ac,
            speed = speed,
            initiativeModifier = dexMod,
            proficiencyBonus = prof,
            abilityScores = abilityScores,
            actions = listOf(defaultWeapon),
            pinnedActionIds = setOf(defaultWeapon.id)
        )
        repository.saveCharacter(newSheet)
        repository.selectCharacter(newSheet.id)
        _selectedTab.value = NavigationTab.COMBAT
    }

    fun saveNewCampaign(title: String, dm: String, description: String) {
        val newCamp = Campaign(
            title = title.ifBlank { "New Campaign" },
            dungeonMaster = dm.ifBlank { "Dungeon Master" },
            description = description
        )
        repository.saveCampaign(newCamp)
        repository.selectCampaign(newCamp.id)
    }

    fun addCustomAction(
        name: String,
        type: ActionType,
        attackBonus: Int?,
        damageFormula: String?,
        damageType: String?,
        range: String?,
        description: String,
        isPinned: Boolean
    ) {
        val char = activeCharacter.value ?: return
        val newAction = ActionItem(
            name = name,
            type = type,
            attackBonus = attackBonus,
            damageFormula = damageFormula,
            damageType = damageType,
            range = range,
            description = description,
            isPinned = isPinned
        )
        val updatedActions = char.actions + newAction
        val updatedPinned = if (isPinned) char.pinnedActionIds + newAction.id else char.pinnedActionIds
        repository.saveCharacter(char.copy(actions = updatedActions, pinnedActionIds = updatedPinned))
    }
}
