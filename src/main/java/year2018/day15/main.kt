package year2018.day15

import base.BaseSolution
import year2018.day15.Solution.Grid
import java.util.*

class Solution : BaseSolution<Grid, Int, Int>("Day 15") {
    class Grid(val grid: List<List<Type>>, val units: List<Entity>) {
        val aliveUnits: List<Entity> get() = units.filterNot(Entity::isDead)

        override fun toString(): String = StringBuilder().apply {
            val aliveUnits = units.filterNot(Entity::isDead)
            for (y in 0 until grid.size) {
                for (x in 0 until grid[y].size) {
                    val unit = aliveUnits.find { it.position == Coordinate(x, y) }
                    if (unit != null) {
                        append(unit)
                    } else {
                        append(grid[y][x])
                    }
                }
                append("  ")
                units.filter { !it.isDead && it.position.y == y }
                    .sortedBy { it.position }
                    .forEach { unit ->
                        append("%s (%d); ".format(unit.toString(), unit.hp))
                    }

                append("\n")
            }
        }.toString()

        operator fun get(coord: Coordinate) = grid[coord.y][coord.x]

        val width by lazy {
            grid.maxBy { it.size }!!.size
        }

        val height= grid.size

        // Returns true if round finished
        fun tick(): Boolean {
            for (unit in units.sortedBy { it.position }) {
                if (unit.isDead) {
                    continue
                }
                unit.moveDirection(this)?.let {
                    unit.position += it
                }

                val position = unit.position

                val enemies = aliveUnits.filter(unit::isEnemy)
                if (enemies.isEmpty()) {
                    return false
                }

                val target = Direction.values().mapNotNull { dir ->
                    enemies.find { it.position == position + dir }
                }
                    .sortedBy { it.position }
                    .minBy { it.hp }
                if (target != null) {
                    target.hp -= unit.attackPower
//                    println("" + unit.position + "kicks " + target.position)
                }
            }

            return true
        }
    }

    enum class Type {
        WALL, OPEN;

        override fun toString(): String = when (this) {
            WALL -> "#"
            OPEN -> "."
        }

        companion object {
            fun of(c: Char): Type = when (c) {
                '#' -> WALL
                '.', 'E', 'G' -> OPEN
                else -> throw IllegalArgumentException("Unknown type")
            }
        }
    }

    enum class Direction(val dx: Int, val dy: Int) {
        UP(0, -1), LEFT(-1, 0), RIGHT(+1, 0), DOWN(0, +1);
    }
    data class Node(val position: Coordinate, val distance: Int, val previous: Node? = null, val direction: Direction? = null)

    sealed class Entity(val startingPosition: Coordinate,
                        var position: Coordinate = startingPosition,
                        var hp: Int = 200,
                        val attackPower: Int = 3) {
        val isDead: Boolean get() = hp < 0
        fun moveDirection(grid: Grid): Direction? {
            val aliveUnits = grid.units.filterNot(Entity::isDead)
            val targets = aliveUnits.filter(this@Entity::isEnemy)

            val inRangeOfTarget = targets.filter { unit ->
                Direction.values().filter {
                    unit.position == position + it
                }.any()
            }

            if (inRangeOfTarget.isNotEmpty()) {
                // We don't need to move, we can attack directly
                return null
            }

            // BFS
            val visited = mutableMapOf<Coordinate, Boolean>()
            val toVisit: Deque<Node> = ArrayDeque<Node>().apply {
                add(Node(position, 1))
            }
            val targetUnits = mutableListOf<Node>()
            var distance = Int.MAX_VALUE
            while (toVisit.isNotEmpty()) {
                val current = toVisit.pollFirst()
                if (visited[current.position] == true) {
                    continue
                }

                if (distance < current.distance) {
                    break
                }

                visited[current.position] = true
                for (dir in Direction.values()) {
                    val newPosition = current.position + dir
                    if (visited[newPosition] == true) {
                        continue
                    }

                    val targetUnit = aliveUnits.firstOrNull { it.position == newPosition }
                    if (grid[newPosition] == Type.OPEN && targetUnit == null) {
                        toVisit.add(Node(newPosition, current.distance + 1, current, dir))
                    } else if (targetUnit != null && isEnemy(targetUnit)) {
                        targetUnits += current
                        distance = current.distance
                    }
                }
            }

            val target = targetUnits.minBy { it.position }
            if (target != null) {
                return generateSequence(target) {
                    it.previous
                }.first { it.previous?.previous == null }.direction
            }

            return null

        }
        abstract fun isEnemy(unit: Entity): Boolean

        class Elf(startingPosition: Coordinate) : Entity(startingPosition) {
            override fun toString(): String = "E"
            override fun isEnemy(unit: Entity): Boolean = unit !is Elf
        }
        class Goblin(startingPosition: Coordinate) : Entity(startingPosition) {
            override fun toString(): String = "G"
            override fun isEnemy(unit: Entity): Boolean = unit !is Goblin
        }

        companion object {
            fun isEntity(char: Char): Boolean = char in listOf('G', 'E')
            fun of(c: Char, position: Coordinate): Entity = when (c) {
                'E' -> Elf(position)
                'G' -> Goblin(position)
                else -> throw IllegalArgumentException("Entity not found")
            }
        }
    }

    data class Coordinate(val x: Int, val y: Int) : Comparator<Coordinate> by compareBy(Coordinate::y, Coordinate::x), Comparable<Coordinate> {
        override fun compareTo(other: Coordinate): Int = this.compare(this, other)

        operator fun plus(coord: Coordinate) = Coordinate(x+coord.x, y+coord.y)
        operator fun plus(direction: Direction) = Coordinate(x+direction.dx, y+direction.dy)
    }

    override fun parseInput(): Grid {
        val units = mutableListOf<Entity>()
        val grid = loadInput()
            .split("\n")
            .filter { it.isNotBlank() }
            .mapIndexed { y, it -> it.toCharArray().mapIndexed { x, c ->
                if (Entity.isEntity(c)) {
                    units += Entity.of(c, Coordinate(x, y))
                }
                Type.of(c) }
            }

        // Reading order for units is automatically achieved
        return Grid(grid, units)
    }

    override fun calculateResult1(): Int {
        val input = parseInput()
        println(input)
        var i = 0
        while (input.tick()) {
            println("Round ${++i}")
            println(input)
        }

        println("END")
        println(input)

        return input.aliveUnits.sumBy { it.hp } * i
    }

    override fun calculateResult2(): Int {
        return -1
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
