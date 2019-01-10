package year2017.day5

import base.BaseSolution

class Solution : BaseSolution<List<Int>, Int, Int>("Day 5") {
    override fun parseInput(): List<Int> = loadInput()
        .lines()
        .filter { it.isNotEmpty() }
        .map { it.toInt() }

    override fun calculateResult1(): Int {
        val input = parseInput().toMutableList()
        var idx = 0
        var steps = 0
        while (idx < input.size) {
            val offset = input[idx]
            input[idx] = offset + 1
            idx += offset
            steps++
        }

        return steps
    }

    override fun calculateResult2(): Int {
        val input = parseInput().toMutableList()
        var idx = 0
        var steps = 0
        while (idx < input.size) {
            val offset = input[idx]
            input[idx] = offset + if (offset >= 3) -1 else 1

            idx += offset
            steps++
        }

        return steps
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
