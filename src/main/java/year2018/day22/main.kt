package year2018.day22

import base.BaseSolution
import base.Coordinate
import base.Direction
import java.util.*

class Solution : BaseSolution<Pair<Int, Coordinate>, Int, Int>("Day 22") {
    class Grid(val target: Coordinate, val depth: Int) {
        private val grid = mutableMapOf<Coordinate, Type>()

        private val erosionLevels = mutableMapOf<Coordinate, Int>()

        fun erosionLevel(coord: Coordinate): Int {
            return erosionLevels.getOrPut(coord) {
                erosionLevelImpl(coord)
            }
        }

        private fun erosionLevelImpl(coord: Coordinate): Int {
            if (coord == Coordinate(0,0) || coord == target) {
                return (0 + depth) % 20183
            }

            if (coord.y == 0) {
                return (coord.x * 16807 + depth) % 20183
            }

            if (coord.x == 0) {
                return (coord.y * 48271 + depth) % 20183
            }

            return (erosionLevel(coord.left) * erosionLevel(coord.up) + depth) % 20183
        }

        fun erosionLevelToType(level: Int): Type = when(level % 3) {
            0 -> Type.ROCKY
            1 -> Type.WET
            2 -> Type.NARROW
            else -> throw IllegalArgumentException("Cannot happen")
        }

        fun getType(coord: Coordinate): Type {
            grid[coord]?.let {
                return it
            }

            return erosionLevelToType(erosionLevel(coord)).also {
                grid[coord] = it
            }
        }

        operator fun get(x: Int, y: Int) = grid[Coordinate(x, y)] ?: getType(Coordinate(x, y))
        operator fun get(coord: Coordinate) = this[coord.x, coord.y]
        operator fun set(coord: Coordinate, type: Type) {
            grid[coord] = type
        }
    }

    enum class Type(private val c: Char) {
        ROCKY('.'), WET('='), NARROW('|');

        override fun toString(): String = "$c"

        val validTools: Set<Tool> by lazy {
            when (this) {
                ROCKY -> setOf(Tool.CLIMBING, Tool.TORCH)
                WET -> setOf(Tool.CLIMBING, Tool.NEITHER)
                NARROW -> setOf(Tool.TORCH, Tool.NEITHER)
            }
        }
    }

    enum class Tool {
        CLIMBING, TORCH, NEITHER
    }

    override fun parseInput(): Pair<Int, Coordinate> {
        val (depthStr, targetStr) = loadInput().lines()

        val target = targetStr.substring("target: ".length).split(",").map { it.toInt() }

        return depthStr.substring("depth: ".length).toInt() to Coordinate(target[0], target[1])
    }


    override fun calculateResult1(): Int {
        val (depth, target) = parseInput()

        val grid = Grid(target, depth)

        var riskLevel = 0
        for (y in 0..target.y) {
            for (x in 0..target.x) {
                riskLevel += grid.getType(Coordinate(x, y)).let {
                    when (it) {
                        Type.ROCKY -> 0
                        Type.WET -> 1
                        Type.NARROW -> 2
                    }
                }
            }
        }

        return riskLevel
    }

    data class Node(val position: Coordinate, val cost: Int, val tool: Tool) : Comparable<Node> {
        override fun compareTo(other: Node): Int = cost.compareTo(other.cost)
    }

    override fun calculateResult2(): Int {
        val (depth, target) = parseInput()
        val grid = Grid(target, depth)

        // BFS
        val toVisit: PriorityQueue<Node> = PriorityQueue<Node>().apply {
            add(Node(Coordinate(0, 0), 0, Tool.TORCH))
        }
        val visited = mutableMapOf<Pair<Coordinate, Tool>, Int>()

        while (toVisit.isNotEmpty()) {
            val node = toVisit.poll()
            val pos = node.position
            val tool = node.tool
            if (pos == target && tool == Tool.TORCH) {
                return node.cost
            }

            val nextNodes = mutableListOf<Node>()

            for (nTool in Tool.values()) {
                if (nTool in grid[pos].validTools && nTool != tool) {
                    nextNodes.add(Node(pos, node.cost + 7, nTool))
                }
            }

            for (dir in Direction.values()) {
                val newPos = pos + dir
                if (newPos.x >= 0 && newPos.y >= 0 && node.tool in grid[pos + dir].validTools) {
                    nextNodes.add(Node(pos + dir, node.cost + 1, node.tool))
                }
            }

            for (nextNode in nextNodes) {
                val pair = nextNode.position to nextNode.tool
                val previousCost = visited[pair]
                if (previousCost == null || previousCost > nextNode.cost) {
                    toVisit.add(nextNode)
                    visited[pair] = nextNode.cost
                }
            }
        }

        throw IllegalStateException("No path found")
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
