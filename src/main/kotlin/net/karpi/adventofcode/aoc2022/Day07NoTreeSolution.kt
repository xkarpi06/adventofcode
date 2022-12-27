package net.karpi.adventofcode.aoc2022

import java.util.*

/**
 * Second solution to the Day 7 challenge.
 * This solution does not create a tree model of the filesystem, but only puts dirs into stack as they are entered (dirs
 * are popped as we step out of them) and for every file found, its size is added to all dirs inside this stack.
 */
class NTSInterpreter {
    val dirStack: Stack<NTSDirectory> = Stack()

    // add dirs to stack & for each file add its size to all directories in stack!
    val allDirs: MutableList<NTSDirectory> = mutableListOf()

    // navigate up, down, add file, ...
    fun readLine(line: Day07.CmdLine) {
        when (line) {
            is Day07.CmdLine.Cmd.CdIn -> {
                dirStack.push(NTSDirectory(line.dir))
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

    fun getDirs(): List<NTSDirectory> {
        return allDirs + dirStack
    }
}

class NTSDirectory(val name: String) {
    var size: Int = 0

    fun addFile(size: Int) {
        this.size += size
    }
}
