package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 06.12.2022
 *
 * time: 26 min + 1 min (start 6:56)
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *   6   01:21:24  21332      0   01:22:02  20486      0
 */
class Day06 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day06Input")
            part1(input[0])
            part2(input[0])
        }

        /**
         * To fix the communication system, you need to add a subroutine to the device that detects a start-of-packet
         * marker. It is indicated by a sequence of four characters that are all different.
         *
         * How many characters need to be processed before the first start-of-packet marker is detected?
         */
        private fun part1(input: String) {
            println("p1> ${findMarker(input, 4)}")
        }

        private fun part2(input: String) {
            println("p2> ${findMarker(input, 14)}")
        }

        private fun findMarker(stream: String, markerSize: Int): Int {
            val index = stream.windowed(markerSize).indexOfFirst { it.toSet().size == markerSize }
            return if (index >= 0) index + markerSize else index
        }
    }

}
