package year2017.day10

import base.BaseSolution

class Solution : BaseSolution<List<Int>, Int, String>("Day 11") {
    override fun parseInput(): List<Int> = loadInput().split(",")
            .filter { it.isNotBlank() }
            .map { it.trim().toInt() }

    override fun calculateResult1(): Int {
        var list = List(256) { it }
        val input = parseInput()

        var skip = 0
        var current = 0

        for (i in input) {
            val indices = List(i) { (current + it) % list.size }
            val reversed = list.slice(indices).reversed()
            list = List(list.size) {
                if (it in indices) {
                    reversed[indices.indexOf(it)]
                } else {
                    list[it]
                }
            }

            current += (i + skip) % list.size
            skip++
        }

        return list[0] * list[1]
    }

    override fun calculateResult2(): String {
        val inputString = loadInput()
        return knotHash(inputString)
    }
}

fun knotHash(inputString: String): String {
    val input = inputString.trim().toByteArray().toList() +
            listOf(17, 31, 73, 47, 23.toByte())

    var list = List(256) { it }

    var skip = 0
    var current = 0

    repeat(64) {
        for (i in input) {
            val indices = List(i.toInt()) { (current + it) % list.size }
            val reversed = list.slice(indices).reversed()
            list = List(list.size) {
                if (it in indices) {
                    reversed[indices.indexOf(it)]
                } else {
                    list[it]
                }
            }

            current += (i + skip) % list.size
            skip++
        }
    }

    return list.chunked(16)
            .map { it.fold(0) { acc, it -> acc xor it } }
            .map { String.format("%02x", it) }
            .joinToString("")
}

fun main() {
    Solution().solveWithMeasurement()
}