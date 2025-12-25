package com.example.login

import com.example.login.network.DetectionDB

/**
 * Modelo de datos de la Detección para la UI.
 */
data class Detection(
    val id_deteccion: Int,
    val id_dron: Int,
    val id_usuario: Int,
    val clasificacion: String,
    val confianza_ia: Float,
    val latitud: Float,
    val longitud: Float,
    val fecha_hora: String,
    val ruta_imagen: String?
)

/**
 * Función de extensión para transformar el modelo de red (DetectionDB) en el modelo local (Detection).
 */
fun DetectionDB.toLocalDetection(): Detection {
    return Detection(
        id_deteccion = this.id_deteccion,
        id_dron = this.id_dron,
        id_usuario = this.id_usuario,
        clasificacion = this.clasificacion,
        confianza_ia = this.confianza_ia,
        latitud = this.latitud,
        longitud = this.longitud,
        fecha_hora = this.fecha_hora,
        ruta_imagen = this.ruta_imagen
    )
}