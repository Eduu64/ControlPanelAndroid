package com.example.login

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.login.network.EmailVerify
import com.example.login.network.RetrofitClient
import kotlinx.coroutines.launch

class ForgotpassActivity : AppCompatActivity() {

    // --- 1. Referencias a las Vistas ---
    private lateinit var etForgotEmail: EditText
    private lateinit var btnVerify: Button
    private lateinit var tvStatusMessage: TextView
    private lateinit var tvBackToLogin: TextView

    // Servicio de Red
    private val authService = RetrofitClient.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpass)

        initializeViews()
        setupListeners()
    }

    private fun initializeViews() {
        etForgotEmail = findViewById(R.id.et_forgotUsername)
        btnVerify = findViewById(R.id.btnResetPassword)
        tvStatusMessage = findViewById(R.id.tv_status_message)
        tvBackToLogin = findViewById(R.id.tv_backToLogin)
    }

    private fun setupListeners() {

        // Clic en el texto de abajo
        tvBackToLogin.setOnClickListener { finish() }

        // Clic en el botón de verificar
        btnVerify.setOnClickListener {
            val email = etForgotEmail.text.toString().trim()

            if (email.isEmpty()) {
                showInScreenNotification("Please enter your email address", isSuccess = false)
            } else {
                performEmailVerification(email)
            }
        }
    }

    private fun performEmailVerification(email: String) {
        lifecycleScope.launch {
            try {
                // Creamos el objeto JSON que viajará en el Body
                val request = EmailVerify(Email = email)

                val response = authService.verifyEmail(request)

                if (response.success) {
                    showInScreenNotification(response.message, true)
                    btnVerify.isEnabled = false
                    btnVerify.alpha = 0.5f
                } else {
                    showInScreenNotification(response.message, false)
                }
            } catch (e: Exception) {
                showInScreenNotification("Connection Error", false)
            }
        }
    }
    /**
     * Muestra el mensaje directamente en el layout centrado
     */
    private fun showInScreenNotification(message: String, isSuccess: Boolean) {
        tvStatusMessage.text = message
        tvStatusMessage.visibility = View.VISIBLE

        if (isSuccess) {
            // Verde para éxito
            tvStatusMessage.setTextColor(Color.GREEN)
        } else {
            // Rojo para errores
            tvStatusMessage.setTextColor(Color.RED)
        }
    }
}