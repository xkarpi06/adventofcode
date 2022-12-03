package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader
import kotlin.math.max

/**
 * Created by xkarpi06 on 01.12.2022
 *
 * topics:
 * 1. finding max in array
 * 2. finding sum of max 3 in array
 *    a) mutable list operations
 *    b) fold to sum values in list
 *
 * time: 40 min
 */
class Day01 {

    companion object {

        /**
         * In case the Elves get hungry and need extra snacks, they need to know which Elf to ask: they'd like to know
         * how many Calories are being carried by the Elf carrying the most Calories.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val measurements = InputLoader(AoCYear.AOC_2022).loadStrings("Day01Input")
            part1(measurements)
            part2(measurements)
        }

        /**
         * Find the Elf carrying the most Calories. How many total Calories is that Elf carrying?
         */
        private fun part1(measurements: List<String>) {
            var maxElfCalories = 0
            var accElfCalories = 0
            measurements.forEach {
                if (it.isEmpty()) {
                    maxElfCalories = max(accElfCalories, maxElfCalories)
                    accElfCalories = 0
                } else {
                    accElfCalories += it.toInt()
                }
            }
            println("Max: $maxElfCalories")
        }

        /**
         * Find the top three Elves carrying the most Calories. How many Calories are those Elves carrying in total?
         */
        private fun part2(measurements: List<String>) {
            val max3ElfCalories = mutableListOf(0,0,0)
            var accElfCalories = 0
            measurements.forEach {
                if (it.isEmpty()) {
                    if (max3ElfCalories.any { it < accElfCalories }) {
                        max3ElfCalories.apply {
                            sortDescending()
                            removeAt(max3ElfCalories.lastIndex)
                            add(accElfCalories)
                        }
                    }
                    accElfCalories = 0
                } else {
                    accElfCalories += it.toInt()
                }
            }
            println("Max 3: ${max3ElfCalories.fold(0) { a,b -> a+b }}")
        }

    }

}
