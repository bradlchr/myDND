package com.droid.mydnd.dice

import com.droid.mydnd.model.DiceRollResult
import com.droid.mydnd.model.RollType
import kotlin.random.Random

object DiceRoller {

    fun rollDie(sides: Int, random: Random = Random.Default): Int {
        if (sides <= 1) return 1
        return random.nextInt(1, sides + 1)
    }

    fun rollD20(
        rollType: RollType = RollType.NORMAL,
        modifier: Int = 0,
        label: String = "d20 Check",
        random: Random = Random.Default
    ): DiceRollResult {
        return when (rollType) {
            RollType.NORMAL -> {
                val roll = rollDie(20, random)
                DiceRollResult(
                    formula = if (modifier >= 0) "1d20+$modifier" else "1d20$modifier",
                    total = roll + modifier,
                    individualRolls = listOf(roll),
                    modifier = modifier,
                    rollType = rollType,
                    isCritical = (roll == 20),
                    isFumble = (roll == 1),
                    label = label
                )
            }
            RollType.ADVANTAGE -> {
                val roll1 = rollDie(20, random)
                val roll2 = rollDie(20, random)
                val selected = maxOf(roll1, roll2)
                DiceRollResult(
                    formula = "1d20 (Adv) ${if (modifier >= 0) "+$modifier" else "$modifier"}",
                    total = selected + modifier,
                    individualRolls = listOf(roll1, roll2),
                    modifier = modifier,
                    rollType = rollType,
                    isCritical = (selected == 20),
                    isFumble = (selected == 1),
                    label = label
                )
            }
            RollType.DISADVANTAGE -> {
                val roll1 = rollDie(20, random)
                val roll2 = rollDie(20, random)
                val selected = minOf(roll1, roll2)
                DiceRollResult(
                    formula = "1d20 (Disadv) ${if (modifier >= 0) "+$modifier" else "$modifier"}",
                    total = selected + modifier,
                    individualRolls = listOf(roll1, roll2),
                    modifier = modifier,
                    rollType = rollType,
                    isCritical = (selected == 20),
                    isFumble = (selected == 1),
                    label = label
                )
            }
        }
    }

    /**
     * Parses and rolls standard dice formulas like "2d6+3", "1d8", "3d10-1", "d20+7".
     */
    fun rollFormula(
        formula: String,
        label: String = "Damage / Roll",
        random: Random = Random.Default
    ): DiceRollResult {
        val cleaned = formula.replace(" ", "").lowercase()
        val regex = Regex("""^(\d*)d(\d+)([+-]\d+)?$""")
        val match = regex.find(cleaned)

        if (match == null) {
            // Fallback: if user typed simple number or unknown
            val singleDie = cleaned.toIntOrNull()
            if (singleDie != null) {
                return DiceRollResult(
                    formula = formula,
                    total = singleDie,
                    individualRolls = listOf(singleDie),
                    modifier = 0,
                    label = label
                )
            }
            // default fallback d20
            val roll = rollDie(20, random)
            return DiceRollResult(
                formula = "1d20",
                total = roll,
                individualRolls = listOf(roll),
                label = label
            )
        }

        val countStr = match.groupValues[1]
        val count = if (countStr.isEmpty()) 1 else countStr.toInt().coerceIn(1, 100)
        val sides = match.groupValues[2].toInt().coerceIn(1, 1000)
        val modStr = match.groupValues[3]
        val modifier = if (modStr.isNotEmpty()) modStr.toInt() else 0

        val rolls = mutableListOf<Int>()
        for (i in 0 until count) {
            rolls.add(rollDie(sides, random))
        }
        val sum = rolls.sum() + modifier
        val isCrit = (count == 1 && sides == 20 && rolls.first() == 20)
        val isFumble = (count == 1 && sides == 20 && rolls.first() == 1)

        return DiceRollResult(
            formula = formula,
            total = sum,
            individualRolls = rolls,
            modifier = modifier,
            isCritical = isCrit,
            isFumble = isFumble,
            label = label
        )
    }
}
