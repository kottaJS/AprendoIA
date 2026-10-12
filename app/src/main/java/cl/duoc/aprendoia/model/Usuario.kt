package cl.duoc.aprendoia.model

data class Usuario(

    val email: String,
    val nombre: String,
    val rol: String = "Estudiante",
    val puntaje: Int = 120
)
