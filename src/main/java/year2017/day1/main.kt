package year2017.day1

import base.BaseSolution

class Solution : BaseSolution<List<Int>, Int, Int>("Day 1") {
    override fun parseInput(): List<Int> = loadInput().trim().toCharArray().map {
        it - '0'
    }

    override fun calculateResult1(): Int {
        return calculateSum(1)
    }

    override fun calculateResult2(): Int {
        return calculateSum(parseInput().size / 2)
    }

    private fun calculateSum(n: Int): Int {
        val input = parseInput()
        var sum = 0

        for (i in 0 until input.size) {
            val first = input[i]
            val second = input[(i + n) % input.size]
            if (first == second) {
                sum += first
            }
        }

        return sum
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
