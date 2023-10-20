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
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import java.io.Console
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


class Othello (context: Context, attrs: AttributeSet) : View(context, attrs), GestureDetector.OnGestureListener {
    private var mWidth : Float = 0.0f
    private var mHeight : Float = 0.0f
    private var gridSize : Float = 0.0f
    private var boxSize : Float = 0.0f

    private var turn : Int = 1

    private var mDetector = GestureDetectorCompat(this.context, this)

    var board = Array(9) { Array(9) {0} }

    var end: Boolean = false

    init {
        resetBoard()
    }

    fun resetBoard() {
        turn = 1
        end = false

        board = Array(9) { Array(9) {0} }

        // Initial pieces
        board[3][3] = 2
        board[3][4] = 1
        board[4][3] = 1
        board[4][4] = 2

        computeMoves()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val paint = Paint()

        // Make background box
        paint.color = Color.rgb(0, 175, 0)
        canvas.drawRect(0.0f, 0.0f, gridSize, gridSize, paint)

        // Make lines
        paint.color = Color.BLACK
        paint.strokeWidth = 3.0f

        // Horizontal
        for (horizontal in 0..8) {
            for (vertical in 0..8) {
                canvas.drawLine(0.0f, boxSize * horizontal, gridSize, boxSize * horizontal, paint) // horizontal
            }
        }

        // Vertical
        for (vertical in 0..8) {
            canvas.drawLine(boxSize * vertical, 0.0f, boxSize * vertical, gridSize, paint) // vertical
        }

        var value = 0

        // Draws pieces
        for (horizontal in 1..8) {
            for (vertical in 1..8) {
                value = board[vertical - 1][horizontal - 1]

                // If black piece
                if (value == 1) {
                    paint.color = Color.BLACK
                }
                // If white piece
                else if (value == 2) {
                    paint.color = Color.WHITE
                }
                // If black is able to place a piece there
                else if (value == 3) {
                    paint.color = Color.argb(150, 0, 0, 0)
                }
                // If white is able to place a piece there
                else if (value == 4) {
                    paint.color = Color.argb(175, 255, 255, 255)
                }
                // No pieces and can't place any
                else {
                    paint.color = Color.argb(0, 0, 0, 0)
                }

                canvas.drawCircle(boxSize * (horizontal - 0.5f), boxSize * (vertical - 0.5f), boxSize / 2.5f, paint)
            }
        }

        val mTextPaint = TextPaint()
        mTextPaint.color = Color.BLACK
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = boxSize * 0.55f;

        // New game button
        paint.color = Color.rgb(100, 100, 255)
        canvas.drawRect(boxSize * 0.5f, boxSize * 9.5f, boxSize * 3.5f, boxSize * 10.5f, paint)
        canvas.drawText("New Game", boxSize * 2.0f, boxSize * 10.15f, mTextPaint)

        mTextPaint.textSize = boxSize * 0.7f;

        // Displays who's turn it is
        canvas.drawText("Player " + turn.toString() + "'s turn", boxSize * 2.25f, boxSize * 8.75f, mTextPaint)

        mTextPaint.textSize = boxSize * 0.5f;

        // Displays scores
        canvas.drawText("P1: " + score(1).toString(), boxSize * 6.25f, boxSize * 8.75f, mTextPaint)
        canvas.drawText("P2: " + score(2).toString(), boxSize * 6.25f, boxSize * 9.75f, mTextPaint)

        // Win text
        if (end == true) {
            var player : Int = 1

            var score = score(player)

            if (score(2) > score) {
                player = 2
                score = score(player)
            }

            // In case of tie
            if (score(1) == score(2)) {
                mTextPaint.textSize = boxSize

                canvas.drawText("It's a tie!", boxSize * 4, boxSize * 1.5f, mTextPaint)

                mTextPaint.textSize = boxSize * 0.75f

                canvas.drawText("Score: " + score.toString(), boxSize * 4, boxSize * 2.5f, mTextPaint)

                return
            }

            mTextPaint.textSize = boxSize

            canvas.drawText("Player " + player.toString() + " wins!", boxSize * 4, boxSize * 1.5f, mTextPaint)

            mTextPaint.textSize = boxSize * 0.75f

            canvas.drawText("Score: " + score.toString(), boxSize * 4, boxSize * 2.5f, mTextPaint)
        }
    }

