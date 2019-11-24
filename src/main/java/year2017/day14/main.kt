package year2017.day14

import base.BaseSolution
import base.Coordinate
import base.Direction
import year2017.day10.knotHash
import java.math.BigInteger

class Solution : BaseSolution<String, Int, Int>("Day 14") {
    class Grid(private val rows: List<CharArray>) {
        private var lastRegion = 0
        fun findNeighbors(): Map<Int, List<Coordinate>> {
            val ret = mutableMapOf<Coordinate, Int>()

            for (x in 0..127) {
                for (y in 0..127) {
                    findNeighbors(Coordinate(x, y), ret)
                }
            }

            return ret.invert()
        }

        private fun <K, V> Map<K, V>.invert() = toList().groupBy({ it.second }, { it.first })

        private fun findNeighbors(coord: Coordinate, map: MutableMap<Coordinate, Int>) {
            if (rows[coord.y][coord.x] == '0' || map.containsKey(coord)) {
                return
            }

            val directNeighbors = coord.directNeighbors()
            val neighborRegion = directNeighbors.mapNotNull { map[it] }.firstOrNull() // Find existing neighbor with assigned region

            if (neighborRegion != null) {
                map[coord] = neighborRegion
            } else {
                map[coord] = ++lastRegion
            }

            for (directNeighbor in directNeighbors) {
                findNeighbors(directNeighbor, map)
            }
        }

        private fun Coordinate.directNeighbors(): List<Coordinate> {
            return Direction.values().filter {
                (x + it.dx) in 0..127 && (y + it.dy) in 0..127
            }.map { Coordinate(x + it.dx, y + it.dy) }
        }
    }
    override fun parseInput(): String = loadInput()

    override fun calculateResult1(): Int {
        val input = loadInput()

        return (0..127).sumBy {
            val hash = knotHash("$input-$it")
            BigInteger(hash, 16).toString(2).count { it == '1' }
        }
    }

    override fun calculateResult2(): Int {
        val input = loadInput()
        val hashes = List(128) {
            knotHash("$input-$it")
        }
        val grid = hashesToGrid(hashes)

        return grid.findNeighbors().size
    }

    private fun hashesToGrid(hashes: List<String>): Grid {
        val bytes = hashes.map {
            BigInteger(it, 16)
                    .toString(2)
                    .padStart(128, '0')
                    .toCharArray()
        }

        return Grid(bytes)
    }
}

fun main() {
    Solution().solveWithMeasurement()
}