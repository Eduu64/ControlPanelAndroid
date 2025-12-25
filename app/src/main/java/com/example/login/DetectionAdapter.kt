package com.example.login

//import android.graphics.BitmapFactory
//import android.util.Base64


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DetectionAdapter(
    private var detections: List<Detection>,
    private val onDetectionClick: (Detection) -> Unit
) : RecyclerView.Adapter<DetectionAdapter.DetectionViewHolder>() {

    inner class DetectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val detectionImage: ImageView = view.findViewById(R.id.detectionImage)
        val confidenceBadge: TextView = view.findViewById(R.id.detectionConfidence)
        val classText: TextView = view.findViewById(R.id.detectionClass)
        val timestampText: TextView = view.findViewById(R.id.detectionTimestamp)
        val coordsText: TextView = view.findViewById(R.id.detectionCoords)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_detection_card, parent, false)
        return DetectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetectionViewHolder, position: Int) {
        val detection = detections[position]
        val context = holder.itemView.context

        // 1. Decodificación nativa de Imagen (Asumiendo Base64 desde la API)
        if (!detection.ruta_imagen.isNullOrEmpty()) {
            try {
                /* Convertimos el String Base64 a bytes y luego a Bitmap
                val imageBytes = Base64.decode(detection.ruta_imagen, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.detectionImage.setImageBitmap(bitmap)*/

                //Por ahora:

                holder.detectionImage.setImageResource(R.drawable.placeholder_detection)

            } catch (e: Exception) {
                // Si la ruta no es Base64 o hay error, ponemos el placeholder local
                holder.detectionImage.setImageResource(R.drawable.placeholder_detection)
            }
        } else {
            holder.detectionImage.setImageResource(R.drawable.placeholder_detection)
        }

        // 2. Porcentaje de Confianza de la IA (Conversión de Double a Entero)
        val confidenceInt = (detection.confianza_ia * 100).toInt()
        holder.confidenceBadge.text = "$confidenceInt% Match"

        // 3. Clasificación y Estilo Visual
        holder.classText.text = "ALERTA: ${detection.clasificacion.uppercase()}"

        // Color según el tipo de riesgo
        val colorAlert = if (detection.clasificacion.contains("FUEGO", ignoreCase = true)) {
            android.R.color.holo_red_dark
        } else {
            android.R.color.holo_orange_dark
        }
        holder.classText.setTextColor(ContextCompat.getColor(context, colorAlert))

        // 4. Datos de tiempo y GPS
        // Limpiamos la fecha por si trae milisegundos (ej: 2025-12-14 10:00:00.000)
        holder.timestampText.text = detection.fecha_hora.substringBefore('.')
        holder.coordsText.text = "Ubicación: ${detection.latitud}, ${detection.longitud}"

        // Click en la tarjeta
        holder.itemView.setOnClickListener { onDetectionClick(detection) }
    }

    override fun getItemCount() = detections.size

    fun updateDetections(newDetections: List<Detection>) {
        this.detections = newDetections
        notifyDataSetChanged()
    }
}