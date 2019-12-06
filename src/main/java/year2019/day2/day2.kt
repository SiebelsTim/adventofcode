package year2019.day2

import base.BaseSolution
import year2019.intcode.Processor
import year2019.intcode.Program


class Solution : BaseSolution<Program, Int, Int>("Day 2") {
    override fun parseInput() = Program(loadInput().split(",").map { it.toInt() }.toMutableList())

    override fun calculateResult1(): Int {
        val processor = Processor(parseInput())
        return processor.runProgram(12, 2)
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val target = 19690720
        val noun = generateSequence(0, Int::inc).map {
            val processor = Processor(input.copy())

            it to processor.runProgram(it, 0)
        }.first { it.second >= target }.first - 1

        val processor = Processor(input)
        val result = processor.runProgram(noun, 0)

        val verb = target - result

        return 100 * noun + verb;
    }
}

fun main() {
    Solution().solveWithMeasurement()
}