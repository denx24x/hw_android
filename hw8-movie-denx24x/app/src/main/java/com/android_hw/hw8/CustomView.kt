package com.android_hw.hw8

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf

private class Arc(val start: Float, val sweep: Float)

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val rect: RectF = RectF(0f, 0f, 0f, 0f)

    private var startAngle = 0f
    private var currentAngle = 360f

    var animatorSet = AnimatorSet()
    //lateinit var animator1 : ValueAnimator
    lateinit var animator2 : ValueAnimator


    private val paint: Paint = Paint()
    private var arcs = emptyList<Arc>()

    private var arcColor : Int
    private val count: Int

    init {
        val a: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.CustomView, defStyleAttr, defStyleRes)
        try {
            count = a.getInt(R.styleable.CustomView_arcCount, 13)
            arcColor = a.getColor(R.styleable.CustomView_arcColor, ContextCompat.getColor(context, R.color.blue))

        } finally {
            a.recycle()
        }

        paint.style = Paint.Style.FILL
        paint.color = arcColor
        computeArcs()
        prepareAnimation()
    }

    private fun computeArcs() {
        val sweep = 360f / (count * 2)
        val buff = mutableListOf<Arc>()
        for(index in 0..count * 2){
            buff.add(Arc(index * sweep * 2, sweep))
        }
        arcs = buff
    }

    private fun prepareAnimation() {
        /*
        animator1 = ValueAnimator.ofFloat(0f, 360f, 0f).apply {
            duration = 2000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            //repeatMode = ValueAnimator.REVERSE
            addUpdateListener { valueAnimator ->
                currentAngle = valueAnimator.animatedValue as Float
                invalidate()
            }
        }*/
        animator2 = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 9000
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            //repeatMode = ValueAnimator.RESTART
            addUpdateListener { valueAnimator ->
                startAngle = (valueAnimator.animatedValue as Float) % 360
                invalidate()
            }
        }
        animatorSet.playTogether(animator2)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(startAngle, rect.width() / 2, rect.height() / 2)
        arcs.forEach { arc ->
            if (currentAngle < arc.start) {
                return
            }
            canvas.drawArc(rect.left, rect.top, rect.right, rect.bottom - rect.height() * 0.25f,
                0f,
                if (currentAngle  < arc.start + arc.sweep) currentAngle - arc.start else arc.sweep,
                true,
                paint)
            canvas.rotate(360f / count, rect.width() / 2, rect.height() / 2)
        }
        canvas.restore()
    }

    override fun onSaveInstanceState() : Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superstate", super.onSaveInstanceState())
        bundle.putFloat("start", startAngle)
        //bundle.putFloat("current", currentAngle)
        return bundle
    }

    override fun onRestoreInstanceState(state : Parcelable) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(bundle.getParcelable("superstate"))
        startAngle = bundle.getFloat("start")
        //currentAngle = bundle.getFloat("current")
        //animator1.setFloatValues(currentAngle, currentAngle + 360, currentAngle)
        animator2.setFloatValues(startAngle, startAngle + 360)
    }
}
