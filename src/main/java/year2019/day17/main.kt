package year2019.day17

import base.BaseSolution
import base.Coordinate
import base.Direction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import year2019.intcode.Processor
import year2019.intcode.Program

/**
 * Everything
 * R,12,L,6,R,12,L,8,L,6,L,10,R,12,L,6,R,12,R,12,L,10,L,6,R,10,L,8,L,6,L,10,R,12,L,10,L,6,R,10,L,8,L,6,L,10,R,12,L,10,L,6,R,10,R,12,L,6,R,12,R,12,L,10,L,6,R,10
 *
 * Pattern
 * A,B,A,C,B,C,B,C,A,C
 *
 * A
 * R,12,L,6,R,12
 *
 * B
 * L,8,L,6,L,10
 *
 * C
 * R,12,L,10,L,6,R,10
 */
class Solution : BaseSolution<Program, Int, Long>("Day 17") {
    override fun parseInput(): Program = Program(loadInput())

    override fun calculateResult1(): Int = runBlocking {
        val output = Channel<Long>(UNLIMITED)
        val processor = Processor(parseInput(), output = output)

        processor.runProgram()

        val map = output.consumeMap()
        val intersections = map.findIntersections()

        intersections.sumBy { it.alignmentParameter }
    }

    override fun calculateResult2(): Long = runBlocking {
        val output = Channel<Long>(UNLIMITED)
        val input = Channel<Long>(UNLIMITED)
        val processor = Processor(parseInput().withAddressChanged(0, 2), input = input, output = output)
        var lastOutput = -1L // Might be racy, but we only access it from a single coroutine

        coroutineScope {
            launch {
                processor.runProgram()
            }

            launch {
                for (o in output) {
                    val c = o.toChar()
                    if (c == '\n' && lastOutput.toChar() == c) {
                        delay(100)
                    }
                    print(c)
                    lastOutput = o
                }
            }

            input.sendAsciiLine("A,B,A,C,B,C,B,C,A,C") // Main movement routine
            input.sendAsciiLine("R,12,L,6,R,12") // A routine
            input.sendAsciiLine("L,8,L,6,L,10") // B routine
            input.sendAsciiLine("R,12,L,10,L,6,R,10") // C routine
            input.sendAsciiLine("n")          // Video feed? y/n
        }

        lastOutput
    }

    private fun printMap(map: Map<Coordinate, Char>, intersections: Set<Coordinate>) {
        val maxX = map.maxBy { it.key.x }!!.key.x
        val maxY = map.maxBy { it.key.y }!!.key.y
        val minX = map.minBy { it.key.x }!!.key.x
        val minY = map.minBy { it.key.y }!!.key.y

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                if (Coordinate(x, y) in intersections) {
                    print('O')
                } else {
                    print(map[Coordinate(x, y)] ?: '.')
                }
            }
            println()
        }
    }

    // Returns a map of coordinate to field that does not contain a .
    private suspend fun Channel<Long>.consumeMap(): Map<Coordinate, Char> {
        val stringBuilder = StringBuilder()
        for (o in this) { // channel to string
            stringBuilder.append(o.toChar())
        }

        val output = stringBuilder.toString()
        val map = mutableMapOf<Coordinate, Char>()
        for ((y, line) in output.lines().withIndex()) {
            for ((x, c) in line.toCharArray().withIndex().filter { it.value != '.' }) {
                map[Coordinate(x, y)] = c
            }
        }

        return map
    }

    private fun Map<Coordinate, Char>.findIntersections(): Set<Coordinate> {
        val ret = mutableSetOf<Coordinate>()
        for ((coord, c) in this) {
            if (this.containsKey(coord + Direction.LEFT)
                    && this.containsKey(coord + Direction.RIGHT)
                    && this.containsKey(coord + Direction.UP)
                    && this.containsKey(coord + Direction.DOWN)) {
                ret += coord
            }
        }

        return ret
    }

    private val Coordinate.alignmentParameter get() = x * y

    private suspend fun Channel<Long>.sendAsciiLine(string: String) {
        string.toCharArray().forEach { send(it.toLong()) }
        send('\n'.toLong())
    }
}

fun main() {
    Solution().solveWithMeasurement()
}