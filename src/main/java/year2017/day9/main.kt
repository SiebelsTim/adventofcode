package year2017.day9

import base.BaseSolution
import year2017.day9.Solution.Element.Garbage
import year2017.day9.Solution.Element.Group

class Solution : BaseSolution<Group, Int, Int>("Day 9") {
    sealed class Element() {
        data class Group(
                val children: List<Element>
        ) : Element() {
            val garbageCount: Int
                get() = children.mapNotNull { it as? Group }.sumBy { it.garbageCount } + children.mapNotNull { it as? Garbage }.sumBy { it.content.length }
        }
        data class Garbage(val content: String) : Element()
    }

    val input = loadInput()
    var pos = 0
    override fun parseInput(): Group {
        val input = loadInput()

        assert(input[pos] == '{')
        return parseGroup()
    }

    private fun parseGroup(): Group {
        assert(input[pos] == '{')
        pos++
        val children = mutableListOf<Element>()
        while (input[pos] != '}') {
            when (input[pos]) {
                '{' -> children += parseGroup()
                '<' -> children += parseGarbage()
                ',' -> pos++
                else -> throw IllegalStateException("This should never happen")
            }
        }

        ++pos
        return Group(children)
    }

    private fun parseGarbage(): Garbage {
        assert(input[pos] == '<')
        pos++
        var content = ""
        while (input[pos] != '>') {
            if (input[pos] == '!') {
                pos += 2
                continue
            }
            content += input[pos++]
        }

        ++pos

        return Garbage(content)
    }

    override fun calculateResult1(): Int {
        val input = parseInput()
        pos = 0
        return calculateScore(input)
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        pos = 0
        return input.garbageCount
    }

    private fun calculateScore(input: Group, depth: Int = 1): Int {
        return depth + input.children.mapNotNull { it as? Group }.sumBy { calculateScore(it, depth + 1) }
    }
}

fun main() {
    Solution().solveWithMeasurement()
}