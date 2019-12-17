package year2019.intcode

typealias MemoryCell = Long
class Instruction(val opcode: MemoryCell, val parameterModes: ParameterModes) {
    constructor(instruction: MemoryCell): this(instruction % 100, ParameterModes(instruction))
}

class Program(val instructions: List<MemoryCell>) {
    constructor(instructions: String) : this(instructions.split(",").map { it.trim().toLong() })

    fun withNoun(instruction: MemoryCell): Program = withAddressChanged(1, instruction)
    fun withVerb(instruction: MemoryCell): Program = withAddressChanged(2, instruction)
    fun withAddressChanged(pos: Int, instruction: MemoryCell): Program = Program(instructions.toMutableList().also { it[pos] = instruction })
}
