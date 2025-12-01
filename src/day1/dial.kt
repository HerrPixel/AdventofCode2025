package day1

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.absoluteValue

// Use the remainder of the distance by 100 and either add or subtract that from the current dial value
// If the result is zero, increment the counter
// Finally, return the dial value to the correct range by calculating the modulo remainder by 100
fun part1(): Int {
    val input = parseInput()

    var dial = 50
    var zeroCount = 0

    for (i in input) {
        dial = (dial + i).mod(100)
        if (dial == 0) {
            zeroCount++
        }
    }

    return zeroCount
}

// For each distance, we will do at least distance / 100 laps, so we can surely add that to the counter
// We will then do another lap, if the new dial value is outside the valid range from 1 to 99
// EXCEPT for when it already was at zero, then we don't count the additional lap
fun part2(): Int {
    val input = parseInput()

    var dial = 50
    var zeroCount = 0

    for (i in input) {
        // Guaranteed laps
        zeroCount += i.div(100).absoluteValue

        // Note that we only add i / 100, since we already counted the guaranteed laps above
        // Otherwise we would count 11 laps for an instruction like R1000
        // Also note the special case when dial == 0
        if (dial + i.rem(100) !in 1..99 && dial != 0) {
            zeroCount++
        }

        dial = (dial + i).mod(100)
    }

    return zeroCount
}

fun parseInput() =
    Path("src/day1/input.txt").readText().trim().lines().map { s ->
        val direction = s[0]
        val magnitude = s.substring(1)

        when (direction) {
            'L' -> -1 * magnitude.toInt()
            'R' -> magnitude.toInt()
            else -> 0
        }
    }
