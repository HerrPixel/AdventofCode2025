package day5

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.max
import kotlin.math.min

// Naive check for each ingredient if it is in any range by testing if it is mathematically in the bounds
fun part1(): Int {
    val (ranges, ingredients) = parseInput()

    var counter = 0

    for (i in ingredients) {
        for ((start, end) in ranges) {
            if (i in start..end) {
                counter += 1
                break
            }
        }
    }

    return counter
}

// First reduce the list of ranges to a list of disjoint ranges by combining intersecting ranges to a bigger one
// then get the number of elements in a range by (high - low + 1) and sum this number over all disjoint ranges
fun part2(): Long {
    val (ranges, _) = parseInput()
    val disjointRanges = disjointRanges(ranges)

    return disjointRanges.sumOf { (start, end) -> end - start + 1 }
}

fun disjointRanges(ranges: List<Pair<Long, Long>>): List<Pair<Long, Long>> {
    val disjointRanges = mutableListOf<Pair<Long, Long>>()

    val queue = ArrayDeque<Pair<Long, Long>>()

    for (range in ranges) {
        queue.addLast(range)
    }

    while (queue.isNotEmpty()) {
        val (start, end) = queue.removeFirst()

        val indexOfIntersection =
            disjointRanges.indexOfFirst { (low, high) -> low in start..end || high in start..end || start in low..high }

        // remove intersected range and add expanded range to the queue again
        if (indexOfIntersection != -1) {
            val (low, high) = disjointRanges.removeAt(indexOfIntersection)
            queue.addLast(Pair(min(low, start), max(high, end)))
            continue
        }

        disjointRanges.add(Pair(start, end))
    }

    return disjointRanges
}

fun parseInput(): Pair<List<Pair<Long, Long>>, List<Long>> {
    val (rangeString, ingredientString) = Path("src/day5/input.txt").readText().trim().split("\n\n")

    val ranges = rangeString.trim().lines().map { s ->
        val (a, b) = s.split("-", limit = 2)
        Pair(a.toLong(), b.toLong())
    }

    val ingredients = ingredientString.trim().lines().map { s -> s.toLong() }

    return Pair(ranges, ingredients)
}