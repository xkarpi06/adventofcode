package net.karpi.adventofcode.helpers

import java.util.*

object Helper {

    /**
     * Computes manhattan distance from start to each reachable point in matrix and returns matrix of these distances
     * for unreachable point in matrix, it's distance from start will be Int.MAX_VALUE.
     *
     * Algorithm: BFS from start point
     * for current point finds new reachable points by [advancingRule]
     *
     * @param advancingRule rule for advancing from one point to it's neighbor, if true, distance from current to
     * neighbor is 1
     */
    fun <T> getDistances(
        matrix: List<List<T>>,
        start: XY,
        advancingRule: (current: T, neighbor: T) -> Boolean,
    ): List<List<Int>> {
        val dist = MutableList(matrix.size) { MutableList(matrix[0].size) { Int.MAX_VALUE } }
        dist[start.y][start.x] = 0
        val visited = mutableSetOf<XY>()
        val queue = LinkedList(listOf(start))
        while (queue.isNotEmpty()) {
            val current = queue.poll()
            // find neighbours
            val reachable = listOf(
                XY(current.x - 1, current.y),
                XY(current.x + 1, current.y),
                XY(current.x, current.y - 1),
                XY(current.x, current.y + 1),
            ).filter {
                it.x in dist[0].indices && it.y in dist.indices // indexes in bounds
                        && it !in queue // not queued yet
                        && it !in visited // not visited yet
                        && advancingRule(matrix[current.y][current.x], matrix[it.y][it.x])
            }
            // set distance to neighbour & add to queue
            reachable.forEach {
                dist[it.y][it.x] = dist[current.y][current.x] + 1
                queue.add(it)
            }
            visited.add(current)
        }
        return dist
    }
    /**
     * Prints 2D matrix of integers separated by tab & for values Int.MAX_VALUE prints 'max'
     */
    fun printDistances(dist: List<List<Int>>) {
        printPretty(dist.map { it.map { if (it == Int.MAX_VALUE) "max" else "$it" } }, "\t")
    }

    /**
     * For 2D matrix of integers prints 'X' for values Int.MAX_VALUE and '.' otherwise. Tucked without spaces.
     */
    fun printDiscovered(dist: List<List<Int>>) {
        printPretty(dist.mapIndexed { y, it -> it.mapIndexed { x, it -> if (it == Int.MAX_VALUE) "X" else "." } })
    }

    /**
     * Util function for printing 2D matrices.
     */
    private fun <T> printPretty(list: List<List<T>>, delimeter: String = "") {
        list.forEach { println(it.joinToString(delimeter)) }
    }
}