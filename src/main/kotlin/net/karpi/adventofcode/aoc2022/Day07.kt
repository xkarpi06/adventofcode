package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.aoc2022.helper.Dir
import net.karpi.adventofcode.aoc2022.helper.File
import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 07.12.2022
 *
 * tried to only record stack of entered dirs and add size of read file to all of them
 * worked for example input, but not real input
 * real input goes in and out of dirs repeatedly...
 *
 * time: 3 hod + 10 min
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
            part1(cmdLines)
//            part2(cmdLines)
        }

        private fun part1(input: List<CmdLine>) {
            val interpret = Interpreter()
            // skip first instruction
            input.takeLast(input.size - 1).forEach {
                interpret.readLine(it)
            }
            val tree = interpret.getTree()
            tree.updateSize()
//            println(tree)
            val allDirsWithSize = tree.subdirsWithSize()
            val targetDirs = allDirsWithSize.filter { it.second <= 100000 }
//            targetDirs.forEach { println(it) }
            val answer = targetDirs.sumOf { it.second }
            val rootSize = tree.size
            val totalDiskSpace = 70_000_000
            val updateNeeds = 30_000_000
            val deleteAtLeast = updateNeeds - (totalDiskSpace - rootSize)
            println("r: $rootSize, d: $deleteAtLeast")
            val answer2 = allDirsWithSize.filter { it.second > deleteAtLeast }.minByOrNull { it.second }?.second

            println("p1> $answer")
            println("p2> $answer2")
            // 1081027 is too low
        }

//        private fun part2(input: List<CmdLine>) {
//            var acc = 0
//            input.forEach {
//
//            }
//            println("p2> TODO ")
//        }
    }

    // skip first line
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

    private sealed class CmdLine {

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

//    val root = Dir("/", 0)
//    root.files.put("a", File("a", 12, root.depth + 1))
//    root.files.put("b", File("b", 13, root.depth + 1))
//    root.files.put("c", File("c", 14, root.depth + 1))
//    root.dirs.put("x", Dir("x", root.depth + 1))
//    root.dirs["x"]?.let { it.files.put("d", File("d", 15, it.depth + 1)) }
//    println(root)