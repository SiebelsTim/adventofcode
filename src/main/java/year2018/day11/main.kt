package year2018.day11

import year2018.base.BaseSolution

class Solution : BaseSolution<Int, Solution.Coordinate, Pair<Solution.Coordinate, Int>>("Day 11") {
    data class Coordinate(val x: Int, val y: Int)
    class Grid(val serial: Int) {
        private val table by lazy {
            calculateSummedAreaTable()
        }
        val size: Int = 300

        fun power(x: Int, y: Int): Int {
            if (x !in 1..size || y !in 1..size) {
                throw IllegalArgumentException("Illegal X or Y")
            }
            val rackId = x + 10
            val fullPowerLevel = (rackId * y + serial) * rackId
            val hundreds = (fullPowerLevel / 100) % 10

            return hundreds - 5
        }

        private fun calculateSummedAreaTable(): Array<IntArray> {
            val size = size
            val table = Array(size) {
                IntArray(size) { 10 }
            }

            fun I(x: Int, y: Int): Int {
                if (x-1 < 0 || y-1 < 0) {
                    return 0
                }
                table[x-1][y-1].takeIf { it != 10 }?.let {
                    return it
                }
                val ret = power(x, y) + I(x-1, y) + I(x, y-1) - I(x-1, y-1)
                table[x-1][y-1] = ret
                return ret
            }

            I(size, size)

            return table
        }

        fun regionPower(startX: Int, startY: Int, n: Int = 3): Int {
            val x1 = if (startX - 1 - 1 >= 0) {
                startX - 1 - 1
            } else {
                0
            }
            val y1 = if (startY - 1 - 1 >= 0) {
                startY - 1 - 1
            } else {
                0
            }
            val x2 = if (x1 + n < size) {
                x1 + n
            } else {
                size - 1
            }
            val y2 = if (y1 + n < size) {
                y1 + n
            } else {
                size - 1
            }
            val A = table[x1][y1]
            val B = table[x2][y1]
            val C = table[x1][y2]
            val D = table[x2][y2]

            return D + A - B - C
        }
    }
    override fun parseInput(): Int = loadInput().trim().toInt()

    override fun calculateResult1(): Coordinate {
        return findMaxRegion(3).first
    }

    override fun calculateResult2(): Pair<Coordinate, Int> {
        return (1..300).map { n ->
            val power = findMaxRegion(n)
            Triple(n, power.first, power.second)
        }.maxBy { it.third }!!.let {
            it.second to it.first
        }
    }

    private fun findMaxRegion(n: Int): Pair<Coordinate, Int> {
        val grid = Grid(parseInput())
        val summed = Array(grid.size) {
            IntArray(grid.size)
        }

        val regions = summed.mapIndexed { x, arr ->
            arr.mapIndexed { y, _ ->
                Coordinate(x, y) to grid.regionPower(x, y, n)
            }
        }.flatten()

        return regions.maxBy { it.second }!!
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
