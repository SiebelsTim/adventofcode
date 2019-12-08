package year2019.day5

import base.BaseSolution
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.first
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import year2019.intcode.Processor
import year2019.intcode.Program

class Solution : BaseSolution<Program, Int, Int>("Day 5") {
    override fun parseInput(): Program = Program(loadInput())

    override fun calculateResult1(): Int = runBlocking {
        val program = parseInput()
        val input = Channel<Int>(UNLIMITED)
        input.send(1)
        val output = Channel<Int>(UNLIMITED)
        val processor = Processor(program, input, output)
        processor.runProgram()

        output.toList().last()
    }

    override fun calculateResult2(): Int  = runBlocking {
        val program = parseInput()

        val input = Channel<Int>(UNLIMITED)
        input.send(5)
        val output = Channel<Int>(UNLIMITED)
        val processor = Processor(program, input, output)
        processor.runProgram()

        output.toList().last()
    }
}

fun main() {
    Solution().solveWithMeasurement()
}