package year2017.day3

import base.BaseSolution
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.sqrt

class Solution : BaseSolution<Int, Int, Int>("Day 3") {
    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1), LEFT(-1, 0), RIGHT(+1, 0), DOWN(0, +1);

        val nextTurn: Direction
            get() = when(this) {
                UP -> LEFT
                LEFT -> DOWN
                RIGHT -> UP
                DOWN -> RIGHT
            }
    }

    override fun parseInput(): Int = loadInput().trim().toInt()

    override fun calculateResult1(): Int {
        val input = parseInput()
        val sideLength = sideLength(input)
        val stepsToCenter = (sideLength - 1) / 2
        val midpoints = getMidpoints(sideLength)

        return stepsToCenter + midpoints.map { abs(input - it) }.min()!!
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val size = sideLength(input)
        val grid = Array<IntArray>(size) { IntArray(size) }
        grid[size/2][size/2] = 1

        var currentPosition = Pair(size/2, size/2)
        var direction = Direction.RIGHT

        return generateSequence(1) {
            currentPosition += direction

            if (grid.valueFor(currentPosition + direction.nextTurn) == 0) {
                // Turn as soon as possible
                direction = direction.nextTurn
            }
            grid.sumNeighbors(currentPosition).apply {
                grid[currentPosition.second][currentPosition.first] = this
            }
        }.first { it > input }
    }

    fun Array<IntArray>.valueFor(pos: Pair<Int, Int>) = valueFor(pos.first, pos.second)
    fun Array<IntArray>.valueFor(x: Int, y: Int) = if (x in 0 until size && y in 0 until size) {
        this[y][x]
    } else {
        null
    }

    fun Array<IntArray>.sumNeighbors(position: Pair<Int, Int>): Int =
        (position.first - 1..position.first + 1).map { x ->
            (position.second - 1..position.second + 1).map { y ->
                valueFor(x, y)
            }
        }.flatten()
            .filterNotNull()
            .sum()


    private fun getMidpoints(sideLength: Int): List<Int> {
        val lowerRightCorner = sideLength * sideLength
        val midLength = (sideLength - 1) / 2
        return List(4) {
            lowerRightCorner - (midLength + (it * sideLength.dec()))
        }
    }

    private fun sideLength(n: Int): Int {
        val length = ceil(sqrt(n.toDouble())).toInt()
        if (length % 2 == 0) {
            return length + 1
        }

        return length
    }

    private operator fun Pair<Int, Int>.plus(dir: Direction) = Pair(first + dir.dx, second + dir.dy)
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
