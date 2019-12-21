package base

import kotlin.math.abs

data class Coordinate(val x: Int, val y: Int) {
    operator fun plus(coord: Coordinate) = Coordinate(x + coord.x, y + coord.y)
    operator fun plus(direction: Direction) = Coordinate(x + direction.dx, y + direction.dy)

    fun manhattenDistance(coord: Coordinate) = abs(coord.x - x) + abs(coord.y - y)

    val down get() = this + Direction.DOWN
    val up get() = this + Direction.UP
    val left get() = this + Direction.LEFT
    val right get() = this + Direction.RIGHT

    fun neighbors() = listOf(
            up, right, down, left
    )
}

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1), LEFT(-1, 0), RIGHT(+1, 0), DOWN(0, +1);

    fun reverse(): Direction = when(this) {
        UP -> DOWN
        LEFT -> RIGHT
        RIGHT -> LEFT
        DOWN -> UP
    }
}

data class Coordinate3d(val x: Int, val y: Int, val z: Int) {
    operator fun plus(coord: Coordinate3d) = Coordinate3d(x + coord.x, y + coord.y, z + coord.z)
    operator fun minus(coord: Coordinate3d) = Coordinate3d(x - coord.x, y - coord.y, z - coord.z)
    operator fun unaryMinus() = Coordinate3d(0, 0, 0) - this

    override fun toString(): String = "<x=$x, y=$y, z=$z>"
}
