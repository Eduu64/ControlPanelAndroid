package com.example.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.min

class DutyActivity : AppCompatActivity() {

    // --- 1. Datos y Servicios ---
    private var userId: Int = -1
    private var username: String = ""
    private var email: String = ""
    private var activo: Int = 0
    private var fechaCreacion: String = ""
    private var ultimoLogin: String = ""

    private var droneList: List<Drone> = emptyList()
    private val droneDataService = DroneDataService()
    private lateinit var droneAdapter: DroneAdapter

    // --- 2. Referencias de Vistas (Agrupadas) ---
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private lateinit var droneRecyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var bottomSheetLayout: View
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var contenedorVariable: LinearLayout

    // --- 3. Permisos ---
    private val requiredPermissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar datos antes de cargar la UI
        loadIntentData()

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Configuración necesaria para OpenStreetMap (OSM)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_duty)

        // Inicializar todas las vistas
        initializeViews()

        // Configuraciones de UI
        setupBottomSheet()
        setupMap()
        setupBottomNavigation()
        setupMapInteractions()

        // Iniciar carga de drones
        loadDroneData()
    }

    private fun loadIntentData() {
        userId = intent.getIntExtra("USER_ID", -1)
        username = intent.getStringExtra("USERNAME") ?: "No Name"
        email = intent.getStringExtra("EMAIL") ?: "No Email"
        activo = intent.getIntExtra("ACTIVO", 0)
        fechaCreacion = intent.getStringExtra("FECHA_CREACION") ?: ""
        ultimoLogin = intent.getStringExtra("ULTIMO_LOGIN") ?: ""
    }

    private fun initializeViews() {
        // Centralización de todos los findView
        map = findViewById(R.id.map)
        droneRecyclerView = findViewById(R.id.droneRecyclerView)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomSheetLayout = findViewById(R.id.bottom_sheet)

        // El contenedor variable está DENTRO del layout del BottomSheet
        contenedorVariable = bottomSheetLayout.findViewById(R.id.contenedor_variable)

        // Inicializar el controlador del mapa
        mapController = map.controller
    }

    // --- Lógica de Drones y RecyclerView ---

    private fun loadDroneData() {
        lifecycleScope.launch {
            try {
                val drones = droneDataService.loadUserDrones(userId)
                droneList = drones

                setupRecyclerView()
                adjustBottomSheetHeight()
                addDroneMarkers()
                centerMapOnDrones()

            } catch (e: Exception) {
                Log.e("DutyActivity", "Error al cargar drones: ${e.message}")
                Toast.makeText(this@DutyActivity, "Error de red al cargar drones", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        if (!::droneAdapter.isInitialized) {
            droneAdapter = DroneAdapter(droneList) { drone ->
                // Al hacer clic en un dron de la lista, lo centramos en el mapa
                val geoPoint = GeoPoint(drone.lat?.toDouble() ?: 0.0, drone.lon?.toDouble() ?: 0.0)
                mapController.animateTo(geoPoint)
                Toast.makeText(this, "Enfocando: ${drone.modelo}", Toast.LENGTH_SHORT).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            droneRecyclerView.layoutManager = LinearLayoutManager(this)
            droneRecyclerView.adapter = droneAdapter
        } else {
            droneAdapter.updateDrones(droneList)
        }

        droneRecyclerView.visibility = if (droneList.isNotEmpty()) View.VISIBLE else View.GONE
        droneRecyclerView.requestLayout()
    }

    private fun adjustBottomSheetHeight() {
        val numberOfItems = droneList.size
        val itemHeightDp = 250 // Altura estimada de cada card de dron
        val displayMetrics: DisplayMetrics = resources.displayMetrics

        val totalHeightPx = (itemHeightDp * numberOfItems * displayMetrics.density).toInt()
        val maxSheetHeight = (displayMetrics.heightPixels * 0.4).toInt()

        val params = contenedorVariable.layoutParams
        params.height = min(totalHeightPx, maxSheetHeight)
        contenedorVariable.layoutParams = params
        contenedorVariable.requestLayout()
    }

    // --- Configuración de Mapas y Navegación ---

    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        mapController.setZoom(12.0)
    }

    private fun addDroneMarkers() {
        map.overlays.clear()
        val droneIcon: Drawable? = ContextCompat.getDrawable(this, R.drawable.ic_drone_2)

        droneList.forEach { drone ->
            if (drone.lat != null && drone.lon != null) {
                val marker = Marker(map)
                marker.position = GeoPoint(drone.lat.toDouble(), drone.lon.toDouble())
                marker.title = drone.modelo
                marker.snippet = "Serie: ${drone.numero_serie}"

                droneIcon?.let {
                    it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
                    marker.icon = it
                }

                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                map.overlays.add(marker)
            }
        }
        map.invalidate()
    }

    private fun centerMapOnDrones() {
        val firstDrone = droneList.firstOrNull { it.lat != null && it.lon != null }
        val center = if (firstDrone != null) {
            GeoPoint(firstDrone.lat!!.toDouble(), firstDrone.lon!!.toDouble())
        } else {
            GeoPoint(40.4167, -3.7037) // Madrid por defecto
        }
        mapController.setCenter(center)
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetBehavior.peekHeight = (100 * resources.displayMetrics.density).toInt()
        bottomSheetBehavior.isHideable = false

        // Ajuste de altura máxima inicial de la hoja
        bottomSheetLayout.post {
            val maxHeight = (resources.displayMetrics.heightPixels * 0.5).toInt()
            bottomSheetLayout.layoutParams.height = maxHeight
            bottomSheetLayout.requestLayout()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.nav_map
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    true
                }
                R.id.nav_account -> {
                    val intent = Intent(this, AccountActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    intent.putExtra("USERNAME", username)
                    intent.putExtra("EMAIL", email)
                    intent.putExtra("ACTIVO", activo)
                    intent.putExtra("FECHA_CREACION", fechaCreacion)
                    intent.putExtra("ULTIMO_LOGIN", ultimoLogin)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMapInteractions() {
        map.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}