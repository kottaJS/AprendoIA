package cl.duoc.aprendoia.model

data class PuntoInteres(

    val id: Int,
    val nombre: String,
    val descripcion: String,
    val comuna: String,
    val latitud: Double,
    val longitud: Double,
    val categoriaIA: String,
    val puntosRecompensa: Int,
    val completada: Boolean = false,
    val codigoQR: String
)
