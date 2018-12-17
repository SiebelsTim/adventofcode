package year2018.day16

import base.BaseSolution
import year2018.day16.Solution.Instruction
import year2018.day16.Solution.Sample

typealias InstructionRunner = IntArray.(Int, Int) -> Int
class Solution : BaseSolution<Pair<List<Sample>, List<Instruction>>, Int, Int>("Day 16") {
    data class Instruction(val opcode: Int, val A: Int, val B: Int, val C: Int) {
        companion object {
            fun of(str: String): Instruction {
                val items = str.split(" ").map { it.toInt() }
                return Instruction(items[0], items[1], items[2], items[3])
            }
        }
    }
    class Sample(val registersBefore: IntArray, val instruction: Instruction, val registersAfter: IntArray)

    private fun Boolean.toInt() = if (this) 1 else 0

    private val instructions = listOf<InstructionRunner>(
        { a, b -> this[a] + this[b] },              // addr
        { a, b -> this[a] + b },                    // addi
        { a, b -> this[a] * this[b] },              // mulr
        { a, b -> this[a] * b },                    // muli
        { a, b -> this[a] and this[b] },            // banr
        { a, b -> this[a] and b },                  // bani
        { a, b -> this[a] or this[b] },             // borr
        { a, b -> this[a] or b },                   // bori
        { a, _ -> this[a] },                        // setr
        { a, _ -> a },                              // seti
        { a, b -> (a > this[b]).toInt() },          // gtir
        { a, b -> (this[a] > b).toInt() },          // gtri
        { a, b -> (this[a] > this[b]).toInt() },    // gtrr
        { a, b -> (a == this[b]).toInt() },         // eqir
        { a, b -> (this[a] == b).toInt() },         // eqri
        { a, b -> (this[a] == this[b]).toInt() }    // eqrr
    )

    override fun parseInput(): Pair<List<Sample>, List<Instruction>> {
        val beforeRegex = Regex("""Before:\s+\[(\d+), (\d+), (\d+), (\d+)\]""")
        val afterRegex = Regex("""After:\s+\[(\d+), (\d+), (\d+), (\d+)\]""")
        val (samplesStr, instructionsStr) = loadInput().split("\n\n\n")
        val samples = samplesStr.splitToSequence("\n\n")
            .map {
                val (beforeStr, instrStr, afterStr) = it.lines()
                val before = beforeRegex.find(beforeStr)!!.destructured.toList().map { it.toInt() }.toIntArray()
                val after = afterRegex.find(afterStr)!!.destructured.toList().map { it.toInt() }.toIntArray()
                Sample(before, Instruction.of(instrStr), after)
            }.toList()

        return Pair(samples, instructionsStr
            .split("\n")
            .filter { it.isNotBlank() }
            .map { Instruction.of(it) }
        )
    }

    override fun calculateResult1(): Int {
        val (samples, _) = parseInput()

        var ret = 0
        for (sample in samples) {
            var matchingInstructions = 0
            for (instruction in instructions) {
                if (instruction.isValid(sample)) {
                    matchingInstructions++
                }

                if (matchingInstructions >= 3) {
                    ret++
                    break
                }
            }
        }

        return ret
    }

    override fun calculateResult2(): Int {
        val (samples, program) = parseInput()

        val possibleFunctions = Array(instructions.size) {
            mutableSetOf<InstructionRunner>(*instructions.toTypedArray())
        }
        for (sample in samples) {
            val instr = sample.instruction
            possibleFunctions[instr.opcode].removeIf { runner ->
                !runner.isValid(sample)
            }
        }

        // Remove identifiable instructions from other sets
        while (possibleFunctions.any { it.size > 1 }) {
            val (singlesSet, many) = possibleFunctions.partition { it.size == 1 }
            val singles = singlesSet.map { it.first() }
            for (single in singles) {
                many.forEach {
                    it.remove(single)
                }
            }
        }

        val functionMapping = possibleFunctions.map { it.first() }

        // Run program
        val registers = IntArray(4)
        for (instruction in program) {
            val runner = functionMapping[instruction.opcode]
            registers[instruction.C] = registers.runner(instruction.A, instruction.B)
        }


        return registers[0]
    }

    private fun InstructionRunner.isValid(sample: Sample): Boolean {
        val instr = sample.instruction
        val registers = sample.registersBefore.copyOf()
        registers[instr.C] = registers.this(instr.A, instr.B)

        return registers.contentEquals(sample.registersAfter)
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
