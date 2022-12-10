package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 10.12.2022
 *
 * Day 10: Cathode-Ray Tube (CRT)
 *
 * time: 38 min + 25 min (start 8:02)
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *  10   02:40:10  15052      0   03:05:02  13357      0
 */
class Day10 {

    companion object {

        /**
         * It seems to be some kind of cathode-ray tube screen and simple CPU that are both driven by a precise clock
         * circuit. The clock circuit ticks at a constant rate; each tick is called a cycle. The CPU uses these
         * instructions in a program (your puzzle input) to, somehow, tell the screen what to draw.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day10Input")
            val instructions = parseInput(input)
            val cpu = CPU()
            instructions.forEach {
                cpu.execute(it)
            }
            part1(cpu)
            part2(cpu)
        }

        /**
         * Find the signal strength (the cycle number multiplied by the value of the X register) during the 20th, 60th,
         * 100th, 140th, 180th, and 220th cycles. What is the sum of these six signal strengths?
         */
        private fun part1(cpu: CPU) {
            val answer = cpu.signalStrengths.sum()
            println("p1> $answer")
        }

        /**
         * Render the image given by your program. What eight capital letters appear on your CRT?
         */
        private fun part2(cpu: CPU) {
            println("p2>")
            cpu.crtScreen.windowed(cpu.crtScreenWidth, cpu.crtScreenWidth).forEach {
                println(it)
            }
        }

        private fun parseInput(input: List<String>): List<CPU.Ins> {
            return input.map { line ->
                when {
                    line == "noop" -> CPU.Ins.Noop
                    else -> {
                        val x = line.split(" ").last().toInt()
                        CPU.Ins.Addx(x)
                    }
                }
            }
        }
    }

    private class CPU(val crtScreenWidth: Int = 40) {
        var crtScreen = ""
        private var register: Int = 1 // part2: middle of a 3-wide sprite
        private var cycleCount: Int = 0
        val signalStrengths = mutableListOf<Int>()
        private val significantCycles = listOf(20, 60, 100, 140, 180, 220)

        fun execute(ins: Ins) {
            repeat(ins.cycles) { cycleIncrement() }
            if (ins is Ins.Addx) register += ins.x
        }

        private fun cycleIncrement() {
            val (old, new) = listOf(cycleCount, cycleCount + 1)
            // part 1
            if (new in significantCycles) {
                val newStrength = register * new
                signalStrengths += newStrength
            }
            // part 2
            val crtPixel = if (old % crtScreenWidth in (register - 1)..(register + 1)) '#' else '.'
            crtScreen += crtPixel
            cycleCount = new
        }

        sealed class Ins {
            abstract val cycles: Int

            object Noop : Ins() {
                override val cycles: Int = 1
            }

            data class Addx(val x: Int) : Ins() {
                override val cycles: Int = 2
            }
        }
    }
}
