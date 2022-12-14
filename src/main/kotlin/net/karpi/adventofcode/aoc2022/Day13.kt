package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 13.12.2022
 *
 * Day 13: Distress Signal - Sorting Packets
 *
 * time: 2h 30min + 11 min (start 18:32)
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *  13   15:00:50  25654      0   15:11:43  24464      0
 */
class Day13 {

    companion object {

        /**
         * Your list consists of pairs of packets; pairs are separated by a blank line. You need to identify how many
         * pairs of packets are in the right order.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day13Input")
            val packets = parseInput(input)
            part1(packets)
            part2(packets)
        }

        /**
         * Determine which pairs of packets are already in the right order. What is the sum of the indices of those
         * pairs?
         */
        private fun part1(packets: List<Packet>) {
            val answer = mutableListOf<Int>()
            packets.windowed(2, 2).forEachIndexed { i, it ->
                val tmp = it[0].compareTo(it[1])
                if (tmp < 0) answer += (i + 1)
            }
            println("p1> ${answer.sum()}")
        }

        /**
         * To find the decoder key for this distress signal, you need to determine the indices of the two divider
         * packets and multiply them together.
         * Organize all of the packets into the correct order. What is the decoder key for the distress signal?
         */
        private fun part2(packets: List<Packet>) {
            val dividerA = parsePacket("[[2]]")
            val dividerB = parsePacket("[[6]]")
            val withDividers = mutableListOf(dividerA, dividerB)
            withDividers += packets
            withDividers.sort()
            val answer = (withDividers.indexOf(dividerA) + 1) * (withDividers.indexOf(dividerB) + 1)
            println("p2> $answer")
        }

        private fun parseInput(input: List<String>): List<Packet> {
            return input.windowed(2, 3).flatMapIndexed { i, it ->
                listOf(parsePacket(it[0]), parsePacket(it[1]))
            }
        }

        /**
         * Examples of input:
         *
         * [[8,[8],3,0]]
         * [1,[2,[3,[4,[5,6,7]]]],8,9]
         * [[[[2],6,[],1],1],[],[6,[6]],[0]]
         */
        private fun parsePacket(input: String): Packet {
            val output = mutableListOf<Packet>()
            var sub = input.substring(1, input.lastIndex)
            while (sub.isNotEmpty()) {
                if (sub.startsWith('[')) {
                    val firstSublist = firstListIn(sub)
                    output += parsePacket(firstSublist)
                    sub = sub.substring(firstSublist.length)
                } else if (sub.startsWith(',')) {
                    sub = sub.substring(1)
                } else {
                    val firstInt = sub.takeWhile { it != ',' }
                    output += Packet.Int(firstInt.toInt())
                    sub = sub.substring(firstInt.length)
                }
            }
            return Packet.List(output)
        }

        /**
         * find list in string and return a substring with this list only
         * find first '[' and then search for correct closing ']'
         */
        private fun firstListIn(input: String): String {
            val s = input.indexOf('[')
            var counter = 0
            for (i in s..input.lastIndex) {
                when (input[i]) {
                    '[' -> counter++
                    ']' -> counter--
                }
                if (counter == 0) return input.substring(s, i + 1)
            }
            return input
        }
    }

    /**
     * Packet data consists of lists and integers.
     *
     * Each list starts with [, ends with ], and contains zero or more comma-separated values (either integers or other
     * lists). Each packet is always a list and appears on its own line.
     *
     * Comparison rules:
     *
     * #1 If both values are integers, the lower integer should come first. If the left integer is lower than the right
     * integer, the inputs are in the right order. If the left integer is higher than the right integer, the inputs are
     * not in the right order. Otherwise, the inputs are the same integer; continue checking the next part of the input.
     *
     * #2 If both values are lists, compare the first value of each list, then the second value, and so on. If the left
     * list runs out of items first, the inputs are in the right order. If the right list runs out of items first, the
     * inputs are not in the right order. If the lists are the same length and no comparison makes a decision about the
     * order, continue checking the next part of the input.
     *
     * #3 If exactly one value is an integer, convert the integer to a list which contains that integer as its only
     * value, then retry the comparison. For example, if comparing [0,0,0] and 2, convert the right value to [2] (a list
     * containing 2); the result is then found by instead comparing [0,0,0] and [2].
     *
     * more info: https://adventofcode.com/2022/day/13
     */
    private sealed class Packet : Comparable<Packet> {

        data class Int(val value: kotlin.Int) : Packet() {

            override fun compareTo(other: Packet): kotlin.Int {
                return when (other) {
                    is List -> {
                        // If exactly one value is an integer
                        List(listOf(Int(value))).compareTo(other)
                    }
                    is Int -> {
                        // If both values are integers
                        value - other.value
                    }
                }
            }

            override fun toString() = "$value"
        }

        data class List(val value: kotlin.collections.List<Packet>) : Packet() {

            override fun compareTo(other: Packet): kotlin.Int {
                return when (other) {
                    is Int -> {
                        // If exactly one value is an integer
                        this.compareTo(List(listOf(Int(other.value))))
                    }
                    is List -> {
                        // If both values are lists
                        for (i in value.indices) {
                            if (i > other.value.lastIndex) return 1
                            val copmarison = value[i].compareTo(other.value[i])
                            if (copmarison != 0) return copmarison
                        }
                        // got to the end of list this.value without any diff
                        if (value.size == other.value.size) 0 else -1
                    }
                }
            }

            override fun toString() = "$value"
        }
    }
}
