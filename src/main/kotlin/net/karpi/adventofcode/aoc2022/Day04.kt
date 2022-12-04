package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 04.12.2022
 *
 * time: 13 min (12 min)
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *   4   00:11:47   5612      0   00:13:29   4068      0
 */
class Day04 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day04Input")
            part1(input)
            part2(input)
        }

        /**
         * input:
         * 2-4,6-8
         * 2-3,4-5
         * 5-7,7-9
         * ...
         * In how many assignment pairs does one range fully contain the other?
         */
        private fun part1(input: List<String>) {
            var acc = 0
            input.forEach { line ->
                val (rangeA, rangeB) = line.split(",").map {
                    val (start, end) = it.split("-")
                    (start.toInt()..end.toInt()).toList()
                }
                if (rangeA.intersect(rangeB).isNotEmpty() &&
                    (rangeA.containsAll(rangeB) || rangeB.containsAll(rangeA))
                ) {
//                    println("$rangeA, $rangeB")
                    acc++
                }
            }
            println("p1> $acc")
        }

        /**
         * input:
         * 2-4,6-8
         * 2-3,4-5
         * 5-7,7-9
         * ...
         * In how many assignment pairs do the ranges overlap?
         */
        private fun part2(input: List<String>) {
            var acc = 0
            input.forEach { line ->
                val (rangeA, rangeB) = line.split(",").map {
                    val (start, end) = it.split("-")
                    start.toInt()..end.toInt()
                }
                if (rangeA.intersect(rangeB).isNotEmpty()) {
//                    println("$rangeA, $rangeB")
                    acc++
                }
            }
            println("p2> $acc")
        }

    }

}
