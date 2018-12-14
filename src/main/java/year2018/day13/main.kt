package year2018.day13

import base.BaseSolution
import year2018.day13.Solution.Coordinate
import year2018.day13.Solution.System
import kotlin.IllegalStateException

class Solution : BaseSolution<System, Coordinate, Coordinate>("Day 13") {
    class System(private val grid: Array<Array<Type>>, val carts: MutableList<Cart>) {
        override fun toString(): String = StringBuilder().apply {
            for (y in 0 until grid.size) {
                for (x in 0 until grid[y].size) {
                    val cart = carts.firstOrNull { !it.crashed && it.coordinate == Coordinate(x, y) }
                    if (cart != null) {
                        append(cart.direction.toString())
                    } else {
                        append(grid[y][x].toString())
                    }
                }
                append("\n")
            }
        }.toString()

        operator fun get(x: Int, y: Int) = grid[y][x]
        operator fun get(coord: Coordinate) = grid[coord.y][coord.x]

        inline fun tick(crashCb: (Cart) -> Unit = {}) {
            carts.sortWith(compareBy({ it.coordinate.y }, { it.coordinate.x }))
            for (cart in carts) {
                if (cart.crashed) {
                    continue
                }
                cart.move()
                val currentPosition = this[cart.coordinate]
                cart.turn(currentPosition)
                carts
                    .groupBy { it.coordinate }
                    .filter { it.value.size > 1 }
                    .flatMap { it.value } // Now contains all collisions
                    .forEach {
                        it.crashed = true
                        crashCb(it)
                    }
            }
            carts.removeIf { it.crashed }
        }
    }
    class Cart(var coordinate: Coordinate,
               var direction: Direction,
               var nextIntersectionTurn: Turn = Turn.LEFT,
               var crashed: Boolean = false) {
        companion object {
            fun isCart(c: Char) = c in listOf('<', '>', 'v', '^')
            fun of(x: Int, y: Int, c: Char) = Cart(Coordinate(x, y), Direction.of(c))
        }

        fun turn(currentPosition: Type) {
            when (currentPosition) {
                Type.INTERSECTION -> {
                    direction = direction.turn(nextIntersectionTurn)
                    nextIntersectionTurn = nextIntersectionTurn.next()
                }
                Type.RIGHTCURVE -> direction = when (direction) {
                    Direction.UP, Direction.DOWN -> direction.turnRight()
                    Direction.LEFT, Direction.RIGHT -> direction.turnLeft()
                }
                Type.LEFTCURVE -> direction = when (direction) {
                    Direction.UP, Direction.DOWN -> direction.turnLeft()
                    Direction.LEFT, Direction.RIGHT -> direction.turnRight()
                }
                Type.EMPTY -> throw IllegalStateException("Cannot drive into empty space")
            }
        }

        fun move() {
            coordinate = coordinate + direction
        }
    }

    data class Coordinate(val x: Int, val y: Int) {
        override fun toString(): String = "$x,$y"

        operator fun plus(direction: Direction) = Coordinate(x + direction.dx, y + direction.dy)
    }

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1),
        RIGHT(+1, 0),
        DOWN(0, +1),
        LEFT(-1, 0);

        fun turnLeft() = values()[(ordinal - 1 + values().size) % values().size]
        fun turnRight() = values()[(ordinal + 1) % values().size]
        fun turn(turn: Turn) = when (turn) {
            Turn.LEFT -> turnLeft()
            Turn.RIGHT -> turnRight()
            Turn.STRAIGHT -> this
        }

        override fun toString(): String = when (this) {
            UP -> "^"
            DOWN -> "v"
            LEFT -> "<"
            RIGHT -> ">"
        }

        companion object {
            fun of(c: Char) = when (c) {
                '^' -> UP
                'v' -> DOWN
                '<' -> LEFT
                '>' -> RIGHT
                else -> throw IllegalArgumentException("'$c' is not a direction")
            }
        }
    }

    enum class Turn {
        LEFT, STRAIGHT, RIGHT;

        fun next(): Turn = values()[(ordinal + 1) % values().size]
    }

    enum class Type {
        HORIZONTAL,
        VERTICAL,
        INTERSECTION,
        RIGHTCURVE,
        LEFTCURVE,
        EMPTY;

        override fun toString(): String = when(this) {
            HORIZONTAL -> "-"
            VERTICAL -> "|"
            INTERSECTION -> "+"
            RIGHTCURVE -> "/"
            LEFTCURVE -> "\\"
            EMPTY -> " "
        }

        companion object {
            fun of(c: Char): Type {
                return when (c) {
                    '+' -> INTERSECTION
                    '-', '>', '<' -> HORIZONTAL
                    '|', 'v', '^' -> VERTICAL
                    '/' -> RIGHTCURVE
                    '\\' -> LEFTCURVE
                    ' ' -> EMPTY
                    else -> throw IllegalArgumentException("Unknown track type '$c'")
                }
            }
        }
    }

    override fun parseInput(): System {
        val lines = loadInput().lines().filter { it.isNotBlank() }
        val height = lines.size
        val width = lines.maxBy { it.length }!!.length
        val carts = mutableListOf<Cart>()
        val grid = Array(height) { y ->
            Array(width) { x ->
                val c = if (x < lines[y].length) {
                    lines[y][x]
                } else {
                    ' '
                }
                if (Cart.isCart(c)) {
                    carts += Cart.of(x, y, c)
                }
                Type.of(c)
            }
        }

        return System(grid, carts)
    }

    override fun calculateResult1(): Coordinate {
        val input = parseInput()
        val carts = input.carts
        while (carts.map { it.coordinate }.distinct().size == carts.size) {
//            println(input)
//            println("====================================================")
            input.tick() {
                return it.coordinate
            }
        }

        throw IllegalStateException("No crash found")
    }

    override fun calculateResult2(): Coordinate {
        val input = parseInput()
        val carts = input.carts
        while (carts.size > 1) {
            input.tick()
        }
        return carts.first().coordinate
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
