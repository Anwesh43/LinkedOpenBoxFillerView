package com.anwesh.uiprojects.openboxfillerview

/**
 * Created by anweshmishra on 17/09/20.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.graphics.RectF
import android.app.Activity

val colors : Array<Int> = arrayOf(
        "#F44336",
        "#4CAF50",
        "#FF5722",
        "#3F51B5",
        "#00BCD4"
).map({Color.parseColor(it)}).toTypedArray()
val lines : Int = 3
val parts : Int = lines + 1
val scGap : Float = 0.02f / (parts + 1)
val strokeFactor : Float = 90f
val sizeFactor : Float = 3.8f
val delay : Long = 20
val gapDeg : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawOpenBoxFiller(scale : Float, w : Float, h : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val size : Float = Math.min(w, h) / sizeFactor
    val sfLast : Float = sf.divideScale(parts - 1, parts + 1)
    save()
    translate(w / 2, h / 2)
    for (j in 0..(lines - 1)) {
        val sfj : Float = sf.divideScale(j, parts + 1)
        save()
        rotate(gapDeg * j)
        drawLine(size / 2, -size / 2, size / 2, -size / 2 + size * sfj, paint)
        restore()
    }
    val y : Float = size / 2 * (1f - sfLast)
    drawRect(RectF(-size / 2, y, size / 2, size / 2), paint)
    restore()
}

fun Canvas.drawOBFNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawOpenBoxFiller(scale, w, h, paint)
}

class OpenBoxFillerView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class OBFNode(var i : Int, val state : State = State()) {

        private var next : OBFNode? = null
        private var prev : OBFNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = OBFNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawOBFNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : OBFNode {
            var curr : OBFNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }
}