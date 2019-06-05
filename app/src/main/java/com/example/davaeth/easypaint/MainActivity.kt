package com.example.davaeth.easypaint

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var background: Drawing
    private var permissions: MutableList<String> = mutableListOf<String>() // List of required permissions.

    private var drawing: Bitmap? = null

    private var hasFocus: Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeDrawingView()

        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        this.requestPermissions(permissions.toTypedArray(), 47) // Ask user for required permissions.

        try {
            text_drawingName.setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    saveDrawingFile()
                }
                false
            }
        } catch (e: Exception) {
            println(e.stackTrace)
        }

        //region MotionEvents
        background.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.background.Paths.add(Path())
                    this.background.Position += 1

                    if (this.background.Paints.count() < this.background.Paths.count())
                        setPaint(this.background.Paints[this.background.Position - 1].color)


                    this.background.StartX.add(motionEvent.x)
                    this.background.StartY.add(motionEvent.y)

                    this.background.moveDown(motionEvent.x, motionEvent.y)
                }

                MotionEvent.ACTION_MOVE -> {
                    this.background.moveForward(motionEvent.x, motionEvent.y)
                }

                MotionEvent.ACTION_UP -> {
                    this.background.EndX.add(motionEvent.x)
                    this.background.EndY.add(motionEvent.y)

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

        this.background.BackgroungColorToSave = Color.BLACK
        this.background.setBackgroundColor(Color.BLACK)
    }

    fun drawBlack(v: View) {
        setPaint(Color.BLACK)

        this.background.BackgroungColorToSave = Color.WHITE
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

    fun saveDrawing(v: View) {
        text_drawingName.visibility = View.VISIBLE

        try {
            text_drawingName.requestFocus().apply {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(text_drawingName, InputMethodManager.SHOW_IMPLICIT)
            }
        } catch (e: Exception) {
            println(e.stackTrace)
        }
    }

    fun clearScreen(v: View) {
        val intent: Intent = Intent(this, this::class.java)
        startActivity(intent)
    }

    //endregion

    private fun setPaint(paintColor: Int) {
        if (background.Paints.count() > this.background.Paths.count())
            background.Paints[background.Paints.lastIndex].color = paintColor
        else
            background.Paints.add(Paint().apply {
                color = paintColor
                style = Paint.Style.STROKE
                strokeWidth = 12f
                isAntiAlias = true
            })
    }

    private fun saveDrawingFile() {
        if (!background.createFileToSaveDrawing(text_drawingName)) {
            var ostream: FileOutputStream? = null

            background.createDrawingToSave()

            try {
                background.fileToSave.createNewFile()
                ostream = FileOutputStream(background.fileToSave)

                background.bitmap?.compress(Bitmap.CompressFormat.PNG, 100, ostream)

                ostream.flush()
                ostream.fd.sync();

                Toast.makeText(this, "Drawing saved!", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                println(e.printStackTrace())
            } finally {
                ostream?.close()

//                MediaScannerConnection.scanFile(this, String[] {background.fileToSave.absolutePath}, null, null)
                sendBroadcast(
                    Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://${text_drawingName.text}")
                    )
                )

            text_drawingName.setText("")
        }
    } else
    {
        Toast.makeText(this, "File exists!", Toast.LENGTH_LONG).show()
    }
}

private fun initializeDrawingView() {
    background = Drawing(this)
    mainLayout.addView(background)

    setPaint(Color.BLACK) // Starting paint color
}
}
