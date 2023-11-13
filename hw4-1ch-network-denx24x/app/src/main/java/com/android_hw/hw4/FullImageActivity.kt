package com.android_hw.hw4

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.IOException


class FullImageActivity : AppCompatActivity() {

    private lateinit var imgFullImage: ImageView
    private val TAG = "FullImageActivity"
    private var bmp: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)
        imgFullImage = findViewById<View>(R.id.image) as ImageView

        val filepath = cacheDir.absolutePath + "/big_image"
        val file = File(filepath)
        Log.i(TAG, bmp.toString())

        try {
            bmp = BitmapFactory.decodeStream(FileInputStream(file))
            Log.i(TAG, "Image loaded in activity from $filepath")
        } catch (e: IOException) {
            Log.i(TAG, "Image failed to load in activity from cache $filepath")
            finish()
            return
        }
        imgFullImage.setImageBitmap(bmp)
        imgFullImage.setOnClickListener{
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(bmp != null) bmp!!.recycle()
    }

}