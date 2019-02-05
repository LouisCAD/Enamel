package com.thorebenoit.enamel.kotlin.animations.keyframe

import com.thorebenoit.enamel.kotlin.animations.EasingInterpolators
import com.thorebenoit.enamel.kotlin.core.i
import com.thorebenoit.enamel.kotlin.core.percent

private object Example {

    private val red = 0xFF_FF_00_00.i
    private val black = 0xFF_00_00_00.i
    private val blue = 0xFF_00_00_FF.i
    private val green = 0xFF_00_FF_00.i
    private val yellow = 0xFF_FF_FF_00.i

    fun test() {
        keyFrameAnim.color.animateColor(.5)
    }

    data class AlphaXY(
        val x: MutableList<FrameProperty<Float>> = mutableListOf(),
        val y: MutableList<FrameProperty<Float>> = mutableListOf(),
        val alpha: MutableList<FrameProperty<Float>> = mutableListOf(),
        val color: MutableList<FrameProperty<Int>> = mutableListOf(),
        val color2: MutableList<FrameProperty<Int>> = mutableListOf()
    ) : Normalisable {
        override var propertyList: List<List<FrameProperty<*>>> = mutableListOf(
            x,
            y,
            alpha,
            color,
            color2
        )

    }

    val keyFrameAnim = FrameAnimationBuilder.createNormalised<AlphaXY> {
        with(it) {

            frame {
                x set 0.percent
                y set 0.percent
                alpha set 100.percent
                color set red
                color2 set black
            }

            frame {
                color goto blue
                x goto 100.percent by EasingInterpolators.accelerate(3)
                y goto 100.percent by EasingInterpolators.accelerate()
            }

            frame {
                color goto green
                x goto 50.percent by EasingInterpolators.deccelerate()
                y goto 50.percent by EasingInterpolators.deccelerate(3)
                alpha lockSince last(alpha)
            }

            frameAfter(.5) {
                color goto blue
            }

            frame {
                color goto yellow
                x goto 0.percent by EasingInterpolators.quadInOut
                y goto 100.percent by EasingInterpolators.quadInOut
            }

            frame {
                x goto 10.percent by EasingInterpolators.accelerate(6)
            }

            frame {
                color goto 0xFF_5592FF.i
                x goto 50.percent by EasingInterpolators.deccelerate(3)
                y goto 50.percent by EasingInterpolators.deccelerate(3)
            }

        }
    }
}
