package year2017.day13

import base.BaseSolution
import com.github.ajalt.mordant.TermColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

val t = TermColors()
class Solution : BaseSolution<Solution.Firewall, Int, Int>("Day 13") {
    enum class Direction {
        UP, DOWN
    }

    class Guard {
        var direction: Direction = Direction.DOWN
            private set
        var position: Int = 0
            private set

        fun step(range: Int) {
            if (position + 1 >= range) {
                direction = Direction.UP
            }

            if (position - 1 < 0) {
                direction = Direction.DOWN
            }

            when (direction) {
                Direction.UP -> position--
                Direction.DOWN -> position++
            }
        }
    }

    data class Layer(val depth: Int, val range: Int, val guard: Guard? = if (range > 0) Guard() else null)  {
        /**
         * The packet is caught at the timeframe if the guard is at top at that time frame
         * with an offset of the layer's depth to determine when the packet reached it
         */
        fun caughtAt(time: Int): Boolean = guardIsAtTopAtTime(depth + time)
        /**
         * We pretend ranges are twice as long to have the math work out better
         * as we can use modulus to simply calculate if the guard is at position 0 at any point in time
         */
        fun guardIsAtTopAtTime(time: Int) =
                guard !== null && time % ((range - 1) * 2) == 0

        fun toString(hasPlayer: Boolean): String {
            val ret = StringBuilder()

            ret.append(t.bold(depth.toString()))
            ret.append(" ")

            if (range == 0 && hasPlayer) {
                ret.append(t.bold("( )"))
            }

            repeat(range) {
                if (hasPlayer && it == 0) {
                    ret.append(t.bold("("))
                } else {
                    ret.append(t.gray("["))
                }

                if (guard?.position == it) {
                    ret.append(t.blue("S"))
                } else {
                    ret.append(" ")
                }

                if (hasPlayer && it == 0) {
                    ret.append(t.bold(")"))
                } else {
                    ret.append(t.gray("]"))
                }
            }

            return ret.toString()
        }

        fun step() {
            guard?.step(range)
        }
    }

    data class Firewall(val layers: List<Layer>, var player: Int = -1, private var steps: Int = 0, var caughtAtLayer: List<Layer> = emptyList()) {
        // Additionally to having Layer::caughtAt we also remember where the packet was caught during animation
        private val playerIsCaught: Boolean
            get() = player >= 0 && layers[player].guard?.position == 0

        val layerCount: Int get() = layers.size

        override fun toString(): String {
            val ret = StringBuilder()

            for (layer in layers) {
                ret.append(layer.toString(player == layer.depth))
                ret.append("\n")
            }

            return ret.toString()
        }

        fun step() {
            if (steps++ % 2 == 0) {
                player++
                if (playerIsCaught) {
                    caughtAtLayer += layers[player]
                }
            } else {
                for (layer in layers) {
                    layer.step()
                }
            }
        }
    }
    override fun parseInput(): Firewall {
        val layers = loadInput().lineSequence().map { line ->
            val (layer, depth) = line.split(": ").map { it.toInt() }
            layer to depth
        }.toMap()

        val lastLayer = layers.maxBy { it.key }!!.key // We could simply use the last entry, but we don't want to depend on it
        return Firewall(List(lastLayer + 1) {
            Layer(it, layers[it] ?: 0)
        })
    }

    override fun calculateResult1(): Int {
        val firewall = parseInput()
        val animate = false
        if (animate) {
            runBlocking {
                repeat(firewall.layerCount * 2) { // two steps per layer, one for packet, one for guards
                    println(firewall)
                    firewall.step()
                    delay(100)
                    clear(firewall.layerCount + 1)
                }

                println(firewall)
            }

            return firewall.caughtAtLayer.sumBy {
                it.depth * it.range
            }
        } else {
            return firewall.layers.sumBy {
                if (it.caughtAt(0)) {
                    it.depth * it.range
                } else {
                    0
                }
            }
        }
    }

    override fun calculateResult2(): Int {
        val firewall = parseInput()

        return generateSequence(0, Int::inc)
                .filter { time -> firewall.layers.none { it.caughtAt(time) } }
                .first()
    }

    private fun clear(lines: Int) {
        repeat(lines) {
            print("\u001B[1A"); // Move cursor up
            print("\u001B[2K"); // Remove line
        }
    }
}

fun main() {
    Solution().solveWithMeasurement()
}