package cl.duoc.aprendoia.model

data class NotificacionItem(
    val id: Int = 0,
    val titulo: String,
    val mensaje: String,
    val tiempo: String,
    val tipo: String = "punto", // "punto", "desafio", "video", "felicitacion", "evento"
    val puntoIdDestino: Int? = null
)

