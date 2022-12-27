package net.karpi.adventofcode.aoc2022

import net.karpi.adventofcode.helpers.AoCYear
import net.karpi.adventofcode.helpers.InputLoader

/**
 * Created by xkarpi06 on 07.12.2022
 *
 * Day 7: No Space Left On Device
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

        /**
         * You browse around the filesystem to assess the situation and save the resulting terminal output (your puzzle
         * input). For example:
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val input = InputLoader(AoCYear.AOC_2022).loadStrings("Day07Input")
            val cmdLines = Parser.parseInput(input)
            val interpreter = Interpreter()
            val ntsInterpreter = NTSInterpreter() // no tree solution
            // skip first instruction
            cmdLines.takeLast(input.size - 1).forEach {
                interpreter.readLine(it)
                ntsInterpreter.readLine(it)
            }
            val tree = interpreter.getTree()
            tree.updateSize()

            val ntsPart1 = ntsInterpreter.getDirs().filter { it.size <= 100_000 }.sumOf { it.size }
            println("ntsp1> $ntsPart1")
            part1(tree)
            part2(tree)
        }

        /**
         * Find all of the directories with a total size of at most 100000. What is the sum of the total sizes of those
         * directories?
         */
        private fun part1(tree: Directory) {
            val answer = tree.subDirsWithSize().filter { it.second <= 100_000 }.sumOf { it.second }
            println("p1> $answer")
        }

        /**
         * The total disk space available to the filesystem is 70000000. To run the update, you need unused space of at
         * least 30000000. Find the smallest directory that, if deleted, would free up enough space on the filesystem to
         * run the update. What is the total size of that directory?
         */
        private fun part2(tree: Directory) {
            val totalDiskSpace = 70_000_000
            val updateNeeds = 30_000_000
            val deleteAtLeast = updateNeeds - (totalDiskSpace - tree.size)
            val answer = tree.subDirsWithSize().filter { it.second > deleteAtLeast }.minByOrNull { it.second }?.second

            println("p2> $answer")
        }
    }

    private class Interpreter {
        private val root = Directory("/", null, 0)
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
                        Directory(name = line.name, parent = currentDir, depth = currentDir.depth + 1)
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

    /**
     * Represents directory tree
     */
    private class Directory(
        val name: String,
        val parent: Directory?, // null for root
        val depth: Int
    ) {
        private val subDirs: MutableMap<String, Directory> = mutableMapOf()
        private val files: MutableMap<String, File> = mutableMapOf()
        var size = 0
            private set

        fun putFile(file: File) {
            if (file.name !in files.keys) {
                files[file.name] = file
            } else {
                println("duplicate file ${file.name}")
            }
        }

        fun putDir(dir: Directory) {
            if (dir.name !in subDirs.keys) {
                subDirs[dir.name] = dir
            } else {
                println("duplicate dir ${dir.name}")
            }
        }

        fun hasSubDir(name: String) = subDirs[name] != null
        fun subDir(name: String) = subDirs[name]
        fun updateSize() {
            subDirs.values.forEach { it.updateSize() }
            size = files.values.sumOf { it.size } + subDirs.values.sumOf { it.size }
        }

        fun subDirsWithSize(): List<Pair<String, Int>> {
            val result = mutableListOf(name to size)
            subDirs.values.forEach { result += it.subDirsWithSize() }
            return result
        }

        override fun toString(): String {
            val spacing = List(depth) { "  " }.joinToString("")
            val xxx = if (size <= 100_000) "XXX" else ""
            val dirStr = "$spacing- $name (dir $xxx, size=$size)"
            return dirStr +
                    files.map { "\n${it.value}" }.joinToString("") +
                    subDirs.map { "\n${it.value}" }.joinToString("")
        }
    }

    private class File(val name: String, val size: Int, val depth: Int) {
        override fun toString(): String {
            val spacing = List(depth) { "  " }.joinToString("")
            return "$spacing- $name (file, size=$size)"
        }
    }
}
