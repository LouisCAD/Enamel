package com.thorebenoit.enamel.kotlin.animations.keyframe

import com.thorebenoit.enamel.kotlin.animations.Interpolator
import com.thorebenoit.enamel.kotlin.animations.endInterpolator
import com.thorebenoit.enamel.kotlin.animations.linearInterpolator

interface Normalisable {
    val propertyList: List<List<FrameProperty<*>>>

    fun normalise(over: Float = 1f) {
        val max = propertyList.maxBy {
            it.maxBy { it.position }?.position ?: 0f
        }
                ?.maxBy { it.position }?.position ?: return

        propertyList.forEach { property: List<FrameProperty<*>> ->
            // it = x, y , alpha

            property.forEach {
                val newPosition = over * it.position / max
                it.position = newPosition
            }

        }
    }
}

class FrameAnimationBuilder<T>(val data: T) {

    var defaultInterpolator: Interpolator =
        linearInterpolator

    companion object {
        val ε = 0.001f

        inline fun <reified T> create(crossinline block: T.(FrameAnimationBuilder<T>) -> Unit) =
                FrameAnimationBuilder<T>(T::class.java.newInstance()).build(block)

        inline fun <reified T : Normalisable> createNormalised(over: Float = 1f, crossinline block: T.(FrameAnimationBuilder<T>) -> Unit) =
                FrameAnimationBuilder<T>(T::class.java.newInstance()).build(block).apply { normalise(over) }


    }

    var nextPosition: Float = 0f

    inline fun build(block: T.(FrameAnimationBuilder<T>) -> Unit): T {
        data.block(this)
        return data
    }

    inline fun initialFrame(crossinline block: FrameBuilder.() -> Unit) {
        val frameBuilder = FrameBuilder(-ε)
        frameBuilder.block()
        frameBuilder.build()
    }

    inline fun frameAfter(wait: Number, crossinline block: FrameBuilder.() -> Unit)//
            = frame(nextPosition + wait.toFloat(), block)

    inline fun frame(position: Number? = null, block: FrameBuilder.() -> Unit) {
        val position = position?.toFloat() ?: nextPosition

        val frameBuilder = FrameBuilder(position)
        frameBuilder.block()
        frameBuilder.build()

        nextPosition = if (position < 0) {
            0f
        } else {
            position.toInt() + 1f
        }
    }

    inner class FrameBuilder(val position: Float) {
        val frameData: MutableList<AnimPropBuilder<*>> = mutableListOf()

        fun build() {
            frameData.forEach {
                it.addAtPosition(position)
            }
        }

        fun <T> last(list: MutableList<FrameProperty<T>>): FrameProperty<T>? = list.maxBy { it.position }

        infix fun <T> MutableList<FrameProperty<T>>.lockSince(frame: FrameProperty<T>?) {
            frame?.position?.let { lockSince(it) }
        }

        // Don't let the user pick the frameIndex (he might be wrong)
        private fun <T> MutableList<FrameProperty<T>>.lockSince(frameIndex: Number) {
            val frameIndex = frameIndex.toFloat()
            forEach {
                if (it.position == frameIndex) {
                    set(it.data)
                    return
                }
            }
            System.err.println("Impossible to find frame with index $frameIndex")
        }

        infix fun <T> MutableList<FrameProperty<T>>.set(value: T) {
            this goto value by endInterpolator
        }

        infix fun <T> MutableList<FrameProperty<T>>.goto(value: T): AnimPropBuilder<T> {
            return AnimPropBuilder(value, this).apply { frameData.add(this) }
        }

        infix fun <T> MutableList<FrameProperty<T>>.reaches(value: T) = this goto value
    }

////////////////////////////
////////////////////////////
////////////////////////////

    inner class AnimPropBuilder<T>(val value: T, val targetList: MutableList<FrameProperty<T>>) {

        // TODO -> Don't use default interpolator
        /*
        If set to null, the interpolator should take the next available one when animating
        Only when animating, if no interpolator specified : Use linearInterpolator


        The goal is to be able to interpolate a field over several frame

        Currently the framework can only do 1 interpolation for 2 fields,
            it needs to be able to do it for x fields
         */
        var interpolator: Interpolator = defaultInterpolator
            private set

        infix fun by(interpolator: Interpolator): AnimPropBuilder<T> {
            this.interpolator = interpolator
            return this
        }

        fun addAtPosition(position: Float) {
            targetList.add(
                FrameProperty(
                    position,
                    value,
                    interpolator
                )
            )
        }
    }
}