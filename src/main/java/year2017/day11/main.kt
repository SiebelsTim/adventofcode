package year2017.day11

import base.BaseSolution
import base.Coordinate
import kotlin.math.abs
import kotlin.math.max

class Solution : BaseSolution<Solution.Path, Int, Int>("Day 11") {
    // We use double height coordinates
    enum class Direction(val x: Int, val y: Int) {
        N(0, 2),
        NE(1, 1),
        SE(1, -1),
        S(0, -2),
        SW(-1, -1),
        NW(-1, 1);

        companion object {
            fun fromString(str: String) = when(str) {
                "n" -> N
                "ne" -> NE
                "se" -> SE
                "s" -> S
                "sw" -> SW
                "nw" -> NW
                else -> throw IllegalArgumentException("Unexpected direction $str")
            }
        }
    }

    class Path(val directions: List<Direction>) {
        val coordinates: List<Coordinate> by lazy {
            directions.fold(listOf(Coordinate(0, 0))) { acc, d ->
                acc + (acc.last() + Coordinate(d.x, d.y))
            }
        }

        fun getCoordinate(): Coordinate {
            var position = Coordinate(0, 0)
            for (direction in directions) {
                position += Coordinate(direction.x, direction.y)
            }

            return position
        }
    }

    override fun parseInput(): Path = loadInput().split(",").map { Direction.fromString(it) }.let { Path(it) }

    override fun calculateResult1(): Int {
        val path = parseInput()
        val target = path.getCoordinate()

        return target.length()
    }

    override fun calculateResult2(): Int {
        val path = parseInput()

        return path.coordinates.map { it.length() }.max()!!
    }

    fun Coordinate.length(): Int {
        val dx = abs(x)
        val dy = abs(y)
        return dx + max(0, (dy - dx) / 2)
    }
}

fun main() {
    Solution().solveWithMeasurement()
}