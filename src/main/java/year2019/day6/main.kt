package year2019.day6

import base.BaseSolution

class Solution : BaseSolution<Solution.Orbit, Int, Int>("Day 6") {
    data class Orbit(val name: String, val children: List<Orbit>, var parent: Orbit? = null) {
        fun totalOrbits(depth: Int = 0): Int =  depth + children.sumBy { it.totalOrbits(depth + 1) }
        val depth: Int
            get() = parent?.let {
                1 + it.depth
            } ?: 0
        override fun toString(): String {
            return "$name[${children.joinToString(", ") { it.toString() }}]"
        }

        override fun hashCode(): Int {
            return name.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return (other as? Orbit)?.name == name
        }
    }

    override fun parseInput(): Orbit {
        // map parent -> children
        val lines = loadInput().lines().map {
            val (parent, child) = it.split(")")
            parent to child
        }.groupBy { it.first }.mapValues { entry ->
            entry.value.map {
                it.second // map to only children
            }
        }

        return lines.getOrbit("COM")
    }

    private fun Orbit.setParent() {
        for (child in children) {
            child.parent = this
            child.setParent()
        }
    }

    private fun Map<String, List<String>>.getOrbit(name: String, map: MutableMap<String, Orbit> = mutableMapOf()): Orbit {
        if (map.containsKey(name)) {
            return map[name]!!
        }

        map[name] = Orbit(name, (this[name] ?: emptyList()).map {
            getOrbit(it, map)
        })

        return map[name]!!.also {
            it.setParent()
        }
    }

    override fun calculateResult1(): Int {
        val rootOrbit = parseInput()
        return rootOrbit.totalOrbits()
    }


    private fun Orbit.findOrbitWithName(name: String): Orbit? {

        if (this.name == name) {
            return this
        }

        return this.children.asSequence().mapNotNull {
            it.findOrbitWithName(name)
        }.firstOrNull()
    }

    private fun Pair<Orbit, Orbit>.findNearestCommonAncestor(): Orbit {
        val ancestorsA = first.getAncestors()
        val ancestorsB = second.getAncestors()
        val commonAncestors = ancestorsA.intersect(ancestorsB)

        return commonAncestors.maxBy { it.depth }!!
    }

    private fun Orbit.getAncestors(): Set<Orbit> {
        val parent = parent ?: return setOf();
        return setOf(parent) + parent.getAncestors()
    }

    override fun calculateResult2(): Int {
        val rootOrbit = parseInput()
        val YOU = rootOrbit.findOrbitWithName("YOU")!!
        val SANTA = rootOrbit.findOrbitWithName("SAN")!!
        val nearestAncestor = (YOU to SANTA).findNearestCommonAncestor()
        return (YOU.depth - nearestAncestor.depth) + (SANTA.depth - nearestAncestor.depth) - 2
    }
}

fun main() {
    Solution().solveWithMeasurement()
}