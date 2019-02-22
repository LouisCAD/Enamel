package com.thorebenoit.enamel.processingtest.doodleclassifier

import com.thorebenoit.enamel.kotlin.ai.neurtalnetwork.NeuralNetwork
import com.thorebenoit.enamel.kotlin.core._2dec
import com.thorebenoit.enamel.kotlin.core.backingfield.ExtraValueHolder
import com.thorebenoit.enamel.kotlin.core.colorFromGray
import com.thorebenoit.enamel.kotlin.core.data.Grid
import com.thorebenoit.enamel.kotlin.core.math.f
import com.thorebenoit.enamel.kotlin.core.math.i
import com.thorebenoit.enamel.kotlin.core.data.split
import com.thorebenoit.enamel.kotlin.core.data.toGrid
import com.thorebenoit.enamel.kotlin.core.math.b
import com.thorebenoit.enamel.kotlin.core.math.d
import com.thorebenoit.enamel.kotlin.core.print
import com.thorebenoit.enamel.kotlin.core.time.ETimer
import com.thorebenoit.enamel.kotlin.geometry.figures.size
import com.thorebenoit.enamel.kotlin.geometry.primitives.point
import com.thorebenoit.enamel.kotlin.threading.forEachParallel
import com.thorebenoit.enamel.kotlin.threading.initParallelExecutors
import com.thorebenoit.enamel.processingtest.kotlinapplet.applet.PlaygroundApplet
import processing.core.PConstants
import processing.core.PImage
import java.io.File
import java.lang.Exception
import kotlin.experimental.and
import kotlin.math.roundToInt


object DoodleImageExtractor {

    fun getNetwork(objects: List<String>): NeuralNetwork {

        objects.forEach {
            ETimer.time {
                it.doodleList
            }.print
            println()
        }

        val training = objects
            .mapIndexed { index, s -> s.doodleList to index }
            .flatMap { (dataList, label) ->
                dataList.map {
                    LabeledData(it, label, objects)
                }
            }.shuffled()


        val nn = NeuralNetwork(training.first().data.size, 256, objects.size)

        println("training")
        val time = ETimer.time {
            training.forEach {
                nn.train(it.data, it.createVector())
            }
        }
        println("training done in $time ms")

        return nn
    }

    val imgWidth = 28
    val imgSize = imgWidth * imgWidth
    private val header = 80


    fun generate(path: String, nbImages: Int = 10) = generate(File(path), nbImages)
    fun generate(from: File, nbImages: Int = 10): List<List<Float>> {
        "Reading ${from.name}".print
        val imagesBytes =
            from.readBytes().toList().let { bytes ->
                bytes.subList(header, bytes.size).split(imgSize)
            }
        "${from.name} read".print


        val t = ETimer()
        var i = 0
        return imagesBytes.subList(0, nbImages)
            .map {
                it.map {
                    if (t.elapsed > 100) {
                        "${t.elapsed} ms on index $i".print
                    }
                    i++

                    val c = (it.toInt() and 0xFF) / 255f
                    t.start()
                    c
                }
            }

    }

    fun draw(
        images: List<List<Float>>,
        imagePerRow: Int = Math.sqrt(images.size.d).roundToInt()
    ) {
        val nbImages = images.size

        val images = images.mapIndexed { imageIndex, data ->

            val image = PImage(imgWidth, imgWidth, PConstants.RGB)

            image.loadPixels()
            for (i in 0 until imgSize) {
                image.pixels[i] = colorFromGray((data[i] * 255).toInt() and 0xFF)
            }
            image.updatePixels()

            image
        }

        val imagePerCol = nbImages / imagePerRow
        val grid = images.toGrid(rows = imagePerRow, cols = imagePerCol)

        PlaygroundApplet.start {
            esize = imgWidth * grid.cols size imgWidth * grid.rows
            windowLocation = 0 point 0

            onDraw {
                grid.forEach { (x, y, img) ->
                    image(img, x * imgWidth.f, y * imgWidth.f)
                }
            }
        }
    }
}

data class LabeledData<T>(val data: T, val label: Int, val labelList: List<Any>) {
    fun createVector() = (0 until labelList.size).mapIndexed { index, i -> if (label == index) 1 else 0 }
}

// https://console.cloud.google.com/storage/browser/quickdraw_dataset/full/numpy_bitmap
private fun main() {

    val objects = listOf("dog", "spoon")
    val trainingRatio = 0.99f

    objects.forEach {
        ETimer.time {
            it.doodleList
        }.print
        println()
    }

    val map = objects
        .mapIndexed { index, s -> s.doodleList to index }
        .flatMap { (dataList, label) ->
            dataList.map {
                LabeledData(it, label, objects)
            }
        }.shuffled()


    val training = map.subList(0, (map.size * trainingRatio).toInt())
    val testing = map.subList((map.size * trainingRatio).toInt(), map.size)


    val nn = NeuralNetwork(map.first().data.size, 256, objects.size)

    println("training")
    training.forEach {
        nn.train(it.data, it.createVector())
    }
    println("training done")

    var totalError = 0.0
    testing.forEach {
        val result = nn.feedForward(it.data)
        val error = nn.error(it.data, it.createVector())

        totalError += error
        println("Error ${error._2dec} with guess $result, expecting ${it.label}")
    }

    val avgError = (totalError / testing.size)
    println("Average error : $avgError")
}

val String.doodleList by ExtraValueHolder<String, List<List<Float>>> {
    DoodleImageExtractor.generate(
        "doodle/$this.npy",
        1_000
    )
}


private fun testSlowReading() {

    fun <E> List<E>.split(splitSize: Int): List<List<E>> {
        val ret = mutableListOf<List<E>>()
        for (i in 0 until size step splitSize) {
            if (i + splitSize <= size) {
                ret += subList(i, i + splitSize)
            } else {
                ret += subList(i, size)
            }
        }
        return ret
    }

    println("Reading file")
    val fileData = File("/Users/benoit/tmp/testdata.bin").readBytes().toList().let { bytes ->
        bytes.split(DoodleImageExtractor.imgSize)
    }

    fileData.size.print

    println("file read")

    var t: Long

    t = System.currentTimeMillis()
    fileData.map { it.map { it.toInt() } }
    println("It took ${System.currentTimeMillis() - t}")


    t = System.currentTimeMillis()
    fileData.map { it.map { it.toInt() } }
    println("It took ${System.currentTimeMillis() - t}")


    println("done")
}