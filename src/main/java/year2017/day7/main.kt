package year2017.day7

import base.BaseSolution


class Solution : BaseSolution<Solution.Tower, String, Int>("Day 7") {
    data class TowerInput(val name: String, val weight: Int, val children: List<String>)
    data class Tower(val name: String, val weight: Int, val children: MutableList<Tower> = mutableListOf(), var parent: Tower? = null) {
        val totalWeight: Int
            get() = weight + children.sumBy { it.totalWeight }

        val isBalanced: Boolean
            get() = children.map { it.totalWeight }.toSet().size == 1

        val depth: Int by lazy {
            var ret = 0
            var current: Tower? = this
            while (current != null) {
                ++ret
                current = current.parent
            }

            ret
        }
    }

    override fun parseInput(): Tower = loadInput()
            .lineSequence()
            .filter { it.isNotEmpty() }
            .map {
                val (name, weightStr, childrenStr) = Regex("""([A-Za-z]+) \((\d+)\)(?: -> (.*))?""").find(it)!!.destructured
                val children = childrenStr.split(", ").filter { it.isNotEmpty() }
                TowerInput(name, weightStr.toInt(), children)
            }
            .toList()
            .toTower()

    override fun calculateResult1(): String {
        val input = parseInput()
        return input.name
    }

    override fun calculateResult2(): Int {
        val input = parseInput()

        val allUnbalancedTowers = findUnbalanced(input)
        val unbalancedDepth = allUnbalancedTowers.groupBy { it.depth }
        val maxDepth = unbalancedDepth.keys.max()!!
        val unbalancedTower = unbalancedDepth.getValue(maxDepth).first() // Deepest unbalanced tower
        val erroringTower = unbalancedTower.children.groupBy { it.totalWeight }.values.find { it.size == 1 }!!.first() // one of his children has a different weight
        val correctWeight = unbalancedTower.children.map { it.totalWeight }.find { it != erroringTower.totalWeight }!! // This is the weight the other children have

        val diff = erroringTower.totalWeight - correctWeight

        return erroringTower.weight - diff
    }

    private fun findUnbalanced(tower: Tower, list: MutableList<Tower> = mutableListOf()): List<Tower> {
        if (!tower.isBalanced) {
            list += tower
            tower.children.forEach {
                findUnbalanced(it, list)
            }
        }

        return list
    }

    private fun List<TowerInput>.toTower(): Tower {
        val towers = this.associate { it.name to Tower(it.name, it.weight) }.toMutableMap()
        for (towerInput in this) {
            for (childName in towerInput.children) {
                val parent = towers[towerInput.name]!!
                parent.children.add(towers[childName]!!.also { child ->
                    child.parent = parent
                })
            }
        }

        return towers.values.find { it.parent == null }!!
    }

}

fun main() {
    Solution().solveWithMeasurement()
}