    // Calculates score
    fun score(p: Int) : Int {
        var score = 0

        for (horizontal in 0..8) {
            for (vertical in 0..8) {
                if (board[vertical][horizontal] == p) {
                    score++
                }
            }
        }

        return score
    }

    // Removes potential moves from the grid (called before calculating new moves)
    fun clearMoves() {
        for (horizontal in 0..8) {
            for (vertical in 0..8) {
                if (board[vertical][horizontal] == 3 || board[vertical][horizontal] == 4) {
                    board[vertical][horizontal] = 0
                }
            }
        }
    }

    // Primary game logic
    // Calculates available moves
    fun computeMoves() {
        end = true

        var otherPlayer = 1

        if (turn == 1) {
            otherPlayer = 2
        }

        // Game logic
        for (horizontal in 0..8) {
            for (vertical in 0..8) {
                // If the piece is one from the player who's turn it is
                if (board[vertical][horizontal] == turn) {
                    // Check for adjacent pieces of the other color
                    for (h in max((horizontal - 1), 0)..min((horizontal + 1), 7)) {
                        for (v in max((vertical - 1), 0)..min((vertical + 1), 7)) {
                            // If piece is other color
                            if (board[v][h] == otherPlayer) {
                                val hdif = h - horizontal
                                val vdif = v - vertical

                                if ((v + vdif) < 0 || (v + vdif) > 7 || (h + hdif) < 0 || (v + vdif) > 7) {
                                    continue
                                }

                                // If the spot one further in that direction is empty
                                if (board[v + vdif][h + hdif] == 0) {
                                    // It's a possible move
                                    board[v + vdif][h + hdif] = turn + 2
                                    end = false
                                }
                            }
                        }
                    }
                }
            }
        }

        end = !availableMoves()
    }

    fun availableMoves(): Boolean {
        for (horizontal in 0..8) {
            for (vertical in 0..8) {
                if (board[vertical][horizontal] == 3 || board[vertical][horizontal] == 4) {
                    return true
                }
            }
        }

        return false
    }

    // Logic for when a move is made
    fun place(row: Int, col: Int) {
        board[row][col] = turn

        var otherPlayer = 1

        if (turn == 1) {
            otherPlayer = 2
        }

        // Check for adjacent pieces of the other color
        for (h in max((col - 1), 0)..min((col + 1), 7)) {
            for (v in max((row - 1), 0)..min((row + 1), 7)) {
                // If piece is other color
                if (board[v][h] == otherPlayer) {
                    val hdif = h - col
                    val vdif = v - row

                    if ((v + vdif) < 0 || (v + vdif) > 7 || (h + hdif) < 0 || (v + vdif) > 7) {
                        return
                    }

                    // If the spot one further in that direction is current player's piece
                    if (board[v + vdif][h + hdif] == turn) {
                        // Capture piece
                        board[v][h] = turn
                    }
                }
            }
        }
    }

    // I copied this but its only a few lines of code so I hope that's ok?
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWidth = w.toFloat()
        mHeight = h.toFloat()

        gridSize = min(mHeight, mWidth)
        boxSize = gridSize / 8
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

        // If clicked new game button
        if (p0.x > boxSize * 0.5f && p0.x < boxSize * 3.5f && p0.y > boxSize * 9.5f && p0.y < boxSize * 10.5f) {
            resetBoard()
            invalidate()
            return true
        }

        if (p0.x > boxSize * 8 || p0.y > boxSize * 8) {
            return false
        }

        val value = board[row][col]

        if (turn == 1 && value == 3) {
            place(row, col)
            turn = 2

            clearMoves()
            computeMoves()
        }
        else if (turn == 2 && value == 4) {
            place(row, col)
            turn = 1

            clearMoves()
            computeMoves()
        }

        invalidate()

        return true
    }

    override fun onScroll(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean { return false }

    override fun onLongPress(p0: MotionEvent) { }

    override fun onFling(p0: MotionEvent, p1: MotionEvent, p2: Float, p3: Float): Boolean { return false }
}