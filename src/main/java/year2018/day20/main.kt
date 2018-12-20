package year2018.day20

import base.BaseSolution
import base.Coordinate
import base.Direction
import java.util.*

class Solution : BaseSolution<Solution.Regexp, Int, Int>("Day 20") {
    sealed class Regexp() {
        class Terminal(val directions: List<Direction>) : Regexp() {
            override fun toString(): String = directions.joinToString("") {
                when (it) {
                    Direction.UP -> "N"
                    Direction.LEFT -> "W"
                    Direction.RIGHT -> "E"
                    Direction.DOWN -> "S"
                }
            }
        }

        class Composite(val regexps: List<Regexp>) : Regexp() {
            override fun toString(): String = regexps.joinToString("")
        }

        class Branch(val regexps: List<Regexp>) : Regexp() {
            override fun toString(): String = buildString {
                append('(')
                append(regexps.joinToString("|"))
                append(')')
            }
        }
    }

    override fun parseInput(): Regexp {
        val input = loadInput().trim().toCharArray().iterator()

        return RegexParser(input).parse()
    }

    class RegexParser(val input: CharIterator) {
        private var current = input.next()

        fun parse(): Regexp {
            expect('^')
            return parseRegex().also {
                expect('$')
            }
        }

        private fun next(): Char {
            if (input.hasNext()) {
                current = input.next()
            }
            return current
        }

        private fun accept(c: Char): Boolean {
            if (current == c) {
                next()
                return true
            }

            return false
        }

        private fun expect(vararg cs: Char) {
            if (current !in cs) {
                throw IllegalArgumentException("Expected ${cs.joinToString(",")}, was $current")
            }

            next()
        }

        private fun parseRegex(): Regexp {
            val regexps = mutableListOf<Regexp>()

            while (!accept('$')) {
                if (accept('(')) {
                    regexps += parseBranchRegex()
                } else if (current.isDirection()) {
                    regexps += parseTerminalRegex()
                } else if (current in ")|") {
                    break
                }
            }

            if (regexps.size == 1) {
                return regexps.first()
            }

            return Regexp.Composite(regexps)
        }


        private fun parseBranchRegex(): Regexp.Branch {
            val children = mutableListOf<Regexp>()

            while (!accept(')')) {
                children += parseRegex()
                if (accept('|')) { // |) means empty group
                    if (current == ')') {
                        children += Regexp.Terminal(listOf())
                    }
                }
            }

            return Regexp.Branch(children)
        }

        private fun parseTerminalRegex(): Regexp.Terminal {
            val ret = mutableListOf<Direction>()
            while (current.isDirection()) {
                ret += current.toDirection()
                next()
            }

            return Regexp.Terminal(ret)
        }

        private fun Char.toDirection(): Direction = when(this) {
            'N' -> Direction.UP
            'E' -> Direction.RIGHT
            'W' -> Direction.LEFT
            'S' -> Direction.DOWN
            else -> throw IllegalArgumentException("Invalid direction $this")
        }

        private fun Char.isDirection() = this in "NEWS"
    }

    class Grid(val size: Int) {
        private val grid = Array(size) {
            CharArray(size) { '#' }
        }

        override fun toString(): String = grid.joinToString("\n") {
            it.joinToString("")
        }

        operator fun get(x: Int, y: Int) = grid[y][x]
        operator fun get(coord: Coordinate) = this[coord.x, coord.y]
        operator fun set(coord: Coordinate, c: Char) {
            grid[coord.y][coord.x] = c
        }

    }

    override fun calculateResult1(): Int {
        val grid = getGrid()
        val start = Coordinate(grid.size / 2, grid.size / 2)

        return grid.paths(start, 1).maxBy { it.distance }!!.distance
    }

    override fun calculateResult2(): Int {
        val grid = getGrid()
        val start = Coordinate(grid.size / 2, grid.size / 2)

        return grid.paths(start, 1000).count()
    }

    private fun getGrid(): Grid {
        val input = parseInput()
        val grid = Grid(210) // Manual inspection. `input.length*2` would always work but is way overkill
        val start = Coordinate(grid.size / 2, grid.size / 2)

        grid.move(input, start)

        return grid
    }

    private fun Grid.move(input: Regexp, position: Coordinate): Coordinate {
        var pos = position
        this[pos] = '.'

        when (input) {
            is Regexp.Terminal -> {
                for (dir in input.directions) {
                    // Move one for wall
                    pos += dir
                    this[pos] = if (dir in listOf(Direction.UP, Direction.DOWN)) '-' else '|'
                    // And another one for the next room
                    pos += dir
                    this[pos] = '.'
                }
            }
            is Regexp.Composite -> input.regexps.forEach { pos = move(it, pos) }
            is Regexp.Branch -> input.regexps.forEach { move(it, pos) }
        }

        return pos
    }


    data class Node(val position: Coordinate, val distance: Int, val previous: Node? = null)
    private fun Grid.paths(start: Coordinate, minDistance: Int): Set<Node> {
        // BFS
        val toVisit: Deque<Node> = ArrayDeque<Node>().apply {
            push(Node(start, 0))
        }
        val visited = mutableMapOf<Coordinate, Boolean>()
        val paths = mutableSetOf<Node>()

        while (toVisit.isNotEmpty()) {
            val node = toVisit.pollLast()
            if (visited[node.position] == true) {
                continue
            }

            if (node.distance >= minDistance) {
                paths.add(node)
            }

            val pos = node.position
            visited[pos] = true
            for (dir in Direction.values()) {
                if (this[pos + dir] in "-|") {
                    toVisit.push(Node(pos + dir + dir, node.distance + 1, node))
                }
            }
        }

        return paths
    }

}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
