package net.karpi.adventofcode.aoc2022

import java.util.*

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
