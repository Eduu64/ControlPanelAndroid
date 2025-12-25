package com.example.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DroneAdapter(
    private var drones: List<Drone>,
    private val onDroneClick: (Drone) -> Unit
) : RecyclerView.Adapter<DroneAdapter.DroneViewHolder>() {

    inner class DroneViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.droneName)
        val statusTextView: TextView = view.findViewById(R.id.droneStatus)
        val detectionBoolTextView: TextView = view.findViewById(R.id.droneDetectionBool) // Nuevo campo
        val detailsTextView: TextView = view.findViewById(R.id.droneDetails)
        val locationTextView: TextView = view.findViewById(R.id.droneLocation)
        val detectionTimeTextView: TextView = view.findViewById(R.id.droneDetection)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DroneViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drone_card, parent, false)
        return DroneViewHolder(view)
    }

    override fun onBindViewHolder(holder: DroneViewHolder, position: Int) {
        val drone = drones[position]
        val context = holder.itemView.context

        // 1. Modelo y Estado de Conexión
        holder.nameTextView.text = "Model: ${drone.modelo}"
        holder.statusTextView.text = "Status: ${drone.estado}"

        val isConnected = drone.estado.equals("CONECTADO", ignoreCase = true)
        holder.statusTextView.setTextColor(
            ContextCompat.getColor(context, if (isConnected) android.R.color.holo_green_light else android.R.color.darker_gray)
        )

        // 2. Alerta de Detección (Tu nueva columna de la BD)
        if (drone.alerta_activa) {
            holder.detectionBoolTextView.text = "DETECTION: ACTIVE"
            holder.detectionBoolTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
            holder.detectionBoolTextView.visibility = View.VISIBLE
        } else {
            holder.detectionBoolTextView.text = "Detection: None"
            holder.detectionBoolTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white))
            // Puedes elegir ocultarlo o dejarlo como "None"
        }

        // 3. Detalles Técnicos (IP y S/N)
        holder.detailsTextView.text = "IP: ${drone.ip_address ?: "N/A"} | S/N: ${drone.numero_serie}"

        // 4. Ubicación
        val latFormatted = drone.lat?.let { String.format("%.4f", it) } ?: "0.0"
        val lonFormatted = drone.lon?.let { String.format("%.4f", it) } ?: "0.0"
        holder.locationTextView.text = "Lat: $latFormatted, Lon: $lonFormatted"

        // 5. Timestamp de última detección exitosa
        if (!drone.ultima_deteccion_exitosa.isNullOrEmpty()) {
            // Quitamos los milisegundos si vienen de la BD para que no sea tan largo
            val cleanDate = drone.ultima_deteccion_exitosa.substringBefore('.')
            holder.detectionTimeTextView.text = "Last Detection: $cleanDate"
            holder.detectionTimeTextView.visibility = View.VISIBLE
        } else {
            holder.detectionTimeTextView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onDroneClick(drone) }
    }

    override fun getItemCount() = drones.size

    fun updateDrones(newDrones: List<Drone>) {
        this.drones = newDrones
        notifyDataSetChanged()
    }
}