package net.karpi.adventofcode.aoc2022.helper

// prevents duplications
class Dir(
    val name: String,
    val parent: Dir?, // null for root
    val depth: Int
) {
    val subDirs: MutableMap<String, Dir> = mutableMapOf()
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

    fun putDir(dir: Dir) {
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

    fun subdirsWithSize(): List<Pair<String, Int>> {
        val result = mutableListOf(name to size)
        subDirs.values.forEach { result += it.subdirsWithSize() }
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

class File(val name: String, val size: Int, val depth: Int) {
    override fun toString(): String {
        val spacing = List(depth) { "  " }.joinToString("")
        return "$spacing- $name (file, size=$size)"
    }
}

//private class Interpret {
//    val dirStack: Stack<DirX> = Stack()
//    // add dirs to stack & for each file add its size to all directories in stack!
//    val allDirs: MutableMap<String, DirX> = mutableMapOf()
//    private var lastIsDuplicate: Boolean = false // TODO: delete probably
//
//    // navigate up, down, add file, ...
//    fun readLine(line: Day07.CmdLine) {
//        when (line) {
//            is Day07.CmdLine.Cmd.CdIn -> {
//                if (line.dir in allDirs.keys) {
//                    println("Duplication for ${line.dir}")
//                    lastIsDuplicate = true
//                } else {
//                    lastIsDuplicate = false
//                }
//                dirStack.push(DirX(line.dir))
//            }
//            Day07.CmdLine.Cmd.CdOut -> {
//                val dir = dirStack.pop()
//                allDirs.put(dir.name, dir)
//            }
//            Day07.CmdLine.Cmd.Ls -> {}
//            is Day07.CmdLine.Answer.Dir -> {}
//            is Day07.CmdLine.Answer.File -> dirStack.forEachIndexed { i, it ->
//                if (!lastIsDuplicate || !(i == dirStack.lastIndex)) {
//                    it.addFile(line.size)
//                }
//            }
//        }
//    }
//
//    fun getDirs(): Set<DirX> {
//        return allDirs.values.toSet() + dirStack.toSet()
//    }
//}