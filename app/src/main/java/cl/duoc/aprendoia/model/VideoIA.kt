package cl.duoc.aprendoia.model

/**
 * Representa el contenido del JSON embebido en el código QR del video educativo.
 * Formato requerido por la pauta del proyecto:
 * { "titulo": "...", "descripcion": "...", "url": "..." }
 */
data class VideoIA(
    val titulo: String,
    val descripcion: String,
    val url: String
)
