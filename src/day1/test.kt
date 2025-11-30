package day1

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.absoluteValue

fun part1(): Int {
    val input = parseInput()
    val leftList = input.map { (a, _) -> a }.sorted()
    val rightList = input.map { (_, b) -> b }.sorted()
    val zippedList = leftList.zip(rightList)

    return zippedList.sumOf { (a, b) -> (a - b).absoluteValue }
}

fun part2(): Int {
    val input = parseInput()
    val leftList = input.map { (a, _) -> a }
    val rightList = input.map { (_, b) -> b }.groupingBy { it }.eachCount()

    return leftList.sumOf { a -> a * rightList.getOrDefault(a, 0) }
}

fun parseInput() =
    Path("src/day1/input.txt").readText().trim().lines().map { s -> s.split("   ").map { n -> n.toInt() } }
        .map { l ->
            val a = l[0]
            val b = l[1]
            Pair(a, b)
        }