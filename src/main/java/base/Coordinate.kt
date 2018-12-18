package base

data class Coordinate(val x: Int, val y: Int) {
    operator fun plus(coord: Coordinate) = Coordinate(x + coord.x, y + coord.y)
    operator fun plus(direction: Direction) = Coordinate(x + direction.dx, y + direction.dy)
}

enum class Direction(val dx: Int, val dy: Int) {
    UP(0, -1), LEFT(-1, 0), RIGHT(+1, 0), DOWN(0, +1);
}
