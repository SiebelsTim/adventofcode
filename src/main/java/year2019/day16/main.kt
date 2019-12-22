package year2019.day16

import base.BaseSolution
import kotlin.math.abs

class Solution : BaseSolution<List<Byte>, Int, Int>("Day 16") {
    override fun parseInput(): List<Byte> = loadInput().toCharArray().map { it.toString().toByte() }

    override fun calculateResult1(): Int {
        return runFft(parseInput(), 100).take(8).joinToString("").toInt()
    }

    override fun calculateResult2(): Int {
        val parsedInput = parseInput()
        val offset = parsedInput.take(7).joinToString("").toInt()
        val outputSize = 10000 * parsedInput.size - offset // only the part we care about
        val output = List(outputSize) { parsedInput[(offset+it) % parsedInput.size] }.toMutableList() // We replace this in place
        repeat(100) { // 100 phases
            for (i in (0 until outputSize-1).reversed()) {
                output[i] = ((output[i] + output[i + 1]) % 10).toByte()
            }
        }


        return output.take(8).joinToString("").toInt()
    }

    private fun runFft(input: List<Byte>, phaseCount: Int): List<Byte> {
        var output = input
        for (phase in 1..phaseCount) {
            output = runFft(output)
        }

        return output
    }

    private fun runFft(input: List<Byte>): List<Byte> {
        val output = mutableListOf<Byte>()
        for (position in 1..input.size) {
            val result = input
                    .drop(position - 1) // The first n factors are 0
                    .chunked(position) // these are 1, 0, -1, 0, ...
                    .mapIndexed { idx, it ->
                        when (idx % 4) {
                            0 -> it.sum() // every 4th chunk is 1
                            2 -> -it.sum() // every 2nd chunk is -1
                            else -> 0 // 1st and 3rd chunk is 0
                        }
                    }.sum()
            output += (abs(result) % 10).toByte()
        }
        return output
    }
}

fun main() {
    Solution().solveWithMeasurement()
}