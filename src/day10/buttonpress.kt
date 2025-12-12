package day10

import kotlin.io.path.Path
import kotlin.io.path.readText

// There are 2^n valid button combinations, iterate over all of them
// and save the one that leads to the target state that has the fewest buttons pressed
fun part1(): Int {
    val input = parseInput()
    var moveSum = 0

    for ((target, buttons, _) in input) {
        moveSum += getMapOfValidCombinations(target.size,buttons)[target]!!.minOf { it.first }
    }

    return moveSum
}

// We first 'even' out the initial joltage counter by using the solution to Part1 and then solve the simpler problem
// with the remaining joltage levels divided by 2 and multiply the best solution there by 2. For a more in-depth
// explanation, see the comments in the bifurcation function.
// Thanks to u/tenthmascot on Reddit for finding this efficient solution!
fun part2(): Int {
    val input = parseInput()
    var moveSum = 0

    for ((_, buttons, joltages) in input) {
        val result = bifurcate(joltages,buttons,mutableMapOf(List(joltages.size) {0} to 0),getMapOfValidCombinations(joltages.size,buttons))
        moveSum += result

    }

    return moveSum
}

fun intToBinary(value: Int, length: Int): List<Boolean> =
    List(length) { i ->
        val shift = length - 1 - i
        ((value shr shift) and 1) == 1
    }

// Returns a Map, mapping a target state to any combination of buttons needed to press to get this target state
fun getMapOfValidCombinations(size: Int, buttons: List<List<Int>>): Map<List<Boolean>, MutableList<Pair<Int, IntArray>>> {
    val combinations = mutableMapOf<List<Boolean>, MutableList<Pair<Int, IntArray>>>()

    // For n buttons there are 2^n combinations of them. 2^n is the same as (1 << n)
    for (combinationIndex in 0..<(1 shl buttons.size)) {
        val counts = IntArray(size)

        // get binary representation of combination by integer representation
        val combination = intToBinary(combinationIndex,buttons.size)

        // get the effect on the counter displays of this button combination
        for (i in buttons.indices) {
            if (combination[i]) {
                for (bit in buttons[i]) {
                    counts[bit]++
                }
            }
        }

        // get binary effect of this combination
        val reducedCounts = counts.map { it % 2 == 1 }

        // Add this combination to the map of binary effects -> button combination
        val specificCombination = combinations.getOrPut(reducedCounts) {mutableListOf()}
        specificCombination.add(Pair(combination.filter { it }.size,counts))

    }

    return combinations
}

// For a list of joltages, each counter with an odd number, also gets invoked an odd number of times.
// Equivalently, an even counter gets invoked an even number of times. Since the final joltage level is 0 for all counters,
// Odd counters have to all be canceled out at some point. Since order does not matter, we first cancel out the odd counters
// and try out every combination that leaves a joltage list with only even values.
// For those even values, if any button would only be pressed once, we would find it in another solution to
// even out those initial values. This means that at least one of those combinations of evening-out-buttons leads
// to an optimal solution where for the left over even counter values, buttons are only pressed in sets of two.
// For those, we can solve the reduced problem:
// Divide the remaining joltage levels by 2,
// Solve this simpler problem
// Multiply the solution by 2
// Add the number of initial button that evens out the joltage levels
// Do this for every combination of buttons that evens out the initial joltage levels.
//
// These simpler subproblems after dividing by 2 can then be solved in the same way. By memoizing results for subproblems,
// the whole thing is very efficient.
fun bifurcate(joltages: List<Int>, buttons: List<List<Int>>, memoizedResults: MutableMap<List<Int>,Int>, validCombinations: Map<List<Boolean>, MutableList<Pair<Int, IntArray>>>): Int {
    // Already encountered this computation, return memoized result
    if (memoizedResults.containsKey(joltages)) {
        return memoizedResults[joltages]!!
    }

    // invalid subproblem, return invalid
    if (joltages.any { it < 0 }) {
        return Int.MAX_VALUE
    }

    val parity = joltages.map { it % 2 == 1 }
    var bestFound = Int.MAX_VALUE

    // For each combination that transforms this state to one with all even values, branch
    for ((size,counts) in validCombinations.getOrDefault(parity, listOf())) {

        // calculate the new joltage levels
        val newJoltage = joltages.withIndex().map { (i,v) -> (v - counts[i]).div(2)}

        val bestContinuation = bifurcate(newJoltage,buttons,memoizedResults,validCombinations)

        // if the branch is invalid or does not optimize our previous best result, throw it away
        if (bestContinuation == Int.MAX_VALUE || size + 2 * bestContinuation >= bestFound) {
            continue
        }

        bestFound = size + 2 * bestContinuation
    }

    // Memoize result
    memoizedResults[joltages] = bestFound

    return bestFound
}

fun parseInput() =
    Path("src/day10/input.txt").readText().trim().lines().map { s ->
        val lineComponents = s.split(" ")
        val target = lineComponents.first().removePrefix("[").removeSuffix("]").map { c ->
            when (c) {
                '#' -> true
                else -> false
            }
        }
        val joltages = lineComponents.last().removePrefix("{").removeSuffix("}").split(",").map { it.toInt() }
        val buttons = lineComponents.drop(1).dropLast(1)
            .map { s -> s.removePrefix("(").removeSuffix(")").split(",").map { it.toInt() } }

        Triple(target, buttons, joltages)
    }