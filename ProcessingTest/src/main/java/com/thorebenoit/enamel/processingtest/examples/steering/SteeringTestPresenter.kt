package com.thorebenoit.enamel.processingtest.examples.steering

import com.thorebenoit.enamel.kotlin.core.math.randomSign
import com.thorebenoit.enamel.kotlin.geometry.figures.ERectType
import com.thorebenoit.enamel.kotlin.geometry.figures.ESizeType
import com.thorebenoit.enamel.kotlin.geometry.primitives.EPointType
import com.thorebenoit.enamel.kotlin.geometry.primitives.RandomPoint
import com.thorebenoit.enamel.kotlin.geometry.primitives.plus
import com.thorebenoit.enamel.kotlin.geometry.primitives.point
import com.thorebenoit.enamel.kotlin.geometry.toRect
import com.thorebenoit.enamel.kotlin.physics.physicsLoop

interface DotDrawer {
    fun update()
    var dotList: List<SteeringVehicle>
    val size: ESizeType
    val mousePosition: EPointType
    var onMouseClicked: () -> Unit
    var onScroll: (Int) -> Unit
    var constraintFrame: ERectType
    var mouseRadius : Float

}

val DotDrawer.center: EPointType get() = with(size) { width / 2 point height / 2 }

//class SteeringTestPresenter(val view: DotDrawer) {
class SteeringTestPresenter(val view: DotDrawer) {


    // synchronized not working
    private val _dotList =
        mutableListOf<SteeringVehicle>()//Collections.synchronizedCollection(mutableListOf<SteeringVehicle>())

    var mouseRadius = 100f
        set(value) {
            field = value
            view.mouseRadius = value
        }

    init {
        _dotList.addAll(MutableList(1) { SteeringVehicle(view.center + RandomPoint(0)) })

        view.onMouseClicked = {

            synchronized(_dotList) {
                //                _dotList.removeAll { it.isOnMouse() }
                _dotList += SteeringVehicle(view.mousePosition.toMutable()).apply {
                    body.velocity.set(randomSign() * maxVelocity, randomSign() * maxVelocity)
                }
                view.dotList = _dotList.toList()
            }

        }

        view.onScroll = {
            mouseRadius += it
            val minRadius = 5f
            if (mouseRadius < minRadius) {
                mouseRadius = minRadius
            }
        }

        view.dotList = _dotList.toList()
        view.mouseRadius = mouseRadius
        startLoop()


    }

    private fun SteeringVehicle.isOnMouse() = view.mousePosition.distanceTo(this.position) < (mouseRadius + this.radius)

    private fun startLoop() {
        val frame = view.size.toRect().selfInset(200)
        view.constraintFrame = frame
        physicsLoop { deltaTime ->

            val mousePosition = view.mousePosition


            synchronized(_dotList) {
                _dotList.forEach { dot ->
                    dot.apply {

                        if (!frame.contains(position, radius)) {
                            controller.withTorqueMult(10f) {
                                controller.steerInside(frame)
                            }
                        }

                        _dotList.forEach {
                            if (it != dot && it.position.distanceTo(dot.position) < radius) {
                                controller.steerAway(it.position)
                            }
                        }

                        if (dot.isOnMouse()) {
                            controller.withTorqueMult(5f) {
                                controller.steerAway(mousePosition)
                            }
                        }

                    }


                }

                // </constraintFrame>

                _dotList.forEach { it.body.update(deltaTime) }
            }



            view.update()
        }
    }

}