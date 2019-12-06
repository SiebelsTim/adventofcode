package year2019.day4

import base.BaseSolution

class Solution : BaseSolution<IntRange, Int, Int>("Day 4") {
    override fun parseInput(): IntRange {
        val (start, end) = loadInput().split("-").map(String::toInt)

        return start..end
    }

    fun CharArray.isIncreasing(): Boolean = withIndex().drop(1).all { (idx, it) ->
        this[idx - 1] <= it
    }

    fun Int.isValid(): Boolean {
        val chars = this.toString().toCharArray()

        if (!chars.isIncreasing()) {
            return false
        }

        return chars.distinct().size != chars.size
    }

    fun Int.isValid2(): Boolean {
        val chars = this.toString().toCharArray()

        if (!chars.isIncreasing()) {
            return false
        }

        return chars.duplicates().any { it.value == 2 }
    }

    fun CharArray.duplicates() = groupBy { it }.mapValues { it.value.size }

    override fun calculateResult1(): Int {
        return parseInput().count { it.isValid() }
    }

    override fun calculateResult2(): Int {
        return parseInput().count { it.isValid2() }
    }
}

fun main() {
    Solution().solveWithMeasurement()
}