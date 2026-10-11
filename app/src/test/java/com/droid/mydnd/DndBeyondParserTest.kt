package com.droid.mydnd

import com.droid.mydnd.importer.DndBeyondParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DndBeyondParserTest {

    @Test
    fun extractCharacterId_fromUrlAndDirectId() {
        val url = "https://www.dndbeyond.com/characters/142958192"
        val id = DndBeyondParser.extractCharacterId(url)
        assertEquals("142958192", id)

        val direct = "98765432"
        val directId = DndBeyondParser.extractCharacterId(direct)
        assertEquals("98765432", directId)

        val shareLink = "https://www.dndbeyond.com/characters/55443322/builder"
        val shareId = DndBeyondParser.extractCharacterId(shareLink)
        assertEquals("55443322", shareId)
    }

    @Test
    fun parseCharacterJson_extractsAllCoreAttributes() {
        val sampleJson = """
        {
          "data": {
            "id": 142958192,
            "name": "Valeros Stormblade",
            "baseHitPoints": 40,
            "removedHitPoints": 5,
            "temporaryHitPoints": 8,
            "classes": [
              {
                "level": 5,
                "definition": {
                  "name": "Fighter",
                  "hitDice": 10
                },
                "subclassDefinition": {
                  "name": "Battle Master"
                }
              }
            ],
            "race": {
              "fullName": "Variant Human"
            },
            "stats": [
              { "id": 1, "value": 16 },
              { "id": 2, "value": 14 },
              { "id": 3, "value": 16 },
              { "id": 4, "value": 10 },
              { "id": 5, "value": 12 },
              { "id": 6, "value": 8 }
            ],
            "inventory": [
              {
                "equipped": true,
                "quantity": 1,
                "definition": {
                  "name": "Greatsword +1",
                  "type": "Martial Weapon",
                  "damage": { "diceString": "2d6" },
                  "damageType": { "name": "Slashing" },
                  "weight": 6.0,
                  "description": "A finely forged blade etched with draconic runes."
                }
              },
              {
                "equipped": true,
                "quantity": 1,
                "definition": {
                  "name": "Plate Armor",
                  "armorClass": 18,
                  "weight": 65.0,
                  "description": "Full heavy plate suit."
                }
              }
            ],
            "actions": {
              "class": [
                {
                  "name": "Action Surge",
                  "description": "You can push yourself beyond your normal limits for a moment."
                },
                {
                  "name": "Second Wind",
                  "description": "You have a limited well of stamina you can draw on to protect yourself."
                }
              ]
            }
          }
        }
        """.trimIndent()

        val character = DndBeyondParser.parseCharacterJson(sampleJson)

        assertEquals("Valeros Stormblade", character.name)
        assertEquals("Variant Human", character.race)
        assertEquals(5, character.level)
        assertTrue(character.characterClass.contains("Fighter (Battle Master) 5"))

        // Check Ability scores
        assertEquals(16, character.abilityScores["STR"])
        assertEquals(14, character.abilityScores["DEX"])
        assertEquals(16, character.abilityScores["CON"])
        assertEquals(3, character.getAbilityModifier("STR"))
        assertEquals(2, character.getAbilityModifier("DEX"))
        assertEquals(3, character.getAbilityModifier("CON"))
        assertEquals(-1, character.getAbilityModifier("CHA"))

        // Proficiency bonus: Level 5 -> +3
        assertEquals(3, character.proficiencyBonus)

        // Hit Points: base 40 + (3 con * 5 level) = 55 maxHp
        assertEquals(55, character.maxHp)
        assertEquals(50, character.currentHp) // 55 - 5 removed
        assertEquals(8, character.tempHp)

        // Actions & Weapons
        val greatsword = character.actions.find { it.name == "Greatsword +1" }
        assertNotNull("Greatsword +1 should be extracted as an action", greatsword)
        assertEquals("2d6+3", greatsword?.damageFormula)
        assertEquals(6, greatsword?.attackBonus) // +3 STR + 3 prof = +6
        assertTrue(greatsword?.isPinned == true)

        // Class Actions extracted
        val actionSurge = character.actions.find { it.name == "Action Surge" }
        assertNotNull(actionSurge)
        assertTrue(actionSurge?.isPinned == true)

        // Resources added
        assertTrue(character.resources.any { it.name == "Action Surge" })
        assertTrue(character.resources.any { it.name == "Second Wind" })
    }
}
