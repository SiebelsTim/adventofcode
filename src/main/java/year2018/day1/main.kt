package year2018.day1

import base.BaseSolution

class Solution : BaseSolution<List<Int>, Int, Int>("Day 1") {
    override fun parseInput(): List<Int> = loadInput().split("\n")
        .filter { it.isNotBlank() }
        .map { it.toInt() }

    override fun calculateResult1(): Int  = parseInput().sum()

    override fun calculateResult2(): Int {
        val input = parseInput()
        var ret = 0
        val results = mutableSetOf<Int>()
        var idx = 0
        while (true) {
            ret += input[idx]
            if (!results.add(ret)) {
                return ret
            }
            idx = (idx + 1) % input.size
        }
    }
}

fun main() {
    Solution().solve()
}
