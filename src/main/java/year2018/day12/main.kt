package year2018.day12

import year2018.base.BaseSolution
import year2018.day12.Solution.Input

class Solution : BaseSolution<Input, Int, Long>("Day 12") {
    data class Input(val initialState: List<Pot>, val rules: List<Rule>)
    data class Rule(val left: List<Pot>, val result: Pot)
    enum class Pot {
        EMPTY, FILLED;

        override fun toString(): String = when(this) {
            EMPTY -> "."
            FILLED -> "#"
        }

        companion object {
            fun of(c: Char): Pot = if (c == '#') {
                Pot.FILLED
            } else {
                Pot.EMPTY
            }
        }
    }

    override fun parseInput(): Input {
        val input = loadInput().split("\n")
            .filter { it.isNotBlank() }

        val initialState = input.first().substring("initial state: ".length).map(Pot.Companion::of)
        val rules = input.drop(1).map {
            val (left, result) = it.split(" => ")
            val pots = left.toCharArray().map(Pot.Companion::of)

            Rule(pots, Pot.of(result[0]))
        }

        return Input(initialState, rules)
    }

    override fun calculateResult1(): Int {
        return calculateGeneration().mapIndexed { index, pot ->
            when (pot) {
                Pot.EMPTY -> 0
                Pot.FILLED -> index - 3
                // The first 3 pots are negative
            }
        }.sum()
    }

    override fun calculateResult2(): Long {
        val calculateFor = 50000000000
        val stabilizedGenerationNumber = 91 // We observed it by hand, that this stabilized after 91 iterations
                                            // After that it only moves one to the right
        val stabilizedGeneration = calculateGeneration(stabilizedGenerationNumber)


        return stabilizedGeneration.mapIndexed { index, pot ->
            when (pot) {
                Pot.EMPTY -> 0
                Pot.FILLED -> index - 3 + calculateFor - stabilizedGenerationNumber
                // We have 3 pots that are negative
                // Every following generations moves the plants one to the right,
                // so we simply add it
            }
        }.sum()
    }

    private fun calculateGeneration(generations: Int = 20): List<Pot> {
        val (initialState, rules) = parseInput()
        var state = listOf(Pot.EMPTY, Pot.EMPTY, Pot.EMPTY) + initialState + listOf(Pot.EMPTY, Pot.EMPTY, Pot.EMPTY)

        for (i in 1..generations) {
            val windowed = state.windowed(5)
            val nextGeneration = state.toMutableList()
            for (j in 0 until windowed.size) {
                val window = windowed[j]
                val newPot = rules.match(window) ?: Pot.EMPTY
                nextGeneration[j + 2] = newPot
            }

            if (nextGeneration.takeLast(3).any { it == Pot.FILLED }) {
                // We need to move to the right, make room for it
                nextGeneration += listOf(Pot.EMPTY, Pot.EMPTY, Pot.EMPTY, Pot.EMPTY)
            }
//            println("%2d: %s".format(i, toString(nextGeneration)))
            state = nextGeneration
        }

        return state
    }

    private fun List<Rule>.match(pots: List<Pot>): Pot? {
        if (pots.size != 5) {
            throw IllegalArgumentException("There must be exactly 5 pots")
        }

        return find { it.left == pots }?.result
    }

    private fun toString(state: List<Pot>) = state.fold("") { acc, it ->
        acc + it
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
