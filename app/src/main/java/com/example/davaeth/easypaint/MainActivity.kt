package com.example.davaeth.easypaint

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var background: Drawing
    private var permissions: MutableList<String> = mutableListOf<String>() // List of required permissions.

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        background = Drawing(this)
        mainLayout.addView(background)

        permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        setPaint(Color.BLACK) // Starting paint color

        this.requestPermissions(permissions.toTypedArray(), 47) // Ask user for required permissions.

        //region MotionEvents
        background.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.background.Paths.add(Path())
                    this.background.Position += 1

                    if (this.background.Paints.count() < this.background.Paths.count())
                        setPaint(this.background.Paints[this.background.Position - 1].color)


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
        saveDrawing()
    }

    fun drawGreen(v: View) {
        setPaint(Color.GREEN)
    }

    fun drawRed(v: View) {
        setPaint(Color.RED)

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            47 -> {
                if (grantResults.count() > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                )
                    saveDrawing()
                return
            }
        }
    }

    private fun saveDrawing() {
        background.createDrawingToSave()

        try {
            background.fileToSave.createNewFile()
            background.ostream = FileOutputStream(background.fileToSave)

            background.bitmap?.compress(Bitmap.CompressFormat.PNG, 100, background.ostream)

            background.ostream.flush()

            Toast.makeText(this, "Drawing saved!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            println(e.printStackTrace())
        } finally {
            background.ostream.close()
        }
    }
}
