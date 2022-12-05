package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 05.12.2022
 *
 * time: 67 min + 2 min (start 7:16)
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *   5   02:22:54  19892      0   02:25:13  18739      0
 */
class Day05 {
// TODO: clean list im/mutability

    companion object {

        /**
         * The ship has a giant cargo crane capable of moving crates between stacks.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day05Input")
            part1(input)
            part2(input)
        }

        /**
         * The CrateMover 9000 can pick up and move one crate at a time.
         * After the rearrangement procedure completes, what crate ends up on top of each stack?
         */
        private fun part1(input: List<String>) {
            craneWork(input, Crane.C9000)
        }

        /**
         * The CrateMover 9001 is notable for the ability to pick up and move multiple crates at once.
         * After the rearrangement procedure completes, what crate ends up on top of each stack?
         */
        private fun part2(input: List<String>) {
            craneWork(input, Crane.C9001)
        }

        private enum class Crane { C9000, C9001 }

        private fun craneWork(
            input: List<String>,
            crane: Crane,
        ) {
            val crates = input.takeWhile { it.isNotEmpty() }.dropLast(1) // last line is only crate order
            val instructions = input.takeLastWhile { it.isNotEmpty() }

            var crateStacks = List<MutableList<Char>>(10) { mutableListOf() }
            crates.forEachIndexed { i, line ->
                val split = line.filterIndexed { j, _ -> j % 4 == 1 }.toCharArray().toList()
                println(line)
                split.forEachIndexed { k, char ->
                    if (!char.isWhitespace()) crateStacks[k].add(char)
                }
            }
            crateStacks = crateStacks.mapNotNull { if (it.isNotEmpty()) it.asReversed() else null }
            instructions.forEach { line ->
                val (move, from, to) = line.split(" ").filterIndexed { i, _ -> i % 2 == 1 }
                crateStacks = when (crane) {
                    Crane.C9000 -> moveCratesOneByOne(crateStacks, move.toInt(), from.toInt() - 1, to.toInt() - 1)
                    Crane.C9001 -> moveCratesMoreAtATime(crateStacks, move.toInt(), from.toInt() - 1, to.toInt() - 1)
                }
            }
            println("result> ${crateStacks.map { it.takeLast(1)[0] }.joinToString("")}")
        }

        private fun moveCratesOneByOne(
            state: List<MutableList<Char>>,
            move: Int,
            from: Int,
            to: Int
        ): List<MutableList<Char>> {
            for (i in 0 until move) {
                val crate = state[from].takeLast(1)[0]
                state[from].removeLast()
                state[to].add(crate)
            }
            return state
        }

        private fun moveCratesMoreAtATime(
            state: List<MutableList<Char>>,
            move: Int,
            from: Int,
            to: Int
        ): List<MutableList<Char>> {
            val crates = state[from].takeLast(move)
            for (i in 0 until move) {
                state[from].removeLast()
            }
            state[to].addAll(crates)
            return state
        }
    }

}
