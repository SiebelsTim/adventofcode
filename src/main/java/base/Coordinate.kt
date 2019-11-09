package base

data class Coordinate(val x: Int, val y: Int) {
    operator fun plus(coord: Coordinate) = Coordinate(x + coord.x, y + coord.y)
    operator fun plus(direction: Direction) = Coordinate(x + direction.dx, y + direction.dy)

    val down get() = this + Direction.DOWN
    val up get() = this + Direction.UP
    val left get() = this + Direction.LEFT
    val right get() = this + Direction.RIGHT
}

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1), LEFT(-1, 0), RIGHT(+1, 0), DOWN(0, +1);
}

data class Coordinate3d(val x: Int, val y: Int, val z: Int) {
    operator fun plus(coord: Coordinate3d) = Coordinate3d(x + coord.x, y + coord.y, z + coord.z)
}
