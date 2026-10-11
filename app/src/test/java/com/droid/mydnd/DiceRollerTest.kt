package com.droid.mydnd

import com.droid.mydnd.dice.DiceRoller
import com.droid.mydnd.model.RollType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class DiceRollerTest {

    @Test
    fun rollDie_staysWithinBounds() {
        val random = Random(42)
        for (i in 0 until 100) {
            val d20 = DiceRoller.rollDie(20, random)
            assertTrue("d20 must be between 1 and 20, got $d20", d20 in 1..20)
            val d6 = DiceRoller.rollDie(6, random)
            assertTrue("d6 must be between 1 and 6, got $d6", d6 in 1..6)
        }
    }

    @Test
    fun rollD20_normal_appliesModifierCorrectly() {
        val result = DiceRoller.rollD20(
            rollType = RollType.NORMAL,
            modifier = 5,
            label = "Attack Roll"
        )
        val dieRoll = result.individualRolls.first()
        assertEquals(dieRoll + 5, result.total)
        assertEquals("Attack Roll", result.label)
        if (dieRoll == 20) {
            assertTrue(result.isCritical)
        } else {
            assertFalse(result.isCritical)
        }
    }

    @Test
    fun rollD20_advantage_takesHigherRoll() {
        // Mock random that returns predictable pairs
        val predictable = object : Random() {
            var counter = 0
            override fun nextBits(bitCount: Int): Int = 0
            override fun nextInt(from: Int, until: Int): Int {
                counter++
                return if (counter % 2 == 1) 4 else 18
            }
        }

        val result = DiceRoller.rollD20(
            rollType = RollType.ADVANTAGE,
            modifier = 2,
            random = predictable
        )

        assertEquals(2, result.individualRolls.size)
        assertEquals(4, result.individualRolls[0])
        assertEquals(18, result.individualRolls[1])
        assertEquals(18 + 2, result.total)
    }

    @Test
    fun rollD20_disadvantage_takesLowerRoll() {
        val predictable = object : Random() {
            var counter = 0
            override fun nextBits(bitCount: Int): Int = 0
            override fun nextInt(from: Int, until: Int): Int {
                counter++
                return if (counter % 2 == 1) 19 else 5
            }
        }

        val result = DiceRoller.rollD20(
            rollType = RollType.DISADVANTAGE,
            modifier = 3,
            random = predictable
        )

        assertEquals(2, result.individualRolls.size)
        assertEquals(19, result.individualRolls[0])
        assertEquals(5, result.individualRolls[1])
        assertEquals(5 + 3, result.total)
    }

    @Test
    fun rollFormula_parsesDiceAndModifier() {
        val fixedRandom = object : Random() {
            override fun nextBits(bitCount: Int): Int = 0
            override fun nextInt(from: Int, until: Int): Int = 4 // always rolls 4
        }

        val result = DiceRoller.rollFormula("2d6+5", "Greatsword Damage", random = fixedRandom)
        assertEquals(2, result.individualRolls.size)
        assertEquals(listOf(4, 4), result.individualRolls)
        assertEquals(5, result.modifier)
        assertEquals(4 + 4 + 5, result.total)
        assertEquals("Greatsword Damage", result.label)
    }
}
