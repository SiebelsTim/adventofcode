package year2019.day14

import base.BaseSolution
import kotlin.math.ceil

class Solution : BaseSolution<List<Solution.Reaction>, Long, Long>("Day 14") {
    private fun Material(str: String): Material {
        val (amount, name) = str.split(" ")
        return Material(amount.toLong(), name)
    }

    data class Material(val amount: Long, val name: String) {
        override fun toString(): String = "$amount $name"
    }

    data class Reaction(val inputs: List<Material>, val output: Material) {
        override fun toString(): String = inputs.joinToString(", ") + " => $output"
    }

    class Factory(private val reactions: List<Reaction>) {
        private val inverseReactions = reactions.groupBy { it.output.name }.mapValues { it.value.first() }
        private val storage = mutableMapOf<String, Long>()

        // Returns amount of ore used
        private fun consume(amount: Long, name: String): Long {
            if (name == "ORE") {
                return amount
            }
            if (storage[name] ?: 0 >= amount) { // We still got all of it in our storage
                storage[name] = (storage[name] ?: 0) - amount
                return 0
            } else { // We haven't got enough, we first need to produce it
                val ret = produce(amount, name)
                consume(amount, name)
                return ret
            }
        }

        // returns amount of ore used
        fun produce(amount: Long, name: String): Long {
            val reaction = inverseReactions[name]!!
            val missingAmount = amount - (storage[name] ?: 0) // Only produce as much as needed
            val count = ceil(missingAmount.toDouble() / reaction.output.amount).toLong()

            val results = reaction.inputs.map {
                consume(it.amount * count, it.name)
            }
            val output = reaction.output

            // put output back into storage
            storage[output.name] = (storage[output.name] ?: 0) + output.amount * count

            return results.sum()
        }
    }

    override fun parseInput(): List<Reaction> = loadInput().lines().map {
        val (inputs, output) = it.split(" => ")

        Reaction(inputs.split(", ").map {
            Material(it)
        }, Material(output))
    }

    override fun calculateResult1(): Long {
        val reactions = parseInput()
        val factory = Factory(reactions)

        return factory.produce(1, "FUEL")
    }

    override fun calculateResult2(): Long {
        val reactions = parseInput()

        val trillion = 1_000_000_000_000L

        var min = 0L
        var max = trillion
        while (min < max) {
            val middle = min + (max - min) / 2L
            val factory = Factory(reactions)
            val result = factory.produce(middle, "FUEL")
            if (result > trillion) {
                max = middle - 1
            } else if (result < trillion) {
                min = middle + 1
            } else {
                return middle
            }
        }

        return min - 1
    }
}

fun main() {
    Solution().solveWithMeasurement()
}