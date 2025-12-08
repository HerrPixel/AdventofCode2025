package day8

import java.util.PriorityQueue
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.sqrt

data class DistancePair (
    val a: Int, // index of first point
    val b: Int, // index of second points
    val dist: Double
)

// Precompute every distance and store them in a min-heap, then extract the smallest one 1000 times.
// Use a Union-Find datastructure to test if two endpoints of a connection are already connected
// Then just iterate over the components and get the largest 3
fun part1(): Int {
    val points = parseInput()

    return kruskal(points,1000).first
}

// Full implementation of Kruskals algorithm, again with a min-heap for all distances and a Union-Find datastructure
fun part2(): Long {
    val points = parseInput()

    return kruskal(points,Int.MAX_VALUE).second
}

fun kruskal(points: List<Triple<Int,Int,Int>>, limit: Int): Pair<Int,Long> {
    val minHeap = PriorityQueue(compareBy<DistancePair> { it.dist })

    // Union-Find datastrcture, parent[i] stores the parent of the respective node in the datastructure
    // and the number of components under it in the tree
    // representatives are parents to themselves.
    val parents = MutableList(points.size) {i -> Pair(i,1)}

    // insert all distances into the minHeap
    for ((index1,p1) in points.withIndex()) {
        val (x1,y1,z1) = p1
        for ((index2,p2) in points.withIndex().drop(index1+1)) {
            val (x2,y2,z2) = p2

            val dx = (x1 - x2).toDouble()
            val dy = (y1 - y2).toDouble()
            val dz = (z1 - z2).toDouble()
            val dist = sqrt(dx * dx + dy * dy + dz * dz)

            minHeap.add(DistancePair(index1,index2,dist))
        }
    }

    var nrAttemptedConnections = 0
    var nrActualConnections = 0
    var lastConnectionScore: Long = 0

    // Main loop: Kruskals algorithm
    // take the lowest cost edge from the min-heap, check if the endpoints are in the same connected component
    // If not, connect them, otherwise, continue
    // The logic is done via a union-find datastructure
    while (nrAttemptedConnections < limit) {
        val (index1,index2,_) = minHeap.poll()
        nrAttemptedConnections += 1

        // Find representative of respective components
        var currIndex1 = index1
        while (currIndex1 != parents[currIndex1].first) {
            currIndex1 = parents[currIndex1].first
        }

        var currIndex2 = index2
        while (currIndex2 != parents[currIndex2].first) {
            currIndex2 = parents[currIndex2].first
        }

        // We do not implement path compression!
        // Due to the small input size, it only slowed it down

        val (representative1,size1) = parents[currIndex1]
        val (representative2,size2) = parents[currIndex2]

        // Same representative <=> same component, no need to do anything
        if (representative1 == representative2) {
            continue
        }

        // Attach smaller component to larger one
        if (size1 <= size2) {
            parents[representative1] = Pair(representative2,size1)
            parents[representative2] = Pair(representative2,size2 + size1)
        } else {
            parents[representative1] = Pair(representative1,size1 + size2)
            parents[representative2] = Pair(representative1,size2)
        }

        nrActualConnections += 1

        if (nrActualConnections == points.size - 1) {
            val (x1,_,_) = points[index1]
            val (x2,_,_) = points[index2]

            lastConnectionScore = x1.toLong() * x2.toLong()
            break
        }
    }

    // filter for only parents, which are parents to themselves, then sort them and get the largest three sizes out of their second component
    val componentSizes = parents.withIndex().filter { (i, pair) -> i == pair.first }.map{ (_,pair) -> pair.second}.sortedDescending()
    val componentSizeScore = componentSizes.getOrElse(0){1} * componentSizes.getOrElse(1){1} * componentSizes.getOrElse(2){1}
    return Pair(componentSizeScore,lastConnectionScore)
}

fun parseInput() =
    Path("src/day8/input.txt").readText().trim().lines().map{ s ->
        val (a,b,c) = s.split(",",limit=3).map { i -> i.toInt() }
        Triple(a,b,c)
    }