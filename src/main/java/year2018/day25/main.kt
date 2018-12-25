package year2018.day25

import base.BaseSolution
import kotlin.math.abs

data class Coord4(
    val x1: Int,
    val x2: Int,
    val x3: Int,
    val x4: Int) {

    fun distanceTo(other: Coord4): Int = abs(x1 - other.x1) + abs(x2 - other.x2) + abs(x3 - other.x3) + abs(x4 - other.x4)
    override fun toString(): String = "($x1,$x2,$x3,$x4)"
}

class Solution : BaseSolution<List<Coord4>, Int, Int>("Day 25") {
    override fun parseInput(): List<Coord4> = loadInput()
        .lineSequence()
        .filter { it.isNotBlank() }
        .map {
            val (x1, x2, x3, x4) = it.split(",").map(String::toInt)

            Coord4(x1, x2, x3, x4)
        }
        .toList()

    override fun calculateResult1(): Int {
        val input = parseInput()
        val groups = MutableList(input.size) {
            mutableListOf(input[it])
        }

        // merge groups as long as something changes
        var changed = true
        while (changed) {
            changed = false
            for (i in 0 until groups.size) {
                for (j in i+1 until groups.size) {
                    if (groups[i].joinable(groups[j])) {
                        groups[i].addAll(groups[j])
                        groups[j] = mutableListOf()
                        changed = true
                    }
                }
            }
        }

        return groups.filter { it.isNotEmpty() }.size
    }

    override fun calculateResult2(): Int {
        return -1
    }

    private fun List<Coord4>.joinable(list: List<Coord4>): Boolean {
        for (point in this) {
            if (list.any { it.distanceTo(point) <= 3 }) {
                return true
            }
        }

        return false
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
