package com.example.login

import com.example.login.network.DroneDB

/**
 * Modelo de datos del Dron para la interfaz de usuario y lógica de la App.
 */
data class Drone(
    val id_dron: Int,
    val modelo: String,
    val numero_serie: String,
    val ip_address: String,
    val id_usuario: Int,
    val estado: String,

    // Double para que sea compatible con los marcadores del mapa (GeoPoint)
    val lat: Double?,
    val lon: Double?,

    val alerta_activa: Boolean,

    // Fechas en formato String
    val ultima_deteccion_exitosa: String?,
    val ultima_hora_conexion: String?,
    val fecha_adquisicion: String?
)

/**
 * Función de extensión para transformar el modelo de red (DroneDB) en el modelo local (Drone).
 * Esta función es el "puente" que conecta base de datos con la aplicación.
 */
fun DroneDB.toLocalDrone(): Drone {
    return Drone(
        id_dron = this.id_dron,
        modelo = this.modelo,
        numero_serie = this.numero_serie,
        ip_address = this.ip_address,
        id_usuario = this.id_usuario,
        estado = this.estado,

        // Mapeamos los nombres en MAYÚSCULAS que vienen de base de datos/API
        // Los convertimos a Double para el mapa
        lat = this.LAT?.toDouble(),
        lon = this.LON?.toDouble(),

        alerta_activa = this.alerta_activa,

        ultima_deteccion_exitosa = this.ultima_deteccion_exitosa,
        ultima_hora_conexion = this.ultima_hora_conexion,
        fecha_adquisicion = this.fecha_adquisicion
    )
}
