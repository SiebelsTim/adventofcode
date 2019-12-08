package year2019.intcode

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

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
}

class Processor(private val program: Program, val input: ReceiveChannel<Int> = Channel<Int>(UNLIMITED), val output: SendChannel<Int> = Channel<Int>(UNLIMITED), val name: String = "Test") {
    private val memory: MutableList<Int> = program.instructions.toMutableList()

    private val opCodeMap = mutableMapOf<Int, suspend (ParameterModes) -> Unit>(
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
    suspend fun runProgram(): Int {
        running = true
        while (running) {
            val instr = Instruction(memory[ip++])
            val operation = opCodeMap[instr.opcode] ?: throw IllegalArgumentException("Unexpected opcode: ip: $ip, code: $instr")
            operation(instr.parameterModes)
        }

        output.close()

        return memory[0]
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

    private suspend fun add(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        require(modes.param3 == ParameterMode.ADDRESS)
        val dest = pop()
        memory[dest] = a + b
    }

    private suspend fun multiply(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        require(modes.param3 == ParameterMode.ADDRESS)
        val dest = pop()
        memory[dest] = a * b
    }

    private suspend fun movInput(modes: ParameterModes) {
        require(modes.param1 == ParameterMode.ADDRESS)
        val inp = input.receive()
        memory[pop()] = inp
    }

    private suspend fun movOutput(modes: ParameterModes) {
        val value = popValue(modes.param1)
        output.send(value)
    }

    private suspend fun jmpTrue(modes: ParameterModes) {
        val value = popValue(modes.param1)
        val dest = popValue(modes.param2)
        if (value != 0) {
            ip = dest
        }
    }

    private suspend fun jmpFalse(modes: ParameterModes) {
        val value = popValue(modes.param1)
        val dest = popValue(modes.param2)
        if (value == 0) {
            ip = dest
        }
    }

    private suspend fun lt(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        val dest = pop()

        memory[dest] = if (a < b) 1 else 0
    }

    private suspend fun eq(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        val dest = pop()

        memory[dest] = if (a == b) 1 else 0
    }

    private suspend fun halt(modes: ParameterModes) {
        running = false
    }
}
