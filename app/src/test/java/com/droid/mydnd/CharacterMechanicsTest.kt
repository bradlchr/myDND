package com.droid.mydnd

import com.droid.mydnd.model.ActionItem
import com.droid.mydnd.model.ActionType
import com.droid.mydnd.model.CharacterSheet
import com.droid.mydnd.model.RechargeType
import com.droid.mydnd.model.ResourceCounter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterMechanicsTest {

    @Test
    fun takeDamage_absorbsTempHpFirst() {
        val sheet = CharacterSheet(
            name = "Test Hero",
            characterClass = "Fighter 5",
            race = "Human",
            maxHp = 40,
            currentHp = 40,
            tempHp = 10
        )

        // Take 6 damage: temp HP absorbs it all
        val damaged1 = sheet.takeDamage(6)
        assertEquals(40, damaged1.currentHp)
        assertEquals(4, damaged1.tempHp)

        // Take 10 more damage: 4 from temp, 6 from real HP
        val damaged2 = damaged1.takeDamage(10)
        assertEquals(34, damaged2.currentHp)
        assertEquals(0, damaged2.tempHp)

        // Take massive damage: cannot go below 0
        val damaged3 = damaged2.takeDamage(100)
        assertEquals(0, damaged3.currentHp)
    }

    @Test
    fun heal_doesNotExceedMaxHp() {
        val sheet = CharacterSheet(
            name = "Test Hero",
            characterClass = "Fighter 5",
            race = "Human",
            maxHp = 40,
            currentHp = 25
        )

        val healed = sheet.heal(10)
        assertEquals(35, healed.currentHp)

        val overHealed = healed.heal(20)
        assertEquals(40, overHealed.currentHp)
    }

    @Test
    fun shortRest_resetsShortRestResources() {
        val resource = ResourceCounter("1", "Action Surge", current = 0, max = 1, recharge = RechargeType.SHORT_REST)
        val action = ActionItem(
            name = "Maneuver",
            type = ActionType.CLASS_FEATURE,
            currentUses = 0,
            maxUses = 4,
            recharge = RechargeType.SHORT_REST
        )

        val sheet = CharacterSheet(
            name = "Fighter",
            characterClass = "Fighter 5",
            race = "Human",
            resources = listOf(resource),
            actions = listOf(action)
        )

        val rested = sheet.applyShortRest()
        assertEquals(1, rested.resources.first().current)
        assertEquals(4, rested.actions.first().currentUses)
    }

    @Test
    fun longRest_fullRecovery() {
        val resource = ResourceCounter("1", "Rages", current = 1, max = 3, recharge = RechargeType.LONG_REST)
        val sheet = CharacterSheet(
            name = "Barbarian",
            characterClass = "Barbarian 5",
            race = "Human",
            maxHp = 50,
            currentHp = 15,
            tempHp = 5,
            deathSavesFailures = 2,
            deathSavesSuccesses = 1,
            spellSlotsTotal = mapOf(1 to 4, 2 to 2),
            spellSlotsUsed = mapOf(1 to 4, 2 to 1),
            resources = listOf(resource)
        )

        val rested = sheet.applyLongRest()
        assertEquals(50, rested.currentHp)
        assertEquals(0, rested.tempHp)
        assertEquals(0, rested.deathSavesFailures)
        assertEquals(0, rested.deathSavesSuccesses)
        assertEquals(0, rested.spellSlotsUsed[1])
        assertEquals(0, rested.spellSlotsUsed[2])
        assertEquals(3, rested.resources.first().current)
    }

    @Test
    fun spellSlots_useAndRestore() {
        val sheet = CharacterSheet(
            name = "Wizard",
            characterClass = "Wizard 5",
            race = "Elf",
            spellSlotsTotal = mapOf(1 to 4, 2 to 3),
            spellSlotsUsed = mapOf(1 to 0, 2 to 0)
        )

        val used1 = sheet.useSpellSlot(1)
        assertEquals(1, used1.spellSlotsUsed[1])

        val used2 = used1.useSpellSlot(1)
        assertEquals(2, used2.spellSlotsUsed[1])

        val restored = used2.restoreSpellSlot(1)
        assertEquals(1, restored.spellSlotsUsed[1])
    }

    @Test
    fun pinAction_togglesPinnedState() {
        val action = ActionItem("act-1", "Dagger", ActionType.MELEE_ATTACK, isPinned = false)
        val sheet = CharacterSheet(
            name = "Rogue",
            characterClass = "Rogue 5",
            race = "Elf",
            actions = listOf(action)
        )

        assertFalse(sheet.actions.first().isPinned)
        assertFalse(sheet.pinnedActionIds.contains("act-1"))

        val pinned = sheet.togglePinAction("act-1")
        assertTrue(pinned.actions.first().isPinned)
        assertTrue(pinned.pinnedActionIds.contains("act-1"))

        val unpinned = pinned.togglePinAction("act-1")
        assertFalse(unpinned.actions.first().isPinned)
        assertFalse(unpinned.pinnedActionIds.contains("act-1"))
    }
}
