package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.*
import java.util.*
import kotlin.math.abs

/**
 * Created by xkarpi06 on 17.12.2022
 *
 * Day 17: Pyroclastic Flow
 *
 * time: 5 hours + ~2 hours
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *  17       >24h  16399      0          -      -      -
 */
class Day17 {

    companion object {
        /**
         * There is a very tall, narrow chamber. Large, oddly-shaped rocks are falling into the chamber from above.
         * The rocks don't spin, but they do get pushed around by jets of hot gas coming out of the walls (input).
         * The tall, vertical chamber is exactly seven units wide.
         * After a rock appears, it alternates between being pushed by a jet of hot gas one unit (in the direction
         * indicated by the next symbol in the jet pattern) and then falling one unit down.
         * If a downward movement would have caused a falling rock to move into the floor or an already-fallen rock,
         * the falling rock stops where it is (having landed on something) and a new rock immediately begins falling.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day17Input")
            val jetFlow = parseInput(input)
            part1(jetFlow)
//            part2(jetFlow)
        }

        /**
         * How many units tall will the tower of rocks be after 2022 rocks have stopped falling?
         */
        private fun part1(jetBursts: List<Dir>) {
            val orderedRocks = Rock.orderedList
            val rockAmount = 2022
            TallNarrowChamber().run {
                var jetCount = 0
                repeat(rockAmount) { i ->
                    spawnRock(orderedRocks[i % orderedRocks.size])
                    do {
                        moveRockWithRules(jetBursts[jetCount++ % jetBursts.size])
                    } while (moveRockWithRules(Dir.DOWN))
                    putRockToSleep()
                }
                println("p1> $rockTowerHeight")
            }
        }

        /**
         * How tall will the tower be after 1000000000000 rocks have stopped?
         *
         * Not finished. Calculation is too slow. 1M rocks per second are being processed, which would require 1M
         * seconds (10 days) to complete.
         */
        private fun part2(jetFlow: List<Dir>) {
            val orderedRocks = Rock.orderedList
            val rockAmount = 10_000_000L // goal was 1_000_000_000_000L
            val reduceChamberPeriod = 5_000
            val printPeriod = 10_000_000
            TallNarrowChamber().run {
                var jetCount = 0
                var rockIndex = 0 // I need Int for list index
                for (i in 0 until rockAmount) {
                    spawnRock(orderedRocks[rockIndex++ % orderedRocks.size])
                    do {
                        moveRockWithRules(jetFlow[jetCount++ % jetFlow.size])
                    } while (moveRockWithRules(Dir.DOWN))
                    putRockToSleep()
                    // reduce chamber size periodically
                    if (i % reduceChamberPeriod == 0L) {
                        rockIndex %= reduceChamberPeriod
                        removeUnreachablePartOfChamber()
                        if (i % printPeriod == 0L) {
                            println("rocks=$i, chamber-size=${state.size}, tower-height=$rockTowerHeight")
                        }
                    }
                }
                println("p2> $rockTowerHeight")
            }
        }

        private fun parseInput(input: List<String>): List<Dir> {
            return input[0].map {
                when (it) {
                    '<' -> Dir.LEFT
                    '>' -> Dir.RIGHT
                    else -> throw InputMismatchException()
                }
            }
        }
    }

    /**
     * There are falling rocks (one at a time) inside a tall chamber and a tower made of stationary fallen rocks.
     * Falling rock is pushed by hot jets to left and right while falling.
     */
    private class TallNarrowChamber(private val width: Int = 7) {
        /**
         * boolean matrix represents chamber: true for still rock, false for void space
         * true -> rock
         * false -> void
         */
        val state = LinkedList<MutableList<Boolean>>()
        var currentRock: CurrentRock? = null
        var cutChamberFloors: Long = 0

        val rockTowerHeight: Long
            get() = state.size - state.takeWhile { it.all { !it } }.size + cutChamberFloors

        /**
         * Spawn new "live" rock in top left corner (with offset) of chamber
         * use only after the previous rock comes to a rest and becomes part of tower
         */
        fun spawnRock(rock: Rock) {
            // increase size of chamber
            val topVoidRows = state.takeWhile { it.all { !it } }
            val voidRowsNeeded = OFFSET_BOTTOM + rock.height
            val rowsMissing = voidRowsNeeded - topVoidRows.size
            repeat(rowsMissing) {
                state.addFirst(MutableList(width) { false })
            }
            // add rock
            currentRock =
                CurrentRock(
                    rock = rock,
                    offset = XY(x = OFFSET_LEFT, y = if (rowsMissing > 0) 0 else 0 - rowsMissing)
                )
        }

