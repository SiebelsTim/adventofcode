package year2017.day2

import base.BaseSolution
import kotlin.math.max
import kotlin.math.min

class Solution : BaseSolution<List<List<Int>>, Int, Int>("Day 2") {
    override fun parseInput(): List<List<Int>> = loadInput()
        .lines()
        .filter { it.isNotBlank() }
        .map { it.split("\t").map { it.trim().toInt() } }

    override fun calculateResult1(): Int = parseInput().sumBy {
        it.max()!! - it.min()!!
    }

    override fun calculateResult2(): Int {
        return parseInput().sumBy {
            for (i in 0 until it.size) {
                for (j in i+1 until it.size) {
                    val a = max(it[i], it[j])
                    val b = min(it[i], it[j])
                    if (a % b == 0) {
                        return@sumBy a / b
                    }
                }
            }

            throw IllegalArgumentException("Did not find evenly divisible numbers")
        }
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
