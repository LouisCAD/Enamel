package com.thorebenoit.enamel.kotlin.animations.keyframe

import com.thorebenoit.enamel.kotlin.animations.*
import com.thorebenoit.enamel.kotlin.core.math.Scale
import com.thorebenoit.enamel.kotlin.core.math.f
import com.thorebenoit.enamel.kotlin.geometry.primitives.EAngle

fun <T> MutableList<FrameProperty<T>>.normalized(over: Number = 1f): MutableList<FrameProperty<T>> {
    if (this.isEmpty()) {
        return this
    }
    val to = maxBy { it.position }!!.position
    val from = 0f

    val scale = Scale(listOf(from, to), listOf(0f, over))

    return asSequence().map {
        it.copy(position = scale[it.position].f)
    }.toMutableList()
}

fun MutableList<FrameProperty<Float>>.animate(fraction: Number) = animate(fraction,
    FloatAnimator
)
fun MutableList<FrameProperty<EAngle>>.animate(fraction: Number) = animate(fraction,
    AngleAnimator
)
fun MutableList<FrameProperty<Int>>.animate(fraction: Number) = animate(fraction,
    IntAnimator
)
fun MutableList<FrameProperty<Int>>.animateColor(fraction: Number) = animate(fraction,
    ColorAnimator
)

fun <T> MutableList<FrameProperty<T>>.animate(fraction: Number, animator: PropertyInterpolator<T>): T? {
    if (isEmpty()) {
        return null
    }
    val ratio = fraction.f

    // Check performance when sorting the second time (sorting should be fine on small lists anyway)
    sortBy { it.position }

    // Can't start the animation yet ? Go to first position
    val first = first()
    if (ratio < first.position) {
        return first.data
    }
    // Animation over ? Go to last postion
    val last = last()
    if (ratio > last.position) {
        return last.data
    }

    // If no element found, use the first one
    var next: FrameProperty<T> = first()
    var previous: FrameProperty<T>? = null

    kotlin.run loop@{
        forEachIndexed { i, it ->
            if (it.position >= ratio) {
                next = it
                if (i > 0) {
                    previous = get(i - 1)
                }
                return@loop
            }
        }
    }
    if (previous == null) {
        return next.data
    }
    val from = previous!!
    val to = next

    val fraction = Scale.map(fraction, from.position, to.position, 0, 1)
    return animator.compute(fraction,
            fromValue = from.data,
            toValue = to.data,
            interpolator = to.interpolator)
}

val ε = 0.00001f // Epsilon

data class FrameProperty<T>(var position: Float,
                            var data: T,
                            var interpolator: Interpolator
) {
    fun toMutable() = FrameProperty(position, data, interpolator)
    fun toImmutable() =
        ImmutableFrameProperty(position, data, interpolator)
}

data class ImmutableFrameProperty<T>(val position: Float,
                                     val data: T,
                                     val interpolator: Interpolator
) {
    fun toMutable() = FrameProperty(position, data, interpolator)
    fun toImmutable() =
        ImmutableFrameProperty(position, data, interpolator)
}

