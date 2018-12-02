package base

abstract class BaseSolution<T>(val title: String) {
    fun loadInput(): String {
        val stream = this.javaClass.getResourceAsStream("input.txt")

        return String(stream.readBytes())
    }

    abstract fun parseInput(): T
    abstract fun calculateResult1(): Int
    abstract fun calculateResult2(): Int

    fun solve() {
        println("Result for $title: " + calculateResult1() + " and " + calculateResult2())
    }
}
