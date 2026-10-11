package com.droid.mydnd.data

import com.droid.mydnd.dice.DiceRoller
import com.droid.mydnd.importer.DndBeyondParser
import com.droid.mydnd.model.Campaign
import com.droid.mydnd.model.CharacterSheet
import com.droid.mydnd.model.Combatant
import com.droid.mydnd.model.DiceRollResult
import com.droid.mydnd.model.RollType
import com.droid.mydnd.model.SessionNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

interface DndRepository {
    val characters: StateFlow<List<CharacterSheet>>
    val activeCharacter: StateFlow<CharacterSheet?>
    val campaigns: StateFlow<List<Campaign>>
    val activeCampaign: StateFlow<Campaign?>
    val combatants: StateFlow<List<Combatant>>
    val diceHistory: StateFlow<List<DiceRollResult>>

    fun selectCharacter(id: String)
    fun saveCharacter(character: CharacterSheet)
    fun deleteCharacter(id: String)

    fun selectCampaign(id: String)
    fun saveCampaign(campaign: Campaign)
    fun deleteCampaign(id: String)
    fun addSessionNote(note: SessionNote)
    fun updateSessionNote(note: SessionNote)
    fun deleteSessionNote(noteId: String)
    fun togglePinSessionNote(noteId: String)

    fun takeDamage(amount: Int)
    fun heal(amount: Int)
    fun setTempHp(amount: Int)
    fun toggleDeathSaveSuccess(index: Int)
    fun toggleDeathSaveFailure(index: Int)
    fun applyShortRest()
    fun applyLongRest()
    fun useSpellSlot(level: Int)
    fun restoreSpellSlot(level: Int)
    fun toggleCondition(condition: String)
    fun togglePinAction(actionId: String)
    fun togglePinSpell(spellId: String)
    fun adjustResource(resourceId: String, delta: Int)

    fun addCombatant(combatant: Combatant)
    fun removeCombatant(id: String)
    fun updateCombatant(combatant: Combatant)
    fun nextCombatTurn()
    fun resetCombatEncounter()

    fun rollD20(rollType: RollType, modifier: Int, label: String): DiceRollResult
    fun rollFormula(formula: String, label: String): DiceRollResult
    fun clearDiceHistory()

    fun importDndBeyondJson(jsonString: String): Result<CharacterSheet>
    suspend fun fetchDndBeyondCharacter(idOrUrl: String): Result<CharacterSheet>
    fun importArchetype(archetypeId: String): CharacterSheet
}

class DndRepositoryImpl : DndRepository {

    private val _characters = MutableStateFlow<List<CharacterSheet>>(
        listOf(
            PreloadedData.valerosFighter,
            PreloadedData.lyraWizard,
            PreloadedData.theronRogue,
            PreloadedData.brotherGarethCleric
        )
    )
    override val characters: StateFlow<List<CharacterSheet>> = _characters.asStateFlow()

    private val _activeCharacter = MutableStateFlow<CharacterSheet?>(PreloadedData.valerosFighter)
    override val activeCharacter: StateFlow<CharacterSheet?> = _activeCharacter.asStateFlow()

    private val _campaigns = MutableStateFlow<List<Campaign>>(
        listOf(
            PreloadedData.strahdCampaign,
            PreloadedData.lostMineCampaign
        )
    )
    override val campaigns: StateFlow<List<Campaign>> = _campaigns.asStateFlow()

    private val _activeCampaign = MutableStateFlow<Campaign?>(PreloadedData.strahdCampaign)
    override val activeCampaign: StateFlow<Campaign?> = _activeCampaign.asStateFlow()

    private val _combatants = MutableStateFlow<List<Combatant>>(
        listOf(
            Combatant(name = "Valeros (Fighter)", initiative = 19, currentHp = 49, maxHp = 49, armorClass = 18, isPlayer = true, isCurrentTurn = true),
            Combatant(name = "Strahd Vampire Spawn", initiative = 16, currentHp = 82, maxHp = 82, armorClass = 15, isPlayer = false, notes = "Regenerates 10 HP unless radiant"),
            Combatant(name = "Lyra (Wizard)", initiative = 15, currentHp = 32, maxHp = 32, armorClass = 13, isPlayer = true),
            Combatant(name = "Brother Gareth (Cleric)", initiative = 11, currentHp = 43, maxHp = 43, armorClass = 18, isPlayer = true)
        )
    )
    override val combatants: StateFlow<List<Combatant>> = _combatants.asStateFlow()

