package year2019.day15

import base.BaseSolution
import base.Coordinate
import base.Direction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import year2019.intcode.Processor
import year2019.intcode.Program
import java.util.*
import kotlin.math.max
import kotlin.math.min

class Solution : BaseSolution<Program, Int, Int>("Day 15") {
    class Robot(program: Program) {
        var position = Coordinate(0, 0)
        private val input = Channel<Long>(UNLIMITED)
        private val output = Channel<Long>(UNLIMITED)
        private val processor = Processor(program, input, output)
        val map = mutableMapOf<Coordinate, Char>()
        private var lastMove: Direction = Direction.UP

        suspend fun start() = processor.runProgram()

        // Returns result, ie what did the robot find at the destination
        suspend fun walk(direction: Direction): Long {
            input.send(when (direction) {
                Direction.UP -> 1L
                Direction.LEFT -> 3L
                Direction.RIGHT -> 4L
                Direction.DOWN -> 2L
            })
            lastMove = direction

            val o = output.receive()
            val result = when (o) {
                0L -> '#'
                1L -> '.'
                2L -> 'T'
                else -> throw IllegalArgumentException("Invalid output $o")
            }

            val newPosition = position + lastMove
            if (map[newPosition] != 'T') { // The processor might overwrite this with a wall, :shrug:
                map[newPosition] = result
            }
            if (o in listOf(1L, 2L)) { // Not a wall, move there
                position = newPosition
            }

            return o
        }

        suspend fun findAll() { // Traverse all fields
            val toBeVisited = Direction.values().filter { position + it !in map }
            for (next in toBeVisited) {
                val walk = walk(next)
                if (walk in listOf(1L, 2L)) { // Not a wall, move further
                    findAll()
                    walk(next.reverse())
                }
            }
        }

        fun stop() {
            input.close()
            output.close()
        }
    }

    override fun parseInput(): Program = Program(loadInput())

    override fun calculateResult1(): Int = runBlocking {
        val robot = Robot(parseInput())
        launch {
            try {
                robot.start()
            } catch (e: ClosedReceiveChannelException) {
                // Okay, processor suspended in INPUT, but we are finished
            }
        }
        robot.findAll()
        robot.stop() // We need to stop to not suspend forever
        val tankCoordinate = robot.map.filter { it.value == 'T' }.toList().first().first
        return@runBlocking findShortestPath(Coordinate(0, 0), tankCoordinate, robot.map).size - 1 // Last one is the move into the tank
    }

    override fun calculateResult2(): Int = runBlocking {
        val robot = Robot(parseInput())
        launch {
            try {
                robot.start()
            } catch (e: ClosedReceiveChannelException) {
                // Okay, processor suspended in INPUT, but we are finished
            }
        }
        robot.findAll()
        robot.stop()
        val tankCoordinate = robot.map.filter { it.value == 'T' }.toList().first().first
        return@runBlocking fillOxygen(robot.map, tankCoordinate)
    }

    private fun findShortestPath(start: Coordinate, target: Coordinate, map: Map<Coordinate, Char>): List<Coordinate> {
        // BFS
        val visited = mutableSetOf<Coordinate>()
        val queue = ArrayDeque<List<Coordinate>>() // Queue of paths
        queue.addAll(start.neighbors().filter { it !in visited }.filter { (map[it] ?: '#') != '#' }.map { listOf(start, it) })

        while (queue.isNotEmpty()) {
            val next = queue.poll()
            if (next.last() == target) {
                return next
            }
            visited.add(next.last())
            queue.addAll(next.last().neighbors().filter { it !in visited }.filter { (map[it] ?: '#') != '#' }.map {
                next + it
            })
        }

        return emptyList()
    }


    private fun fillOxygen(map: Map<Coordinate, Char>, tankLocation: Coordinate): Int {
        val map = map.toMutableMap()
        var queue = ArrayDeque<Coordinate>()
        var nextQueue = ArrayDeque<Coordinate>()
        queue.addAll(tankLocation.neighbors().filter { map[it] !in listOf('#', 'O') })

        var steps = 0
        while (true) {
            while (queue.isNotEmpty()) { // Complete one step
                val next = queue.poll()
                map[next] = 'O'
                nextQueue.addAll(next.neighbors().filter { map[it] !in listOf('#', 'O') })
            }
            // When finished, swap queues and record that step (i.e. a minute)
            queue = nextQueue
            nextQueue = ArrayDeque()
            steps++
            if (queue.isEmpty()) { // Nothing to process, we are finished
                break
            }
        }

        return steps
    }

    var printedLines = 0
    fun Map<Coordinate, Char>.printMap(position: Coordinate = Coordinate(0, 0)) {
        val maxX = max(this.maxBy { it.key.x }!!.key.x, position.x)
        val maxY = max(this.maxBy { it.key.y }!!.key.y, position.y)
        val minX = min(this.minBy { it.key.x }!!.key.x, position.x)
        val minY = min(this.minBy { it.key.y }!!.key.y, position.y)


        clear(printedLines)
        printedLines = maxY - minY + 2
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                if (position == Coordinate(x, y)) {
                    print("R")
                } else if (x == 0 && y == 0) {
                    print("0")
                } else {
                    print(this[Coordinate(x, y)] ?: ' ')
                }
            }
            println()
        }
    }

    private fun clear(lines: Int) {
        repeat(lines) {
            print("\u001B[1A"); // Move cursor up
            print("\u001B[2K"); // Remove line
        }
    }
}

fun main() {
    Solution().solveWithMeasurement()
}