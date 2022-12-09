package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 07.12.2022
 *
 * tried to only record stack of entered dirs and add size of read file to all of them
 * worked for example input, but not real input
 *
 * problem was using Map for processed directories, because there were duplicate names in the input...
 * yep... changed it for List & worked
 *
 * time: ~3 hod + 12 min
 *
 * stats:
 * Day       Time   Rank  Score       Time   Rank  Score
 *   7   15:37:51  52592      0   15:50:26  50615      0
 */
class Day07 {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day07Input")
            val cmdLines = Parser.parseInput(input)
            val interpreter = Interpreter()
            val interpreter2 = Interpreter2() // no tree solution
            // skip first instruction
            cmdLines.takeLast(input.size - 1).forEach {
                interpreter.readLine(it)
                interpreter2.readLine(it) // no tree solution
            }
            val tree = interpreter.getTree()
            tree.updateSize()
//            println(tree)

            val noTreeSolutionPart1 = interpreter2.getDirs().filter { it.size <= 100_000 }.sumOf { it.size }
            println("ntp1> $noTreeSolutionPart1")
            part1(tree)
            part2(tree)
        }

        private fun part1(tree: Dir) {
            val answer = tree.subdirsWithSize().filter { it.second <= 100_000 }.sumOf { it.second }
            println("p1> $answer")
        }

        private fun part2(tree: Dir) {
            val totalDiskSpace = 70_000_000
            val updateNeeds = 30_000_000
            val deleteAtLeast = updateNeeds - (totalDiskSpace - tree.size)
            val answer = tree.subdirsWithSize().filter { it.second > deleteAtLeast }.minByOrNull { it.second }?.second

            println("p2> $answer")
        }
    }

    private class Interpreter {
        private val root = Dir("/", null, 0)
        private var currentDir = root

        fun readLine(line: CmdLine) {
            when (line) {
                CmdLine.Cmd.Ls -> {}
                is CmdLine.Cmd.CdIn -> {
                    if (currentDir.hasSubDir(line.dir)) {
                        currentDir = currentDir.subDir(line.dir)!!
                    } else {
                        println("Invalid cd from ${currentDir.name} to ${line.dir}")
                    }
                }
                is CmdLine.Cmd.CdOut -> {
                    if (currentDir.parent != null) {
                        currentDir = currentDir.parent!!
                    } else {
                        println("Invalid cd .. at ${currentDir.name}")
                    }
                }
                is CmdLine.Answer.Dir -> {
                    currentDir.putDir(
                        Dir(name = line.name, parent = currentDir, depth = currentDir.depth + 1)
                    )
                }
                is CmdLine.Answer.File -> {
                    currentDir.putFile(
                        File(name = line.name, size = line.size, depth = currentDir.depth + 1)
                    )
                }
            }
        }

        fun getTree() = root
    }

    sealed class CmdLine {

        sealed class Cmd : CmdLine() {

            object Ls : Cmd() {
                override fun toString() = "Ls"
            }

            object CdOut : Cmd() {
                override fun toString() = "CdOut"
            }

            data class CdIn(val dir: String) : Cmd()
            object Unknown : Cmd()
        }

        sealed class Answer : CmdLine() {

            data class Dir(val name: String) : Answer()
            data class File(val name: String, val size: Int) : Answer()
        }
    }

    private class Parser {
        companion object {
            fun parseInput(input: List<String>): List<CmdLine> {
                return input.map { line ->
                    when {
                        line.startsWith("$") -> parseCmd(line)
                        line.startsWith("dir") -> CmdLine.Answer.Dir(line.split(" ")[1])
                        else -> parseFile(line)
                    }
                }
            }

            private fun parseFile(line: String): CmdLine.Answer.File {
                val (sizeStr, name) = line.split(" ")
                return CmdLine.Answer.File(name = name, size = sizeStr.toInt())
            }

            private fun parseCmd(line: String): CmdLine.Cmd {
                val parsed = line.split(" ")
                return when {
                    parsed[1] == "ls" -> CmdLine.Cmd.Ls
                    parsed[1] == "cd" && parsed[2] == ".." -> CmdLine.Cmd.CdOut
                    parsed[1] == "cd" -> CmdLine.Cmd.CdIn(parsed[2])
                    else -> CmdLine.Cmd.Unknown
                }
            }
        }
    }
}
