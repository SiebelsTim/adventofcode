package year2019.day13

import base.BaseSolution
import base.Coordinate
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.*
import year2019.intcode.Processor
import year2019.intcode.Program

class Solution : BaseSolution<Program, Int, Int>("Day 13") {
    fun Tile(id: Int) = when (id) {
        0 -> Tile.EMPTY
        1 -> Tile.WALL
        2 -> Tile.BLOCK
        3 -> Tile.PADDLE
        4 -> Tile.BALL
        else -> throw IllegalArgumentException("Unknown tile type $id")
    }

    enum class Tile(val char: Char) {
        EMPTY(' '), WALL('|'), BLOCK('#'), PADDLE('_'), BALL('o');

        override fun toString(): String = "$char"
    }

    sealed class Command {
        data class TileCommand(val x: Int, val y: Int, val type: Tile) : Command() {
            val coord: Coordinate get() = Coordinate(x, y)
        }

        data class ScoreCommand(val score: Int) : Command()
    }

    sealed class Action {
        class TileAction(val tileCommand: Command.TileCommand) : Action()
        class ScoreAction(val score: Int) : Action()
        object MapCompletedAction : Action()
        class GetScoreAction(val future: CompletableDeferred<Int>) : Action()
        object FinishedAction : Action()
    }

    override fun parseInput(): Program = Program(loadInput())

    override fun calculateResult1(): Int = runBlocking {
        val program = parseInput()
        val output = Channel<Long>(UNLIMITED)
        val processor = Processor(program, output = output)

        processor.runProgram()

        val out = output.toList().toCommandList()

        return@runBlocking out.count { (it as? Command.TileCommand)?.type == Tile.BLOCK }
    }

    override fun calculateResult2(): Int = runBlocking {
        val program = parseInput().withAddressChanged(0, 2)
        val output = Channel<Long>(UNLIMITED)
        val input = Channel<Long>(RENDEZVOUS)
        val processor = Processor(program, output = output, input = input, debugging = false)

        val updates = Channel<Pair<Coordinate, Coordinate>>() // Send requests for updates to the controller
                                                                                                   // The actor is responsible for this channel
        val actor = actor<Action> {
            val map = mutableMapOf<Coordinate, Tile>()
            var mapCompleted = false // Only send updates to the controller and only draw map afterwards
            var score = 0

            var ball = Coordinate(0, 0)
            var paddle = Coordinate(0, 0)

            for (action in channel) {
                val exhausted: Any = when (action) {
                    is Action.TileAction -> {
                        map[action.tileCommand.coord] = action.tileCommand.type
                        if (mapCompleted) {
                            printMap(map, score)
                        }

                        when (action.tileCommand.type) {
                            Tile.BALL -> ball = action.tileCommand.coord
                            Tile.PADDLE -> paddle = action.tileCommand.coord
                            else -> Unit
                        }.let {}

                        if (mapCompleted && action.tileCommand.type == Tile.BALL) { // Ball updated, update controller as well
                            updates.send(paddle to ball)
                        }

                        Unit
                    }
                    is Action.ScoreAction -> score = action.score
                    is Action.MapCompletedAction -> {
                        if (!mapCompleted) {
                            mapCompleted = true
                            updates.send(paddle to ball)
                            printMap(map, score)
                        }

                        Unit
                    }
                    is Action.GetScoreAction -> action.future.complete(score)
                    is Action.FinishedAction -> updates.close()
                }
            }
        }


        coroutineScope {
            launch {
                for ((paddle, ball) in updates) {
                    val direction = when {
                        ball.x == paddle.x -> 0
                        ball.x < paddle.x -> -1
                        else -> 1
                    }
                    input.send(direction.toLong())
                }
            }

            launch {
                output.chunked(3)
                        .consumeAsFlow()
                        .map { it.toCommand() }
                        .collect {
                            if (it is Command.TileCommand) {
                                actor.send(Action.TileAction(it))
                            } else {
                                actor.send(Action.ScoreAction((it as Command.ScoreCommand).score))
                                actor.send(Action.MapCompletedAction)
                            }
                        }
            }

            processor.runProgram()

            actor.send(Action.FinishedAction)
        }

        // coroutinescope makes sure the entire program finished before asking for the score
        val scoreDeferred = CompletableDeferred<Int>()
        actor.send(Action.GetScoreAction(scoreDeferred))
        scoreDeferred.await().also {
            actor.close()
        }
    }

    var printedLines = 0
    private suspend fun printMap(map: Map<Coordinate, Tile>, score: Int) {
        if (true) return;
        val minX = map.keys.minBy { it.x }!!.x
        val minY = map.keys.minBy { it.y }!!.y
        val maxX = map.keys.maxBy { it.x }!!.x
        val maxY = map.keys.maxBy { it.y }!!.y

        clear(printedLines)
        printedLines = maxY + 2

        println("SCORE: $score")
        for (y in minY..maxY) {
            for (x in minX..maxX) {
                print(map[Coordinate(x, y)] ?: " ")
            }
            println()
        }

        delay(100)
    }

    private fun List<Long>.toCommandList() = chunked(3).map {
        it.toCommand()
    }

    private fun List<Long>.toCommand(): Command {
        require(size == 3)
        val (x, y, type) = this
        return if (x == -1L && y == 0L) {
            Command.ScoreCommand(type.toInt())
        } else {
            Command.TileCommand(x.toInt(), y.toInt(), Tile(type.toInt()))
        }
    }

    private suspend fun <T> Channel<T>.chunked(size: Int) = GlobalScope.produce {
        var chunk = listOf<T>()
        this@chunked.consumeEach { it: T ->
            chunk += it
            if (chunk.size == size) {
                send(chunk)
                chunk = listOf()
            }
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