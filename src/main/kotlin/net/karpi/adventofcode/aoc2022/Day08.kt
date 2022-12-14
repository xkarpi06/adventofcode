package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader
import kotlin.math.max
import kotlin.math.min

/**
 * Created by xkarpi06 on 08.12.2022
 *
 * time: 35 min + 25 min (start 7:30)
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *   8   02:05:56  16349      0   02:29:24  13940      0
 */
class Day08 {

    companion object {

        /**
         * The Elves have already launched a quadcopter to generate a map with the height of each tree
         * (your puzzle input). Each tree is represented as a single digit whose value is its height,
         * where 0 is the shortest and 9 is the tallest.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadInts2("Day08Input")
            parts1and2(input)
        }

        /**
         * p1: how many trees are visible from outside the grid?
         *
         * p2: A tree's scenic score is found by multiplying together its viewing distance in each of the four
         * directions. What is the highest scenic score possible for any tree?
         */
        private fun parts1and2(input: List<List<Int>>) {
            var acc = 0
            var max = 0
            for (i in input.indices) {
                for (j in input[i].indices) {
                    max = max(max, scenicScore(i, j, input))
                    if (isVisible(i, j, input)) acc++
                }
            }
            println("p1> $acc")
            println("p2> $max")
        }

        private fun scenicScore(row: Int, col: Int, forest: List<List<Int>>): Int {
            val height = forest[row][col]
            // seen from top
            var scoreUp = forest.take(row).map { it[col] }.takeLastWhile { it < height }.size + 1 // count last tree
            scoreUp = min(scoreUp, row) // if edge can be seen, +1 was wrongly added
            // seenFromDown
            var scoreDown = forest.takeLast(forest.lastIndex - row).map { it[col] }
                .takeWhile { it < height }.size + 1 // count last tree
            scoreDown = min(scoreDown, forest.lastIndex - row)
            // seenFromLeft
            var scoreLeft = forest[row].take(col).takeLastWhile { it < height }.size + 1 // count last tree
            scoreLeft = min(scoreLeft, col)
            // seenFromRight
            var scoreRight = forest[row].takeLast(forest[row].lastIndex - col)
                .takeWhile { it < height }.size + 1 // count last tree
            scoreRight = min(scoreRight, forest[row].lastIndex - col)

            return scoreUp * scoreDown * scoreLeft * scoreRight
        }

        private fun isVisible(row: Int, col: Int, forest: List<List<Int>>): Boolean {
            return when {
                row in listOf(0, forest.lastIndex) -> true // first or last row
                col in listOf(0, forest[row].lastIndex) -> true // first or last col
                else -> {
                    val height = forest[row][col]
                    // seen from top
                    height > forest.take(row).map { it[col] }.maxOf { it } ||
                    // seenFromDown
                    height > forest.takeLast(forest.lastIndex - row).map { it[col] }.maxOf { it } ||
                    // seenFromLeft
                    height > forest[row].take(col).maxOf { it } ||
                    // seenFromRight
                    height > forest[row].takeLast(forest[row].lastIndex - col).maxOf { it }
                }
            }
        }
    }

}
