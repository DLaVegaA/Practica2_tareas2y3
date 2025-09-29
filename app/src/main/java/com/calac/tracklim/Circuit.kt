package com.calac.tracklim

import com.google.android.gms.maps.model.LatLng
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Enum para manejar las categorías de forma segura
enum class Category {
    WEC, F1, F2, F3, IMSA, INDYCAR
}

@Parcelize
data class Winner(
    val year: Int,
    val driver: String
) : Parcelable

// Data class que representa un circuito con toda su información
@Parcelize
data class Circuit(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val grade: Int,
    val categories: List<Category>,
    // --- Nuevos campos ---
    val location: String,
    val length: String,
    val turns: Int,
    val lapRecord: String,
    val capacity: String,
    val history: String,
    val trackMapResId: Int, // ID de la imagen del trazado en /drawable
    val winners: List<Winner>
) : Parcelable