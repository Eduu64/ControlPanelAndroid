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
import com.example.login.network.RegistrationRequest
import com.example.login.network.RetrofitClient
import kotlinx.coroutines.launch

class SingupActivity : ComponentActivity() {

    private lateinit var editSingupUsername: EditText
    private lateinit var editSingupEmail: EditText 
    private lateinit var editSingupPassword: EditText
    private lateinit var editSingupConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginLink: TextView

    // SERVICIO DE AUTENTICACIÓN
    private val authService = RetrofitClient.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        editSingupUsername = findViewById(R.id.editSingupUsername)
        editSingupEmail = findViewById(R.id.editSingupEmail)
        editSingupPassword = findViewById(R.id.editSingupPassword)
        editSingupConfirmPassword = findViewById(R.id.editSingupConfirmPassword)

        btnRegister = findViewById(R.id.btn_register)
        tvLoginLink = findViewById(R.id.tv_login_link)

        btnRegister.setOnClickListener {
            attemptRegister()
        }

        tvLoginLink.setOnClickListener {
            navigateToLogin()
        }
    }

    /**
     * Extrae las credenciales, valida y ejecuta la llamada de registro.
     */
    private fun attemptRegister() {
        val username = editSingupUsername.text.toString().trim()
        val email = editSingupEmail.text.toString().trim() 
        val password = editSingupPassword.text.toString()
        val confirmPassword = editSingupConfirmPassword.text.toString()

        // 1. Validación de Campos Vacíos
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
