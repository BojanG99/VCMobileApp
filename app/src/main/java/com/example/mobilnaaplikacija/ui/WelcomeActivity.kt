package com.example.mobilnaaplikacija.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.mobilnaaplikacija.R

class WelcomeActivity : ComponentActivity() {
    private lateinit var button:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        button = findViewById(R.id.button)

        button.setOnClickListener{
            val intent = Intent(this,FileUploadActivity::class.java)
            startActivity(intent)
        }

    }
}