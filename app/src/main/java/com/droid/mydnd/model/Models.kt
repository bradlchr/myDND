package com.droid.mydnd.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class RollType {
    NORMAL,
    ADVANTAGE,
    DISADVANTAGE
}

data class DiceRollResult(
    val id: String = UUID.randomUUID().toString(),
    val formula: String,
    val total: Int,
    val individualRolls: List<Int>,
    val modifier: Int = 0,
    val rollType: RollType = RollType.NORMAL,
    val isCritical: Boolean = false,
    val isFumble: Boolean = false,
    val label: String = "Roll",
    val timestamp: Long = System.currentTimeMillis()
)

enum class ActionType {
    MELEE_ATTACK,
    RANGED_ATTACK,
    SPELL_ATTACK,
    BONUS_ACTION,
    REACTION,
    CLASS_FEATURE,
    SPECIAL_ACTION,
    ITEM
}

enum class RechargeType {
    NONE,
    SHORT_REST,
    LONG_REST
}

data class ActionItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: ActionType,
    val attackBonus: Int? = null,
    val damageFormula: String? = null,
    val damageType: String? = null,
    val range: String? = null,
    val description: String = "",
    val isPinned: Boolean = false,
    val currentUses: Int? = null,
    val maxUses: Int? = null,
    val recharge: RechargeType = RechargeType.NONE,
    val spellLevel: Int? = null
)

data class SpellItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val level: Int, // 0 = Cantrip, 1..9
    val school: String = "Evocation",
    val castingTime: String = "1 Action",
    val range: String = "60 ft",
    val components: String = "V, S",
    val duration: String = "Instantaneous",
    val description: String = "",
    val damageOrEffect: String = "",
    val saveDcType: String? = null,
    val isConcentration: Boolean = false,
    val isRitual: Boolean = false,
    val isPrepared: Boolean = true,
    val isPinned: Boolean = false
)

data class InventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val quantity: Int = 1,
    val weight: Double = 0.0,
    val isEquipped: Boolean = false,
    val description: String = ""
)

data class FeatureItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val source: String = "Class",
    val description: String = ""
)

data class ResourceCounter(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val current: Int,
    val max: Int,
    val recharge: RechargeType = RechargeType.SHORT_REST
)

data class SessionNote(
    val id: String = UUID.randomUUID().toString(),
    val campaignId: String = "",
    val title: String,
    val date: String,
    val content: String,
    val isPinnedToDashboard: Boolean = false,
    val tags: List<String> = emptyList()
)

data class QuestItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val reward: String = ""
)

data class NpcItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val role: String,
    val location: String = "",
    val notes: String = "",
    val isHostile: Boolean = false
)

