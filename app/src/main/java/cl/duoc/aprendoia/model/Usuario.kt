package cl.duoc.aprendoia.model

data class Usuario(
    val id: Int = 0,
    val email: String,
    val nombre: String,
    val alias: String = "",
    val password: String = "123",
    val telefono: String = "+56 9 1234 5678",
    val direccion: String = "Santiago Centro",
    val avatar: String = "avatar_1",
    val rol: String = "Cliente", // "Admin", "GenCont", "Cliente"
    val puntaje: Int = 0,
    val qrEncontrados: Int = 0
)
