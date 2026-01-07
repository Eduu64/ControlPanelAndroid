package com.example.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MyReportsActivity : AppCompatActivity() {

    // --- 1. Datos y Servicios ---
    private var userId: Int = -1
    private var username: String = ""

    private var detectionList: List<Detection> = emptyList()
    private val detectionDataService = DetectionDataService()
    private lateinit var detectionAdapter: DetectionAdapter

    // --- 2. Referencias de Vistas ---
    private lateinit var rvDetections: RecyclerView
    private lateinit var btnBack: ImageButton
    private lateinit var btnRefresh: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadIntentData()

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        setContentView(R.layout.activity_my_reports)

        // Inicializar vistas
        initializeViews()

        // Configurar Listeners
        setupListeners()

        // Iniciar carga de datos
        loadDetectionData()
    }

    private fun loadIntentData() {
        userId = intent.getIntExtra("USER_ID", -1)
    }

    private fun initializeViews() {
        rvDetections = findViewById(R.id.rvDetections)
        btnBack = findViewById(R.id.btnBack)
        btnRefresh = findViewById(R.id.btnRefresh)

        rvDetections.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnRefresh.setOnClickListener {
            Toast.makeText(this, "Actualizando reportes...", Toast.LENGTH_SHORT).show()
            loadDetectionData()
        }
    }


    private fun loadDetectionData() {
        lifecycleScope.launch {
            try {
                val detections = detectionDataService.loadUserDetections(userId)
                detectionList = detections

                setupRecyclerView()

            } catch (e: Exception) {
                Log.e("MyReportsActivity", "Error al cargar detecciones: ${e.message}")
                Toast.makeText(this@MyReportsActivity, "Error de red al cargar reportes", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        if (!::detectionAdapter.isInitialized) {
            detectionAdapter = DetectionAdapter(detectionList) { detection ->
                Toast.makeText(this, "Detección: ${detection.clasificacion}", Toast.LENGTH_SHORT).show()
            }
            rvDetections.adapter = detectionAdapter
        } else {
            detectionAdapter.updateDetections(detectionList)
        }

        rvDetections.visibility = if (detectionList.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
