package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.Helper
import net.karpi.adventofcode.helpers.InputLoader
import net.karpi.adventofcode.helpers.XY
import java.util.*

/**
 * Created by xkarpi06 on 12.12.2022
 *
 * Day 12: Hill Climbing Algorithm
 * (Solved in hand with printed input in 40 min, later implemented in ~3h)
 *
 * time by hand: 35 min + 5 min
 * time coding : 3 hours + 20 min
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *  12   11:11:09  22473      0   11:19:57  21555      0
 */
class Day12 {

    companion object {

        /**
         * The heightmap shows the local area from above broken into a grid; the elevation of each square of the grid is
         * given by a single lowercase letter, where a is the lowest elevation, b is the next-lowest, and so on up to
         * the highest elevation, z. (S) is your position, (E) is the top of mountain.
         * You'd like to reach E, but to save energy, you should do it in as few steps as possible. During each step,
         * you can move exactly one square up, down, left, or right. To avoid needing to get out your climbing gear, the
         * elevation of the destination square can be at most one higher than the elevation of your current square.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day12Input")
            val hike = parseInput(input) ?: return
            part1(hike)
            part2(hike)
        }

        /**
         * What is the fewest steps required to move from your current position to the location that should get the
         * best signal?
         */
        private fun part1(hike: Hike) {
            val distances = Helper.getDistances<Char>(
                matrix = hike.heightMap,
                start = hike.start,
                advancingRule = { current, neighbor -> current - neighbor >= -1 } // climb 1 up max
            )
            println("p1> ${distances[hike.end.y][hike.end.x]}")
        }

        /**
         * What is the fewest steps required to move starting from any square with elevation a to the location that
         * should get the best signal?
         */
        private fun part2(hike: Hike) {
            val distancesFromEnd = Helper.getDistances(
                matrix = hike.heightMap,
                start = hike.end,
                advancingRule = { current, neighbor -> neighbor - current >= -1 } // descend 1 down max
            )
            // find 'a' with min distance, that is the shortest path from 'a' to end
            val answer = hike.heightMap.flatMapIndexed { i, row ->
                row.mapIndexed { j, char ->
                    if (char == 'a') distancesFromEnd[i][j] else null
                }.filterNotNull()
            }.minOrNull()
            println("p2> $answer")
        }

        private fun parseInput(input: List<String>): Hike? {
            var start: XY? = null
            var end: XY? = null
            val heightMap = input.mapIndexed { i, line ->
                line.toCharArray().toList().mapIndexed { j, char ->
                    when (char) {
                        'S' -> { start = XY(j, i); 'a' }
                        'E' -> { end = XY(j, i); 'z' }
                        else -> char
                    }
                }
            }
            return start?.let { s -> end?.let { e -> Hike(start = s, end = e, heightMap = heightMap) } }
        }
    }

    private data class Hike(
        val start: XY,
        val end: XY,
        val heightMap: List<List<Char>>,
    )
}
