package com.example.login

import com.example.login.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Clase de Servicio (Repository) que maneja las operaciones de red
 * para la gestión de Drones.
 */
class DroneDataService {

    /**
     * Llama a la API para obtener todos los drones asociados a un ID de usuario y
     * los transforma en objetos Drone locales.
     *
     * @param userId El ID del usuario que inició sesión.
     * @return Una lista de objetos Drone o una lista vacía si falla.
     */
    suspend fun loadUserDrones(userId: Int): List<Drone> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Llama al endpoint de la API
                val dronesDB = RetrofitClient.authService.getUserDrones(userId)

                // 2. Mapea la lista de objetos de la base de datos (DroneDB)
                //    a los objetos de la aplicación (Drone)
                val drones: List<Drone> = dronesDB.map { it.toLocalDrone() }

                println("Cargados ${drones.size} drones para el usuario ID: $userId.")
                return@withContext drones

            } catch (e: Exception) {
                println("Error al cargar drones del usuario $userId: ${e.message}")
                // Devuelve una lista vacía en caso de error
                return@withContext emptyList()
            }
        }
    }

}