package com.example.login

import com.example.login.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Clase de Servicio que maneja las operaciones de red para Detecciones.
 */
class DetectionDataService {

    suspend fun loadUserDetections(userId: Int): List<Detection> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Llama a la API (devuelve List<DetectionDB>)
                val detectionsDB = RetrofitClient.authService.getDetectionsByUser(userId)

                // 2. Mapea usando la función que creamos en Detection.kt
                val detections: List<Detection> = detectionsDB.map { it.toLocalDetection() }

                println("Cargadas ${detections.size} detecciones.")
                return@withContext detections

            } catch (e: Exception) {
                println("Error: ${e.message}")
                return@withContext emptyList()
            }
        }
    }
}