        /**
         * move rock if it can be moved (tower interference & walls prevent movement)
         * returns false if rock can't fall anymore
         */
        fun moveRockWithRules(direction: Dir): Boolean {
            // check if rock can move (checks only walls and bottom)
            if (chamberWallPreventsMove(direction)) return direction != Dir.DOWN
            // move rock
            moveRock(direction)
            // check tower interference & undo move if needed
            return if (currentRockOverlaysTower()) {
                moveRock(direction.opposite())
                direction != Dir.DOWN
            } else {
                true
            }
        }

        /**
         * moves rock without checking interference (walls prevent movement)
         */
        private fun moveRock(direction: Dir) {
            val old = currentRock?.offset ?: return
            if (chamberWallPreventsMove(direction)) return
            val new = when (direction) {
                Dir.LEFT -> old.copy(x = old.x - 1)
                Dir.RIGHT -> old.copy(x = old.x + 1)
                Dir.DOWN -> old.copy(y = old.y + 1) // fall
                Dir.UP -> old.copy(y = old.y - 1) // unused
            }
            currentRock!!.offset = new
        }

        private fun chamberWallPreventsMove(direction: Dir): Boolean {
            val rock = currentRock ?: return false
            return when (direction) {
                Dir.LEFT -> rock.offset.x < 1 // include dead rocks
                Dir.RIGHT -> rock.offset.x >= width - rock.rock.width // include dead rocks
                Dir.DOWN -> rock.offset.y >= state.size - rock.rock.height // include dead rocks
                else -> false
            }
        }

        private fun currentRockOverlaysTower(): Boolean {
            return currentRock?.let { curr ->
                curr.rock.coords.any {
                    state[curr.offset.y + it.y][curr.offset.x + it.x]
                }
            } ?: false
        }

        /**
         * Mark current falling rock as stationary (which makes it a part of tower of rocks)
         */
        fun putRockToSleep() {
            currentRock?.let { curr ->
                curr.rock.coords.forEach {
                    state[curr.offset.y + it.y][curr.offset.x + it.x] = true
                }
            }
            currentRock = null
        }

        /**
         * When tower inside chamber reaches particular state (reaches from left wall to right wall), a certain bottom
         * part of chamber will no longer be reachable by new falling rocks. This part of chamber can be removed
         * in order to save memory during calculation. (Main purpose was the second part of challenge)
         */
        fun removeUnreachablePartOfChamber() {
            val dist = Helper.getDistances<Boolean>(
                matrix = state,
                start = XY(0, 0),
                advancingRule = { _, neighbor -> !neighbor } // reachable must be false, contain void
            )
            val yOfLastReachableRow = dist.takeWhile { it.any { it != Int.MAX_VALUE } }.lastIndex
            cutChamberBelow(yOfLastReachableRow)
        }

        private fun cutChamberBelow(y: Int) {
            cutChamberFloors += state.lastIndex - y
            repeat(state.lastIndex - y) { state.pollLast() }
        }

        /**
         * Print chamber state. Falling rock as '@', stationary rocks as '#', empty space as '.'.
         */
        fun printState() {
            val chars = state.map { it.map { if (it) "#" else "." }.toMutableList() }
            currentRock?.let { curr ->
                curr.rock.coords.forEach {
                    chars[curr.offset.y + it.y][curr.offset.x + it.x] = "@"
                }
            }
            chars.forEach {
                println(it.joinToString("", "|", "|"))
            }
            println(List(width) { "-" }.joinToString("", "+", "+"))
            println("tower height: $rockTowerHeight")
        }

        companion object {
            // offset for new rocks
            private const val OFFSET_LEFT = 2
            private const val OFFSET_BOTTOM = 3
        }
    }

    private data class CurrentRock(
        val rock: Rock,
        var offset: XY, // offset from top left corner of chamber
    )

    // region shapes
    /**
     * Each shape has origin (0,0) in top-left corner
     */
    private enum class Rock(val coords: Set<XY>) {
        // ####
        MINUS(setOf(XY(0, 0), XY(1, 0), XY(2, 0), XY(3, 0))),

        // .#.
        // ###
        // .#.
        PLUS(setOf(XY(1, 0), XY(0, 1), XY(1, 1), XY(2, 1), XY(1, 2))),

        // ..#
        // ..#
        // ###
        L(setOf(XY(2, 0), XY(2, 1), XY(2, 2), XY(1, 2), XY(0, 2))),

        // #
        // #
        // #
        // #
        I(setOf(XY(0, 0), XY(0, 1), XY(0, 2), XY(0, 3))),

        // ##
        // ##
        SQUARE(setOf(XY(0, 0), XY(1, 0), XY(0, 1), XY(1, 1)));

        val height = abs(coords.maxOf { it.y } - coords.minOf { it.y }) + 1
        val width = abs(coords.maxOf { it.x } - coords.minOf { it.x }) + 1

        companion object {
            val orderedList = listOf(MINUS, PLUS, L, I, SQUARE)
        }
    }
}