data class Campaign(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dungeonMaster: String = "Dungeon Master",
    val description: String = "",
    val characterIds: List<String> = emptyList(),
    val sessionNotes: List<SessionNote> = emptyList(),
    val quests: List<QuestItem> = emptyList(),
    val npcs: List<NpcItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class Combatant(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val initiative: Int,
    val currentHp: Int,
    val maxHp: Int,
    val armorClass: Int,
    val isPlayer: Boolean = false,
    val isCurrentTurn: Boolean = false,
    val conditions: Set<String> = emptySet(),
    val notes: String = ""
)

data class CharacterSheet(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val characterClass: String,
    val race: String,
    val level: Int = 1,
    val xp: Int = 0,
    val background: String = "",
    val alignment: String = "True Neutral",
    val maxHp: Int = 10,
    val currentHp: Int = 10,
    val tempHp: Int = 0,
    val armorClass: Int = 10,
    val initiativeModifier: Int = 0,
    val speed: Int = 30,
    val passivePerception: Int = 10,
    val proficiencyBonus: Int = 2,
    val hitDiceTotal: String = "1d8",
    val hitDiceRemaining: Int = 1,
    val deathSavesSuccesses: Int = 0,
    val deathSavesFailures: Int = 0,
    val abilityScores: Map<String, Int> = mapOf(
        "STR" to 10,
        "DEX" to 10,
        "CON" to 10,
        "INT" to 10,
        "WIS" to 10,
        "CHA" to 10
    ),
    val savingThrowProficiencies: Set<String> = emptySet(),
    val skillProficiencies: Set<String> = emptySet(),
    val skillExpertise: Set<String> = emptySet(),
    val spellSlotsTotal: Map<Int, Int> = emptyMap(),
    val spellSlotsUsed: Map<Int, Int> = emptyMap(),
    val spellSaveDc: Int = 10,
    val spellAttackBonus: Int = 2,
    val resources: List<ResourceCounter> = emptyList(),
    val conditions: Set<String> = emptySet(),
    val pinnedActionIds: Set<String> = emptySet(),
    val actions: List<ActionItem> = emptyList(),
    val spells: List<SpellItem> = emptyList(),
    val inventory: List<InventoryItem> = emptyList(),
    val features: List<FeatureItem> = emptyList(),
    val notes: String = "",
    val campaignId: String? = null,
    val dndBeyondId: String? = null
) {
    fun getAbilityModifier(ability: String): Int {
        val score = abilityScores[ability.uppercase()] ?: 10
        return Math.floorDiv(score - 10, 2)
    }

    fun getSavingThrowBonus(ability: String): Int {
        val baseMod = getAbilityModifier(ability)
        return if (savingThrowProficiencies.contains(ability.uppercase())) {
            baseMod + proficiencyBonus
        } else {
            baseMod
        }
    }

    fun getSkillBonus(skillName: String, ability: String): Int {
        val baseMod = getAbilityModifier(ability)
        return when {
            skillExpertise.contains(skillName) -> baseMod + (proficiencyBonus * 2)
            skillProficiencies.contains(skillName) -> baseMod + proficiencyBonus
            else -> baseMod
        }
    }

    fun takeDamage(amount: Int): CharacterSheet {
        var remainingDmg = amount
        var newTemp = tempHp
        if (newTemp > 0) {
            if (remainingDmg >= newTemp) {
                remainingDmg -= newTemp
                newTemp = 0
            } else {
                newTemp -= remainingDmg
                remainingDmg = 0
            }
        }
        val newHp = (currentHp - remainingDmg).coerceAtLeast(0)
        return copy(
            currentHp = newHp,
            tempHp = newTemp,
            deathSavesSuccesses = if (newHp > 0) 0 else deathSavesSuccesses,
            deathSavesFailures = if (newHp > 0) 0 else deathSavesFailures
        )
    }

    fun heal(amount: Int): CharacterSheet {
        val newHp = (currentHp + amount).coerceAtMost(maxHp)
        return copy(
            currentHp = newHp,
            deathSavesSuccesses = if (newHp > 0) 0 else deathSavesSuccesses,
            deathSavesFailures = if (newHp > 0) 0 else deathSavesFailures
        )
    }

    fun applyShortRest(): CharacterSheet {
        val restoredResources = resources.map { res ->
            if (res.recharge == RechargeType.SHORT_REST) res.copy(current = res.max) else res
        }
        val restoredActions = actions.map { act ->
            if (act.recharge == RechargeType.SHORT_REST && act.maxUses != null) {
                act.copy(currentUses = act.maxUses)
            } else act
        }
        return copy(
            resources = restoredResources,
            actions = restoredActions
        )
    }

    fun applyLongRest(): CharacterSheet {
        val restoredResources = resources.map { res -> res.copy(current = res.max) }
        val restoredActions = actions.map { act ->
            if (act.maxUses != null) act.copy(currentUses = act.maxUses) else act
        }
        val restoredHitDice = (hitDiceRemaining + (level / 2).coerceAtLeast(1)).coerceAtMost(level)
        return copy(
            currentHp = maxHp,
            tempHp = 0,
            deathSavesSuccesses = 0,
            deathSavesFailures = 0,
            hitDiceRemaining = restoredHitDice,
            spellSlotsUsed = spellSlotsTotal.mapValues { 0 },
            resources = restoredResources,
            actions = restoredActions
        )
    }

    fun toggleCondition(condition: String): CharacterSheet {
        val newConditions = if (conditions.contains(condition)) {
            conditions - condition
        } else {
            conditions + condition
        }
        return copy(conditions = newConditions)
    }

    fun togglePinAction(actionId: String): CharacterSheet {
        val newPinned = if (pinnedActionIds.contains(actionId)) {
            pinnedActionIds - actionId
        } else {
            pinnedActionIds + actionId
        }
        val updatedActions = actions.map {
            if (it.id == actionId) it.copy(isPinned = !it.isPinned) else it
        }
        return copy(pinnedActionIds = newPinned, actions = updatedActions)
    }

    fun togglePinSpell(spellId: String): CharacterSheet {
        val updatedSpells = spells.map {
            if (it.id == spellId) it.copy(isPinned = !it.isPinned) else it
        }
        return copy(spells = updatedSpells)
    }

    fun useSpellSlot(slotLevel: Int): CharacterSheet {
        val currentUsed = spellSlotsUsed[slotLevel] ?: 0
        val total = spellSlotsTotal[slotLevel] ?: 0
        if (currentUsed < total) {
            val updated = spellSlotsUsed.toMutableMap()
            updated[slotLevel] = currentUsed + 1
            return copy(spellSlotsUsed = updated)
        }
        return this
    }

    fun restoreSpellSlot(slotLevel: Int): CharacterSheet {
        val currentUsed = spellSlotsUsed[slotLevel] ?: 0
        if (currentUsed > 0) {
            val updated = spellSlotsUsed.toMutableMap()
            updated[slotLevel] = currentUsed - 1
            return copy(spellSlotsUsed = updated)
        }
        return this
    }
}
