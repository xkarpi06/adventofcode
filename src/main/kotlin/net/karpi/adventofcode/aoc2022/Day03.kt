package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 03.12.2022
 */
class Day03 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val measurements = InputLoader(AoCYear.AOC_2022).loadStrings("Day03Input")
            part1(measurements)
            part2(measurements)
        }

        /**
         * Find the item type that appears in both compartments of each rucksack. What is the sum of the priorities
         * of those item types?
         */
        private fun part1(measurements: List<String>) {
            var acc = 0
            measurements.forEach { rucksack ->
                // split into 2 halves
                val data = listOf(rucksack.substring(0, rucksack.length / 2), rucksack.substring(rucksack.length / 2))
//                println(data)
                data[0].forEachIndexed { i, it ->
                    if (it in data[1]) {
//                        println("matching $it, ${it.myVal()}")
                        acc += it.priority()
                        return@forEach
                    }
                }
            }
            println("p1 $acc")
        }

        /**
         * Find the item type that corresponds to the badges of each three-Elf group. What is the sum of the priorities
         * of those item types?
         */
        private fun part2(measurements: List<String>) {
            var acc = 0
            for (i in 0 until measurements.size step 3) {
                val ruc = listOf(measurements[i], measurements[i + 1], measurements[i + 2])
//                println(ruc)
                var go = true
                ruc[0].forEachIndexed { i, it ->
                    if (it in ruc[1] && it in ruc[2] && go) {
//                        println("matching $it, ${it.myVal()}")
                        acc += it.priority()
                        go = false
                    }
                }
            }
            println("p2 $acc")
        }

        fun Char.priority() = if (isUpperCase()) {
            code - 38
        } else {
            code - 96
        }
    }

}
