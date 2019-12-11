package year2019.day7

import base.BaseSolution
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import year2019.intcode.Processor
import year2019.intcode.Program

class Solution : BaseSolution<Program, Long, Long>("Day 7") {
    override fun parseInput(): Program = Program(loadInput())

    override fun calculateResult1() = runBlocking {
        val program = parseInput()
        val phases = listOf(
                0, 1, 2, 3, 4
        )

        val permutations = phases.permutations()

        permutations.map {
            async {
                runAmplifiers(program, it)
            }
        }.toList().awaitAll().max()!!
    }

    override fun calculateResult2() = runBlocking {
        val program = parseInput()
        val phases = listOf(
                5, 6, 7, 8, 9
        )

        val permutations = phases.permutations()

        permutations.map {
            async {
                runAmplifiers(program, it)
            }
        }.toList().awaitAll().max()!!
    }

    private suspend fun runAmplifiers(program: Program, phases: List<Int>) = coroutineScope {
        val broadcast = BroadcastChannel<Long>(2)
        val inputA = broadcast.openSubscription()
        val outputA = Channel<Long>(UNLIMITED)
        val outputB = Channel<Long>(UNLIMITED)
        val outputC = Channel<Long>(UNLIMITED)
        val outputD = Channel<Long>(UNLIMITED)
        val outputE = broadcast
        val a = Processor(program, inputA, outputA)
        val b = Processor(program, outputA, outputB)
        val c = Processor(program, outputB, outputC)
        val d = Processor(program, outputC, outputD)
        val e = Processor(program, outputD, outputE)
        val amplifiers = listOf(a, b, c, d, e)
        val inputs = listOf(broadcast, outputA, outputB, outputC, outputD)

        for ((idx, input) in inputs.withIndex()) {
            input.send(phases[idx].toLong())
        }

        broadcast.send(0)

        amplifiers.map {
            async {
                it.runProgram()
            }
        }.awaitAll()


        inputA.receive().also {
            inputA.cancel()
        }
    }

    private fun List<Int>.permutations(): Sequence<List<Int>> {
        if (isEmpty()) {
            return emptySequence()
        }

        if (size == 1) {
            return sequenceOf(listOf(first()))
        }

        return sequence {
            for ((idx, el) in withIndex()) {
                val middle = get(idx)
                val newList = subList(0, idx) + subList(idx + 1, size)
                for (permutation in newList.permutations()) {
                    yield(listOf(middle) + permutation)
                }
            }
        }
    }
}

fun main() {
    Solution().solveWithMeasurement()
}