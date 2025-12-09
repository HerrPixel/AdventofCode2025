package day9

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

// Test each pair of point for rectangle size and keep the largest
// Not very efficient but the input size is small
fun part1(): Long {
    val points = parseInput()
    var max: Long = 0

    for ((index,p) in points.withIndex()) {
        val (x1,y1) = p
        for ((x2,y2) in points.drop(index+1)) {
            val area = ((x2-x1).absoluteValue + 1) * ((y2-y1).absoluteValue + 1)
            max = max(max,area)
        }
    }

    return max
}

// Test again each pair, additionally, test that the rectangle is inside the larger rectilinear polygon by
// checking if there is any edge that crosses the rectangle border. Specifics in the function below.
fun part2(): Long {
    val points = parseInput()
    var max: Long = 0

    for ((index,p) in points.withIndex()) {
        val (x1,y1) = p
        for ((x2,y2) in points.drop(index+1)) {
            val xMin = min(x1,x2)
            val xMax = max(x1,x2)

            val yMin = min(y1,y2)
            val yMax = max(y1,y2)

            val area = ((x2-x1).absoluteValue + 1) * ((y2-y1).absoluteValue + 1)
            if (area <= max) {
                continue
            }

            if (isRectangleInsideRectilinearPolygon(xMin,xMax,yMin,yMax,points)) {
                max = area
            }
        }
    }
    return max
}

// A rectangle given by the lower left and upper right corner is completely contained inside the larger rectilinear polygon,
// if there is no edge that crosses the rectangles border, i.e.
//     #--------
//     |
// #OOO|OOO#
// O   |   O
// O   #--------
// O       O
// #OOOOOOO#
// This is no longer completely contained.
// We can test this by checking if there is a polygon line in either the x or y interval of the rectangle and crossing in the other coordinate, i.e.
//    |   X   |
//    |       |
// ---#OOOOOOO#---
//    O       O
// Y  O       O  Y
//    O       O
// ---#OOOOOOO#---
//    |       |
//    |   X   |
// Any line contained in the Y or X strip and passing the rectangle lines.
// Passing the rectangle lines means that either the rectangle xMin, xMax or yMin,yMax respectively is in the interval
// given by the startingX...endingX, respectively startingY...endingY, interval.
//
// Note the edge case:
// ----#       |
//     |       |
//  #OO#-#OO#  |
//  O    |  O  |
//  O    #-----#
//  O       O
//  #OOOOOOO#
// Where one of the lines is directly on the rectangle edge
fun isRectangleInsideRectilinearPolygon(xMin: Long, xMax: Long, yMin: Long, yMax: Long,points: List<Pair<Long,Long>>): Boolean {
    for ((index,p) in points.withIndex()) {
        val (x,y) = p
        val (nextX,nextY) = points[(index + 1).mod(points.size)]

        // is in strip X
        if (x == nextX && x in xMin+1..<xMax) {
            val crossingYMin = min(y,nextY)
            val crossingYMax = max(y,nextY)

            // Crosses rectangle border
            if (yMin in crossingYMin..<crossingYMax || yMax in crossingYMin+1..crossingYMax) {
                return false
            }
        }

        // is in strip Y
        if (y == nextY && y in yMin+1..<yMax) {
            val crossingXMin = min(x,nextX)
            val crossingXMax = max(x,nextX)

            // Crosses rectangle border
            if (xMin in crossingXMin..<crossingXMax || xMax in crossingXMin+1..crossingXMax) {
                return false
            }
        }
    }

    return true
}

fun parseInput() =
    Path("src/day9/input.txt").readText().trim().lines().map{ s ->
        val (a,b) = s.split(",",limit=2).map { i -> i.toLong() }
        Pair(a,b)
    }