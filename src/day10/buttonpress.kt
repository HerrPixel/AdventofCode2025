package day10

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

// Test each pair of point for rectangle size and keep the largest
// Not very efficient but the input size is small
fun part1(): Int {
    val input = parseInput()
    var moveSum = 0

    for ((target, buttons, _) in input) {
        moveSum += configureMachine(target, buttons)
    }

    return moveSum
}

//result is 42
//result is 59
//result is 189
//result is 85
//result is 69
//result is 126
//result is 53
//result is 27
//result is 71
//result is 310
//result is 76
//result is 235
//result is 212
//result is 181
//result is 37
//result is 110
//result is 96
//result is 108
//result is 75
//result is 290
//result is 48
//result is 12
//result is 31
//result is 41
//result is 223
//result is 47
//result is 235
//result is 242
//result is 57
//result is 37
//result is 185
//result is 37
//result is 69
//result is 63
//result is 86
//result is 216
//result is 69
//result is 67
//result is 228
//result is 35
//result is 13
//result is 178
//result is 248
//result is 81
//result is 210
//result is 95
//result is 195
//result is 150
//result is 269
//result is 240
//result is 193
//result is 66
//result is 45
//result is 291
//result is 54
//result is 232
//result is 98
//result is 74
//result is 134
//result is 92
//result is 58
//result is 16
//result is 64
//result is 62
//result is 264
//result is 83
//result is 115
//result is 47
//result is 66
//result is 54
//result is 111
//result is 46
//result is 52
//result is 39
//result is 71
//result is 158
//result is 125
//result is 248
//result is 46
//result is 77
//result is 89
//result is 259
//result is 47
//result is 154
//result is 106
//result is 89
//result is 79
//result is 70
//result is 69
//result is 36
//result is 125
//result is 220
//result is 66
//result is 49
//result is 260
//result is 105
//result is 41
//result is 31
//result is 121
//result is 66
//result is 108
//result is 52
//result is 72
//result is 176
//result is 70
//result is 236
//result is 196
//result is 276
//result is 216
//result is 97
//result is 115
//result is 25
//result is 266
//result is 206
//result is 258
//result is 57
//result is 63
//result is 28
//result is 42
//result is 76
//result is 103
//result is 39
//result is 168
//result is 111
//result is 182
//result is 29
//result is 50
//result is 126
//result is 78
//result is 14
//result is 66
//result is 50
//result is 222
//result is 101
//result is 57
//result is 206
//result is 57
//result is 233
//result is 183
//result is 185
//result is 31
//result is 70
//result is 60
//result is 78
//result is 75
//result is 66
//result is 68
//result is 67
//result is 81
//result is 85
//result is 77
//result is 49
//result is 131
//result is 196
//result is 19
//result is 27
//result is 53
//result is 237
//result is 54
//result is 29
//result is 57
//result is 31
//result is 274
//result is 227
//result is 58
//result is 65
//result is 92
//result is 68
//result is 52
//result is 23
//result is 78
//result is 36
//result is 174
//result is 57
//result is 55
//result is 223
//result is 70
//result is 50
//result is 91
//result is 51
//result is 220
//result is 124
//result is 114
//result is 106
//result is 125
//result is 223
//result is 36
//result is 110
//result is 221
//result is 42
//result is 30
//result is 192
//result is 49
//result is 277
//result is 199
//result is 48
//result is 218
//result is 194
//result is 227
//result is 74
fun part2(): Int {
    val input = parseInput()
    var moveSum = 0

    for ((_, buttons, joltages) in input) {
        val start1 = System.nanoTime()
        val result = branchBound(List(buttons.size) { -1 }, joltages, Int.MAX_VALUE, buttons)
        val end1 = System.nanoTime()
        val time1 = (end1 - start1)
        println("result is $result in ${formatDuration(time1)}")
        moveSum += result

    }

    return moveSum
}

fun binaryToInt(bits: List<Boolean>): Int {
    var n = 0
    for (b in bits) {
        n = (n shl 1) or (if (b) 1 else 0)
    }
    return n
}

fun intToBinary(value: Int, length: Int): List<Boolean> =
    List(length) { i ->
        val shift = length - 1 - i
        ((value shr shift) and 1) == 1
    }

