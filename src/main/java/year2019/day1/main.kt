package year2019.day1

import base.BaseSolution

class Solution : BaseSolution<List<Int>, Int, Int>("Day 1") {
    override fun parseInput(): List<Int> = loadInput().lines().filter { it.isNotBlank() }.map { it.toInt() }

    override fun calculateResult1(): Int = parseInput().sumBy {
        it / 3 - 2
    }

    override fun calculateResult2(): Int = parseInput().sumBy {
        requiredFuel(it)
    }

    private fun requiredFuel(mass: Int): Int {
        val fuel = mass / 3 - 2

        if (fuel <= 0) {
            return 0
        }

        return fuel + requiredFuel(fuel)
    }
}

fun main() {
    Solution().solveWithMeasurement()
}