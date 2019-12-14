package year2019.day10

import base.BaseSolution
import base.Coordinate
import java.lang.Math.atan2
import java.lang.Math.toDegrees

class Solution : BaseSolution<List<Coordinate>, Int, Int>("Day 10") {
    override fun parseInput() = loadInput().lines().mapIndexed { y, line ->
        line.toCharArray().mapIndexed { x, c ->
            if (c == '#') {
                Coordinate(x, y)
            } else {
                null
            }
        }
    }.flatten().filterNotNull()

    override fun calculateResult1(): Int {
        val input = parseInput()
        return input.map { it.countVisible(input) }.max()!!
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val station = input.map { it to it.countVisible(input) }.maxBy {
            it.second
        }!!.first

        // maps angle to coordinates, coordinates sorted by distance to station
        val groups = input.groupBy { station.angleTo(it) }.map { (key, value) ->
            key to value.sortedBy {
                station.manhattenDistance(it)
            }
        }.toMap().toMutableMap()

        val angles = groups.keys.sorted()
        var destroyed = 0
        while (destroyed < input.size) {
            for (angle in angles) {
                groups[angle]?.firstOrNull()?.let {
                    if (++destroyed == 200) {
                        return it.x * 100 + it.y
                    }
                    groups[angle] = groups[angle]!!.drop(1)
                }
            }
        }

        return 0
    }

    private fun Coordinate.countVisible(asteroids: List<Coordinate>): Int = asteroids.map {
        angleTo(it)
    }.distinct().size

    private fun Coordinate.angleTo(other: Coordinate): Double {
        val d = toDegrees(
                atan2(
                        (other.y - y).toDouble(),
                        (other.x - x).toDouble()
                )
        ) + 90
        return if (d < 0) d + 360 else d
    }
}

fun main() {
    Solution().solveWithMeasurement()
}