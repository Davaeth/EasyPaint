package com.example.davaeth.easypaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.view.View
import java.io.File
import java.io.FileOutputStream

class Drawing(context: Context) : View(context) {
    private var startX: Float = 0f
    private var startY: Float = 0f

    private var endX: Float = 0f
    private var endY: Float = 0f

    private var position: Int = -1

    //region Fields that save drawing in a picture directory.

    // Fields that create drawing to save.
    var bitmap: Bitmap? = null
    var view: View = this
    var drawable: Drawable? = this.background
    private lateinit var canvas: Canvas

    // Fields that actually save the drawing.
    private var fileToSavePath: String =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
    lateinit var fileToSave: File
    lateinit var ostream: FileOutputStream

    //endregion

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

        println("POSITION :: $position")
        println("PATH COUNT :: ${Paths.count()}")
        println("PAINT COUNT :: ${Paths.count()} \n\n")

        try {
            // Creating particular path with it particular paint set.
            for (i in 0..this.Paths.count() step 1) {
                canvas.drawPath(paths[i], paints[i])
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Error occurred: ${e.message}, because: ${e.cause}.")
        } finally {
            canvas.save()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createDrawingToSave() {
        fileToSave = File("$fileToSavePath/drawing${position+1}.png")

        bitmap = Bitmap.createBitmap(10000, 10000, Bitmap.Config.ARGB_8888, true)
        canvas = Canvas(bitmap!!)

//            if (drawable != null)
//                drawable?.draw(canvas)
//
//            draw(canvas)

        try {
            // Creating particular path with it particular paint set.
            for (i in 0..this.Paths.count() step 1) {
                canvas.drawPath(paths[i], paints[i])
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Error occurred: ${e.message}, because: ${e.cause}.")
        } finally {
//                canvas.drawBitmap(bitmap!!, \)
        }
    }
}