    private val _diceHistory = MutableStateFlow<List<DiceRollResult>>(emptyList())
    override val diceHistory: StateFlow<List<DiceRollResult>> = _diceHistory.asStateFlow()

    override fun selectCharacter(id: String) {
        val found = _characters.value.find { it.id == id }
        if (found != null) {
            _activeCharacter.value = found
        }
    }

    override fun saveCharacter(character: CharacterSheet) {
        val currentList = _characters.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == character.id }
        if (index >= 0) {
            currentList[index] = character
        } else {
            currentList.add(0, character)
        }
        _characters.value = currentList
        if (_activeCharacter.value?.id == character.id || _activeCharacter.value == null) {
            _activeCharacter.value = character
        }
    }

    override fun deleteCharacter(id: String) {
        val filtered = _characters.value.filterNot { it.id == id }
        _characters.value = filtered
        if (_activeCharacter.value?.id == id) {
            _activeCharacter.value = filtered.firstOrNull()
        }
    }

    override fun selectCampaign(id: String) {
        val found = _campaigns.value.find { it.id == id }
        if (found != null) {
            _activeCampaign.value = found
        }
    }

    override fun saveCampaign(campaign: Campaign) {
        val current = _campaigns.value.toMutableList()
        val index = current.indexOfFirst { it.id == campaign.id }
        if (index >= 0) {
            current[index] = campaign
        } else {
            current.add(0, campaign)
        }
        _campaigns.value = current
        if (_activeCampaign.value?.id == campaign.id || _activeCampaign.value == null) {
            _activeCampaign.value = campaign
        }
    }

    override fun deleteCampaign(id: String) {
        val filtered = _campaigns.value.filterNot { it.id == id }
        _campaigns.value = filtered
        if (_activeCampaign.value?.id == id) {
            _activeCampaign.value = filtered.firstOrNull()
        }
    }

    override fun addSessionNote(note: SessionNote) {
        val camp = _activeCampaign.value ?: return
        val updatedNotes = listOf(note) + camp.sessionNotes
        val updatedCamp = camp.copy(sessionNotes = updatedNotes)
        saveCampaign(updatedCamp)
    }

    override fun updateSessionNote(note: SessionNote) {
        val camp = _activeCampaign.value ?: return
        val updatedNotes = camp.sessionNotes.map { if (it.id == note.id) note else it }
        val updatedCamp = camp.copy(sessionNotes = updatedNotes)
        saveCampaign(updatedCamp)
    }

    override fun deleteSessionNote(noteId: String) {
        val camp = _activeCampaign.value ?: return
        val updatedNotes = camp.sessionNotes.filterNot { it.id == noteId }
        val updatedCamp = camp.copy(sessionNotes = updatedNotes)
        saveCampaign(updatedCamp)
    }

    override fun togglePinSessionNote(noteId: String) {
        val camp = _activeCampaign.value ?: return
        val updatedNotes = camp.sessionNotes.map {
            if (it.id == noteId) it.copy(isPinnedToDashboard = !it.isPinnedToDashboard) else it
        }
        val updatedCamp = camp.copy(sessionNotes = updatedNotes)
        saveCampaign(updatedCamp)
    }

    override fun takeDamage(amount: Int) {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.takeDamage(amount))
    }

    override fun heal(amount: Int) {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.heal(amount))
    }

    override fun setTempHp(amount: Int) {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.copy(tempHp = amount.coerceAtLeast(0)))
    }

    override fun toggleDeathSaveSuccess(index: Int) {
        val char = _activeCharacter.value ?: return
        val current = char.deathSavesSuccesses
        val next = if (current >= index + 1) index else index + 1
        saveCharacter(char.copy(deathSavesSuccesses = next))
    }

    override fun toggleDeathSaveFailure(index: Int) {
        val char = _activeCharacter.value ?: return
        val current = char.deathSavesFailures
        val next = if (current >= index + 1) index else index + 1
        saveCharacter(char.copy(deathSavesFailures = next))
    }

    override fun applyShortRest() {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.applyShortRest())
    }

    override fun applyLongRest() {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.applyLongRest())
    }

    override fun useSpellSlot(level: Int) {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.useSpellSlot(level))
    }

    override fun restoreSpellSlot(level: Int) {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.restoreSpellSlot(level))
    }

    override fun toggleCondition(condition: String) {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.toggleCondition(condition))
    }

    override fun togglePinAction(actionId: String) {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.togglePinAction(actionId))
    }

    override fun togglePinSpell(spellId: String) {
        val char = _activeCharacter.value ?: return
        saveCharacter(char.togglePinSpell(spellId))
    }

    override fun adjustResource(resourceId: String, delta: Int) {
        val char = _activeCharacter.value ?: return
        val updatedResources = char.resources.map { res ->
            if (res.id == resourceId) {
                val newCount = (res.current + delta).coerceIn(0, res.max)
                res.copy(current = newCount)
            } else res
        }
        saveCharacter(char.copy(resources = updatedResources))
    }

    override fun addCombatant(combatant: Combatant) {
        val updated = (_combatants.value + combatant).sortedByDescending { it.initiative }
        _combatants.value = updated
    }

    override fun removeCombatant(id: String) {
        _combatants.value = _combatants.value.filterNot { it.id == id }
    }

    override fun updateCombatant(combatant: Combatant) {
        _combatants.value = _combatants.value.map { if (it.id == combatant.id) combatant else it }
    }

    override fun nextCombatTurn() {
        val list = _combatants.value
        if (list.isEmpty()) return
        val currentIndex = list.indexOfFirst { it.isCurrentTurn }
        val nextIndex = if (currentIndex < 0 || currentIndex >= list.size - 1) 0 else currentIndex + 1
        _combatants.value = list.mapIndexed { idx, item ->
            item.copy(isCurrentTurn = (idx == nextIndex))
        }
    }

    override fun resetCombatEncounter() {
        _combatants.value = _combatants.value.mapIndexed { idx, item ->
            item.copy(isCurrentTurn = (idx == 0))
        }
    }

    override fun rollD20(rollType: RollType, modifier: Int, label: String): DiceRollResult {
        val result = DiceRoller.rollD20(rollType, modifier, label)
        _diceHistory.value = listOf(result) + _diceHistory.value.take(49)
        return result
    }

    override fun rollFormula(formula: String, label: String): DiceRollResult {
        val result = DiceRoller.rollFormula(formula, label)
        _diceHistory.value = listOf(result) + _diceHistory.value.take(49)
        return result
    }

    override fun clearDiceHistory() {
        _diceHistory.value = emptyList()
    }

    override fun importDndBeyondJson(jsonString: String): Result<CharacterSheet> {
        return try {
            val sheet = DndBeyondParser.parseCharacterJson(jsonString)
            saveCharacter(sheet)
            selectCharacter(sheet.id)
            Result.success(sheet)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchDndBeyondCharacter(idOrUrl: String): Result<CharacterSheet> = withContext(Dispatchers.IO) {
        try {
            val charId = DndBeyondParser.extractCharacterId(idOrUrl)
                ?: return@withContext Result.failure(IllegalArgumentException("Invalid D&D Beyond character URL or ID: '$idOrUrl'"))

            val endpoint = "https://character-service.dndbeyond.com/character/v5/character/$charId"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile; rv:109.0)")

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val responseText = reader.use { it.readText() }
                conn.disconnect()
                importDndBeyondJson(responseText)
            } else {
                conn.disconnect()
                Result.failure(Exception("D&D Beyond returned HTTP $responseCode. Please make sure the character is set to 'Public' on D&D Beyond."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network or import error: ${e.localizedMessage ?: "Unknown error"}. You can also paste the JSON directly."))
        }
    }

    override fun importArchetype(archetypeId: String): CharacterSheet {
        val template = when (archetypeId) {
            "valeros" -> PreloadedData.valerosFighter
            "lyra" -> PreloadedData.lyraWizard
            "theron" -> PreloadedData.theronRogue
            "gareth" -> PreloadedData.brotherGarethCleric
            else -> PreloadedData.valerosFighter
        }
        val imported = template.copy(
            id = UUID.randomUUID().toString(),
            name = "${template.name} (Copy)"
        )
        saveCharacter(imported)
        selectCharacter(imported.id)
        return imported
    }
}
