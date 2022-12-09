package net.karpi.adventofcode.aoc2022

import java.util.*

class Interpreter2 {
    val dirStack: Stack<DirX> = Stack()

    // add dirs to stack & for each file add its size to all directories in stack!
    val allDirs: MutableList<DirX> = mutableListOf()

    // navigate up, down, add file, ...
    fun readLine(line: Day07.CmdLine) {
        when (line) {
            is Day07.CmdLine.Cmd.CdIn -> {
                dirStack.push(DirX(line.dir))
            }
            Day07.CmdLine.Cmd.CdOut -> {
                allDirs += dirStack.pop()
            }
            Day07.CmdLine.Cmd.Ls -> {}
            is Day07.CmdLine.Answer.Dir -> {}
            is Day07.CmdLine.Answer.File -> dirStack.forEachIndexed { i, it ->
                it.addFile(line.size)
            }
        }
    }

    fun getDirs(): List<DirX> {
        return allDirs + dirStack
    }
}

class DirX(val name: String) {
    var size: Int = 0

    fun addFile(size: Int) {
        this.size += size
    }
}
