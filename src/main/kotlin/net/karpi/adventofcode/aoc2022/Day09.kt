package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 09.12.2022
 *
 * time: 46 min + 25 min (start 7:00)
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *   9   01:46:38  12163      0   02:11:37   9018      0
 */
class Day09 {

    companion object {

        /**
         * Consider a rope with a knot at each end; these knots mark the head and the tail of the rope. If the head
         * moves far enough away from the tail, the tail is pulled toward the head.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day09Input")
            val instructions = input.map { line ->
                val (a, b) = line.split(" ")
                Instruction(dir = Instruction.Dir.fromChar(a.first()), times = b.toInt())
            }
            part1(instructions)
            part2(instructions)
        }

        /**
         * Simulate your complete hypothetical series of motions. How many positions does the tail of the rope visit
         * at least once?
         */
        private fun part1(instructions: List<Instruction>) {
            val h = Knot(0,0)
            val t = Knot(0,0)

            instructions.forEach { instruction ->
                repeat(instruction.times) {
                    h.moveDirection(instruction.dir)
                    if (!h.isTouching(t)) t.moveTo(h.prev.x, h.prev.y) // sufficient for part1, not part2
                }
            }
            val answer = t.visitedPositions().size
            println("p1> $answer")
        }

        /**
         * Rather than two knots, you now must simulate a rope consisting of ten knots.
         * Simulate your complete series of motions on a larger rope with ten knots. How many positions does the tail
         * of the rope visit at least once?
         */
        private fun part2(instructions: List<Instruction>) {
            val h = Knot(0,0)
            val tail = List(9) { Knot(0,0) }

            instructions.forEach { instruction ->
                repeat(instruction.times) {
                    h.moveDirection(instruction.dir)
                    for (i in tail.indices) {
                        val prev = if (i == 0) h else tail[i-1]
                        val current = tail[i]
                        if (!prev.isTouching(current)) current.follow(prev)
                    }
                }
            }
            val answer = tail.last().visitedPositions().size
            println("p2> $answer")
        }

    }

    private class Knot(x: Int, y: Int) {
        private val history: MutableList<Coord> = mutableListOf(Coord(x, y))
        val current: Coord
            get() = history.last()
        val prev: Coord
            get() = history[history.lastIndex - 1]

        fun isTouching(other: Knot) =
            current.x in (other.current.x - 1)..(other.current.x + 1) &&
            current.y in (other.current.y - 1)..(other.current.y + 1)

        fun moveDirection(dir: Instruction.Dir) {
            when (dir) {
                Instruction.Dir.R -> moveTo(current.x + 1, current.y)
                Instruction.Dir.L -> moveTo(current.x - 1, current.y)
                Instruction.Dir.U -> moveTo(current.x, current.y + 1)
                Instruction.Dir.D -> moveTo(current.x, current.y - 1)
            }
        }

        fun moveTo(x: Int, y: Int) {
            history += Coord(x, y)
        }

        fun follow(other: Knot) {
            val newX = when {
                other.current.x - current.x > 0 -> current.x + 1
                other.current.x - current.x < 0 -> current.x - 1
                else -> current.x
            }
            val newY = when {
                other.current.y - current.y > 0 -> current.y + 1
                other.current.y - current.y < 0 -> current.y - 1
                else -> current.y
            }
            moveTo(newX, newY)
        }

        fun visitedPositions(): Set<Coord> = history.toSet()
    }

    private data class Coord(val x: Int, val y: Int)

    private data class Instruction(val dir: Dir, val times: Int) {
        enum class Dir {
            R, L, U, D;

            companion object {
                fun fromChar(char: Char): Dir = when (char) {
                    'R' -> R
                    'L' -> L
                    'U' -> U
                    else -> D
                }
            }
        }
    }
}
