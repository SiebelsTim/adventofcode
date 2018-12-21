package year2017.day4

import base.BaseSolution

class Solution : BaseSolution<Sequence<String>, Int, Int>("Day 4") {
    override fun parseInput(): Sequence<String> = loadInput()
        .lineSequence()
        .filter { it.isNotBlank() }

    override fun calculateResult1(): Int = parseInput()
        .map { it.split(" ") }
        .filter {
            it.toSet().size == it.size
        }
        .count()

    override fun calculateResult2(): Int = parseInput()
        .map { it.split(" ") }
        .filter { it.toSet().size == it.size }
        .filter {
            for (i in 0 until it.size) {
                loop@ for (j in i+1 until it.size) {
                    val a = it[i]
                    val b = it[j]
                    if (a.isAnagram(b)) {
                        return@filter false
                    }
                }
            }

            return@filter true
        }
        .count()

    private fun String.isAnagram(b: String): Boolean {
        var b = b
        val a = this
        if (a.length != b.length) {
            return false
        }

        for (c in a) {
            val idx = b.indexOf(c)
            if (idx < 0) {
                return false
            }
            b = b.removeRange(idx..idx)
        }

        assert(b.isEmpty())
        return true
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
