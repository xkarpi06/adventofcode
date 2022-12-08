package net.karpi.adventofcode.helpers

/**
 * Created by Nooi/blackmailcz on 01.12.2022
 */
class InputLoader(private val year: AoCYear) {

    fun loadInts2(resource: String): List<List<Int>> {
        return loadStrings(resource).map { it.map { it.code - 48 }}
    }

    fun loadInts(resource: String): List<Int> {
        return loadStrings(resource).map { it.toInt() }
    }

    fun loadStrings(resource: String): List<String> {
        return javaClass
            .getResource("/${year.resourcePath}/$resource")!!
            .readText()
            .lines()
    }

}
