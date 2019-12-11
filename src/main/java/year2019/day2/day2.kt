package year2019.day2

import base.BaseSolution
import kotlinx.coroutines.runBlocking
import year2019.intcode.Processor
import year2019.intcode.Program


class Solution : BaseSolution<Program, Long, Long>("Day 2") {
    override fun parseInput() = Program(loadInput().split(",").map { it.toLong() }.toMutableList())

    override fun calculateResult1() = runBlocking {
        val processor = Processor(parseInput().withNoun(12).withVerb(2))

        processor.runProgram()
    }

    override fun calculateResult2() = runBlocking {
        val input = parseInput()
        val target = 19690720
        val noun = generateSequence(0, Int::inc).map {
            val processor = Processor(input.withNoun(it.toLong()).withVerb(0))

            runBlocking {
                it to processor.runProgram()
            }
        }.first { it.second >= target }.first - 1

        val processor = Processor(input.withNoun(noun.toLong()))
        val result = processor.runProgram()

        val verb = target - result

        100 * noun + verb;
    }
}

fun main() {
    Solution().solveWithMeasurement()
}