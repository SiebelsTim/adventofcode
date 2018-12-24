package year2018.day24

import base.BaseSolution
import year2018.day24.Solution.Army
import java.util.*
import kotlin.math.min

class Solution : BaseSolution<Pair<Army, Army>, Int, Int>("Day 24") {
    class Army(val name: String, val groups: MutableList<Group> = mutableListOf()) {
        override fun toString(): String = name
    }
    data class Group(
                val number: Int,
                val army: Army,
                var units: Int,
                val hitPoints: Int,
                val attackPower: Int,
                val attackType: AttackType,
                val initiative: Int,
                val immunities: List<AttackType> = listOf(),
                val weaknesses: List<AttackType> = listOf()) {

        override fun toString(): String = "$army $number"
        override fun hashCode(): Int = Objects.hash(army, number)

        override fun equals(other: Any?): Boolean = other is Group && other.army == army && other.number == number

        fun damageTo(enemy: Group): Int {
            if (attackType in enemy.immunities) {
                return 0
            }

            val attackPower = if (attackType in enemy.weaknesses) attackPower * 2 else attackPower

            return units * attackPower
        }

        val effectivePower: Int get() = units * attackPower

        companion object {
            fun of(line: String, number: Int, army: Army): Group {
                val regex = Regex("""(\d+) units each with (\d+) hit points(?: \((.+)\))? with an attack that does (\d+) (\w+) damage at initiative (\d+)""")
                val groups = regex.find(line)!!.groups.drop(1)
                val units = groups[0]!!.value.toInt()
                val hitPoints = groups[1]!!.value.toInt()
                val weaknessesStr = groups[2]?.value
                val attPower = groups[3]!!.value.toInt()
                val attType = groups[4]!!.value
                val initiative = groups[5]!!.value.toInt()

                val immunities = mutableListOf<AttackType>()
                val weaknesses = mutableListOf<AttackType>()
                if (weaknessesStr != null) {
                    val types = weaknessesStr.split("; ")
                    for (type in types) {
                        if (type.startsWith("immune")) {
                            immunities += type.substringAfter("immune to ").split(", ").map { AttackType.of(it) }
                        } else {
                            weaknesses += type.substringAfter("weak to ").split(", ").map { AttackType.of(it) }
                        }
                    }
                }

                return Group(number, army, units, hitPoints, attPower, AttackType.of(attType), initiative, immunities, weaknesses)
            }
        }
    }

    enum class AttackType {
        SLASHING, BLUDGEONING, RADIATION, FIRE, COLD;
        companion object {
            fun of(str: String) = when(str) {
                "slashing" -> SLASHING
                "bludgeoning" -> BLUDGEONING
                "radiation" -> RADIATION
                "fire" -> FIRE
                "cold" -> COLD
                else -> throw IllegalArgumentException("Unknown attack type $str")
            }
        }
    }

    override fun parseInput(): Pair<Army, Army> {
        val input = loadInput().lineSequence()

        val armies = mutableListOf<Army>()

        var i = 1
        for (line in input) {
            if (line.contains(':')) {
                armies += Army(line)
                i = 1
            } else if (line.isNotBlank()) {
                armies.last().groups += Group.of(line, i++, armies.last())
            }
        }

        assert(armies.size == 2)
        return armies[0] to armies[1]
    }

    override fun calculateResult1(): Int {
        val groups = fight()!!

        return groups
            .filter { it.units > 0 }
            .sumBy { it.units }
    }

    override fun calculateResult2(): Int {
        for (i in generateSequence(1, Int::inc)) {
            val groups = fight(i)
            if (groups != null && groups.first { it.units > 0 }.army.name.startsWith("Immune")) {
                return groups
                    .filter { it.units > 0 }
                    .sumBy { it.units }
            }
        }

        return -1
    }

    // Returns null if the fight will never finish
    private fun fight(boost: Int = 0): List<Group>? {
        val input = parseInput()
        val groups = (input.first.groups + input.second.groups).map {
            if (it.army.name.startsWith("Immune")) {
                it.copy(attackPower = it.attackPower + boost)
            } else {
                it
            }
        }

        while (groups.filter { it.units > 0 }.groupBy { it.army }.size > 1) {
            var groupsSorted =
                groups.sortedWith(compareByDescending<Group> { it.effectivePower }.thenByDescending { it.initiative })
                    .filter { it.units > 0 }
            val targets = mutableMapOf<Group, Group?>()
            for (group in groupsSorted) {
                val target = groupsSorted
                    .asSequence()
                    .filter { it !in targets.values } // No target twice
                    .filter { it.army != group.army } // only enemies
                    .groupBy { group.damageTo(it) }
                    .maxBy { it.key }
                    ?.value
                    ?.sortedWith(compareByDescending<Group> { it.effectivePower }.thenByDescending { it.initiative })
                    ?.first()
                if (target != null && group.damageTo(target) > 0) {
                    targets[group] = target
                } else {
                    targets[group] = null
                }
            }

            // Attacking
            groupsSorted = groups.sortedByDescending { it.initiative }
            var totalKills = 0
            for (group in groupsSorted) {
                if (group.units <= 0) {
                    continue
                }

                targets[group]?.let { target ->
                    assert(group.army != target.army)
                    val damage = group.damageTo(target)
                    val kills = min(target.units, damage / target.hitPoints)
//                    println("$group hits $target killing $kills units")
                    target.units -= kills
                    totalKills += kills
                }
            }

            if (totalKills == 0) { // We're in a loop where no one can do damage
                return null
            }
//            println("==================")
        }

        return groups
    }
}

fun main(args: Array<String>) {
    Solution().solveWithMeasurement()
}
