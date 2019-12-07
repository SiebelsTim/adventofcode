package year2019.intcode

import java.util.*

fun ParameterMode(mode: Int) = if (mode == 1) ParameterMode.IMMEDIATE else ParameterMode.ADDRESS
enum class ParameterMode {
    ADDRESS, IMMEDIATE
}
fun ParameterModes(instruction: Int): ParameterModes {
    val opcode = (instruction % 100);
    val mode1 = (instruction / 100) % 10
    assert(mode1 <= 1, { "Bad instruction $instruction" })
    val mode2 = (instruction / 1000) % 10
    assert(mode2 <= 1, { "Bad instruction $instruction" })
    val mode3 = (instruction / 10000) % 10
    assert(mode3 <= 1, { "Bad instruction $instruction" })
    return ParameterModes(ParameterMode(mode1), ParameterMode(mode2), ParameterMode(mode3))
}
data class ParameterModes(val param1: ParameterMode, val param2: ParameterMode, val param3: ParameterMode)

class Instruction(val opcode: Int, val parameterModes: ParameterModes) {
    constructor(instruction: Int): this(instruction % 100, ParameterModes(instruction))
}

class Program(val instructions: List<Int>) {
    constructor(instructions: String) : this(instructions.split(",").map { it.toInt() })

    fun withNoun(instruction: Int): Program = Program(instructions.toMutableList().also { it[1] = instruction })
    fun withVerb(instruction: Int): Program = Program(instructions.toMutableList().also { it[2] = instruction })

    fun string(): String {
        val ret = StringBuilder()
        var ip = 0
        fun pop() = instructions[ip++]


        fun formatParameter(mode: ParameterMode, value: Int): String {
            return when (mode) {
                ParameterMode.ADDRESS -> String.format("&%03d", value)
                ParameterMode.IMMEDIATE -> String.format("%03d", value)
            }
        }

        fun stringAdd(instruction: Instruction) {
            val modes = instruction.parameterModes
            ret.append(String.format("%03d: ADD %s + %s -> %s\n", ip, formatParameter(modes.param1, pop()), formatParameter(modes.param1, pop()), formatParameter(modes.param1, pop())))
        }

        fun stringMultiply(instruction: Instruction) {
            val modes = instruction.parameterModes
            ret.append(String.format("%03d: MUL %s * %s -> %s\n", ip, formatParameter(modes.param1, pop()), formatParameter(modes.param1, pop()), formatParameter(modes.param1, pop())))
        }

        fun stringInput(instruction: Instruction) {
            ret.append(String.format("%03d: IPT -> %s\n", ip, formatParameter(instruction.parameterModes.param1, pop())))
        }

        fun stringOutput(instruction: Instruction) {
            ret.append(String.format("%03d: OPT -> %s\n", ip, formatParameter(instruction.parameterModes.param1, pop())))
        }

        fun stringHalt(instruction: Instruction) {
            ret.append(String.format("%03d: HLT\n", ip))
        }

        val stringifyMap = mutableMapOf<Int, (Instruction) -> Unit>(
                1 to ::stringAdd,
                2 to ::stringMultiply,
                3 to ::stringInput,
                4 to ::stringOutput,
                99 to ::stringHalt
        )

        while(true) {
            if (ip >= instructions.size) {
                break
            }
            val instruction = Instruction(pop())
            val stringify = stringifyMap[instruction.opcode]
            if (null == stringify) {
                ret.append(String.format("%03d: UNKNOWN${instruction.opcode}\n", ip))
            } else {
                stringify(instruction)
            }
        }

        ip = 0

        return ret.toString()
    }
}

class Processor(private val program: Program) {
    private val memory: MutableList<Int> = program.instructions.toMutableList()

    private val input: Deque<Int> = ArrayDeque()
    val output = mutableListOf<Int>()

    private val opCodeMap = mutableMapOf<Int, (ParameterModes) -> Unit>(
            1 to ::add,
            2 to ::multiply,
            3 to ::movInput,
            4 to ::movOutput,
            5 to ::jmpTrue,
            6 to ::jmpFalse,
            7 to ::lt,
            8 to ::eq,
            99 to ::halt
    )

    var ip = 0
    var running = false
    fun runProgram(): Int {
        running = true
        while (running) {
            val instr = Instruction(memory[ip++])
            val operation = opCodeMap[instr.opcode] ?: throw IllegalArgumentException("Unexpected opcode: ip: $ip, code: $instr")
            operation(instr.parameterModes)
        }

        return memory[0]
    }

    fun input(vararg values: Int) {
        for (value in values) {
            input.push(value)
        }
    }

    private fun pop(): Int {
        return memory[ip++]
    }

    private fun popValue(mode: ParameterMode): Int {
        val value = pop()

        return when (mode) {
            ParameterMode.ADDRESS -> memory[value]
            ParameterMode.IMMEDIATE -> value
        }
    }

    private fun add(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        require(modes.param3 == ParameterMode.ADDRESS)
        val dest = pop()
        memory[dest] = a + b
    }

    private fun multiply(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        require(modes.param3 == ParameterMode.ADDRESS)
        val dest = pop()
        memory[dest] = a * b
    }

    private fun movInput(modes: ParameterModes) {
        require(modes.param1 == ParameterMode.ADDRESS)
        memory[pop()] = input.pop()
    }

    private fun movOutput(modes: ParameterModes) {
        output += popValue(modes.param1)
    }

    private fun jmpTrue(modes: ParameterModes) {
        val value = popValue(modes.param1)
        val dest = popValue(modes.param2)
        if (value != 0) {
            ip = dest
        }
    }

    private fun jmpFalse(modes: ParameterModes) {
        val value = popValue(modes.param1)
        val dest = popValue(modes.param2)
        if (value == 0) {
            ip = dest
        }
    }

    private fun lt(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        val dest = pop()

        memory[dest] = if (a < b) 1 else 0
    }

    private fun eq(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        val dest = pop()

        memory[dest] = if (a == b) 1 else 0
    }

    private fun halt(modes: ParameterModes) {
        running = false
    }
}
