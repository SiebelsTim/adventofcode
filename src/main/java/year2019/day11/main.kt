package year2019.day11

import base.BaseSolution
import base.Coordinate
import base.Direction
import com.github.ajalt.mordant.TermColors
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import year2019.intcode.Processor
import year2019.intcode.Program

class Solution : BaseSolution<Program, Int, String>("Day 11") {
    data class Robot(var direction: Direction = Direction.UP, var position: Coordinate = Coordinate(0, 0)) {
        fun move() = this.apply {
            position += direction
        }

        fun turnRight() = this.apply {
            direction = when (direction) {
                Direction.UP -> Direction.RIGHT
                Direction.LEFT -> Direction.UP
                Direction.RIGHT -> Direction.DOWN
                Direction.DOWN -> Direction.LEFT
            }
        }

        fun turnLeft() = this.apply {
            direction = when (direction) {
                Direction.UP -> Direction.LEFT
                Direction.LEFT -> Direction.DOWN
                Direction.RIGHT -> Direction.UP
                Direction.DOWN -> Direction.RIGHT
            }
        }

        suspend fun paint(program: Program, startingPanel: Long = 0): Map<Coordinate, Long> = coroutineScope {
            val input = Channel<Long>(1)
            val output = Channel<Long>(2)
            val processor = Processor(program, input, output)

            val robot = Robot()
            val panels = mutableMapOf<Coordinate, Long>()

            input.send(startingPanel)

            launch {
                var outCount = 0
                for (out in output) {
                    if (outCount % 2 == 0) { // First, paint panel
                        if (out == 0L) {
                            panels[robot.position] = 0L
                        } else {
                            require(out == 1L)
                            panels[robot.position] = 1L
                        }
                    } else { // then, move
                        if (out == 0L) {
                            robot.turnLeft()
                        } else {
                            require(out == 1L)
                            robot.turnRight()
                        }
                        robot.move()
                        // After moving, send current color
                        input.send(panels.getOrDefault(robot.position, 0L))
                    }
                    outCount++
                }
            }


            processor.runProgram()

            panels
        }

    }
    override fun parseInput(): Program  = Program(loadInput())

    override fun calculateResult1(): Int = runBlocking {
        Robot().paint(parseInput(), 0).size
    }

    override fun calculateResult2(): String = runBlocking {
        val picture = Robot().paint(parseInput(), 1)

        paintPicture(picture)
        "GREJALPR"
    }

    private fun paintPicture(picture: Map<Coordinate, Long>) {
        val t = TermColors()
        val minX = picture.minBy { it.key.x }!!.key.x
        val minY = picture.minBy { it.key.y }!!.key.y
        val maxX = picture.maxBy { it.key.x }!!.key.x
        val maxY = picture.maxBy { it.key.y }!!.key.y

        for (y in minY..maxY) {
            for (x in minX..maxX) {
                val color = picture.getOrDefault(Coordinate(x, y), 0)
                if (color == 0L) {
                    print(t.black("."))
                } else {
                    print(t.white("#"))
                }
            }
            println()
        }
    }

}

fun main() {
    Solution().solveWithMeasurement()
}