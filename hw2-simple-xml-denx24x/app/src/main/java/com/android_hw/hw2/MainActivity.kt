package com.android_hw.hw2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {

    private lateinit var submitButton : Button
    private lateinit var errorField : TextView
    private lateinit var emailField : TextView
    private lateinit var passwordField : TextView

    private fun login(){
        if(emailField.text.isEmpty()){
            errorField.text = getString(R.string.errorLogin)
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailField.text).matches()){
            errorField.text = getString(R.string.wrongEmail)
        }else if(passwordField.text.isEmpty()){
            errorField.text = getString(R.string.errorPassword)
        }else{
            errorField.text = getString(R.string.errorLoginOrPassword)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        errorField = findViewById(R.id.message)
        submitButton = findViewById(R.id.submitButton)
        emailField = findViewById(R.id.emailInput)
        passwordField = findViewById(R.id.passwordInput)

        submitButton.setOnClickListener{
            login()
        }
    }
}