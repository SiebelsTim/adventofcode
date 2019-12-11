package year2019.intcode

fun ParameterMode(mode: MemoryCell) = when (mode) {
    0L -> ParameterMode.ADDRESS
    1L -> ParameterMode.IMMEDIATE
    2L -> ParameterMode.RELATIVE
    else -> throw IllegalArgumentException("Invalid Parameter mode $mode")
}
enum class ParameterMode {
    ADDRESS, IMMEDIATE, RELATIVE
}
fun ParameterModes(instruction: MemoryCell): ParameterModes {
    val opcode = (instruction % 100);
    val mode1 = (instruction / 100) % 10
    require(mode1 <= 2, { "Bad instruction $instruction" })
    val mode2 = (instruction / 1000) % 10
    require(mode2 <= 2, { "Bad instruction $instruction" })
    val mode3 = (instruction / 10000) % 10
    require(mode3 <= 2, { "Bad instruction $instruction" })
    return ParameterModes(ParameterMode(mode1), ParameterMode(mode2), ParameterMode(mode3))
}
data class ParameterModes(val param1: ParameterMode, val param2: ParameterMode, val param3: ParameterMode)
