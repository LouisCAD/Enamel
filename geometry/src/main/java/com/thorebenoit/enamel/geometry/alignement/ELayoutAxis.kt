package com.thorebenoit.enamel.geometry.alignement

import com.thorebenoit.enamel.geometry.figures.ESize
import com.thorebenoit.enamel.geometry.primitives.EOffset
import com.thorebenoit.enamel.geometry.primitives.times

enum class ELayoutAxis {
    vertical, horizontal
}

val ELayoutAxis.isVertical get() = this == ELayoutAxis.vertical


val ELayoutAxis.opposite
    get() = when (this) {
        ELayoutAxis.vertical -> ELayoutAxis.horizontal
        ELayoutAxis.horizontal -> ELayoutAxis.vertical
    }


val EAlignment.layoutAxis: ELayoutAxis?
    get() {
        if (isVertical) {
            return ELayoutAxis.vertical
        }
        if (isHorizontal) {
            return ELayoutAxis.horizontal
        }
        return null
    }

val ERectEdge.layoutAxis get() = if (isVertical) ELayoutAxis.vertical else ELayoutAxis.horizontal


fun EOffset.along(axis: ELayoutAxis): Float = when (axis) {
    ELayoutAxis.vertical -> vertical
    ELayoutAxis.horizontal -> horizontal
}