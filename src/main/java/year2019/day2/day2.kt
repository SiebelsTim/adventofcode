package year2019.day2

import base.BaseSolution
import year2019.intcode.Processor
import year2019.intcode.Program


class Solution : BaseSolution<Program, Int, Int>("Day 2") {
    override fun parseInput() = Program(loadInput().split(",").map { it.toInt() }.toMutableList())

    override fun calculateResult1(): Int {
        val processor = Processor(parseInput().withNoun(12).withVerb(2))
        return processor.runProgram()
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val target = 19690720
        val noun = generateSequence(0, Int::inc).map {
            val processor = Processor(input.withNoun(it).withVerb(0))

            it to processor.runProgram()
        }.first { it.second >= target }.first - 1

        val processor = Processor(input.withNoun(noun))
        val result = processor.runProgram()

        val verb = target - result

        return 100 * noun + verb;
    }
}

fun main() {
    Solution().solveWithMeasurement()
}