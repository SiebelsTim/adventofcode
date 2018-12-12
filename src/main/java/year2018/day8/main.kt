package year2018.day8

import base.BaseSolution

class Solution : BaseSolution<List<Int>, Int, Int>("Day 8") {
    class Node(val children: Array<Node>, val metadata: IntArray) {
        fun sumMetadata(): Int {
            return metadata.sum() + children.sumBy { it.sumMetadata() }
        }

        fun getValue(): Int = if (children.isEmpty()) {
            metadata.sum()
        } else {
            metadata.sumBy { idx ->
                if (idx-1 < children.size) {
                    children[idx-1].getValue()
                } else {
                    0
                }
            }
        }

        companion object {
            fun parse(iterator: Iterator<Int>): Node {
                val childCount = iterator.next()
                val metadataCount = iterator.next()
                val children = Array(childCount) {
                    Node.parse(iterator)
                }

                val metadata = IntArray(metadataCount) {
                    iterator.next()
                }

                return Node(children, metadata)
            }
        }
    }

    override fun parseInput(): List<Int> = loadInput().split(" ").map { it.trim().toInt() }

    override fun calculateResult1(): Int {
        val input = parseInput()
        val root = Node.parse(input.iterator())

        return root.sumMetadata()
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val root = Node.parse(input.iterator())

        return root.getValue()
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
