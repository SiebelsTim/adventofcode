package year2019.day9

import base.BaseSolution
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import year2019.intcode.Processor
import year2019.intcode.Program

class Solution : BaseSolution<Program, Long, Long>("Day 9") {
    override fun parseInput(): Program  = Program(loadInput())

    override fun calculateResult1() = runBlocking {
        val output = Channel<Long>(UNLIMITED)
        val input = Channel<Long>(UNLIMITED)
        input.send(1L)
        val processor = Processor(parseInput(), input = input, output = output)
        processor.runProgram()

        val toList = output.toList()
        toList.first()
    }

    override fun calculateResult2() = runBlocking {
        val output = Channel<Long>(UNLIMITED)
        val input = Channel<Long>(UNLIMITED)
        input.send(2L)
        val processor = Processor(parseInput(), input = input, output = output)
        processor.runProgram()

        val toList = output.toList()
        toList.first()
    }
}

fun main() {
    Solution().solveWithMeasurement()
}