package com.example.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import androidx.lifecycle.lifecycleScope
import com.example.login.network.EmailVerify
import com.example.login.network.RetrofitClient
import com.example.login.network.UserUpdate
import kotlinx.coroutines.launch
import java.util.*

class MyDetailsActivity : AppCompatActivity() {

    // --- 1. Propiedades Globales ---
    private var userId: Int = -1
    private val authService = RetrofitClient.authService

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var tvAvatarName: TextView
    private lateinit var tvAvatarEmail: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvMemberSince: TextView
    private lateinit var tvLastLogin: TextView
    private lateinit var btnEdit: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var btnChangePassword: Button

    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)

        initializeViews()
        loadDataFromIntent()
        setupListeners()
    }

    private fun initializeViews() {
        etUsername = findViewById(R.id.et_username_value)
        etEmail = findViewById(R.id.et_email_value)
        tvAvatarName = findViewById(R.id.tv_avatar_username)
        tvAvatarEmail = findViewById(R.id.tv_avatar_email)
        tvStatus = findViewById(R.id.tv_status_value)
        tvMemberSince = findViewById(R.id.tv_member_since_value)
        tvLastLogin = findViewById(R.id.tv_last_login_value)
        btnEdit = findViewById(R.id.btn_edit)
        btnBack = findViewById(R.id.btn_back)
        btnChangePassword = findViewById(R.id.btn_change_password)
    }

    private fun loadDataFromIntent() {
        // RECUPERAR EL ID (Si no llega, la API fallará)
        userId = intent.getIntExtra("USER_ID", -1)

        val user = intent.getStringExtra("USERNAME") ?: "User"
        val email = intent.getStringExtra("EMAIL") ?: "No Email"
        val since = intent.getStringExtra("FECHA_CREACION") ?: "N/A"
        val last = intent.getStringExtra("ULTIMO_LOGIN") ?: "N/A"
        val act = intent.getIntExtra("ACTIVO", 0)

        etUsername.setText(user)
        etEmail.setText(email)
        tvAvatarName.text = user
        tvAvatarEmail.text = email
        tvMemberSince.text = since
        tvLastLogin.text = last

        if (act == 1) {
            tvStatus.text = "Active"
            tvStatus.setTextColor(Color.GREEN)
        } else {
            tvStatus.text = "Inactive"
            tvStatus.setTextColor(Color.RED)
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnEdit.setOnClickListener {
            isEditing = !isEditing
            toggleEditMode(isEditing)
        }

        btnChangePassword.setOnClickListener {
            val currentEmail = etEmail.text.toString().trim()

            if (currentEmail.isNotEmpty()) {
                // Ejecutamos la petición sin cambiar de pantalla
                performEmailVerification(currentEmail)
            } else {
                Toast.makeText(this, "Email field is empty", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun toggleEditMode(enable: Boolean) {
        etUsername.isEnabled = enable
        etEmail.isEnabled = enable
        etUsername.isFocusableInTouchMode = enable
        etEmail.isFocusableInTouchMode = enable

        btnEdit.text = if (enable) "Save" else "Edit"
        btnEdit.setIconResource(if (enable) R.drawable.ic_save else R.drawable.ic_edit)

        if (enable) {
            etUsername.requestFocus()
        } else {
            // Guardar cambios en la base de datos
            saveChangesToApi()
        }
    }

    private fun saveChangesToApi() {
        val newUsername = etUsername.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()

        if (newUsername.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == -1) {
            Toast.makeText(this, "Error: User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // El campo "Email" va con mayúscula para coincidir con el @SerializedName
                val updateData = UserUpdate(nombreUsuario = newUsername, Email = newEmail)

                val response = authService.updateUser(userId, updateData)

                if (response.success) {
                    tvAvatarName.text = newUsername
                    tvAvatarEmail.text = newEmail
                    Toast.makeText(this@MyDetailsActivity, "Profile saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MyDetailsActivity, response.message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MyDetailsActivity, "Network Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performEmailVerification(email: String) {
        // Desactivamos el botón para evitar múltiples clics
        btnChangePassword.isEnabled = false

        lifecycleScope.launch {
            try {
                val request = EmailVerify(Email = email)
                val response = authService.verifyEmail(request)

                if (response.success) {
                    // ÉXITO: El servidor ya envió el correo
                    Toast.makeText(this@MyDetailsActivity,
                        "Reset code sent to: $email",
                        Toast.LENGTH_LONG).show()

                    // Aquí podrías mostrar un AlertDialog para ingresar el código si quisieras,
                    // pero por ahora cumplimos con hacerlo "in situ".
                } else {
                    Toast.makeText(this@MyDetailsActivity,
                        "Error: ${response.message}",
                        Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MyDetailsActivity,
                    "Connection error. Please try again.",
                    Toast.LENGTH_SHORT).show()
            } finally {
                // Reactivamos el botón al terminar la petición
                btnChangePassword.isEnabled = true
            }
        }
    }
}