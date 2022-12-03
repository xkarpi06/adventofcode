package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.aoc2022.Day02.Companion.RPS.Companion.beats
import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 02.12.2022
 *
 * time: 36 min (25+11)
 */
class Day02 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val measurements = InputLoader(AoCYear.AOC_2022).loadStrings("Day02Input")
            part1(measurements)
            part2(measurements)
        }

        private fun part1(measurements: List<String>) {
            var acc = 0
            measurements.forEach {
                val duel = it.split(" ").map { RPS.fromChar(it[0]) }
                val outcome = fight(my = duel[1], his = duel[0])
//                println("$duel -> $outcome")
                acc += outcome.pts + duel[1].score
            }
            println("pts: $acc")
        }

        private fun part2(measurements: List<String>) {
            var acc = 0
            measurements.forEach {
                val chars = it.split(" ").map { it[0] }
                val duel = Pair(RPS.fromChar(chars[0]), Outcome.fromChar(chars[1]))
                val myMove = getMoveByOutcome(duel.first, duel.second)
//                println("$duel -> $myMove")
                acc += duel.second.pts + myMove.score
            }
            println("pts: $acc")
        }

        enum class RPS(val score: Int) {
            ROCK(1),
            PAPER(2),
            SCISSORS(3);

            companion object {
                fun RPS.beats(): RPS = when (this) {
                    ROCK -> SCISSORS
                    PAPER -> ROCK
                    SCISSORS -> PAPER
                }

                fun fromChar(char: Char): RPS = when (char) {
                    'X', 'A' -> ROCK
                    'Y', 'B' -> PAPER
                    else -> SCISSORS
                }
            }
        }

        enum class Outcome(val pts: Int) {
            LOST(0),
            DRAW(3),
            WIN(6);

            companion object {
                fun fromChar(char: Char): Outcome = when (char) {
                    'X' -> LOST
                    'Y' -> DRAW
                    else -> WIN
                }
            }
        }

        fun fight(my: RPS, his: RPS): Outcome = when {
            my.beats() == his -> Outcome.WIN
            his.beats() == my -> Outcome.LOST
            else -> Outcome.DRAW
        }

        fun getMoveByOutcome(his: RPS, outcome: Outcome): RPS {
            RPS.values().forEach {
                if (fight(my = it, his = his) == outcome) {
                    return it
                }
            }
            return RPS.ROCK
        }
    }

}
