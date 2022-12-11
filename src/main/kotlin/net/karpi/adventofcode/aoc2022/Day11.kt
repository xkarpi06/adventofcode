package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader
import java.util.*

/**
 * Created by xkarpi06 on 11.12.2022
 *
 * Day 11: Monkey in the Middle
 *
 * topics:
 * BigInteger overflow
 * Chinese remainder theorem (apply mod(monkey0.mod x ... x monkey.N.mod) to keep value in bounds)
 *
 * time: 74 min + ~3 hod total (start 6:00)
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *  11   01:14:07   7081      0   08:14:02  18596      0
 */
class Day11 {

    private enum class Part { ONE, TWO }

    companion object {

        /**
         * To get your stuff back, you need to be able to predict where the monkeys will throw your items. After some
         * careful observation, you realize the monkeys operate based on how worried you are about each item.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day11Input")

            /**
             * Figure out which monkeys to chase by counting how many items they inspect over 20 rounds. What is the
             * level of monkey business after 20 rounds of stuff-slinging simian shenanigans?
             */
            monkeyBusiness(parseInput(input, Part.ONE), 20)

            /**
             * Worry levels are no longer divided by three after each item is inspected; you'll need to find another
             * way to keep your worry levels manageable. Starting again from the initial state in your puzzle input,
             * what is the level of monkey business after 10000 rounds?
             */
            Monkey.reset()
            monkeyBusiness(parseInput(input, Part.TWO), 10_000)
        }

        private fun monkeyBusiness(monkeys: List<Monkey>, times: Int) {
            repeat(times) { round(monkeys) }
            val sortedMonkeys = monkeys.sortedByDescending { it.inspectedItems }
            val answer = sortedMonkeys[0].inspectedItems.toLong() * sortedMonkeys[1].inspectedItems
            println("answer $times> $answer")
        }

        private fun round(monkeys: List<Monkey>) {
            monkeys.forEach { monkey ->
                while (monkey.tryInspectItem() != Monkey.Signal.DONE) {
                    monkey.lastThrownItem?.let { monkeys[it.first].catchItem(it.second) }
                }
            }
        }

        private fun parseInput(input: List<String>, part: Part = Part.ONE): List<Monkey> {
            return input.windowed(7, 7, partialWindows = true).mapIndexed { i, monkeyInput ->
                val items = monkeyInput[1].split(": ").last().split(", ").map { it.toLong() }
                val operation = parseOp(monkeyInput[2].split("= old ").last().split(" "))
                val divisibleByTest = monkeyInput[3].split("divisible by ").last().toInt()
                val throwIfTrue = monkeyInput[4].split("monkey ").last().toInt()
                val throwIfFalse = monkeyInput[5].split("monkey ").last().toInt()
                Monkey(
                    onInspect = operation,
                    testMod = divisibleByTest,
                    nextMonkey = Pair(throwIfTrue, throwIfFalse),
                    part = part,
                ).also { monkey ->
                    items.forEach { monkey.catchItem(Item(it)) }
                }
            }
        }

        // input is [op, int]
        private fun parseOp(input: List<String>): (Long) -> Long {
            val old = input.last() == "old"
            val other = if (old) 0 else input.last().toInt()
            return when (input.first()) {
                "+" -> { x -> if (!old) x + other else x + x }
                "*" -> { x -> if (!old) x * other else x * x }
                else -> { x -> x }
            }
        }
    }

    private class Item(var worryLvl: Long)

    private class Monkey(
        val onInspect: (Long) -> Long,
        val testMod: Int,
        val nextMonkey: Pair<Int, Int>,
        val part: Part = Part.ONE,
    ) {
        var inspectedItems = 0
        val itemsInHand = LinkedList<Item>()
        var lastThrownItem: Pair<Int, Item>? = null // pair of <idx of receiver Monkey, thrown item>

        enum class Signal { THROW, DONE }

        fun tryInspectItem(): Signal =
            if (itemsInHand.isNotEmpty()) {
                val item = itemsInHand.poll()
                item.worryLvl = onInspect(item.worryLvl)
                item.lowerStress()
                throwItem(item)
                inspectedItems++
                Signal.THROW
            } else {
                Signal.DONE
            }


        fun catchItem(item: Item) {
            itemsInHand.add(item)
        }

        fun throwItem(item: Item) {
            val throwTo = if (item.worryLvl % testMod == 0L) nextMonkey.first else nextMonkey.second
            lastThrownItem = Pair(throwTo, item)
        }

        // lower stress to stay in bounds of Long
        private fun Item.lowerStress() {
            when (part) {
                Part.ONE -> worryLvl /= 3
                Part.TWO -> worryLvl %= modProduct
            }
        }

        init {
            // multiply monkey mods to use chinese remainder theorem
            modProduct *= testMod
        }

        companion object {
            var modProduct = 1

            fun reset() {
                modProduct = 1
            }
        }
    }
}
