package com.android_hw.hw8

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val image = findViewById<ImageView>(R.id.imageView)
        (AnimatorInflater.loadAnimator(this, R.animator.circle_animator) as AnimatorSet).apply {
            setTarget(image)
            start()
        }
        val customView = findViewById<CustomView>(R.id.CustomView)
        customView.animatorSet.start()
        customView.setOnClickListener{
            if(!customView.animatorSet.isPaused()){
                customView.animatorSet.pause()
            }else{
                customView.animatorSet.resume()
            }
        }
    }
}