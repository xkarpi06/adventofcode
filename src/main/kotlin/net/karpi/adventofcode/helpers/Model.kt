package net.karpi.adventofcode.helpers

data class XY(val x: Int, val y: Int) {
    override fun toString() = "($x,$y)"
}

enum class Dir {
    UP, DOWN, LEFT, RIGHT;

    fun opposite() = when(this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }
}
