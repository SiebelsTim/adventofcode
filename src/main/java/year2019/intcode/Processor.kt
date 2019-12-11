package year2019.intcode

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class Processor(private val program: Program, val input: ReceiveChannel<Long> = Channel(UNLIMITED), val output: SendChannel<MemoryCell> = Channel(UNLIMITED)) {
    private val memory = program.instructions.toMemory()

    private val opCodeMap = mutableMapOf<MemoryCell, suspend (ParameterModes) -> Unit>(
            1L to ::add,
            2L to ::multiply,
            3L to ::movInput,
            4L to ::movOutput,
            5L to ::jmpTrue,
            6L to ::jmpFalse,
            7L to ::lt,
            8L to ::eq,
            9L to ::adjustRelativeBase,
            99L to ::halt
    )

    var ip = 0L
    var running = false
    var relativeBase = 0L
    suspend fun runProgram(): MemoryCell {
        running = true
        while (running) {
            val instr = Instruction(memory[ip++])
            val operation = opCodeMap[instr.opcode] ?: throw IllegalArgumentException("Unexpected opcode at ip: $ip, code: ${instr.opcode}")
            operation(instr.parameterModes)
        }

        output.close()

        return memory[0]
    }

    private fun pop() = memory[ip++]

    private fun popValue(mode: ParameterMode): MemoryCell {
        val value = pop()

        return when (mode) {
            ParameterMode.ADDRESS -> memory[value]
            ParameterMode.IMMEDIATE -> value
            ParameterMode.RELATIVE -> memory[relativeBase + value]
        }
    }

    private fun setMemory(value: MemoryCell, mode: ParameterMode) = when (mode) {
        ParameterMode.ADDRESS -> {
            val destination = pop()
            memory[destination] = value

            destination
        }
        ParameterMode.RELATIVE -> {
            val destination = relativeBase + pop()
            memory[destination] = value

            destination
        }
        ParameterMode.IMMEDIATE -> throw IllegalArgumentException("Cannot use IMMEDIATE as destination")
    }

    private suspend fun add(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        val result = a + b
        val destAddr = setMemory(result, modes.param3)
        debug("$destAddr <- $a + $b")
    }

    private suspend fun multiply(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        val result = a * b
        val destAddr = setMemory(result, modes.param3)
        debug("$destAddr <- $a * $b")
    }

    private suspend fun movInput(modes: ParameterModes) {
        val inp = input.receive()
        val destAddr = setMemory(inp, modes.param1)
        debug("IN $inp -> $destAddr")
    }

    private suspend fun movOutput(modes: ParameterModes) {
        val value = popValue(modes.param1)
        output.send(value)
        debug("OUT $value")
    }

    private suspend fun jmpTrue(modes: ParameterModes) {
        val value = popValue(modes.param1)
        val dest = popValue(modes.param2)
        if (value != 0L) {
            ip = dest
        }
        debug("JNZ $value ==> $dest")
    }

    private suspend fun jmpFalse(modes: ParameterModes) {
        val value = popValue(modes.param1)
        val dest = popValue(modes.param2)
        if (value == 0L) {
            ip = dest
        }
        debug("JMPZ $value ==> $dest")
    }

    private suspend fun lt(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        val result = if (a < b) 1L else 0L
        val destAddr = setMemory(result, modes.param3)

        debug("$destAddr <- $a < $b")
    }

    private suspend fun eq(modes: ParameterModes) {
        val a = popValue(modes.param1)
        val b = popValue(modes.param2)
        val result = if (a == b) 1L else 0L
        val destAddr = setMemory(result, modes.param3)

        debug("$destAddr <- $a == $b")
    }

    private suspend fun adjustRelativeBase(modes: ParameterModes) {
        val a = popValue(modes.param1)
        relativeBase += a
        debug("BASE += $a == $relativeBase")
    }

    private suspend fun halt(modes: ParameterModes) {
        debug("HLT")
        running = false
    }

    private fun debug(msg: String) {
        if (false) {
            println(msg)
        }
    }
}
