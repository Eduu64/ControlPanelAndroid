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

class SingupActivity : ComponentActivity() {

    // Nombres de variables actualizados
    private lateinit var editSingupUsername: EditText
    private lateinit var editSingupPassword: EditText
    private lateinit var editSingupConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    // SERVICIO DE AUTENTICACIÓN
    private val authService = RetrofitClient.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        // Enlazar vistas usando los IDs solicitados: editLoginXXX
        editSingupUsername = findViewById(R.id.editSingupUsername) // ¡Nuevo ID!
        editSingupPassword = findViewById(R.id.editSingupPassword) // ¡Nuevo ID!
        editSingupConfirmPassword = findViewById(R.id.editSingupConfirmPassword) // ¡Nuevo ID!

        btnRegister = findViewById(R.id.btn_register) // ID consistente
        tvLoginLink = findViewById(R.id.tv_login_link) // ID consistente

        // Lógica del Botón de Registro
        btnRegister.setOnClickListener {
            attemptRegister()
        }

        // Lógica de Navegación: De Singup a Login
        tvLoginLink.setOnClickListener {
            navigateToLogin()
        }
    }

    /**
     * Extrae las credenciales, valida las contraseñas y ejecuta la llamada de registro.
     */
    private fun attemptRegister() {
        val username = editSingupUsername.text.toString().trim()
        val password = editSingupPassword.text.toString()
        val confirmPassword = editSingupConfirmPassword.text.toString()

        // 1. Validación de Campos Vacíos
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validación de Coincidencia de Contraseñas (NUEVO)
        if (password != confirmPassword) {
            showErrorMessage("Las contraseñas no coinciden. Inténtalo de nuevo.")
            // Limpiamos solo el campo de confirmación para que el usuario pueda corregir.
            editSingupConfirmPassword.setText("")
            return
        }

        // 3. Envío al Servidor
        lifecycleScope.launch {
            try {
                // Usamos la contraseña principal (password), no la de confirmación
                val request = AuthRequest(username, password)

                val response = authService.registerUser(request)

                if (response.success) {
                    showSuccessMessage("¡Registro exitoso! Ya puedes iniciar sesión.")
                    navigateToLogin()
                } else {
                    showErrorMessage("Fallo en el registro: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("AuthRegister", "Error de conexión/API: ${e.message}", e)
                showErrorMessage("Error de conexión con el servidor.")
            }
        }
    }

    // --- FUNCIONES DE UTILIDAD (Sin cambios) ---

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showSuccessMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.i("RegisterStatus", message)
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.e("RegisterStatus", message)
    }
}