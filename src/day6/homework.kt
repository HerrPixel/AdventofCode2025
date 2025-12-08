package day6

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.reflect.KFunction2

// Split at runs of whitespace and then calculate the i'th equation with the i'th numbers and i'th operation
fun part1(): Long {
    val (operations, numbers,_) = parseInput()
    var total: Long = 0

    for (i in operations.indices) {
        val column = numbers.map { l -> l[i] }

        total += column.reduce(operations[i])
    }

    return total
}

// Get the numbers by looking at the column lists and parse them that way. An empty column splits two equations
fun part2(): Long {
    val (operations, _,numbers) = parseInput()
    var total: Long = 0

    for ((operation,list) in operations.zip(numbers)) {
        total += list.reduce(operation)
    }

    return total
}

fun parseInput(): Triple<List<KFunction2<Long, Long, Long>>, List<List<Long>>, MutableList<List<Long>>> {
    val lines =  Path("src/day6/input.txt").readText().trimEnd().lines()

    // parse operations first, then we can remove that line
    val operations = lines.last().trim().split(Regex("\\s+")).map { op ->
        when (op) {
            "+" -> Long::plus
            "*" -> Long::times
            else -> Long::and
    }}

    // for part1, we can just split at any run of whitespace
    val part1Numbers = lines.dropLast(1).map { l -> l.trim().split(Regex("\\s+")).map { it.toLong() } }

    // for part2, we go column by column and split at any column with only whitespace,
    // this gives runs of column numbers -> inputs for each equation
    val indexRange = 0.rangeTo(lines.maxOf { it.length }) // first line may be too short, get the max column index by other methods
    // each index is mapped to a list containing the chars of that column
    val part2NumberStrings = indexRange.map { index -> lines.dropLast(1).map { it.getOrElse(index) { ' ' } }}

    val part2Numbers = mutableListOf<List<Long>>()
    var currList = mutableListOf<Long>()

    // map every column list to its number and split on empty columns
    for (l in part2NumberStrings) {
        if (l.all { it.isWhitespace() }) {
            part2Numbers.add(currList)
            currList = mutableListOf()
            continue
        }

        currList.add(l.joinToString(separator = "").trim().toLong())
    }

    return Triple(operations,part1Numbers,part2Numbers)
}