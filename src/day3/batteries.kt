package day3

import kotlin.io.path.Path
import kotlin.io.path.readText

// It is always preferable to have the highest digit first, so we search for the highest number in the list
// except for the last index (to make sure we have a two-digit number)
// then search for the highest digit in the remaining list tail
fun part1(): Long {
    val input = parseInput()

    return input.sumOf { b -> highestDigitSubsequence(b, 2) }
}

// Similar to Part1, it is always preferable to have the highest digits first. To preserve the length of the numbers, 12,
// We never search in the last few indices to have enough digits left over to make a 12-digit number
// then we just iteratively repeat the procedure as in Part1
fun part2(): Long {
    val input = parseInput()

    return input.sumOf { b -> highestDigitSubsequence(b, 12) }
}

fun highestDigitSubsequence(bank: List<Int>, length: Int): Long {
    var sum: Long = 0
    var nextIndex = 0

    for (i in 1..length) {
        val (index, digit) = bank.withIndex().drop(nextIndex).dropLast(length - i).maxBy { (_, value) -> value }
        nextIndex = index + 1

        sum = sum * 10 + digit
    }
    return sum
}

fun parseInput() =
    Path("src/day3/input.txt").readText().trim().lines().map { s ->
        s.toList().map { i -> i.digitToInt() }
    }
