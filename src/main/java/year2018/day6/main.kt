package year2018.day6

import year2018.base.BaseSolution

class Solution : BaseSolution<List<Solution.Coordinate>, Int, Int>("Day 6") {
    data class Coordinate(val x: Int, val y: Int, var area: Int = 0) {
        fun distance(x: Int, y: Int): Int {
            return Math.abs(x - this.x) + Math.abs(y - this.y)
        }
    }
    override fun parseInput(): List<Coordinate> = loadInput().split("\n")
        .mapNotNull {
            if (it.isNotBlank()) {
                val (first, second) = it.split(", ")
                Coordinate(first.toInt(), second.toInt())
            } else {
                null
            }
        }

    override fun calculateResult1(): Int {
        val input = parseInput()
        val maxX = parseInput().maxBy { it.x }!!.x
        val maxY = parseInput().maxBy { it.y }!!.y

        for (x in 0..maxX) {
            for (y in 0..maxY) {
                val min = input.minListBy { it.distance(x, y) }
                if (min.size == 1) {
                    min[0].area += 1
                }
            }
        }

        for (x in 0..maxY) {
            for (y in arrayOf(0, maxY)) {
                val min = input.minListBy { it.distance(x, y) }
                if (min.size == 1) {
                    min[0].area = -1000
                }
            }
        }

        for (x in arrayOf(0, maxX)) {
            for (y in 0..maxY) {
                val min = input.minListBy { it.distance(x, y) }
                if (min.size == 1) {
                    min[0].area = -1000
                }
            }
        }

        return input.maxBy { it.area }!!.area
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val maxX = parseInput().maxBy { it.x }!!.x
        val maxY = parseInput().maxBy { it.y }!!.y

        var region = 0
        for (x in 0..maxX) {
            for (y in 0..maxY) {
                val min = input.sumBy { it.distance(x, y) }
                if (min < 10000) {
                    region++
                }
            }
        }

        return region
    }

    fun <T, R : Comparable<R>> Iterable<T>.minListBy(block: (T) -> R): List<T> {
        var ret = mutableListOf<T>()
        var minValue: R? = null

        for (item in this) {
            if (minValue == null || block(item) < minValue) {
                ret = mutableListOf()
                minValue = block(item)
                ret.add(item)
            } else if (block(item) == minValue) {
                ret.add(item)
            }
        }

        return ret
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
