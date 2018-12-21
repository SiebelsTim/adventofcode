package year2018.day21

import base.BaseSolution
import year2018.day19.InstructionRunner
import year2018.day19.Solution.Instruction

class Solution : BaseSolution<Pair<List<Instruction>, Int>, Int, Int>("Day 19") {

    private fun Boolean.toInt() = if (this) 1 else 0

    private val instructionRunners = mapOf<String, InstructionRunner>(
        "addr" to { a, b -> this[a] + this[b] },
        "addi" to { a, b -> this[a] + b },                    // addi
        "mulr" to { a, b -> this[a] * this[b] },              // mulr
        "muli" to { a, b -> this[a] * b },                    // muli
        "banr" to { a, b -> this[a] and this[b] },            // banr
        "bani" to { a, b -> this[a] and b },                  // bani
        "borr" to { a, b -> this[a] or this[b] },             // borr
        "bori" to { a, b -> this[a] or b },                   // bori
        "setr" to { a, _ -> this[a] },                        // setr
        "seti" to { a, _ -> a },                              // seti
        "gtir" to { a, b -> (a > this[b]).toInt() },          // gtir
        "gtri" to { a, b -> (this[a] > b).toInt() },          // gtri
        "gtrr" to { a, b -> (this[a] > this[b]).toInt() },    // gtrr
        "eqir" to { a, b -> (a == this[b]).toInt() },         // eqir
        "eqri" to { a, b -> (this[a] == b).toInt() },         // eqri
        "eqrr" to { a, b -> (this[a] == this[b]).toInt() }    // eqrr
    )

    override fun parseInput(): Pair<List<Instruction>, Int> {
        val lines = loadInput().lineSequence().filter { it.isNotBlank() }
        val ip = lines.first().drop(4).toInt()
        val instructions = lines.drop(1).map(Instruction.Companion::of).toList()

        return instructions to ip
    }

    override fun calculateResult1(): Int {
        val (instructions, ip) = parseInput()
        val registers = intArrayOf(0, 0, 0, 0, 0, 0)

        while (registers[ip] < instructions.size) {
            val ipVal = registers[ip]
            val instruction = instructions[ipVal]
            val opcode = instruction.opcode
            val runner = instructionRunners[opcode]!!
            registers[instruction.C] = registers.runner(instruction.A, instruction.B)
            registers[ip] += 1
            if (ipVal == 28) { // The only instruction that accesses reg0
                return registers[3] // It checks for equality of reg 3
            }
        }

        throw IllegalStateException("No solution found?")
    }

    override fun calculateResult2(): Int {
        val (instructions, ip) = parseInput()
        val registers = intArrayOf(0, 0, 0, 0, 0, 0)

        val reg3Values = mutableSetOf<Int>()
        while (registers[ip] < instructions.size) {
            val ipVal = registers[ip]
            val instruction = instructions[ipVal]
            val opcode = instruction.opcode
            val runner = instructionRunners[opcode]!!
            registers[instruction.C] = registers.runner(instruction.A, instruction.B)
            registers[ip] += 1


            if (ipVal == 28) { // The only instruction that accesses reg0
                if (registers[3] in reg3Values) { // Found cycle. Use last non-repeating value
                    return reg3Values.last()
                }
                reg3Values += registers[3]
            }
        }

        throw IllegalStateException("No solution found?")
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
