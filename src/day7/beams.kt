package day7

import kotlin.io.path.Path
import kotlin.io.path.readText

// Simulate the Beams as a BFS row-wise with a HashSet to deduplicate beams overlapping
fun part1(): Int {
    val grid = parseInput()
    val (splits,_) = simulateBeams(grid)

    return splits
}

// Same BFS as Part1 but we now also store the number of beams that overlap in a HashMap now,
// Number of timelines is then the sum of the number of beams in each spot in the bottom row at the end
fun part2(): Long {
    val grid = parseInput()
    val (_,timelines) = simulateBeams(grid)

    return timelines
}

fun simulateBeams(grid: List<List<Char>>): Pair<Int,Long> {
    val queue = ArrayDeque<Pair<Int,Int>>()

    // beams per coordinate
    val nrOfBeams = mutableMapOf<Pair<Int,Int>,Long>()
    var nrOfSplits = 0
    val height = grid.size

    // Assume the 'S' is on the first line
    val startingCoordinate = grid.first().indexOfFirst{c -> c == 'S'}
    nrOfBeams[Pair(startingCoordinate,0)] = 1
    queue.addLast(Pair(startingCoordinate,0))

    // BFS-like simulation, add each new space into a queue, if it is not yet visited (not in the nrOfBeams map)
    while (queue.isNotEmpty()) {
        val (x,y) = queue.removeFirst()
        val multiplicity = nrOfBeams.getValue(Pair(x,y))

        // Beam exits the grid
        if (y + 1 >= height) {
            continue
        }

        when (grid[y+1][x]) {
            '^' -> {
                // Split the beam

                // If the new beam was already discovered, don't add it to the queue again
                if (nrOfBeams.containsKey(Pair(x - 1, y + 1)).not()) {
                    queue.addLast(Pair(x-1,y+1))
                }

                // If the new beam was already discovered, don't add it to the queue again
                if (nrOfBeams.containsKey(Pair(x+1,y+1)).not()) {
                    queue.addLast(Pair(x+1,y+1))
                }

                // Add the number of beams to the tally or introduce it, in case it is new
                nrOfBeams.merge(Pair(x-1,y+1),multiplicity,Long::plus)
                nrOfBeams.merge(Pair(x+1,y+1),multiplicity,Long::plus)
                nrOfSplits += 1
            }
            else -> {
                // Just go straight down, still check if that space was found before though
                if (nrOfBeams.containsKey(Pair(x,y+1)).not()) {
                    queue.addLast(Pair(x,y+1))
                }
                nrOfBeams.merge(Pair(x,y+1),multiplicity,Long::plus)
            }
        }
    }

    // nr of timelines = sum of different beams arriving in the bottom row
    val timelines = grid.first().indices.sumOf { index -> nrOfBeams.getOrDefault(Pair(index, height - 1), 0) }

    return Pair(nrOfSplits,timelines)
}

fun parseInput() =
    Path("src/day7/input.txt").readText().trim().lines().map{ s -> s.toList()}