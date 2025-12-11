package com.example.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.login.network.AuthRequest
import com.example.login.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // 1. VISTAS
    private lateinit var editLoginUsername: EditText
    private lateinit var editLoginPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    // 2. SERVICIO DE AUTENTICACIÓN (Usando el Singleton)
    private val authService = RetrofitClient.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de Vistas
        editLoginUsername = findViewById(R.id.editLoginUsername)
        editLoginPassword = findViewById(R.id.editLoginPassword)
        btnLogin =  findViewById(R.id.editLoginButtonLogin)
        tvSignUp =  findViewById(R.id.editLoginSingUp)
        tvForgotPassword = findViewById(R.id.editLoginForgotPassword)

        // Listener para el BOTÓN DE LOGIN
        btnLogin.setOnClickListener {
            attemptLogin()
        }

        // Listener para el ENLACE DE REGISTRO
        tvSignUp.setOnClickListener {
            val intent = Intent(this, SingupActivity::class.java)
            startActivity(intent)
        }

        // Listener para el ENLACE OLVIDÉ CONTRASEÑA
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotpassActivity::class.java)
            startActivity(intent)        }
    }

    private fun attemptLogin() {
        val username = editLoginUsername.text.toString()
        val password = editLoginPassword.text.toString()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Por favor, ingresa usuario y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Usamos AuthRequest y llamamos al servicio del Singleton
                val response = authService.loginUser(AuthRequest(username, password))

                if (response.success) {
                    showSuccessMessage("Inicio de sesión exitoso! Token: ${response.token}")
                    val intent = Intent(this@MainActivity, DutyActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showErrorMessage("Fallo al iniciar sesión: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("AuthError", "Error de conexión/API: ${e.message}", e)
                showErrorMessage("Error de conexión. Asegúrate que el backend está corriendo.")
            }
        }
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.i("LoginStatus", message)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.e("LoginStatus", message)
    }
}