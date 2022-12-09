package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader
import java.util.*

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
            craneWork(input, Crane(Crane.Type.C9000))
        }

        /**
         * The CrateMover 9001 is notable for the ability to pick up and move multiple crates at once.
         * After the rearrangement procedure completes, what crate ends up on top of each stack?
         */
        private fun part2(input: List<String>) {
            craneWork(input, Crane(Crane.Type.C9001))
        }

        private fun craneWork(
            input: List<String>,
            crane: Crane,
        ) {
            // take crates input and drop last line which is unused stack numbering
            val crates = input.takeWhile { it.isNotEmpty() }.dropLast(1)
            val instructions = input.takeLastWhile { it.isNotEmpty() }

            val crateStacks = List<Stack<Char>>(9) { Stack() }
            // read stacks from bottom up
            crates.reversed().forEachIndexed { i, line ->
                val split = line.filterIndexed { j, _ -> j % 4 == 1 }.toCharArray().toList()
//                println(line)
                split.forEachIndexed { k, char ->
                    if (!char.isWhitespace()) crateStacks[k].push(char)
                }
            }
            instructions.forEach { line ->
                val (move, from, to) = line.split(" ").filterIndexed { i, _ -> i % 2 == 1 }
                crane.work(crateStacks, Instruction(move.toInt(), from.toInt() - 1, to.toInt() - 1))
            }
            println("result> ${crateStacks.map { it.takeLast(1)[0] }.joinToString("")}")
        }

    }

    private class Crane(
        private val type: Type,
    ) {
        enum class Type { C9000, C9001 }

        fun work(
            crateStacks: List<Stack<Char>>,
            instruction: Instruction,
        ) {
            when (type) {
                Type.C9000 -> workC9000(crateStacks, instruction)
                Type.C9001 -> workC9001(crateStacks, instruction)
            }
        }

        // moves one crate at a time
        private fun workC9000(stacks: List<Stack<Char>>, i: Instruction) {
            repeat(i.move) {
                val crate = stacks[i.from].pop()
                stacks[i.to].push(crate)
            }
        }

        // moves many crates at a time
        private fun workC9001(stacks: List<Stack<Char>>, i: Instruction) {
            val crates = stacks[i.from].takeLast(i.move)
            repeat(i.move) { stacks[i.from].pop() }
            stacks[i.to].addAll(crates)
        }
    }

    private data class Instruction(
        val move: Int,
        val from: Int,
        val to: Int,
    )
}
