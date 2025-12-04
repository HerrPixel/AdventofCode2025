package day4

import kotlin.io.path.Path
import kotlin.io.path.readText

// Go through the grid once and check all valid neighbors spots for rolls, if the amount is less than 4, add +1 to the counter
fun part1(): Int {
    val grid = parseInput()
    val width = grid.size
    val height = grid[0].size

    var accessibleRolls = 0

    for (x in 0..<width) {
        for (y in 0..<height) {
            if (!grid[x][y]) {
                continue
            }

            // validNeighbors returns all in-bounds coordinates, we additionally filter only for those where rolls are present
            val number_of_neighbors = validNeighbors(x, y, width, height).filter { (a, b) -> grid[a][b] }.size

            if (number_of_neighbors < 4) {
                accessibleRolls += 1
            }
        }
    }

    return accessibleRolls
}

// Go through the whole grid initially and do the same checks as in Part1, whenever a roll gets removed, when add the 8 neighbors
// to a queue of spots that have changed. We then iteratively check a spot in the queue and remove it, if it is removable.
// Note that whenever a roll is removed, it could only aid in the removal of the 8 neighboring rolls, so this is enough.
fun part2(): Int {
    val grid = parseInput()
    val width = grid.size
    val height = grid[0].size

    val queue = ArrayDeque<Pair<Int, Int>>()
    var removedRolls = 0

    // Initial queue seeding
    // Could also remove all spots that are not initially rolls
    for (x in 0..<width) {
        for (y in 0..<height) {
            queue.addLast(Pair(x, y))
        }
    }

    while (queue.isNotEmpty()) {
        val (x, y) = queue.removeFirst()

        if (!grid[x][y]) {
            continue
        }

        val neighbors = validNeighbors(x, y, width, height).filter { (a, b) -> grid[a][b] }
        val numberOfNeighbors = neighbors.size

        // If roll is removable, do so and add the neighbors to the queue
        if (numberOfNeighbors < 4) {
            grid[x][y] = false
            removedRolls += 1

            for ((a, b) in neighbors) {
                queue.addLast(Pair(a, b))
            }
        }
    }

    return removedRolls
}

fun validNeighbors(x: Int, y: Int, width: Int, height: Int): List<Pair<Int, Int>> {
    return listOf(
        Pair(x - 1, y - 1),
        Pair(x - 1, y),
        Pair(x - 1, y + 1),
        Pair(x, y - 1),
        Pair(x, y + 1),
        Pair(x + 1, y - 1),
        Pair(x + 1, y),
        Pair(x + 1, y + 1)
    ).filter { (a, b) -> a in 0..<width && b in 0..<height }

}

fun parseInput() =
    Path("src/day4/input.txt").readText().trim().lines().map { s ->
        s.toList().map { c ->
            when (c) {
                '@' -> true
                else -> false
            }
        }.toMutableList()
    }