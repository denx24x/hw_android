package com.hw_android.hw1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal
import java.math.MathContext
import java.util.*


class MainActivity : AppCompatActivity(), TextSetter {

    companion object {
        const val TAG = "MainActivity"
    }
    private lateinit var calculator : Calculator
    private lateinit var query : TextView

    override fun set(value : String){
        query.text = value
    }

    private fun copy() : Boolean {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("input", calculator.query)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(findViewById(R.id.main_layout), "Copied", Snackbar.LENGTH_SHORT).show()
        return true
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putString("Input", calculator.query)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.i(TAG, "onRestoreInstanceState")
        super.onRestoreInstanceState(savedInstanceState)
        calculator.query = savedInstanceState.getString("Input") ?: ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate")
        query = findViewById(R.id.query)

        calculator = Calculator(resources.getString(R.string.error_result), this)

        query.movementMethod = ScrollingMovementMethod()
        query.setOnLongClickListener{copy()}

        findViewById<Button>(R.id.button_0).setOnClickListener{calculator.addNumber("0")}
        findViewById<Button>(R.id.button_1).setOnClickListener{calculator.addNumber("1")}
        findViewById<Button>(R.id.button_2).setOnClickListener{calculator.addNumber("2")}
        findViewById<Button>(R.id.button_3).setOnClickListener{calculator.addNumber("3")}
        findViewById<Button>(R.id.button_4).setOnClickListener{calculator.addNumber("4")}
        findViewById<Button>(R.id.button_5).setOnClickListener{calculator.addNumber("5")}
        findViewById<Button>(R.id.button_6).setOnClickListener{calculator.addNumber("6")}
        findViewById<Button>(R.id.button_7).setOnClickListener{calculator.addNumber("7")}
        findViewById<Button>(R.id.button_8).setOnClickListener{calculator.addNumber("8")}
        findViewById<Button>(R.id.button_9).setOnClickListener{calculator.addNumber("9")}

        findViewById<Button>(R.id.button_sub).setOnClickListener{calculator.addOperation("-")}
        findViewById<Button>(R.id.button_mul).setOnClickListener{calculator.addOperation("*")}
        findViewById<Button>(R.id.button_add).setOnClickListener{calculator.addOperation("+")}
        findViewById<Button>(R.id.button_div).setOnClickListener{calculator.addOperation("/")}
        findViewById<Button>(R.id.button_point).setOnClickListener{calculator.addPoint()}
        findViewById<Button>(R.id.button_clear).setOnClickListener{calculator.clear()}
        findViewById<Button>(R.id.button_rem).setOnClickListener{calculator.rem()}
        findViewById<Button>(R.id.button_answer).setOnClickListener{calculator.calculate()}
    }
}