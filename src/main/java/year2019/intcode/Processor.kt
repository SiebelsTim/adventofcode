package year2019.intcode

class Program(val instructions: MutableList<Int>) {
    fun copy() = Program(instructions.toMutableList())


    fun string(): String {
        val ret = StringBuilder()
        var ip = 0
        fun pop() = instructions[ip++]

        while(true) {
            if (ip >= instructions.size) {
                break
            }

            when(val instruction = pop()) {
                1 -> {
                    ret.append(String.format("%03d: ADD %03d + %03d -> %03d\n", ip, pop(), pop(), pop()))
                }
                2 -> {
                    ret.append(String.format("%03d: MUL %03d * %03d -> %03d\n", ip, pop(), pop(), pop()))
                }
                99 -> {
                    ret.append(String.format("%03d: HLT\n", ip))
                }
                else -> {
                    ret.append(String.format("%03d: $instruction\n", ip))
                }
            }
        }

        ip = 0

        return ret.toString()
    }
}

class Processor(private val program: Program) {
    private val instructions: MutableList<Int>
        get() = program.instructions

    private val opCodeMap = mutableMapOf<Int, () -> Unit>(
            1 to ::add,
            2 to ::multiply,
            99 to ::halt
    )

    var ip = 0
    var running = false
    fun runProgram(noun: Int, verb: Int): Int {
        instructions[1] = noun
        instructions[2] = verb

        running = true
        while (running) {
            val opcode = instructions[ip++]
            val operation = opCodeMap[opcode] ?: throw IllegalArgumentException("Unexpected opcode: ip: $ip, code: $opcode")
            operation()
        }

        return instructions[0]
    }

    private fun pop(): Int {
        return instructions[ip++]
    }

    private fun add() {
        val a = instructions[pop()]
        val b = instructions[pop()]
        val dest = pop()
        instructions[dest] = a + b
    }

    private fun multiply() {
        val a = instructions[pop()]
        val b = instructions[pop()]
        val dest = pop()
        instructions[dest] = a * b
    }

    private fun halt() {
        running = false
    }
}
