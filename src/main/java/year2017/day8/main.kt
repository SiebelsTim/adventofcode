package year2017.day8

import base.BaseSolution
import kotlin.math.max

class Solution : BaseSolution<List<Solution.Instruction>, Int, Int>("Day 8") {
    data class Instruction(val register: String, val operation: Operation, val value: Int, val condition: Condition)
    sealed class Operation {
        companion object {
            fun fromString(str: String) = when(str) {
                "inc" -> Inc
                "dec" -> Dec
                else -> throw IllegalArgumentException("Did not expect $str")
            }
        }
        abstract operator fun invoke(lhs: Int, rhs: Int): Int
        object Inc : Operation() {
            override fun invoke(lhs: Int, rhs: Int): Int  = lhs + rhs
        }

        object Dec : Operation() {
            override fun invoke(lhs: Int, rhs: Int): Int  = lhs - rhs
        }
    }

    class Condition(val register: String, val value: Int, val type: ConditionType) {
        operator fun invoke(registerValue: (String) -> Int) = type(registerValue(register), value)
    }

    sealed class ConditionType {
        companion object {
            fun fromString(str: String) = when(str) {
                "==" -> Eq
                "!=" -> Neq
                "<=" -> Leq
                ">=" -> Geq
                "<" -> LessThan
                ">" -> GreaterThan
                else -> throw IllegalArgumentException("Did not expect $str")
            }
        }
        abstract operator fun invoke(lhs: Int, rhs: Int): Boolean

        object Eq : ConditionType() {
            override fun invoke(lhs: Int, rhs: Int) = lhs == rhs
        }

        object Neq : ConditionType() {
            override fun invoke(lhs: Int, rhs: Int): Boolean  = lhs != rhs
        }

        object LessThan : ConditionType() {
            override fun invoke(lhs: Int, rhs: Int): Boolean = lhs < rhs
        }

        object GreaterThan : ConditionType() {
            override fun invoke(lhs: Int, rhs: Int): Boolean  = lhs > rhs
        }

        object Leq : ConditionType() {
            override fun invoke(lhs: Int, rhs: Int): Boolean  = lhs <= rhs
        }

        object Geq : ConditionType() {
            override fun invoke(lhs: Int, rhs: Int): Boolean  = lhs >= rhs
        }
    }

    override fun parseInput(): List<Instruction> = loadInput()
            .lineSequence()
            .filter { it.isNotBlank() }
            .map {
                val destructured = Regex("""([A-Za-z]+) (inc|dec) ([0-9\-]+) if ([a-zA-Z]+) ([!<=>]{1,2}) ([0-9\-]+)""").find(it)!!.destructured
                val (register, operation, value, conditionRegister, conditionType, conditionValue) = destructured

                Instruction(register, Operation.fromString(operation), value.toInt(), Condition(conditionRegister, conditionValue.toInt(), ConditionType.fromString(conditionType)))
            }
            .toList()

    override fun calculateResult1(): Int {
        return getResult().first
    }

    override fun calculateResult2(): Int {
        return getResult().second
    }

    private fun getResult(): Pair<Int, Int> {
        val input = parseInput()
        val registers = mutableMapOf<String, Int>()

        var max = 0
        for (instruction in input) {
            if (instruction.condition { registers.getRegisterValue(it) }) {
                registers[instruction.register] = instruction.operation(registers.getRegisterValue(instruction.register), instruction.value).also {
                    max = max(max, it)
                }
            }
        }
        return registers.values.max()!! to max
    }

    private fun MutableMap<String, Int>.getRegisterValue(register: String): Int = this.getOrPut(register) { 0 }
}

fun main() {
    Solution().solveWithMeasurement()
}