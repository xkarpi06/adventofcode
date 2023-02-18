package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Created by xkarpi06 on 18.02.2023
 *
 * Day 25: Full of Hot Air - modified base 5 number system
 *
 * time: 1 hod
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *  25       >24h  18592      0          -      -      -
 */
class Day25 {

    companion object {

        /**
         * Special Numeral-Analogue Fuel Units - SNAFU
         * SNAFU is base 5 number system, where instead of using digits 0-4, the digits are {=,-,0,1,2}
         * '=' stands for -2 and '-' stands for -1, so there is subtracting as well as adding in this system
         * Decimal    SNAFU
         *       1        1     (1*1)
         *       2        2     (2*1)
         *       3       1=     (1x5 + (-2)*1)
         *       4       1-     (1x5 + (-1)*1)
         *       5       10     (1x5 + 0*1)
         *       6       11     (1x5 + 1*1)
         *       7       12     (1x5 + 2*1)
         *       8       2=     (2x5 + (-2)*1)
         *       9       2-     (2x5 + (-1)*1)
         *      10       20     (2x5 + 0*1)
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day25Input")
            part1(input)
        }

        /**
         * Find the sum of the SNAFU numbers in input. Answer is the sum in SNAFU.
         */
        private fun part1(input: List<String>) {
            val sum = input.sumOf { parseSNAFU(it) }
            val answer = toSNAFU(sum)
            println("p1> $answer")
        }

        /**
         * Parses base 5 SNAFU number to base 10 Long
         */
        private fun parseSNAFU(input: String): Long {
            var result = 0L
            input.reversed().forEachIndexed { i, c ->
                val digit = when (c) {
                    '2' -> 2
                    '1' -> 1
                    '-' -> -1
                    '=' -> -2
                    else -> 0
                }
                result += digit * 5.0.pow(i).roundToLong()
            }
            return result
        }

        /**
         * Converts base 10 Long to base 5 SNAFU number
         *
         * The algorithm is similar to normal converting between different bases, with the exception that when you
         * write '-' (-1) or '=' (-2), you add 1 to the remainder
         *
         * while remainder > 0
         *  1. find modulo 5 of remainder and write down (write '=' instead of 3 and '-' instead of 4)
         *  2. divide remainder by 5, that is the new remainder
         *  3. if the modulo result was 3 or 4, add 1 to the remainder
         *  4. repeat
         * read the written symbols backwards to get result
         */
        private fun toSNAFU(input: Long): String {
            var remaining = input
            var symbols = ""
            while (remaining > 0) {
                when (remaining % 5) {
                    0L -> { symbols += '0'; remaining /= 5 }
                    1L -> { symbols += '1'; remaining /= 5 }
                    2L -> { symbols += '2'; remaining /= 5 }
                    3L -> { symbols += '='; remaining /= 5; remaining++ } // carry +1
                    4L -> { symbols += '-'; remaining /= 5; remaining++ } // carry +1
                }
            }
            return symbols.reversed()
        }

    }

}
