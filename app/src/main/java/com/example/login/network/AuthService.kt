package com.example.login.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.PUT

// --- 1. ESTRUCTURAS DE DATOS DE RED (Modelos JSON) ---

/** Modelo para Login y Reset de Contraseña. */
data class AuthRequest(
    val nombreUsuario: String,
    val contrasena: String,
    val client_time: String
)

data class AuthRequestPass(
    val nombreUsuario: String,
    val contrasena: String,
)

data class EmailVerify(
    val Email: String
)
data class EmailVerifyResponse(
    val success: Boolean,
    val message: String
)

/** Modelo ESPECÍFICO para el REGISTRO (requiere email). */
data class RegistrationRequest(
    val nombreUsuario: String,
    val email: String,
    val contrasena: String
)
data class UserUpdate(
    val nombreUsuario: String,
    val Email: String
)

/** Modelo de respuesta de autenticación. */
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val UsuarioID: Int,
    val username: String? = null,
    val Email: String? = null,
    val activo: Int? = null,
    val fechaCreacion: String? = null,
    val ultimoInicioSesion: String? = null
)

/** Modelo Base para registrar un dron (sin id_usuario, se pasa en la URL). */
data class DroneBase(
    val modelo: String,
    val numero_serie: String,
    val ip_address: String
)

/** Modelo completo para un dron (Salida de la API/DB). */
data class DroneDB(
    val modelo: String,
    val numero_serie: String,
    val ip_address: String,
    val id_usuario: Int,
    val id_dron: Int,
    val estado: String,

    // Nombres exactos del JSON de Postman
    val LAT: Float?,
    val LON: Float? ,
    val alerta_activa: Boolean,

    val ultima_deteccion_exitosa: String?,
    val ultima_hora_conexion: String?,
    val fecha_adquisicion: String?
)

/** Modelo para una detección. */
data class DetectionDB(
    val id_deteccion: Int,
    val id_dron: Int,
    val id_usuario: Int,
    val clasificacion: String,
    val confianza_ia: Float,
    val latitud: Float,
    val longitud: Float,
    val fecha_hora: String,
    val ruta_imagen: String? = null
)



// --- 2. INTERFAZ DEL SERVICIO API (Endpoints) ---

/**
 * Interfaz de Retrofit para interactuar con la API.
 */
interface AuthService {

    // --- Autenticación ---
    @POST("api/login")
    suspend fun loginUser(@Body request: AuthRequest): AuthResponse

    @POST("api/registro")
    suspend fun registerUser(@Body request: RegistrationRequest): AuthResponse

    @POST("api/reset-password")
    suspend fun resetPassword(@Body request: AuthRequestPass): AuthResponse

    @PUT("api/users/{user_id}")
    suspend fun updateUser(
        @Path("user_id") userId: Int,
        @Body request: UserUpdate
    ): AuthResponse

    // --- Gestión de Drones (Centrada en el Usuario) ---

    /** Registra un nuevo dron asociándolo directamente al usuario logueado. */
    @POST("api/users/{user_id}/drones")
    suspend fun registerDrone(
        @Path("user_id") userId: Int,
        @Body drone: DroneBase
    ): DroneDB

    /** Obtiene la lista de todos los drones de un usuario específico. */
    @GET("api/users/{user_id}/drones")
    suspend fun getUserDrones(@Path("user_id") userId: Int): List<DroneDB>

    @GET("api/drones/{drone_id}")
    suspend fun getDroneInfo(@Path("drone_id") droneId: Int): DroneDB

    // --- Detecciones ---
    @GET("api/detecciones/{userId}")
    suspend fun getDetectionsByUser(@Path("userId") userId: Int): List<DetectionDB>

    @POST("api/verify-email/")
    suspend fun verifyEmail(@Body request: EmailVerify): EmailVerifyResponse
}

// --- 3. CONFIGURACIÓN DEL CLIENTE (Instancia Singleton) ---

private const val BASE_URL = "http://10.0.2.2:8000/"

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
}