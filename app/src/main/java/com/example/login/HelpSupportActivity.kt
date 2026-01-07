package com.example.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton 
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class HelpSupportActivity : AppCompatActivity() {

    private lateinit var btnSupport: Button
    private lateinit var btnBack: ImageButton 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

        btnSupport = findViewById(R.id.btn_contact_support)
        btnBack = findViewById(R.id.btn_back)

        btnSupport.setOnClickListener {
            enviarCorreoSoporte()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun enviarCorreoSoporte() {
        val email = "soporte@tuapp.com"
        val subject = "Soporte App - Eduardo Morales (UE)"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        try {
            startActivity(Intent.createChooser(intent, "Select email app"))
        } catch (e: Exception) {
            Toast.makeText(this, "No email app installed", Toast.LENGTH_SHORT).show()
        }
    }
}
