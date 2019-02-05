package com.thorebenoit.enamel.processingtest

import com.thorebenoit.enamel.kotlin.core.f
import com.thorebenoit.enamel.kotlin.geometry.figures.ESize
import com.thorebenoit.enamel.kotlin.geometry.figures.ESizeImmutable
import com.thorebenoit.enamel.kotlin.geometry.primitives.EPoint
import com.thorebenoit.enamel.kotlin.geometry.primitives.EPointType
import com.thorebenoit.enamel.processingtest.examples.KeyFrameDemo
import processing.core.PApplet


object ProcessingTestMain {
    @JvmStatic
    fun main(args: Array<String>) {
        PApplet.main(KeyFrameDemo::class.java)
    }

}

/*
These function are used to help drawing only, they shouldn't be put in the library yet
 */
private operator fun ESizeImmutable.div(n: Number) = ESize(width / n.f, height / n.f)

private operator fun EPointType.div(n: Number) = EPoint(x / n.f, y / n.f)

