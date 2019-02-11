package com.thorebenoit.enamel.kotlin.animations.keyframe

import com.thorebenoit.enamel.kotlin.animations.Interpolator
import com.thorebenoit.enamel.kotlin.animations.linearInterpolator
import com.thorebenoit.enamel.kotlin.core.ARGB_evaluate
import com.thorebenoit.enamel.kotlin.core.math.Scale
import com.thorebenoit.enamel.kotlin.core.math.f
import com.thorebenoit.enamel.kotlin.core.math.i
import com.thorebenoit.enamel.kotlin.geometry.primitives.EAngle
import com.thorebenoit.enamel.kotlin.geometry.primitives.degrees

interface PropertyInterpolator<T> {
    fun compute(fraction: Number, fromValue: T, toValue: T, interpolator: Interpolator = linearInterpolator): T
}

object AngleAnimator : PropertyInterpolator<EAngle> {
    override fun compute(fraction: Number, fromValue: EAngle, toValue: EAngle, interpolator: Interpolator): EAngle {
        return Scale.map(interpolator(fraction.f), 0f, 1f, fromValue.degrees, toValue.degrees).degrees()
    }
}

object FloatAnimator : PropertyInterpolator<Float> {
    override fun compute(fraction: Number, fromValue: Float, toValue: Float, interpolator: Interpolator): Float {
        return Scale.map(interpolator(fraction.f), 0f, 1f, fromValue, toValue)
    }
}

object IntAnimator : PropertyInterpolator<Int> {
    override fun compute(fraction: Number, fromValue: Int, toValue: Int, interpolator: Interpolator): Int {
        return Scale.map(interpolator(fraction.f), 0f, 1f, fromValue, toValue).i
    }
}

object ColorAnimator : PropertyInterpolator<Int> {
    override fun compute(fraction: Number, fromValue: Int, toValue: Int, interpolator: Interpolator): Int {
        return ARGB_evaluate(interpolator(fraction.f), fromValue, toValue)
    }
}
