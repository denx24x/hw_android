package com.android_hw.hw4

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso


class FullImageActivity : AppCompatActivity() {

    private lateinit var imgFullImage: ImageView
    private val TAG = "FullImageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)
        imgFullImage = findViewById<View>(R.id.image) as ImageView
        val url = intent.getStringExtra("link")
        Picasso.get().load("http://213.189.221.170:8008/img/${url}").error(
            com.google.android.material.R.drawable.mtrl_ic_error).into(imgFullImage)
        imgFullImage.setOnClickListener{
            finish()
        }

    }
}