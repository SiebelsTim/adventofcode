package year2018.day18

import base.BaseSolution
import base.Coordinate
import year2018.day18.Solution.Grid
import year2018.day18.Solution.Tile
import kotlin.math.max
import kotlin.math.min


class Solution : BaseSolution<Grid, Int, Int>("Day 18") {
    class Grid(private val grid: Array<Array<Tile>>) {
        val height = grid.size
        val width by lazy {
            grid.maxBy { it.size }!!.size
        }

        override fun toString(): String = grid.joinToString("\n") {
            it.joinToString("")
        }

        operator fun get(x: Int, y: Int) = grid[y][x]
        operator fun get(coord: Coordinate) = this[coord.x, coord.y]
        operator fun set(coord: Coordinate, tile: Tile) {
            grid[coord.y][coord.x] = tile
        }

        fun calculateResourceValue(): Int = grid.flatten().let {
            it.count { it == Tile.TREES } * it.count { it == Tile.LUMBERYARD }
        }

        fun copyOf(): Grid = Grid(grid.map { it.copyOf() }.toTypedArray())

        override fun equals(other: Any?): Boolean {
            if (other !is Grid) return false

            return grid.contentDeepEquals(other.grid)
        }
    }

    enum class Tile(val c: Char) {
        OPEN('.'), TREES('|'), LUMBERYARD('#');

        override fun toString(): String = c.toString()

        companion object {
            fun of(c: Char): Tile = when (c) {
                '.' -> OPEN
                '|' -> TREES
                '#' -> LUMBERYARD
                else -> throw IllegalArgumentException("Tile $c not found")
            }
        }
    }

    private val rules = mapOf<Tile, (List<Tile>) -> Tile?>(
        Tile.OPEN to { adj -> if (adj.count { it == Tile.TREES } >= 3) Tile.TREES else null },
        Tile.TREES to { adj -> if (adj.count { it == Tile.LUMBERYARD } >= 3) Tile.LUMBERYARD else null },
        Tile.LUMBERYARD to { adj -> if (adj.contains(Tile.TREES) && adj.contains(Tile.LUMBERYARD))  Tile.LUMBERYARD else Tile.OPEN }
    )

    override fun parseInput(): Grid = loadInput()
        .split("\n")
        .filter { it.isNotBlank() }
        .map { line ->
            line.toCharArray().map(Tile.Companion::of).toTypedArray()
        }.let {
            Grid(it.toTypedArray())
        }

    override fun calculateResult1(): Int {
        val grid = parseInput()

        return grid.calculateResourceValue()
    }

    override fun calculateResult2(): Int {
        val current = parseInput()
        val grids = mutableListOf<Grid>()

        while (!grids.contains(current)) { // Find cycle
            grids += current.copyOf()
            current.afterMinutes(1)
        }

        val cycles = grids.drop(grids.indexOf(current)) // Drop non-cyclic first items
        val grid = cycles[(1000000000 - grids.indexOf(current)) % cycles.size]

        return grid.calculateResourceValue()
    }

    private fun Grid.afterMinutes(minutes: Int) {
        repeat(minutes) { n ->
//            println("Minute $n")
//            println(this)
            val changes = mutableMapOf<Coordinate, Tile>()
            for (x in 0 until this.width) {
                for (y in 0 until this.height) {
                    val adjacent = this.getAdjacentAcres(Coordinate(x, y))
                    rules[this[x, y]]!!(adjacent)?.let {
                        changes[Coordinate(x, y)] = it
                    }
                }
            }

            for ((coord, tile) in changes) {
                this[coord] = tile
            }
        }
    }

    private fun Grid.getAdjacentAcres(coord: Coordinate): List<Tile> {
        val minX = max(0, coord.x - 1)
        val maxX = min(this.width - 1, coord.x + 1)
        val minY = max(0, coord.y - 1)
        val maxY = min(this.height - 1, coord.y + 1)
        val ret = mutableListOf<Tile>()
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                if (Coordinate(x, y) == coord) {
                    continue
                }
                ret += this[x, y]
            }
        }

        return ret
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
