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


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var query : TextView

    private fun parseExpression(input : String) : BigDecimal {
        var pos = 0
        val values = Vector<BigDecimal>()
        val ops = Vector<Char>()
        var cur = ""

        while(pos <= input.length){

            if(pos == input.length || (isOperation(input[pos]) && !(pos > 0 && isOperation(input[pos - 1]) || pos == 0))){
                if(cur.isNotEmpty()){
                    values.add(cur.toBigDecimal())
                    if(ops.isNotEmpty()){
                        if(ops.last() == '*'){
                            values.add(values.removeLast().multiply(values.removeLast(), MathContext.DECIMAL64))
                            ops.removeLast()
                        }else if(ops.last() == '/'){
                            val v1 = values.removeLast()
                            val v2 = values.removeLast()
                            values.add(v2.divide(v1, MathContext.DECIMAL64))
                            ops.removeLast()
                        }
                    }
                    cur = ""
                }
                if (pos == input.length) break
                ops.add(input[pos])
            }else{
                cur += input[pos]
            }

            pos++
        }
        while (values.size > 1){
            val op = ops.removeLast()
            if(op == '+'){
                values.add(values.removeLast().add(values.removeLast(), MathContext.DECIMAL64))
            }else if(op == '-'){
                values.add(values.removeLast().negate().add(values.removeLast(), MathContext.DECIMAL64))
            }
        }
        return values.last()
    }

    private fun addNumber(num: String){
        Log.i(TAG, "addNumber: $num")
        query.append(num)
    }

    private fun isOperation(v : Char) : Boolean {
        return v in "+-*/"
    }

    private fun popLast(){
        Log.i(TAG, "popLast")
        query.text = query.text.dropLast(1)
    }

    private fun addOperation(op: String){
        Log.i(TAG, "addOperation: $op")
        if(query.text.isEmpty() && op != "-"|| op == "-" && query.text.isNotEmpty() && query.text.last() == '-'){
            return
        }
        if(query.text.length > 1 && isOperation(query.text.last()) && isOperation(query.text[query.length() - 2])){
            return
        }
        if(query.text.isNotEmpty() && isOperation(query.text.last()) && op != "-"){
            popLast()
        }
        query.append(op)
    }

    private fun addPoint(){
        Log.i(TAG, "addPoint")
        var pos = query.text.lastIndexOf('.') + 1
        var can = pos == 0
        while (pos != query.text.length){
            if(!query.text[pos].isDigit()){
                can = true
            }
            pos++
        }
        if(!can){
            return
        }
        if(query.text.isEmpty() || !query.text.last().isDigit()){
            query.append("0.")
        }else if(query.text.last().isDigit()){
            query.append(".")
        }
    }


    fun rem(){
        Log.i(TAG, "rem")
        if(query.text.isEmpty()){
            return
        }
        popLast()
    }

    private fun calculate(){
        try {
            query.text = parseExpression(query.text.toString()).stripTrailingZeros().toPlainString()
        }catch (e : Throwable){
            query.text = getString(R.string.error_result)
        }
    }

    private fun copy() : Boolean {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("input",query.text)
        clipboard.setPrimaryClip(clip)
        Snackbar.make(findViewById(R.id.main_layout), "Copied", Snackbar.LENGTH_SHORT).show()
        return true
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putString("Input", query.text.toString())
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.i(TAG, "onRestoreInstanceState")
        super.onRestoreInstanceState(savedInstanceState)
        query.text = savedInstanceState.getString("Input")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate")
        query = findViewById(R.id.textView3)
        query.movementMethod = ScrollingMovementMethod()
        query.setOnLongClickListener{copy()}

        findViewById<Button>(R.id.button_0).setOnClickListener{addNumber("0")}
        findViewById<Button>(R.id.button_1).setOnClickListener{addNumber("1")}
        findViewById<Button>(R.id.button_2).setOnClickListener{addNumber("2")}
        findViewById<Button>(R.id.button_3).setOnClickListener{addNumber("3")}
        findViewById<Button>(R.id.button_4).setOnClickListener{addNumber("4")}
        findViewById<Button>(R.id.button_5).setOnClickListener{addNumber("5")}
        findViewById<Button>(R.id.button_6).setOnClickListener{addNumber("6")}
        findViewById<Button>(R.id.button_7).setOnClickListener{addNumber("7")}
        findViewById<Button>(R.id.button_8).setOnClickListener{addNumber("8")}
        findViewById<Button>(R.id.button_9).setOnClickListener{addNumber("9")}

        findViewById<Button>(R.id.button_sub).setOnClickListener{addOperation("-")}
        findViewById<Button>(R.id.button_mul).setOnClickListener{addOperation("*")}
        findViewById<Button>(R.id.button_add).setOnClickListener{addOperation("+")}
        findViewById<Button>(R.id.button_div).setOnClickListener{addOperation("/")}
        findViewById<Button>(R.id.button_point).setOnClickListener{addPoint()}
        findViewById<Button>(R.id.button_clear).setOnClickListener{query.text = ""}
        findViewById<Button>(R.id.button_rem).setOnClickListener{rem()}
        findViewById<Button>(R.id.button_answer).setOnClickListener{calculate()}
    }
}