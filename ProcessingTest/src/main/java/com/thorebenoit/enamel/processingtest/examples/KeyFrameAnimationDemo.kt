package com.thorebenoit.enamel.processingtest.examples

import com.thorebenoit.enamel.kotlin.animations.keyframe.KeyFrameAnimationExample
import com.thorebenoit.enamel.kotlin.animations.keyframe.animate
import com.thorebenoit.enamel.kotlin.animations.keyframe.animateColor
import com.thorebenoit.enamel.kotlin.core.math.f
import com.thorebenoit.enamel.kotlin.core.print
import com.thorebenoit.enamel.kotlin.core.time.ETimerAnimator
import com.thorebenoit.enamel.kotlin.geometry.AllocationTracker
import com.thorebenoit.enamel.kotlin.geometry.allocate
import com.thorebenoit.enamel.kotlin.geometry.figures.size
import com.thorebenoit.enamel.kotlin.geometry.primitives.EPoint
import com.thorebenoit.enamel.kotlin.geometry.toCircle
import com.thorebenoit.enamel.processingtest.KotlinPApplet
import processing.core.PConstants

class KeyFrameDemo : KotlinPApplet() {

    private val animationData = KeyFrameAnimationExample.keyFrameAnim

    init {
        AllocationTracker.debugAllocations = false
    }

    override fun settings() {
        super.settings()
        esize = allocate { 400 size 400 }

        onMouseMoved {

        }

    }


    override fun setup() {
        frame.isResizable = true
    }

    val animator = ETimerAnimator().apply { duration = 10_000L }
    val position = EPoint()
    override fun draw() {

        if (mousePressed) {
            val p = (mouseX.f / width)
            kotlin.io.println(p)
            animator.progress = p
        }

        background(255)

        val drawingFrame = eframe.inset(50)

        noStroke()

        val progress = animator.progress


        animationData.apply {
            fill(color.animateColor(progress)!!)

            position.set(x.animate(progress)!!, y.animate(progress)!!)

            position.selfScale(drawingFrame.width, drawingFrame.height)
            position.selfOffset(drawingFrame.origin)

            position.toCircle(radius.animate(progress)!!).draw()
        }

        if (progress > 1f) {
            animator.progress = 0f
        }


    }


}
