package year2019.day8

import base.BaseSolution
import base.Coordinate
import com.github.ajalt.mordant.TermColors

class Solution : BaseSolution<Solution.Image, Int, String>("Day 8") {
    data class Image(val pixels: Map<Coordinate, List<Int>>, val width: Int, val height: Int) {
        val layers: List<List<Int>>
            get() {
                val layerCount = pixels.entries.first().value.size
                return List(layerCount) { layerNumber ->
                    pixels.entries.map { it.value[layerNumber] }
                }
            }
    }

    override fun parseInput(): Image {
        val width = 25
        val height = 6
        val pixels = mutableMapOf<Coordinate, List<Int>>()
        val lines = loadInput().toCharArray().map { it.toString().toInt() }.chunked(width)
        for ((lineNo, line) in lines.withIndex()) {
            val y = lineNo % height
            for ((x, value) in line.withIndex()) {
                val pixel = pixels.computeIfAbsent(Coordinate(x, y)) {
                    emptyList()
                }
                pixels[Coordinate(x ,y)] = pixel + value
            }
        }

        return Image(pixels, width, height)
    }

    override fun calculateResult1(): Int {
        val input = parseInput()
        val layers = input.layers
        return layers
                .minBy { layer -> layer.count { it == 0 } }!!
                .let { layer ->
                    layer.count { it == 1 } * layer.count { it == 2 }
                }
    }

    override fun calculateResult2(): String {
        val t = TermColors()
        val input = parseInput()
        for (y in 0 until input.height) {
            for (x in 0 until input.width) {
                val color = input.pixels[Coordinate(x, y)]!!.first { it != 2 }
                val symbol = when (color) {
                    0 -> t.gray("X")
                    1 -> t.white("O")
                    else -> IllegalArgumentException("Pixel value invalid: $color")
                }
                print(symbol)
            }
            println()
        }
        return "FAHEF"
    }
}

fun main() {
    Solution().solveWithMeasurement()
}