package com.hw_android.hw1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import java.math.BigDecimal
import java.math.MathContext
import java.util.*

interface TextSetter{
    fun set(value : String)
}

class Calculator(private val errorString : String, private val setter : TextSetter) {
    var query : String = ""

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

    fun addNumber(num: String){
        //Log.i(MainActivity.TAG, "addNumber: $num")
        query += num
        setter.set(query)
    }

    private fun isOperation(v : Char) : Boolean {
        return v in "+-*/"
    }

    private fun popLast(){
        //Log.i(MainActivity.TAG, "popLast")
        query = query.dropLast(1)
        setter.set(query)
    }

    fun addOperation(op: String){
        //Log.i(MainActivity.TAG, "addOperation: $op")
        if(query.isEmpty() && op != "-"|| op == "-" && query.isNotEmpty() && query.last() == '-'){
            return
        }
        if(query.length > 1 && isOperation(query.last()) && isOperation(query[query.length - 2])){
            return
        }
        if(query.isNotEmpty() && isOperation(query.last()) && op != "-"){
            popLast()
        }
        query += op
        setter.set(query)
    }

    fun addPoint(){
        //Log.i(MainActivity.TAG, "addPoint")
        var pos = query.lastIndexOf('.') + 1
        var can = pos == 0
        while (pos != query.length){
            if(!query[pos].isDigit()){
                can = true
            }
            pos++
        }
        if(!can){
            return
        }
        if(query.isEmpty() || !query.last().isDigit()){
            query += "0."
        }else if(query.last().isDigit()){
            query += "."
        }
        setter.set(query)
    }

    fun clear(){
        query = ""
        setter.set(query)
    }

    fun rem(){
        //Log.i(MainActivity.TAG, "rem")
        if(query.isEmpty()){
            return
        }
        popLast()
    }

    fun calculate(){
        try {
            query = parseExpression(query).stripTrailingZeros().toPlainString()
        }catch (e : Throwable){
            query = errorString
        }
        setter.set(query)
    }



}