fun configureMachine(target: List<Boolean>, buttons: List<List<Int>>): Int {
    val queue = ArrayDeque<Pair<Int, Int>>()
    val set = HashSet<Int>()
    val targetInt = binaryToInt(target)
    val targetLength = target.size

    if (targetInt == 0) {
        return 0
    }

    queue.addLast(Pair(0, 0))
    set.add(0)

    while (queue.isNotEmpty()) {
        val (state, nrOfMoves) = queue.removeFirst()

        for (button in buttons) {
            val newStateList = intToBinary(state, targetLength).toMutableList()

            for (index in button) {
                newStateList[index] = !newStateList[index]
            }

            val newState = binaryToInt(newStateList)

            if (newState == targetInt) {
                return nrOfMoves + 1
            }

            if (set.contains(newState)) {
                continue
            }

            set.add(newState)
            queue.addLast(Pair(newState, nrOfMoves + 1))
        }

    }
    return -1
}

fun branchBound(state: List<Int>, remaining: List<Int>, currBest: Int, buttons: List<List<Int>>): Int {
    var currBest = currBest
    val currValue = state.sumOf { i -> (if (i == -1) 0 else i) }

    //println("trying state $state with remaining $remaining, current Best is $currBest")

    if (remaining.any { it < 0 }) {
        return currBest
    }

    if (currValue + remaining.max() > currBest) {
        return currBest
    }

    if (remaining.all { i -> i == 0 }) {
        return currValue
    }

    if (state.all { i -> i != -1 }) {
        return currBest
    }

    val unpressedButtons = buttons.withIndex().filter { state[it.index] == -1 }
    val unfinishedCounters = remaining.withIndex().filter { it.value != 0 }

    var mostRestricted = Pair(Int.MAX_VALUE,0)

    // If there is a counter for which no button can change it anymore, prune
    // If there is a counter that can only be changed by one button, press it
    for ((counterIndex,counterValue) in unfinishedCounters) {
        val validButtons = unpressedButtons.filter { it.value.contains(counterIndex) }

        if (validButtons.isEmpty()) {
            return currBest
        }

        if (validButtons.size == 1) {

            val newState = state.toMutableList()
            newState[validButtons.first().index] = counterValue

            val newRemaining = remaining.toMutableList()
            for (index in validButtons.first().value) {
                newRemaining[index] -= counterValue
            }

            return branchBound(newState, newRemaining, currBest, buttons)
        }

        if (validButtons.size < mostRestricted.first) {
            mostRestricted = Pair(validButtons.size,counterIndex)
        }
    }

    // If for any two counters, a and b, there are only buttons that change both a and b and they have a different value, prune
    // else, if there is exactly one such button, it must equalize them
    for ((index1,counter1) in unfinishedCounters.withIndex()) {
        for (counter2 in unfinishedCounters.drop(index1+1)) {
            if (counter1.value == counter2.value) {
                continue
            }

            val unbalancers = unpressedButtons.filter { it.value.contains(counter1.index).xor(it.value.contains(counter2.index)) }

            if (unbalancers.isEmpty()) {
                return currBest
            }

            if (unbalancers.size == 1) {
                val difference = (counter1.value - counter2.value).absoluteValue

                val newState = state.toMutableList()
                newState[unbalancers.first().index] = difference

                val newRemaining = remaining.toMutableList()
                for (index in unbalancers.first().value) {
                    newRemaining[index] -= difference
                }

                return branchBound(newState, newRemaining, currBest, buttons)
            }
        }
    }


    //
    val buttonIndex = buttons.withIndex().filter { state[it.index] == -1}.filter {it.value.contains(mostRestricted.second)}.minBy { it.value.size }.index
    //minBy { (_,button) ->
    //    remaining.withIndex().filter { button.contains(it.index) }.minOf { it.value }
    //}.index

    val maxPresses =
        remaining.withIndex().filter { (index, _) -> buttons[buttonIndex].contains(index) }.minOf { it.value }

    for (i in 0..maxPresses) {
        val newState = state.toMutableList()
        newState[buttonIndex] = maxPresses - i

        val newRemaining = remaining.toMutableList()
        for (index in buttons[buttonIndex]) {
            newRemaining[index] -= maxPresses - i
        }

        currBest = branchBound(newState, newRemaining, currBest, buttons)
    }


    return currBest
}

fun formatDuration(ns: Long): String {
    return when {
        ns >= 1_000_000_000 -> "${ns / 1_000_000_000.0}s"
        ns >= 1_000_000 -> "${ns / 1_000_000.0}ms"
        ns >= 1_000 -> "${ns / 1_000.0}µs"
        else -> "${ns}ns"
    }
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