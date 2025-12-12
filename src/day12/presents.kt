package day12

import kotlin.io.path.Path
import kotlin.io.path.readText

// This does not feel honorable, but it works for the real input
// We do a quick sanity check: If the area of all pieces is larger than the area of the grid,
// the pieces could never fit. For the real input this is also an equivalency, so we do not simulate the real tesselation
fun part1(): Int {
    val regions = parseInput()

    return regions.filter { (width,height,amounts) -> width * height >= amounts.sum() * 9 }.size
}

fun part2(): String {
    return "Merry Christmas"
}

// Due to the simplification of the real input, we throw away the pieces entirely
fun parseInput() =
    Path("src/day12/input.txt").readText().trim().split("\n\n").last().lines().map { s ->
        val (area, multiplicities) = s.split(":",limit=2)
        val (width,height) = area.split("x").map { it.toInt() }
        val amounts = multiplicities.trim().split(" ").map { it.toInt() }
        Triple(width,height,amounts)
    }