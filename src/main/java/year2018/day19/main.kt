package year2018.day19

import base.BaseSolution
import year2018.day19.Solution.Instruction

typealias InstructionRunner = IntArray.(Int, Int) -> Int
class Solution : BaseSolution<Pair<List<Instruction>, Int>, Int, Int>("Day 19") {
    data class Instruction(val opcode: String, val A: Int, val B: Int, val C: Int) {
        override fun toString(): String = "$opcode $A $B $C"

        companion object {
            fun of(str: String): Instruction {
                val items = str.split(" ")
                val opcode = items.first()
                val data = items.drop(1).map { it.toInt() }
                return Instruction(opcode, data[0], data[1], data[2])
            }
        }
    }

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
        val registers = IntArray(6)

        while (registers[ip] < instructions.size) {
            val instruction = instructions[registers[ip]]
            val opcode = instruction.opcode
            val runner = instructionRunners[opcode]!!
            registers[instruction.C] = registers.runner(instruction.A, instruction.B)
            registers[ip] += 1
        }

        return registers[0]
    }

    override fun calculateResult2(): Int {
        //        3:
//        if (B*F == 10551347) {
//            A += B
//        }
//
//        F++
//        if (F > 10551347) {
//            12:
//            if (++B > 10551347) {
//                IP = IP^2
//            } else {
//                F = 1
//                jmp 3
//            }
//        }


        val n = 10551347
        val result = (1..n).filter { n % it == 0 }.sum()

        return result//10695960 // All divisors of 10551347
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
