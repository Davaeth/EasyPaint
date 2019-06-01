package com.example.davaeth.easypaint

import android.content.Context
import android.graphics.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Attributes

class MainActivity : AppCompatActivity() {

    private lateinit var background: Drawing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        background = Drawing(this)
        mainLayout.addView(background)

        setPaint(Color.BLACK)

        //region MotionEvents
        background.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.background.Paths.add(Path())
                    this.background.Position += 1

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
        //endregion
    }

    //region Button listeners
    fun drawWhite(v: View) {
        setPaint(Color.WHITE)

        this.background.setBackgroundColor(Color.BLACK)
    }

    fun drawBlack(v: View) {
        setPaint(Color.BLACK)

        this.background.setBackgroundColor(Color.WHITE)
    }

    fun drawBlue(v: View) {
        setPaint(Color.BLUE)
    }

    fun drawGreen(v: View) {
        setPaint(Color.GREEN)
    }

    fun drawRed(v: View) {
        setPaint(Color.RED)

    }
    //endregion

    private fun setPaint(paintColor: Int) {
        background.Paints.add(Paint().apply {
            color = paintColor
            style = Paint.Style.STROKE
            strokeWidth = 12f
            isAntiAlias = true
        })
    }

    class Drawing(context: Context) : View(context) {
        private var startX: Float = 0f
        private var startY: Float = 0f

        private var endX: Float = 0f
        private var endY: Float = 0f

        private var position: Int = -1

        private var bitmap: Bitmap? = null

        //region Particular drawing
        private var paints: MutableList<Paint> = mutableListOf<Paint>()
        private val paths: MutableList<Path> = mutableListOf<Path>()
//        private val pathPaint: MutableMap<Int, Int> = mutableMapOf<Int, Int>()

        //endregion

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

        var Position: Int
            get() = this.position
            set(value) {
                this.position = value
            }

        val Paths: MutableList<Path>
            get() = this.paths

        val Paints: MutableList<Paint>
            get() = this.paints
        //endregion

        init {
            bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        }

        override fun performClick(): Boolean {
            return super.performClick()
        }

        //region The "art" drawing methods

        /**
         * Method that sets the initial/start position of drawing.
         */
        fun moveDown(x: Float, y: Float) {
            this.Paths[position].moveTo(x, y)
        }

        /**
         * Method that specifies the "road" of a drawing.
         */
        fun moveForward(startX: Float, startY: Float) {
            this.Paths[position].quadTo(startX, startY, startX, startY)
        }


        /**
         * Method that sets the end position of drawing.
         */
        fun moveUp(x: Float, y: Float) {
            this.Paths[position].lineTo(x, y)
        }


        //endregion

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawBitmap(this.bitmap!!, 100f, 0f, null)

            println("POSITION :: $position")
            println("PATH COUNT :: ${Paths.count()}")
            println("PAINT COUNT :: ${Paths.count()} \n\n")

            try {
                // Creating particular path with it particular paint set.
                for(i in 0..this.Paths.count() step 1) {
                    canvas.drawPath(paths[i], paints[i])
                }
            } catch (e: IndexOutOfBoundsException) {
                println("Error occurred: ${e.message}")
            } finally {
                canvas.save()
            }
        }
    }
}
