package com.thorebenoit.enamel.processingtest.examples

import com.thorebenoit.enamel.kotlin.animations.keyframe.KeyFrameAnimationExample
import com.thorebenoit.enamel.kotlin.animations.keyframe.animate
import com.thorebenoit.enamel.kotlin.animations.keyframe.animateColor
import com.thorebenoit.enamel.kotlin.core.f
import com.thorebenoit.enamel.kotlin.geometry.AllocationTracker
import com.thorebenoit.enamel.kotlin.geometry.allocate
import com.thorebenoit.enamel.kotlin.geometry.figures.size
import com.thorebenoit.enamel.kotlin.geometry.primitives.EPoint
import com.thorebenoit.enamel.kotlin.geometry.toCircle
import com.thorebenoit.enamel.processingtest.KotlinPApplet

class KeyFrameDemo : KotlinPApplet() {

    private val animationData = KeyFrameAnimationExample.keyFrameAnim

    init {
        AllocationTracker.debugAllocations = false
    }

    override fun settings() {
        super.settings()
        esize = allocate { 400 size 400 }

    }


    val startAt = System.currentTimeMillis()
    val duration = 10_000L
    val position = EPoint()
    override fun draw() {

        noStroke()
        background(255)

        val elapsed = System.currentTimeMillis() - startAt

        val progress = elapsed.f / duration.f


        animationData.apply {
            fill(color.animateColor(progress)!!)

            position.set(x.animate(progress)!!, y.animate(progress)!!)
            position.selfScale(width, height)
            position.toCircle(20).draw()
        }


    }


}
