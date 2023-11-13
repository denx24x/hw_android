package com.hw_android.hw1

import android.util.Log
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.internal.runners.JUnit4ClassRunner
import org.junit.runner.RunWith
import org.junit.runners.BlockJUnit4ClassRunner

/*
Calculator имеет несколько методов для работы с интерфейсом
addNumber - принимает строку, через приложение можно ввести аргументом только цифру от 0 до 9 в виде строки
addPoint - ставит точку
rem - стирает символ
addOperation - добавляет операцию, через приложение можно ввести аргументом только "+", "-", "/", "*"
calculate - подсчитывает выражение и записывает ответ в query
clear - очищает поле query
 */
@RunWith(BlockJUnit4ClassRunner::class)
class CalculatorTest : TextSetter {
    var queryField : String = ""

    override fun set(value: String) {
        queryField = value
    }

    private val calculator = Calculator("Error", this)

    @Before
    fun setUp() {
        calculator.query = ""
    }

    @Before
    fun isResultFieldEmpty() {
        Assert.assertEquals("Result field is not empty", "", queryField)
    }

    @Test
    fun addDigitString() {
        calculator.addNumber("1")
        calculator.addNumber("2")
        calculator.addNumber("3")
        calculator.addNumber("4")
        calculator.addNumber("5")
        calculator.addNumber("6")
        calculator.addNumber("7")
        calculator.addNumber("8")
        calculator.addNumber("9")
        calculator.addNumber("0")

        Assert.assertEquals(
            "Incorrect addition of digit string",
            "1234567890", queryField
        )
    }

    @Test
    fun addCommand() {
        calculator.addNumber("1")
        calculator.addOperation("+")
        calculator.addNumber("2")

        Assert.assertEquals(
            "Incorrect addition of a command",
            queryField, "1+2"
        )
    }


    @Test
    fun addSingleNumber() {
        calculator.addNumber("1")
        Assert.assertEquals(
            "Incorrect addition of digit string",
            "1", queryField
        )
    }

    @Test
    fun singlePoint() {
        calculator.addPoint()

        Assert.assertEquals("Expected double zero", "0.", queryField)
    }

    @Test
    fun doublePoint() {
        calculator.addNumber("1")
        calculator.addPoint()
        calculator.addPoint()

        Assert.assertEquals("Expected only one point", "1.", queryField)
    }

    @Test
    fun expression() {
        calculator.addNumber("1")
        calculator.addOperation("+")
        calculator.addNumber("1")

        calculator.calculate()

        Assert.assertEquals("Invalid result", "2", queryField)
    }
}