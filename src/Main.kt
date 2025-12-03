fun main() {
    // Each entry: (label, function1, function2)
    val solutions: List<Triple<String, () -> Any, () -> Any>> = listOf(
        Triple(
            "Secret Entrance",
            { day1.part1() },
            { day1.part2() }
        ),
        Triple(
            "Gift Shop",
            { day2.part1() },
            { day2.part2() }
        ),
        Triple(
            "Lobby",
            { day3.part1() },
            { day3.part2() }
        )
    )

    val start = System.nanoTime()

    for ((index, solution) in solutions.withIndex()) {
        val (title, part1, part2) = solution
        println("Day ${index + 1}: $title")

        val start1 = System.nanoTime()
        val result1 = part1()
        val end1 = System.nanoTime()
        val time1 = (end1 - start1)

        println("   Part 1: $result1")
        println("   Took: ${formatDuration(time1)}")
        println("")

        val start2 = System.nanoTime()
        val result2 = part2()
        val end2 = System.nanoTime()
        val time2 = (end2 - start2)

        println("   Part 2: $result2")
        println("   Took: ${formatDuration(time2)}")
        println("")
    }

    val end = System.nanoTime()
    val time = (end - start)

    println("Took ${formatDuration(time)} in total")
}

fun formatDuration(ns: Long): String {
    return when {
        ns >= 1_000_000_000 -> "${ns / 1_000_000_000.0}s"
        ns >= 1_000_000 -> "${ns / 1_000_000.0}ms"
        ns >= 1_000 -> "${ns / 1_000.0}µs"
        else -> "${ns}ns"
    }
}
