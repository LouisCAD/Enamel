package com.thorebenoit.enamel.android


fun randomColor() = colorHSL(Math.random().toFloat())

fun colorHSL(h: Number) = HSLToColor(floatArrayOf(h.toFloat() * 360, 1f, .5f)) or 0xFF000000.toInt()

private fun HSLToColor(hsl: FloatArray): Int {
    val h = hsl[0]
    val s = hsl[1]
    val l = hsl[2]

    val c = (1f - Math.abs(2 * l - 1f)) * s
    val m = l - 0.5f * c
    val x = c * (1f - Math.abs(h / 60f % 2f - 1f))

    val hueSegment = h.toInt() / 60

    var r = 0
    var g = 0
    var b = 0

    when (hueSegment) {
        0 -> {
            r = Math.round(255 * (c + m))
            g = Math.round(255 * (x + m))
            b = Math.round(255 * m)
        }
        1 -> {
            r = Math.round(255 * (x + m))
            g = Math.round(255 * (c + m))
            b = Math.round(255 * m)
        }
        2 -> {
            r = Math.round(255 * m)
            g = Math.round(255 * (c + m))
            b = Math.round(255 * (x + m))
        }
        3 -> {
            r = Math.round(255 * m)
            g = Math.round(255 * (x + m))
            b = Math.round(255 * (c + m))
        }
        4 -> {
            r = Math.round(255 * (x + m))
            g = Math.round(255 * m)
            b = Math.round(255 * (c + m))
        }
        5, 6 -> {
            r = Math.round(255 * (c + m))
            g = Math.round(255 * m)
            b = Math.round(255 * (x + m))
        }
    }

    r = r.constrain(0, 255).toInt()
    g = g.constrain(0, 255).toInt()
    b = b.constrain(0, 255).toInt()

    return rgb(r, g, b)
}

private fun Number.constrain(min: Number, max: Number): Float {
    val max = max.toFloat()
    val min = min.toFloat()
    val number = this.toFloat()
    return if (number < min) min else if (number > max) max else number
}


private fun rgb(
    red: Int,
    green: Int,
    blue: Int
): Int {
    return -0x1000000 or (red shl 16) or (green shl 8) or blue
}
