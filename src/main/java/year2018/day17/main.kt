package year2018.day17

import base.BaseSolution
import base.Coordinate
import year2018.day17.Solution.Grid

class Solution : BaseSolution<Grid, Int, Int>("Day 17") {
    class Grid(
        private val grid: Array<Array<Type>>, // Y from 0 to maxY, X from minX to maxX + 1 tile left and right to allow flow right of a wall
        private val minX: Int,
        private val minY: Int
    ) {
        private val maxX get() = minX+grid.first().size-2
        private val maxY get() = grid.size-1

        override fun toString(): String = grid.joinToString("\n") {
            it.joinToString("")
        }

        operator fun get(coord: Coordinate) = grid[coord.y][coord.x - minX + 1]
        operator fun get(x: Int, y: Int) = this[Coordinate(x, y)]

        operator fun set(coord: Coordinate, type: Type) {
            grid[coord.y][coord.x - minX + 1] = type
        }
        operator fun set(x: Int, y: Int, type: Type) {
            this[Coordinate(x, y)] = type
        }

        operator fun contains(coord: Coordinate): Boolean {
            return coord.y in 0..maxY && coord.x in minX-1..maxX
        }

        fun count(predicate: (Type) -> Boolean): Int = grid
            .drop(minY) // Drop the y offset.
            .toTypedArray()
            .flatten()
            .count(predicate)
    }

    enum class Type(private val c: Char) {
        CLAY('#'), SAND('.'), WATER('|'), REST('~');

        val blocks get() = this in listOf(CLAY, REST)

        override fun toString(): String = "$c"
    }

    override fun parseInput(): Grid {
        val clay = loadInput()
            .lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val numbers = Regex("[yx]=(\\d+), [yx]=(\\d+)\\.\\.(\\d+)")
                        .find(it)!!
                        .destructured.toList()
                        .map(String::toInt)
                if (it[0] == 'x') {
                    numbers[0]..numbers[0] to numbers[1]..numbers[2]
                } else {
                    assert(it[1] == 'y')
                    numbers[1]..numbers[2] to numbers[0]..numbers[0]
                }
            }
        val maxX = clay.maxBy { it.first.endInclusive }!!.first.endInclusive
        val minX = clay.minBy { it.first.start }!!.first.start
        val maxY = clay.maxBy { it.second.endInclusive }!!.second.endInclusive
        val minY = clay.minBy { it.second.start }!!.second.start

        val grid = Array(maxY + 1) {
            Array(maxX - minX + 1 + 2) { // +2 for overflowing to right and left
                Type.SAND
            }
        }


        return Grid(grid, minX, minY).apply {
            for ((xs, ys) in clay) {
                for (y in ys) {
                    for (x in xs) {
                        this[x, y] = Type.CLAY
                    }
                }
            }
        }
    }

    override fun calculateResult1(): Int {
        val fountain = Coordinate(500, 0)
        val grid = parseInput()
        grid.flow(fountain)

        return grid.count { it in listOf(Type.REST, Type.WATER) }
    }

    override fun calculateResult2(): Int {
        val fountain = Coordinate(500, 0)
        val grid = parseInput()
        grid.flow(fountain)

        return grid.count { it == Type.REST }
    }

    private fun Grid.flow(coord: Coordinate) {
        this[coord] = Type.WATER

        if (coord.down !in this) {
            return
        }

        if (this[coord.down] == Type.SAND) {
            flow(coord.down)
        }

        if (this[coord.down].blocks && coord.right in this && this[coord.right] == Type.SAND) {
            flow(coord.right)
        }

        if (this[coord.down].blocks && coord.left in this && this[coord.left] == Type.SAND) {
            flow(coord.left)
        }

        if (waterRests(coord)) {
            restWater(coord)
        }
    }

    private fun Grid.waterRests(coord: Coordinate): Boolean = waterRests(coord, Coordinate::left) && waterRests(coord, Coordinate::right)
    private fun Grid.waterRests(coord: Coordinate, next: (Coordinate) -> Coordinate): Boolean {
        var current = coord
        while (current in this) {
            if (this[current] == Type.CLAY) {
                return true
            } else if (this[current] == Type.SAND) {
                return false
            }
            current = next(current)
        }

        return false
    }

    private fun Grid.restWater(coord: Coordinate) { // Turn water into resting water
        var current = coord
        while (this[current] != Type.CLAY) {
            this[current] = Type.REST
            current = current.left
        }

        current = coord
        while (this[current] != Type.CLAY) {
            this[current] = Type.REST
            current = current.right
        }
    }

}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
