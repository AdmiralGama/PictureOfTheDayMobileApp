package com.coslund.lazycalc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import org.w3c.dom.Text
import kotlin.math.floor
import kotlin.math.min

class NumberGrid (context: Context, attrs: AttributeSet) : View(context, attrs), GestureDetector.OnGestureListener {
    private var mWidth : Float = 0.0f
    private var mHeight : Float = 0.0f
    private var gridSize : Float = 0.0f
    private var boxSize : Float = 0.0f

    private var mDetector = GestureDetectorCompat(this.context, this)

    var values = Array(9) { Array(9) {0} }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //values[0][0] = 1

        val paint = Paint()

        // Make background box
        paint.color = Color.WHITE
        canvas.drawRect(0.0f, 0.0f, gridSize, gridSize, paint)

        // Make lines
        paint.color = Color.BLACK
        paint.strokeWidth = 3.0f

        // Horizontal
        for (horizontal in 0..9) {
            for (vertical in 0..9) {
                canvas.drawLine(0.0f, boxSize * horizontal, gridSize, boxSize * horizontal, paint) // horizontal
            }
        }

        // Vertical
        for (vertical in 0..9) {
            canvas.drawLine(boxSize * vertical, 0.0f, boxSize * vertical, gridSize, paint) // vertical
        }

        // Make zeros
        val mTextPaint = TextPaint()
        mTextPaint.color = Color.BLACK
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = boxSize;

        for (horizontal in 1..9) {
            for (vertical in 1..9) {
                canvas.drawText(values[vertical - 1][horizontal - 1].toString(), boxSize * (horizontal - 0.5f), boxSize * (vertical - 0.15f), mTextPaint)
                //canvas.drawText("0", boxSize * (horizontal - 0.5f), boxSize * (vertical - 0.15f), mTextPaint)
            }
        }
    }

    // I copied this but its only a few lines of code so I hope that's ok?
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWidth = w.toFloat()
        mHeight = h.toFloat()

        gridSize = min(mHeight, mWidth)
        boxSize = gridSize / 9
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(mDetector.onTouchEvent(event)) {
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDown(p0: MotionEvent): Boolean { return true }

    override fun onShowPress(p0: MotionEvent) { }

    override fun onSingleTapUp(p0: MotionEvent): Boolean {
        val col = floor(p0.x / boxSize).toInt()
        val row = floor(p0.y / boxSize).toInt()

        if (col >= 9 || row >= 9) { return false }

        if (values[row][col] == 9) {
            values[row][col] = 0
        }
        else {
            values[row][col] = values[row][col] + 1
        }

        invalidate()

        return true
    }

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean { return false }

    override fun onLongPress(p0: MotionEvent) { }

    override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean { return false }
}