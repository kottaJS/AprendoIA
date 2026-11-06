package cl.duoc.aprendoia.model

data class HistorialItem(
    val id: Int = 0,
    val usuarioId: Int = 0,
    val tipo: String, // "QR", "Video", "Selfie"
    val titulo: String,
    val lugar: String,
    val fecha: String,
    val hora: String,
    val puntos: Int
)

