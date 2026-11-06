package cl.duoc.aprendoia.model

import android.graphics.Bitmap

data class PuntoInteres(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val comuna: String,
    val latitud: Double,
    val longitud: Double,
    val categoriaIA: String,
    val puntosRecompensa: Int = 10,
    val completada: Boolean = false,
    val codigoQR: String,
    val selfieTomada: Boolean = false,
    val selfieBitmap: Bitmap? = null,
    val selfieUri: String? = null,
    val urlVideo: String = "https://www.youtube.com/shorts/dQw4w9WgXcQ",
    val videoTitulo: String = "",
    val videoTema: String = "",
    val distanciaAprox: String = "250 m",
    val esHitoTuristico: Boolean = true,
    val videoVisto: Boolean = false
)
