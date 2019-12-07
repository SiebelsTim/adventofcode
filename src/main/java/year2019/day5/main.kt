package year2019.day5

import base.BaseSolution
import year2019.intcode.Processor
import year2019.intcode.Program

class Solution : BaseSolution<Program, Int, Int>("Day 5") {
    override fun parseInput(): Program = Program(loadInput())

    override fun calculateResult1(): Int {
        val program = parseInput()
        val processor = Processor(program)
        processor.input(1)
        processor.runProgram()
        return processor.output.last()
    }

    override fun calculateResult2(): Int {
        val program = parseInput()
        val processor = Processor(program)
        processor.input(5)
        processor.runProgram()
        return processor.output.last()
    }
}

fun main() {
    Solution().solveWithMeasurement()
}