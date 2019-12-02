package year2019.day2

import base.BaseSolution
import java.lang.StringBuilder

class Solution : BaseSolution<List<Int>, Int, Int>("Day 2") {
    class Processor(val instructions: MutableList<Int>) {
        var ip = 0
        fun runProgram(noun: Int, verb: Int): Int {
            instructions[1] = noun
            instructions[2] = verb

            var running = true
            while (running) {
                when (instructions[ip]) {
                    1 -> add()
                    2 -> multiply()
                    99 -> running = false
                    else -> throw IllegalArgumentException("Unexpected opcode")
                }
            }

            return instructions[0]
        }

        private fun add() {
            val a = instructions[this[1]]
            val b = instructions[this[2]]
            val dest = this[3]
            instructions[dest] = a + b
            ip += 4
        }

        private fun multiply() {
            val a = instructions[this[1]]
            val b = instructions[this[2]]
            val dest = this[3]
            instructions[dest] = a * b
            ip += 4
        }

        private operator fun get(offset: Int): Int {
            if (ip + offset >= instructions.size) {
                return -1
            }
            return instructions[ip + offset]
        }

        fun string(): String {
            val ret = StringBuilder()
            while(true) {
                if (ip >= instructions.size) {
                    break
                }

                val instruction = this[0]
                when(instruction) {
                    1 -> {
                        ret.append(String.format("%03d: ADD %03d + %03d -> %03d\n", ip, this[1], this[2], this[3]))
                        ip += 4
                    }
                    2 -> {
                        ret.append(String.format("%03d: MUL %03d * %03d -> %03d\n", ip, this[1], this[2], this[3]))
                        ip += 4
                    }
                    99 -> {
                        ret.append(String.format("%03d: HLT\n", ip))
                        ip++
                    }
                    else -> {
                        ret.append(String.format("%03d: $instruction\n", ip))
                        ip++
                    }
                }
            }

            ip = 0

            return ret.toString()
        }
    }

    override fun parseInput(): List<Int> = loadInput().split(",").map { it.toInt() }

    override fun calculateResult1(): Int {
        val processor = Processor(parseInput().toMutableList())
        return processor.runProgram(12, 2)
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val target = 19690720
        val noun = generateSequence(0, Int::inc).map {
            val processor = Processor(input.toMutableList())

            it to processor.runProgram(it, 0)
        }.first { it.second >= target }.first - 1

        val processor = Processor(input.toMutableList())
        val result = processor.runProgram(noun, 0)

        val verb = target - result

        return 100 * noun + verb;
    }
}

fun main() {
    Solution().solveWithMeasurement()
}