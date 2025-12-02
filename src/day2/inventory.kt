package day2

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.pow

// Looking at the first half of digits of the range borders gives us a range of possible repeated blocks
// Try every number in that range, repeat it and bound check it, then sum up all of those numbers.
fun part1(): Long {
    val input = parseInput()

    return input.sumOf { (a, b) -> getRepeatedNumbersInRange(a, b, 2).sum() }
}

// Looking at the first 1/nth of digits of the range borders gives us a range of possible repeated blocks for n repeats.
// Try every number in that range, repeat it and bound check it, then sum up all of those numbers.
// Note that we have to deduplicate the list here, since for example '2222' could be 2 blocks of '22' or 4 blocks of '2'.
fun part2(): Long {
    val input = parseInput()
    var sum: Long = 0

    for ((start, end) in input) {
        val digits = end.toString().length
        val numbers = mutableListOf<Long>()

        for (i in 2..digits) {
            numbers.addAll(getRepeatedNumbersInRange(start, end, i))
        }

        sum += numbers.distinct().sum()
    }
    return sum
}

// Get a list of numbers between start and end that are made up of repeats of digits
// We do this by getting a different range of number sequences that could be repeated and still be in the range.
// we then just try every number in that range, repeat it and check the bounds
fun getRepeatedNumbersInRange(start: Long, end: Long, repeats: Int): List<Long> {
    val numbers = mutableListOf<Long>()

    val startString = start.toString()
    val startStringLength = startString.length

    val endString = end.toString()
    val endStringLength = endString.length

    // If the number is exactly divisible by the number of repeat blocks, use the leading digits as a lower bound,
    // else use the first power of 10 that is possible
    val doubleStart = when (startStringLength % repeats) {
        0 -> {
            startString.take(startStringLength / repeats).toLong()
        }

        else -> {
            10.0.pow(startStringLength.floorDiv(repeats)).toLong()
        }
    }

    // If the number is exactly divisible by the number of repeat blocks, use the leading digits as an upper bound,
    // else use the last sequence of 9's still in the range
    val doubleEnd = when (endStringLength % repeats) {
        0 -> {
            endString.take(endStringLength / repeats).toLong()
        }

        else -> {
            10.0.pow(startStringLength.floorDiv(repeats)).toLong() - 1
        }
    }

    // Check that the number is in bounds of the original range
    for (i in doubleStart..doubleEnd) {
        val number = i.toString().repeat(repeats).toLong()

        if (number in start..end) {
            numbers.add(number)
        }
    }

    return numbers
}

fun parseInput() =
    Path("src/day2/input.txt").readText().trim().split(",").map { s -> s.split("-") }
        .map { a ->
            val start = a[0].toLong()
            val end = a[1].toLong()
            Pair(start, end)
        }

