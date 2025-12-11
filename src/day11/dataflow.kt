package day11

import kotlin.io.path.Path
import kotlin.io.path.readText

// The input graph is oriented and loop-free, so a simple Depth-first search while saving the results is enough
fun part1(): Long {
    val connections = parseInput()
    val paths = connections.keys.associateWith { (-1).toLong() }.toMutableMap()
    paths["out"] = 1
    return DFS("you",connections, paths)
}

// We modify the depth-first search to search on the graph multiplied by 4 for each combination visits of DAC and FFT.
// I.e. each node is now (node,hasVisitedDAC,hasVisitedFFT) with the latter two being booleans.
fun part2(): Long {
    val connections = parseInput()
    val paths = connections.keys.flatMap { name -> listOf(
        Triple(name,false,false),
        Triple(name,false,true),
        Triple(name,true,false),
        Triple(name,true,true),
        )}.associateWith {(-1).toLong() }.toMutableMap()
    paths[Triple("out",false,false)] = 0
    paths[Triple("out",false,true)] = 0
    paths[Triple("out",true,false)] = 0
    paths[Triple("out",true,true)] = 1
    return DFSwithSightseeing("svr",connections, paths, visitedDAC = false, visitedFFT = false)
}

// Each node sums the number of paths from its neighbors, if they are not computed yet, recurse into them and compute them first
fun DFS(node: String, connections: Map<String,List<String>>, paths: MutableMap<String,Long>): Long {
    var nrOfPaths: Long = 0
    for (neighbor in connections[node]!!) {
        if (paths[neighbor]!! < 0) {
            DFS(neighbor,connections,paths)
        }
        nrOfPaths += paths[neighbor]!!
    }

    paths[node] = nrOfPaths

    return nrOfPaths
}

// Same thing as above but with the additional booleans denoting the visit state of the current path
fun DFSwithSightseeing(node: String, connections: Map<String, List<String>>, paths: MutableMap<Triple<String, Boolean, Boolean>,Long>, visitedDAC: Boolean, visitedFFT: Boolean): Long {
    var nrOfPaths: Long = 0
    val visitedDACAfterwards = visitedDAC || (node == "dac")
    val visitedFFTAfterwards = visitedFFT || (node == "fft")

    for (neighbor in connections[node]!!) {
        if (paths[Triple(neighbor,visitedDACAfterwards,visitedFFTAfterwards)]!! < 0) {
            DFSwithSightseeing(neighbor,connections,paths,visitedDACAfterwards,visitedFFTAfterwards)
        }
        nrOfPaths += paths[Triple(neighbor,visitedDACAfterwards,visitedFFTAfterwards)]!!
    }

    paths[Triple(node,visitedDAC,visitedFFT)] = nrOfPaths

    return nrOfPaths
}

fun parseInput() =
    Path("src/day11/input.txt").readText().trim().lines().associate { s ->
        val (name, neighborString) = s.split(":", limit = 2)
        val neighbors = neighborString.trim().split(" ")
        Pair(name, neighbors)
    }