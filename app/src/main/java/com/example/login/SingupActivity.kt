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
// Importamos el modelo de registro que requiere el email
import com.example.login.network.RegistrationRequest
import com.example.login.network.RetrofitClient
import kotlinx.coroutines.launch

class SingupActivity : ComponentActivity() {

    // Nombres de variables actualizados
    private lateinit var editSingupUsername: EditText
    private lateinit var editSingupEmail: EditText // <-- ¡NUEVO CAMPO!
    private lateinit var editSingupPassword: EditText
    private lateinit var editSingupConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    // SERVICIO DE AUTENTICACIÓN
    private val authService = RetrofitClient.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        // Enlazar vistas
        editSingupUsername = findViewById(R.id.editSingupUsername)
        // 1. Enlazar el nuevo campo Email (debes asegurar que este ID existe en activity_singup.xml)
        editSingupEmail = findViewById(R.id.editSingupEmail)
        editSingupPassword = findViewById(R.id.editSingupPassword)
        editSingupConfirmPassword = findViewById(R.id.editSingupConfirmPassword)

        btnRegister = findViewById(R.id.btn_register)
        tvLoginLink = findViewById(R.id.tv_login_link)

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
     * Extrae las credenciales, valida y ejecuta la llamada de registro.
     */
    private fun attemptRegister() {
        val username = editSingupUsername.text.toString().trim()
        val email = editSingupEmail.text.toString().trim() // <-- ¡OBTENEMOS EL EMAIL!
        val password = editSingupPassword.text.toString()
        val confirmPassword = editSingupConfirmPassword.text.toString()

        // 1. Validación de Campos Vacíos
        // Se añade 'email.isBlank()' a la validación
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Por favor, completa todos los campos, incluyendo el email.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Validación de Coincidencia de Contraseñas
        if (password != confirmPassword) {
            showErrorMessage("Las contraseñas no coinciden. Inténtalo de nuevo.")
            editSingupConfirmPassword.setText("")
            return
        }

        // 3. Envío al Servidor
        lifecycleScope.launch {
            try {
                // *** ¡CAMBIO CRUCIAL! ***
                // Usamos RegistrationRequest, que requiere username, email y contrasena
                val request = RegistrationRequest(
                    nombreUsuario = username,
                    email = email,
                    contrasena = password
                )

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