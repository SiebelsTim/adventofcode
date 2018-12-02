package base

abstract class BaseSolution<T, T1, T2>(val title: String) {
    fun loadInput(): String {
        val stream = this.javaClass.getResourceAsStream("input.txt")

        return String(stream.readBytes())
    }

    abstract fun parseInput(): T
    abstract fun calculateResult1(): T1
    abstract fun calculateResult2(): T2

    fun solve() {
        println("Result for $title: " + calculateResult1() + " and " + calculateResult2())
    }
}
