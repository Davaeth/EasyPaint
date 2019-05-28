package com.example.davaeth.easypaint

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var background: Drawing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        background = Drawing(this)
        mainLayout.addView(background)

        background.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.background.StartX = motionEvent.x
                    this.background.StartY = motionEvent.y

                    this.background.moveDown(motionEvent.x, motionEvent.y)
                }

                MotionEvent.ACTION_MOVE -> {
                    this.background.moveForward(motionEvent.x, motionEvent.y)
                }

                MotionEvent.ACTION_UP -> {
                    this.background.EndX = motionEvent.x
                    this.background.EndY = motionEvent.y

                    this.background.moveUp(motionEvent.x, motionEvent.y)
                }
            }
            background.invalidate()
            true
        }
    }

    class Drawing(context: Context) : View(context) {
        private var startX: Float = 0f
        private var startY: Float = 0f

        private var endX: Float = 0f
        private var endY: Float = 0f

        private val paint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 12f
            isAntiAlias = true
        }

        private var path: Path = Path()

        //region Properties
        var StartX: Float
            get() = this.startX
            set(value) {
                this.startX = value
            }

        var StartY: Float
            get() = this.startY
            set(value) {
                this.startY = value
            }

        var EndX: Float
            get() = this.endX
            set(value) {
                this.endX = value
            }

        var EndY: Float
            get() = this.endY
            set(value) {
                this.endY = value
            }

        val Path: Path
            get() = this.path
        //endregion

        override fun performClick(): Boolean {
            return super.performClick()
        }

        //region The "art" drawing methods

        /**
         * Method that sets the initial/start position of drawing.
         */
        fun moveDown(x: Float, y: Float) {
            this.Path.moveTo(x, y)
        }

        /**
         * Method that specifies the "road" of a drawing.
         */
        fun moveForward(startX: Float, startY: Float) {
            this.Path.quadTo(startX, startY, startX, startY)
        }

        /**
         * Method that sets the end position of drawing.
         */
        fun moveUp(x: Float, y: Float) {
            this.Path.lineTo(x, y)
        }

        //endregion

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            canvas.drawPath(this.Path, this.paint)

            // Draw circles at the start and end of touch
            // TODO("Create a new Paint object with dot setting and new path for dot so the dot won't be disappearing")
            canvas.drawCircle(StartX, StartY, 20f, paint)
            canvas.drawCircle(EndX, EndY, 20f, paint)
        }
    }
}
