package com.hw_android.hw1

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CalculatorAndroidTest {

    @Rule
    @JvmField
    var activity = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun prepare() {
        activity.launchActivity(Intent())
    }

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("com.hw_android.hw1", appContext.packageName)
    }

    @Test
    fun digit() {
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("1")))
    }

    @Test
    fun multiDigit() {
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_2)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_3)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("123")))
    }

    @Test
    fun double() {
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_point)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("1.1")))
    }

    @Test
    fun expression() {
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_add)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_answer)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("2")))
    }

    @Test
    fun doublePoint() {
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_point)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_point)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_2)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("1.2")))
    }

    @Test
    fun answerWithoutChanges() {
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("1")))
        Espresso.onView(withId(R.id.button_answer)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("1")))
    }

    @Test
    fun division(){
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_div)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_2)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_answer)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("0.5")))
    }

    @Test
    fun multiplication(){
        Espresso.onView(withId(R.id.button_7)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_mul)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_6)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_answer)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("42")))
    }

    @Test
    fun clear(){
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_2)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_3)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_clear)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
    }

    @Test
    fun rem(){
        Espresso.onView(withId(R.id.button_1)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_point)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("1.")))
        Espresso.onView(withId(R.id.button_rem)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.button_rem)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.query))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))

    }

}