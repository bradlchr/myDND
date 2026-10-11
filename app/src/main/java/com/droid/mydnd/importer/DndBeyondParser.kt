package com.droid.mydnd.importer

import com.droid.mydnd.model.ActionItem
import com.droid.mydnd.model.ActionType
import com.droid.mydnd.model.CharacterSheet
import com.droid.mydnd.model.FeatureItem
import com.droid.mydnd.model.InventoryItem
import com.droid.mydnd.model.RechargeType
import com.droid.mydnd.model.ResourceCounter
import com.droid.mydnd.model.SpellItem
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

object DndBeyondParser {

    private val ABILITY_NAMES = listOf("STR", "DEX", "CON", "INT", "WIS", "CHA")

    /**
     * Parses D&D Beyond JSON string (either character-service API response or exported JSON)
     */
    fun parseCharacterJson(jsonString: String): CharacterSheet {
        val root = JSONObject(jsonString)
        val charObj = when {
            root.has("data") -> root.getJSONObject("data")
            root.has("character") -> root.getJSONObject("character")
            else -> root
        }

        val dndBeyondId = charObj.optString("id", UUID.randomUUID().toString())
        val name = charObj.optString("name", "Adventurer").ifBlank { "Adventurer" }

        // Class and Level extraction
        val classesArray = charObj.optJSONArray("classes") ?: JSONArray()
        var totalLevel = 0
        val classNames = mutableListOf<String>()
        var primaryHitDie = 8

        for (i in 0 until classesArray.length()) {
            val c = classesArray.optJSONObject(i) ?: continue
            val level = c.optInt("level", 1)
            totalLevel += level
            val def = c.optJSONObject("definition")
            val baseClassName = def?.optString("name") ?: "Hero"
            val subDef = c.optJSONObject("subclassDefinition")
            val subClassName = subDef?.optString("name")

            val fullClassName = if (!subClassName.isNullOrBlank()) {
                "$baseClassName ($subClassName) $level"
            } else {
                "$baseClassName $level"
            }
            classNames.add(fullClassName)

            if (i == 0 && def != null) {
                primaryHitDie = def.optInt("hitDice", 8)
            }
        }
        if (totalLevel == 0) totalLevel = 1
        val classString = if (classNames.isNotEmpty()) classNames.joinToString(" / ") else "Fighter 1"

        // Race
        val raceObj = charObj.optJSONObject("race")
        val race = raceObj?.optString("fullName")
            ?: raceObj?.optString("baseRaceName")
            ?: "Human"

        // Ability Scores (IDs: 1=STR, 2=DEX, 3=CON, 4=INT, 5=WIS, 6=CHA)
        val abilityScores = mutableMapOf(
            "STR" to 10,
            "DEX" to 10,
            "CON" to 10,
            "INT" to 10,
            "WIS" to 10,
            "CHA" to 10
        )

        val statsArray = charObj.optJSONArray("stats")
        if (statsArray != null) {
            for (i in 0 until statsArray.length()) {
                val statItem = statsArray.optJSONObject(i) ?: continue
                val statId = statItem.optInt("id", i + 1)
                val baseVal = statItem.optInt("value", 10)
                if (statId in 1..6) {
                    val abilityName = ABILITY_NAMES[statId - 1]
                    abilityScores[abilityName] = baseVal
                }
            }
        }

        // Apply bonus stats if available
        val bonusStatsArray = charObj.optJSONArray("bonusStats")
        if (bonusStatsArray != null) {
            for (i in 0 until bonusStatsArray.length()) {
                val statItem = bonusStatsArray.optJSONObject(i) ?: continue
                val statId = statItem.optInt("id", i + 1)
                val bonus = statItem.optInt("value", 0)
                if (statId in 1..6 && bonus > 0) {
                    val abilityName = ABILITY_NAMES[statId - 1]
                    val curr = abilityScores[abilityName] ?: 10
                    abilityScores[abilityName] = curr + bonus
                }
            }
        }

        // Apply overrides if available
        val overrideStatsArray = charObj.optJSONArray("overrideStats")
        if (overrideStatsArray != null) {
            for (i in 0 until overrideStatsArray.length()) {
                val statItem = overrideStatsArray.optJSONObject(i) ?: continue
                val statId = statItem.optInt("id", i + 1)
                val override = statItem.optInt("value", 0)
                if (statId in 1..6 && override > 0) {
                    val abilityName = ABILITY_NAMES[statId - 1]
                    abilityScores[abilityName] = override
                }
            }
        }

        val conMod = Math.floorDiv((abilityScores["CON"] ?: 10) - 10, 2)
        val dexMod = Math.floorDiv((abilityScores["DEX"] ?: 10) - 10, 2)
        val strMod = Math.floorDiv((abilityScores["STR"] ?: 10) - 10, 2)
        val intMod = Math.floorDiv((abilityScores["INT"] ?: 10) - 10, 2)
        val wisMod = Math.floorDiv((abilityScores["WIS"] ?: 10) - 10, 2)
        val chaMod = Math.floorDiv((abilityScores["CHA"] ?: 10) - 10, 2)

        val proficiencyBonus = 2 + ((totalLevel - 1) / 4)

        // HP calculation
        val baseHp = charObj.optInt("baseHitPoints", 0)
        val bonusHp = charObj.optInt("bonusHitPoints", 0)
        val overrideHp = charObj.optInt("overrideHitPoints", 0)
        val removedHp = charObj.optInt("removedHitPoints", 0)
        val tempHp = charObj.optInt("temporaryHitPoints", 0)

        val maxHp = if (overrideHp > 0) {
            overrideHp
        } else if (baseHp > 0) {
            baseHp + (conMod * totalLevel) + bonusHp
        } else {
            // standard estimation: primaryHitDie + (primaryHitDie/2 + 1 + conMod) * (totalLevel - 1)
            (primaryHitDie + conMod) + ((primaryHitDie / 2 + 1 + conMod) * (totalLevel - 1))
        }.coerceAtLeast(1)

        val currentHp = (maxHp - removedHp).coerceIn(0, maxHp)

        // Speed & Armor Class
        var speed = 30
        val weightSpeeds = charObj.optJSONObject("weightSpeeds")
        val normalSpeed = weightSpeeds?.optJSONObject("normal")
        if (normalSpeed != null) {
            speed = normalSpeed.optInt("walk", 30)
        }

        // Inventory and Weapons/Armor
        val inventory = mutableListOf<InventoryItem>()
        val actions = mutableListOf<ActionItem>()
        var armorAc = 0
        var hasShield = false

        val invArray = charObj.optJSONArray("inventory")
        if (invArray != null) {
            for (i in 0 until invArray.length()) {
                val itemObj = invArray.optJSONObject(i) ?: continue
                val def = itemObj.optJSONObject("definition") ?: continue
                val itemName = def.optString("name", "Item")
                val isEquipped = itemObj.optBoolean("equipped", false)
                val quantity = itemObj.optInt("quantity", 1)
                val weight = def.optDouble("weight", 0.0)
                val desc = def.optString("description", "")
                val itemType = def.optString("type", "")

                inventory.add(
                    InventoryItem(
                        name = itemName,
                        quantity = quantity,
                        weight = weight,
                        isEquipped = isEquipped,
                        description = desc
                    )
                )

                // If weapon, create action
                val damageObj = def.optJSONObject("damage")
                val isWeapon = itemType.contains("Weapon", ignoreCase = true) || damageObj != null
                if (isWeapon) {
                    val dice = damageObj?.optString("diceString") ?: "1d6"
                    val dmgTypeObj = def.optJSONObject("damageType")
                    val dmgTypeName = dmgTypeObj?.optString("name")
                        ?: def.optString("damageType", "slashing")
                    val attackType = if (def.optInt("attackType", 1) == 2) ActionType.RANGED_ATTACK else ActionType.MELEE_ATTACK
                    val modToUse = if (attackType == ActionType.RANGED_ATTACK) dexMod else maxOf(strMod, dexMod)
                    val attackBonus = modToUse + proficiencyBonus

                    actions.add(
                        ActionItem(
                            name = itemName,
                            type = attackType,
                            attackBonus = attackBonus,
                            damageFormula = if (modToUse >= 0) "$dice+$modToUse" else "$dice$modToUse",
                            damageType = dmgTypeName,
                            range = if (attackType == ActionType.RANGED_ATTACK) "80/320 ft" else "5 ft",
                            description = desc,
                            isPinned = isEquipped // auto-pin equipped weapons
                        )
                    )
                }

                // Check armor
                val baseArmor = def.optInt("armorClass", 0)
                if (isEquipped && baseArmor > 0) {
                    if (def.optString("armorTypeId") == "4" || itemName.contains("Shield", ignoreCase = true)) {
                        hasShield = true
                    } else if (baseArmor > armorAc) {
                        armorAc = baseArmor
                    }
                }
            }
        }

        val totalAc = if (armorAc > 0) {
            armorAc + (if (hasShield) 2 else 0) + dexMod.coerceAtMost(2)
        } else {
            10 + dexMod + (if (hasShield) 2 else 0)
        }

        // Spells extraction
        val spells = mutableListOf<SpellItem>()
        val classSpellsArray = charObj.optJSONArray("classSpells")
        if (classSpellsArray != null) {
            for (i in 0 until classSpellsArray.length()) {
                val classSpellObj = classSpellsArray.optJSONObject(i) ?: continue
                val spArray = classSpellObj.optJSONArray("spells") ?: continue
                for (j in 0 until spArray.length()) {
                    val sObj = spArray.optJSONObject(j) ?: continue
                    val def = sObj.optJSONObject("definition") ?: continue
                    val spellName = def.optString("name")
                    val level = def.optInt("level", 0)
                    val schoolObj = def.optJSONObject("school")
                    val school = schoolObj?.optString("name") ?: "Evocation"
                    val desc = def.optString("description", "")
                    val isPrepared = sObj.optBoolean("prepared", true)
                    val castingTime = def.optJSONObject("activation")?.optString("activationType") ?: "1 Action"
                    val rangeObj = def.optJSONObject("range")
                    val rangeStr = rangeObj?.optString("rangeValue")?.let { "$it ft" } ?: "60 ft"

                    spells.add(
                        SpellItem(
                            name = spellName,
                            level = level,
                            school = school,
                            castingTime = castingTime,
                            range = rangeStr,
                            description = desc,
                            isPrepared = isPrepared,
                            isPinned = (level == 0 || (level in 1..2 && isPrepared)) // Pin cantrips and prepared low level
                        )
                    )
                }
            }
        }

        // Features & Traits
        val features = mutableListOf<FeatureItem>()
        val actionsCustom = charObj.optJSONObject("actions")
        val classActions = actionsCustom?.optJSONArray("class")
        if (classActions != null) {
            for (i in 0 until classActions.length()) {
                val act = classActions.optJSONObject(i) ?: continue
                val actName = act.optString("name")
                val desc = act.optString("description", "")
                if (actName.isNotBlank()) {
                    features.add(FeatureItem(name = actName, source = "Class", description = desc))
                    // Also create an action item if combat-oriented
                    val isBonus = desc.contains("bonus action", ignoreCase = true)
                    val isReaction = desc.contains("reaction", ignoreCase = true)
                    val type = when {
                        isBonus -> ActionType.BONUS_ACTION
                        isReaction -> ActionType.REACTION
                        else -> ActionType.CLASS_FEATURE
                    }
                    actions.add(
                        ActionItem(
                            name = actName,
                            type = type,
                            description = desc,
                            isPinned = true,
                            recharge = RechargeType.SHORT_REST
                        )
                    )
                }
            }
        }

        // Resources (Hit dice, spell slots, class resources)
        val resources = mutableListOf<ResourceCounter>()
        if (classString.contains("Fighter", ignoreCase = true)) {
            resources.add(ResourceCounter(name = "Action Surge", current = 1, max = 1, recharge = RechargeType.SHORT_REST))
            resources.add(ResourceCounter(name = "Second Wind", current = 1, max = 1, recharge = RechargeType.SHORT_REST))
        }
        if (classString.contains("Rogue", ignoreCase = true)) {
            resources.add(ResourceCounter(name = "Sneak Attack", current = 1, max = 1, recharge = RechargeType.NONE))
        }
        if (classString.contains("Barbarian", ignoreCase = true)) {
            resources.add(ResourceCounter(name = "Rages", current = 3, max = 3, recharge = RechargeType.LONG_REST))
        }
        if (classString.contains("Monk", ignoreCase = true)) {
            resources.add(ResourceCounter(name = "Ki Points", current = totalLevel, max = totalLevel, recharge = RechargeType.SHORT_REST))
        }
        if (classString.contains("Cleric", ignoreCase = true) || classString.contains("Paladin", ignoreCase = true)) {
            resources.add(ResourceCounter(name = "Channel Divinity", current = 2, max = 2, recharge = RechargeType.SHORT_REST))
        }

        // Spell slots based on level if spellcaster
        val spellSlotsTotal = mutableMapOf<Int, Int>()
        if (classString.contains("Wizard", ignoreCase = true) || classString.contains("Cleric", ignoreCase = true)) {
            when (totalLevel) {
                1 -> spellSlotsTotal[1] = 2
                2 -> { spellSlotsTotal[1] = 3 }
                3 -> { spellSlotsTotal[1] = 4; spellSlotsTotal[2] = 2 }
                4 -> { spellSlotsTotal[1] = 4; spellSlotsTotal[2] = 3 }
                5 -> { spellSlotsTotal[1] = 4; spellSlotsTotal[2] = 3; spellSlotsTotal[3] = 2 }
                else -> { spellSlotsTotal[1] = 4; spellSlotsTotal[2] = 3; spellSlotsTotal[3] = 3; spellSlotsTotal[4] = 1 }
            }
        }

        // Pinned actions: collect all pinned action IDs
        val pinnedIds = actions.filter { it.isPinned }.map { it.id }.toSet()

        return CharacterSheet(
            id = UUID.randomUUID().toString(),
            name = name,
            characterClass = classString,
            race = race,
            level = totalLevel,
            maxHp = maxHp,
            currentHp = currentHp,
            tempHp = tempHp,
            armorClass = totalAc,
            initiativeModifier = dexMod,
            speed = speed,
            passivePerception = 10 + wisMod,
            proficiencyBonus = proficiencyBonus,
            hitDiceTotal = "${totalLevel}d$primaryHitDie",
            hitDiceRemaining = totalLevel,
            abilityScores = abilityScores,
            savingThrowProficiencies = setOf("STR", "CON"),
            skillProficiencies = setOf("Athletics", "Perception", "Survival"),
            spellSlotsTotal = spellSlotsTotal,
            spellSlotsUsed = spellSlotsTotal.mapValues { 0 },
            spellSaveDc = 8 + proficiencyBonus + maxOf(intMod, wisMod, chaMod),
            spellAttackBonus = proficiencyBonus + maxOf(intMod, wisMod, chaMod),
            resources = resources,
            pinnedActionIds = pinnedIds,
            actions = actions,
            spells = spells,
            inventory = inventory,
            features = features,
            dndBeyondId = dndBeyondId
        )
    }

    /**
     * Extracts D&D Beyond Character ID from a full URL or direct ID string
     */
    fun extractCharacterId(input: String): String? {
        val trimmed = input.trim()
        if (trimmed.all { it.isDigit() }) return trimmed

        // Check for URL pattern: dndbeyond.com/characters/(\d+)
        val regex = Regex("""dndbeyond\.com/characters/(\d+)""")
        val match = regex.find(trimmed)
        return match?.groupValues?.get(1)
    }
}
