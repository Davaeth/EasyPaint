package com.example.davaeth.easypaint

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Environment
import android.view.View
import android.widget.EditText
import android.widget.Toast
import java.io.File

class Drawing(context: Context) : View(context) {
    private var position: Int = -1

    private var backgroundColorToSave: Int = Color.WHITE

    var oldBitmap: Bitmap? = null

    //region Particular drawing

    // Fields that cover circle drawing.
    private var startX: MutableList<Float> = mutableListOf<Float>()
    private var startY: MutableList<Float> = mutableListOf<Float>()

    private var endX: MutableList<Float> = mutableListOf<Float>()
    private var endY: MutableList<Float> = mutableListOf<Float>()

    // Fields that cover path drawing.
    private var paints: MutableList<Paint> = mutableListOf<Paint>()
    private val paths: MutableList<Path> = mutableListOf<Path>()
    //endregion

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

    //endregion

    //region Properties
    var Position: Int
        get() = this.position
        set(value) {
            this.position = value
        }

    var BackgroungColorToSave: Int
        get() = this.backgroundColorToSave
        set(value) {
            this.backgroundColorToSave = value
        }

    var StartX: MutableList<Float>
        get() = this.startX
        set(value) {
            this.startX = value
        }

    var StartY: MutableList<Float>
        get() = this.startY
        set(value) {
            this.startY = value
        }

    var EndX: MutableList<Float>
        get() = this.endX
        set(value) {
            this.endX = value
        }

    var EndY: MutableList<Float>
        get() = this.endY
        set(value) {
            this.endY = value
        }

    val Paths: MutableList<Path>
        get() = this.paths

    val Paints: MutableList<Paint>
        get() = this.paints
    //endregion

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

    //region Save drawing methods
    fun createFileToSaveDrawing(drawingNameText: EditText): Boolean {
        return if (checkDrawingToSaveName(drawingNameText)) {
            fileToSave = File("$fileToSavePath/${drawingNameText.text.trim()}.png")

            if (fileToSave.exists()) {
                true
            } else {
                Toast.makeText(context, "Drawing saved!", Toast.LENGTH_SHORT).show()
                false
            }
        } else {
            Toast.makeText(context, "Name cannot be blank!", Toast.LENGTH_LONG).show()
            false
        }
    }

    fun createDrawingToSave() {
        val window: Rect = Rect()
        this.getWindowVisibleDisplayFrame(window)

        bitmap = Bitmap.createBitmap(window.width(), window.height(), Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap!!)
        canvas.drawColor(this.BackgroungColorToSave)

        try {
            // Creating particular path with it particular paint set.
            for (i in 0..this.Paths.count() step 1) {
                canvas.drawPath(paths[i], paints[i])
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Error occurred: ${e.message}, because: ${e.cause}.")
        }
    }

    private fun checkDrawingToSaveName(drawingNameText: EditText): Boolean {
        if (backgroundColorToSave == Color.WHITE) {
            drawingNameText.setTextColor(Color.BLACK)
            drawingNameText.setHintTextColor(Color.BLACK)
        } else {
            drawingNameText.setTextColor(Color.WHITE)
            drawingNameText.setHintTextColor(Color.WHITE)
        }

        return if (drawingNameText.text.isBlank()) {
            false
        } else {
            drawingNameText.visibility = GONE
            drawingNameText.clearFocus()

            true
        }
    }
    //endregion

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        try {
            // Creating particular path with it particular paint set.
            for (i in 0..this.Paths.count() step 1) {
                canvas.drawPath(paths[i], paints[i])
                canvas.drawCircle(startX[i], startY[i], 15f, paints[i])
                canvas.drawCircle(endX[i], endY[i], 15f, paints[i])
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Error occurred: ${e.message}, because: ${e.cause}.")
        } finally {
            canvas.save()
        }
    }

}