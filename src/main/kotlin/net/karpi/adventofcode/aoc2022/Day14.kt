package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader
import net.karpi.adventofcode.helpers.XY

/**
 * Created by xkarpi06 on 27.12.2022
 *
 * Day 14: Day 14: Regolith Reservoir - falling sand
 *
 * time: 2 months 4 days (3 hours)
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *  14       >24h  47105      0          -      -      -
 */
class Day14 {

    companion object {

        /**
         * Sand begins pouring into the cave! If you don't quickly figure out where the sand is going, you could quickly
         * become trapped! You have scan traces of the path of each solid rock structure inside cave. Sand falls through
         * cave system.
         *
         * A unit of sand always falls down one step if possible. If the tile immediately below is blocked
         * (by rock or sand), the unit of sand attempts to instead move diagonally one step down and to the left. If
         * that tile is blocked, the unit of sand attempts to instead move diagonally one step down and to the right.
         * If all three possible destinations are blocked, the unit of sand comes to rest.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day14Input")
            val (cave, minX) = parseInput(input)
            part1(cave, minX)
        }

        /**
         * Using your scan, simulate the falling sand. How many units of sand come to rest before sand starts flowing
         * into the abyss below?
         */
        private fun part1(cave: List<MutableList<CavePixel>>, minX: Int) {
            val sandOrigin = XY(500 - minX, 0)
            cave[sandOrigin.y][sandOrigin.x] = CavePixel.FALLING_SAND
            var answer = 0
            while (dropSand(sandOrigin, cave)) answer++
//            printCave(cave)
            println("p1> $answer")
        }

        /**
         * Drop sand particle and put a pixel of still sand where the particle comes to a rest
         * @param from - xy offset (from top left corner) of point where sand particle is dropped into cave
         * @param cave - cave pixels
         * @return true if the particle came to a rest inside the given cave, false otherwise (sand overflow)
         */
        private fun dropSand(from: XY, cave: List<MutableList<CavePixel>>): Boolean {
            var current = from

            // helper functions
            val clearCurrent = { cave[current.y][current.x] = CavePixel.VOID }
            val isPixelAt = { point: XY, pixel: CavePixel -> cave[point.y][point.x] == pixel }
            val putPixelTo = { point: XY, pixel: CavePixel -> cave[point.y][point.x] = pixel }
            val sandFalls = { newPosition: XY ->
                clearCurrent()
                putPixelTo(newPosition, CavePixel.FALLING_SAND)
                current = newPosition
                true
            }

            var canFall = true
            while (canFall) {
                val oneDown = current.copy(y = current.y + 1)
                if (oneDown.y > cave.lastIndex) return false
                canFall = when (cave[oneDown.y][oneDown.x]) {
                    CavePixel.VOID -> sandFalls(oneDown) // fall down
                    CavePixel.ROCK, CavePixel.STILL_SAND -> { // rock/sand below
                        val oneDownOneLeft = oneDown.copy(x = oneDown.x - 1)
                        val oneDownOneRight = oneDown.copy(x = oneDown.x + 1)
                        when {
                            oneDownOneLeft.x < 0 -> return false // overflow left
                            isPixelAt(oneDownOneLeft, CavePixel.VOID) -> sandFalls(oneDownOneLeft) // fall left
                            oneDownOneRight.x > cave[0].lastIndex -> return false // overflow right
                            isPixelAt(oneDownOneRight, CavePixel.VOID) -> sandFalls(oneDownOneRight) // fall right
                            else -> { // come to a rest
                                putPixelTo(current, CavePixel.STILL_SAND); false
                            }
                        }
                    }
                    CavePixel.FALLING_SAND -> false // should never happen
                }
            }
            return true
        }

        /**
         * @returns pair of following:
         * 1. 2D matrix representing cave system with void space (false) and rock (true)
         * 2. Int representing min X coordinate of any rock formation (most left rock)
         */
        private fun parseInput(input: List<String>): Pair<List<MutableList<CavePixel>>, Int> {
            // store rock formations as list of points for each
            val rocks = input.map {
                it.split(" -> ").map {
                    val xy = it.split(",")
                    XY(xy[0].toInt(), xy[1].toInt())
                }
            }
            val minX = rocks.minOf { it.minOf { it.x } }
            val maxX = rocks.maxOf { it.maxOf { it.x } }
            val maxY = rocks.maxOf { it.maxOf { it.y } }
            val cave = List(maxY + 1) { MutableList(maxX - minX + 1) { CavePixel.VOID } }

            // put rock formation into the cave
            rocks.forEach { rock ->
                // for each two points of rock
                rock.windowed(2, 1).forEach {
                    val start = it[0]
                    val end = it[1]
                    val diff = XY(end.x - start.x, end.y - start.y)
                    var current = start

                    // helper functions
                    val drawRock = { cave[current.y][current.x - minX] = CavePixel.ROCK }
                    val drawRockFromStartToEnd = { newCurrent: () -> XY ->
                        while (current != end) {
                            drawRock()
                            current = newCurrent()
                        }
                        drawRock()
                    }

                    // determine the direction from start to end and iterate accordingly
                    when {
                        diff.x > 0 -> drawRockFromStartToEnd { current.copy(x = current.x + 1) } // left to right
                        diff.x < 0 -> drawRockFromStartToEnd { current.copy(x = current.x - 1) } // right to left
                        diff.y > 0 -> drawRockFromStartToEnd { current.copy(y = current.y + 1) } // top to bottom
                        diff.y < 0 -> drawRockFromStartToEnd { current.copy(y = current.y - 1) } // bottom to top
                    }
                }
            }
            return Pair(cave, minX)
        }

        private fun printCave(cave: List<List<CavePixel>>) {
            cave.forEach {
                println(it.joinToString("") {
                    when (it) {
                        CavePixel.VOID -> " "
                        CavePixel.ROCK -> "#" // alternative "\u2588"
                        CavePixel.STILL_SAND -> "."
                        CavePixel.FALLING_SAND -> "+"
                    }
                })
            }
        }
    }

    private enum class CavePixel { VOID, ROCK, STILL_SAND, FALLING_SAND }
}
