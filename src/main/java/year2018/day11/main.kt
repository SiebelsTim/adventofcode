package year2018.day11

import base.BaseSolution
import year2018.day11.Solution.Coordinate

class Solution : BaseSolution<Int, Coordinate, Pair<Coordinate, Int>>("Day 11") {
    data class Coordinate(val x: Int, val y: Int) {
        override fun toString(): String = "$x, $y"
    }

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
            val table = Array(size+1) {
                IntArray(size+1) { 10 }
            }

            fun I(x: Int, y: Int): Int {
                if (x-1 < 0 || y-1 < 0) {
                    return 0
                }
                table[x][y].takeIf { it != 10 }?.let {
                    return it
                }
                val ret = power(x, y) + I(x-1, y) + I(x, y-1) - I(x-1, y-1)
                table[x][y] = ret
                return ret
            }

            I(size, size)

            return table
        }

        fun regionPower(startX: Int, startY: Int, n: Int = 3): Int {
            /**
             * https://en.wikipedia.org/wiki/Summed-area_table
             * startX,startY = 1,1; n=2
             * A X B
             * X[X X]
             * C[X D]
             */
            val x1 = startX - 1
            val y1 = startY - 1
            val x2 = x1 + n
            val y2 = y1 + n
            val A = table[x1][y1]
            val B = table[x2][y1]
            val C = table[x1][y2]
            val D = table[x2][y2]

            return D + A - B - C
        }
    }

    private val grid = Grid(parseInput())

    override fun parseInput(): Int = loadInput().trim().toInt()

    override fun calculateResult1(): Coordinate {
        return findMaxRegion(3).first
    }

    override fun calculateResult2(): Pair<Coordinate, Int> {
        return (1..300).map { n ->
            val power = findMaxRegion(n)
            Triple(n, power.first, power.second) // n, coord, power
        }.maxBy { it.third }!!.let {
            it.second to it.first
        }
    }

    private fun findMaxRegion(n: Int): Pair<Coordinate, Int> {
        val regions = mutableListOf<Pair<Coordinate, Int>>()
        for (y in 1..grid.size-n+1) {
            for (x in 1..grid.size-n+1) {
                regions += Coordinate(x, y) to grid.regionPower(x, y, n)
            }
        }

        return regions.maxBy { it.second }!!
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
