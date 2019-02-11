package com.thorebenoit.enamel.kotlin.geometry.figures

import com.thorebenoit.enamel.kotlin.core.math.f
import com.thorebenoit.enamel.kotlin.core.math.i
import com.thorebenoit.enamel.kotlin.geometry.primitives.*

/*
TODO Make mutable/immutable version
TODO Make allocation free
 */
class XLine(val start: EPointType = EPointType.zero, val end: EPointType = EPointType.zero) {

    private fun Float.opposite() = 1f - this

    companion object {
        val unit = XLine(start = EPoint.zero, end = EPoint.unit)
        val zero = XLine(start = EPoint.zero, end = EPoint.zero)

        val START = 0f
        val END = 1f
    }

    val length
        get() = start.distanceTo(end).f
    val angle
        get() = start.angleTo(end)
    val x1
        get() = start.x
    val x2
        get() = end.x
    val y1
        get() = start.y
    val y2
        get() = end.y

    val linearFunction: ELinearFunction
        get() = run {
            val a = (end.y - start.y) / (end.x - start.x)
            // y = ax + b
            // So -> b  = y - ax
            val b = start.y - a * start.x
            ELinearFunction(a, b)
        }

    fun pointAt(at: Float): EPointType = start.offsetTowards(end, length * at)

    fun pointFrom(distance: Number, from: Float): EPointType {
        val opposite = pointAt(from.opposite())
        return opposite.offsetFrom(pointAt(from), distance)
    }

    fun pointTowards(distance: Number, towards: Float) =
        pointAt(towards.opposite()).offsetTowards(pointAt(towards), distance)

    fun extrapolateFrom(distance: Number, from: Float): EPointType {
        val fromPoint = pointAt(from.opposite())
        val towards = pointAt(from)
        val totalDistance = length + distance.f
        return fromPoint.offsetTowards(towards, totalDistance)
    }

    fun isParallel(other: XLine) = linearFunction.a == other.linearFunction.a

    fun center() = pointAt(0.5f)

    fun rotate(offsetAngle: EAngle, around: EPointType = center()): XLine {
        val newStart = start.rotateAround(offsetAngle, around)
        val newEnd = end.rotateAround(offsetAngle, around)
        return newStart line newEnd
    }

    fun expanded(distance: Number, from: Float): XLine {
        val start = pointAt(from.opposite())
        return start line extrapolateFrom(distance, from)
    }

    fun toListOfPoints(number: Int): List<EPointType> {
        return when (number) {
            0 -> emptyList()
            1 -> listOf(start)
            2 -> listOf(start, end)
            else -> {
                val distance = length.i
                val step = (distance / (number - 1)).i
                (0..distance step step).map { currentDistance ->
                    start.offsetTowards(end, currentDistance)
                }
            }
        }
    }

    fun perpendicularPointLeft(distanceFromLine: Number, distanceTowardsEndPoint: Number, towards: Float): EPointType {
        val x = pointTowards(distanceTowardsEndPoint, towards)
        return x.offsetAngle(angle = angle - 90.degrees(), distance = distanceFromLine)
    }

    fun perpendicularPointRight(distanceFromLine: Number, distanceTowardsEndPoint: Number, towards: Float) =
        perpendicularPointLeft(-distanceFromLine.f, distanceTowardsEndPoint, towards)

    fun perpendicular(
        distance: Number,
        towards: Float,
        leftLength: Number,
        rightLength: Number
    ): XLine {
        val start = perpendicularPointLeft(
            distanceFromLine = leftLength,
            distanceTowardsEndPoint = distance,
            towards = towards
        )
        val end = perpendicularPointRight(
            distanceFromLine = rightLength,
            distanceTowardsEndPoint = distance,
            towards = towards
        )

        return start line end
    }

    fun perpendicular(
        distance: Number,
        towards: Float,
        length: Number
    ) =
        perpendicular(
            distance = distance,
            towards = towards,
            leftLength = length.f / 2f,
            rightLength = length.f / 2f
        )

    fun perpendicular(at: Float, leftLength: Number, rightLength: Number): XLine {
        val offset = length * at
        return perpendicular(
            distance = offset,
            towards = 1f,
            leftLength = leftLength,
            rightLength = rightLength
        )
    }

    fun perpendicular(at: Float, length: Number) = perpendicular(
        at = at,
        leftLength = length.f / 2f,
        rightLength = length.f / 2f
    )

    fun offset(xOff: Number, yOff: Number) = start.offset(xOff, yOff) line end.offset(xOff, yOff)
    fun offset(p: EPointType) = offset(p.x, p.y)


}

infix fun EPointType.line(end: EPointType) = XLine(start = this, end = end)

/// CONVERT
fun List<EPointType>.toListOfLines(): List<XLine> {
    val ret = mutableListOf<XLine>()
    forEachIndexed { i, curr ->
        if (i > 1) {
            val prev = get(i - 1)
            ret.add(prev line curr)
        }
    }
    return ret
}


