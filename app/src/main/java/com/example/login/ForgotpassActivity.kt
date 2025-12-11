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
// Importamos las clases de la capa de red separada
import com.example.login.network.AuthRequest
import com.example.login.network.RetrofitClient
import kotlinx.coroutines.launch

class ForgotpassActivity : ComponentActivity() {

    // Declaración de las vistas
    private lateinit var etForgotUsername: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var tvBackToLogin: TextView

    // Servicio de Autenticación (Usando el objeto Singleton de Retrofit)
    private val authService = RetrofitClient.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpass)

        // 1. Inicialización de Vistas
        etForgotUsername = findViewById(R.id.et_forgotUsername)
        etNewPassword = findViewById(R.id.et_newPassword)
        etConfirmNewPassword = findViewById(R.id.et_confirmNewPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        tvBackToLogin = findViewById(R.id.tv_backToLogin)

        // 2. Listener para el BOTÓN DE RESTABLECER CONTRASEÑA
        btnResetPassword.setOnClickListener {
            attemptPasswordReset()
        }

        // 3. Listener para el ENLACE "VOLVER A LOGIN"
        tvBackToLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    /**
     * Valida las contraseñas y envía la solicitud de cambio al servidor.
     */
    private fun attemptPasswordReset() {
        val username = etForgotUsername.text.toString().trim()
        val newPass = etNewPassword.text.toString()
        val confirmPass = etConfirmNewPassword.text.toString()

        // 1. Validación de campos vacíos
        if (username.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
            showErrorMessage("Por favor, completa todos los campos.")
            return
        }

        // 2. Validación: Las contraseñas deben coincidir
        if (newPass != confirmPass) {
            showErrorMessage("Las nuevas contraseñas no coinciden.")
            etConfirmNewPassword.setText("") // Limpiar el campo de confirmación
            return
        }

        // 3. Llamada asíncrona al servidor
        lifecycleScope.launch {
            try {
                // Usamos AuthRequest: Nombre de usuario y la nueva contraseña
                val request = AuthRequest(username, newPass)

                // Llama al nuevo endpoint /api/reset-password
                val response = authService.resetPassword(request)

                if (response.success) {
                    showSuccessMessage("¡Contraseña restablecida con éxito!")
                    // Navega a Login para que el usuario pueda probar la nueva clave
                    navigateToLogin()
                } else {
                    // Muestra el error del servidor (ej: "Usuario no encontrado")
                    showErrorMessage("Fallo al restablecer clave: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("PassResetError", "Error de conexión/API: ${e.message}", e)
                showErrorMessage("Error de conexión con el servidor. No se pudo cambiar la clave.")
            }
        }
    }

    /**
     * Función privada para manejar la navegación de vuelta a la pantalla principal de Login.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)

        // Limpiamos la pila de actividades
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    // --- Funciones de Utilidad ---

    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.i("PassResetStatus", message)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.e("PassResetStatus", message)
    }
}