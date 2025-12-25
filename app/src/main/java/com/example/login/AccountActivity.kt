package com.example.login

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountActivity : AppCompatActivity() {

    // --- 1. Datos del Usuario ---
    private var userId: Int = -1
    private var username: String = ""
    private var email: String = ""
    private var activo: Int = 0
    private var fechaCreacion: String = ""
    private var ultimoLogin: String = ""

    // --- 2. Vistas ---
    private lateinit var optionMyDetails: LinearLayout
    private lateinit var optionMyReports: LinearLayout
    private lateinit var optionHelp: LinearLayout
    private lateinit var optionLogOut: LinearLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        loadIntentData()
        initializeViews()
        setupBottomNavigation()
        setupListeners()
    }

    private fun loadIntentData() {
        userId = intent.getIntExtra("USER_ID", -1)
        username = intent.getStringExtra("USERNAME") ?: "Usuario"
        email = intent.getStringExtra("EMAIL") ?: "Sin Email"
        activo = intent.getIntExtra("ACTIVO", 0)
        fechaCreacion = intent.getStringExtra("FECHA_CREACION") ?: ""
        ultimoLogin = intent.getStringExtra("ULTIMO_LOGIN") ?: ""
    }

    private fun initializeViews() {
        optionMyDetails = findViewById(R.id.option_my_details)
        optionMyReports = findViewById(R.id.option_my_reports)
        optionHelp = findViewById(R.id.option_help_support)
        optionLogOut = findViewById(R.id.option_log_out)


        bottomNavigationView = findViewById(R.id.bottom_navigation)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.nav_account

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    val intent = Intent(this, DutyActivity::class.java)
                    passUserData(intent)
                    startActivity(intent)
                    true
                }
                R.id.nav_account -> true
                else -> false
            }
        }
    }

    private fun setupListeners() {
        // 1. Detalles del perfil
        optionMyDetails.setOnClickListener {
            val intent = Intent(this, MyDetailsActivity::class.java)
            passUserData(intent)
            startActivity(intent)
        }

        // 2. Reportes / Drones
        optionMyReports.setOnClickListener {
            val intent = Intent(this, MyReportsActivity::class.java)
            passUserData(intent)
            startActivity(intent)
        }


        // 5. Ayuda y Soporte
        optionHelp.setOnClickListener {
            Toast.makeText(this, "Help Center", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HelpSupportActivity::class.java)
            startActivity(intent)

        }

        // 6. Cerrar Sesión
        optionLogOut.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Limpia el stack de actividades para que no se pueda volver atrás
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun passUserData(intent: Intent) {
        intent.putExtra("USER_ID", userId)
        intent.putExtra("USERNAME", username)
        intent.putExtra("EMAIL", email)
        intent.putExtra("ACTIVO", activo)
        intent.putExtra("FECHA_CREACION", fechaCreacion)
        intent.putExtra("ULTIMO_LOGIN", ultimoLogin)
    }
}