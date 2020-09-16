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
