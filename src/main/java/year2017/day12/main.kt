package year2017.day12

import base.BaseSolution

class Solution : BaseSolution<List<Solution.Pipe>, Int, Int>("Day 12") {
    data class Pipe(val idStart: Int, val idDestination: Int)

    override fun parseInput(): List<Pipe> =
        loadInput().splitToSequence("\n").flatMap {
            val (start, destinations) = it.split(" <-> ")
            destinations.splitToSequence(", ").map { dest ->
                Pipe(start.toInt(), dest.toInt())
            }
        }.toList()

    class Node(val id: Int, val vertices: MutableSet<Node> = mutableSetOf()) {
        override fun equals(other: Any?) = id == (other as? Node)?.id
        override fun hashCode(): Int = id.hashCode()

        fun addVertex(node: Node) {
            vertices += node
            if (this !in node.vertices) {
                node.addVertex(this)
            }
        }
    }

    /**
     * Maps id to Node containing the entire graph for that node
     */
    private fun inputToGraph(input: List<Pipe>): Map<Int, Node> {
        val nodes = mutableMapOf<Int, Node>()
        for (pipe in input) {
            val startNode = nodes.computeIfAbsent(pipe.idStart) {
                Node(it)
            }

            val endNode = nodes.computeIfAbsent(pipe.idDestination) {
                Node(it)
            }

            startNode.addVertex(endNode)
        }

        return nodes
    }

    override fun calculateResult1(): Int {
        val input = parseInput()
        val graph = inputToGraph(input).getValue(0)
        return graph.findReachableNodes().size
    }

    override fun calculateResult2(): Int {
        val input = parseInput()
        val maxId = input.maxBy { it.idStart }!!.idStart

        val groups = mutableListOf<Set<Node>>()
        val visitedNodes = mutableSetOf<Int>()
        val graphRaw = inputToGraph(input)
        for (i in 0..maxId) {
            if (i !in visitedNodes) {
                val graph = graphRaw.getValue(i)
                val neighborhood = graph.findReachableNodes()
                neighborhood.forEach { visitedNodes += it.id }
                groups += neighborhood
            }
        }

        return groups.size
    }

    private fun Node.findReachableNodes(visited: MutableSet<Node> = mutableSetOf()): Set<Node> {
        visited += this

        for (vertex in this.vertices) {
            if (vertex !in visited) {
                visited += vertex
                vertex.findReachableNodes(visited)
            }
        }

        return visited
    }

}

fun main() {
    Solution().solveWithMeasurement()
}