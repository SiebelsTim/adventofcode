package year2019.day3

import base.BaseSolution
import base.Coordinate
import base.Direction

class Solution : BaseSolution<Pair<Solution.Wire, Solution.Wire>, Int, Int>("Day 3") {
    data class PathPoint(val count: Int, val direction: Direction) {
    }
    data class Wire(val pathPoints: List<PathPoint>) {
        fun getCoordinates(): Set<Coordinate> {
            val set = mutableSetOf<Coordinate>()

            var position = Coordinate(0, 0)
            for (pathPoint in pathPoints) {
                repeat(pathPoint.count) {
                    position += pathPoint.direction
                    set += position
                }
            }

            return set
        }

        fun distanceTo(coordinate: Coordinate): Int {
            var position = Coordinate(0, 0)
            var i = 0
            for (pathPoint in pathPoints) {
                repeat(pathPoint.count) {
                    ++i
                    position += pathPoint.direction
                    if (position == coordinate) {
                        return i
                    }
                }
            }

            throw IllegalArgumentException("This wire never meets the coordinate")
        }
    }

    override fun parseInput(): Pair<Wire, Wire> {
        val (a, b) = loadInput().lines().map { wireStr ->
            wireStr.split(",").map { it.toPathPoint() }
        }

        return Wire(a) to Wire(b)
    }

    override fun calculateResult1(): Int {
        val input = parseInput()
        val coordinates1 = input.first.getCoordinates()
        val coordinates2 = input.second.getCoordinates()

        val intersection = coordinates1.intersect(coordinates2)

        return intersection.map { it.manhattenDistance(Coordinate(0, 0)) }.min()!!
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val coordinates1 = input.first.getCoordinates()
        val coordinates2 = input.second.getCoordinates()

        val intersection = coordinates1.intersect(coordinates2)

        return intersection.map {
            input.first.distanceTo(it) + input.second.distanceTo(it)
        }.min()!!
    }

    private fun String.toPathPoint(): PathPoint {
        val direction = when (val d = this.first()) {
            'R' -> Direction.RIGHT
            'L' -> Direction.LEFT
            'U' -> Direction.UP
            'D' -> Direction.DOWN
            else -> throw IllegalArgumentException("Unknown direction $d")
        }
        val count = this.drop(1).toInt()
        return PathPoint(count, direction)
    }
}

fun main() {
    Solution().solveWithMeasurement()
}