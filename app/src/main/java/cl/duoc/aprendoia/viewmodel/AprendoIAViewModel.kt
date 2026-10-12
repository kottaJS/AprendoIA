package cl.duoc.aprendoia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.aprendoia.model.CapsulaIA
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario

class AprendoIAViewModel : ViewModel() {

    var usuarioActual by mutableStateOf<Usuario?>(
        Usuario(
            email = "estudiante@duocuc.cl",
            nombre = "Maximiliano Dinamarca",
            rol = "Estudiante",
            puntaje = 150
        )
    )
        private set

    var estaAutenticado by mutableStateOf(true)

    private val _puntosSantiago = mutableStateListOf(
        PuntoInteres(
            id = 1,
            nombre = "Duoc UC Alonso de Ovalle",
            descripcion = "Taller de robótica y sensores para monitorear el centro de Santiago.",
            comuna = "Santiago Centro",
            latitud = -33.4475,
            longitud = -70.6558,
            categoriaIA = "Robótica",
            puntosRecompensa = 50,
            completada = true,
            codigoQR = "QR-DUOC-001"
        ),
        PuntoInteres(
            id = 2,
            nombre = "Palacio de La Moneda",
            descripcion = "Identificación de fachadas y monumentos con fotos y visión artificial.",
            comuna = "Santiago Centro",
            latitud = -33.4429,
            longitud = -70.6540,
            categoriaIA = "Visión Artificial",
            puntosRecompensa = 40,
            completada = false,
            codigoQR = "QR-MONEDA-002"
        ),
        PuntoInteres(
            id = 3,
            nombre = "Plaza de Armas",
            descripcion = "Conteo y estimación de personas en el centro usando machine learning.",
            comuna = "Santiago Centro",
            latitud = -33.4378,
            longitud = -70.6504,
            categoriaIA = "Machine Learning",
            puntosRecompensa = 35,
            completada = false,
            codigoQR = "QR-PLAZA-003"
        ),
        PuntoInteres(
            id = 4,
            nombre = "Cerro Santa Lucía",
            descripcion = "Grabación de audio ambiente para reconocer sonidos y aves del cerro.",
            comuna = "Santiago Centro",
            latitud = -33.4402,
            longitud = -70.6439,
            categoriaIA = "Audio y Sonido",
            puntosRecompensa = 45,
            completada = false,
            codigoQR = "QR-LUCIA-004"
        ),
        PuntoInteres(
            id = 5,
            nombre = "Museo de Bellas Artes",
            descripcion = "Actividad de arte digital y generación de imágenes con IA.",
            comuna = "Santiago Centro",
            latitud = -33.4357,
            longitud = -70.6443,
            categoriaIA = "IA Generativa",
            puntosRecompensa = 60,
            completada = false,
            codigoQR = "QR-BELLASARTES-005"
        )
    )

    val puntosSantiago: List<PuntoInteres> get() = _puntosSantiago

    val capsulasDisponibles = listOf(
        CapsulaIA(1, "¿Qué es una Red Neuronal?", "Fundamentos", 3, 20, "Explicación básica de cómo aprende un modelo a reconocer patrones."),
        CapsulaIA(2, "Reconocimiento de Imágenes", "Visión Artificial", 5, 30, "Cómo una app puede identificar lugares históricos usando la cámara."),
        CapsulaIA(3, "Ética y Datos en Santiago", "Ética en IA", 4, 25, "Cuidado con los datos personales al usar cámaras y sensores en la calle.")
    )

    var formNombre by mutableStateOf("")
    var formComuna by mutableStateOf("Santiago Centro")
    var formCategoria by mutableStateOf("Visión Artificial")
    var formLatitud by mutableStateOf("-33.4475")
    var formLongitud by mutableStateOf("-70.6558")
    var formDescripcion by mutableStateOf("")

    var formErrorMensaje by mutableStateOf<String?>(null)
    var formExitoMensaje by mutableStateOf<String?>(null)

    var sincronizando by mutableStateOf(false)

    fun iniciarSesion(email: String, clave: String): Boolean {
        if (email.isNotBlank() && clave.length >= 3) {
            usuarioActual = Usuario(
                email = email.trim(),
                nombre = if (email.contains("@")) email.substringBefore("@").replace(".", " ") else "Estudiante",
                rol = "Estudiante",
                puntaje = 150
            )
            estaAutenticado = true
            return true
        }
        return false
    }

    fun cerrarSesion() {
        estaAutenticado = false
        usuarioActual = null
    }

    fun completarMision(puntoId: Int): Boolean {
        val index = _puntosSantiago.indexOfFirst { it.id == puntoId }
        if (index != -1 && !_puntosSantiago[index].completada) {
            val punto = _puntosSantiago[index]
            _puntosSantiago[index] = punto.copy(completada = true)

            // Sumamos los puntos al perfil
            usuarioActual = usuarioActual?.let {
                it.copy(puntaje = it.puntaje + punto.puntosRecompensa)
            }
            return true
        }
        return false
    }

    fun simularCapturaGps() {
        formLatitud = "-33.44" + (10..99).random()
        formLongitud = "-70.65" + (10..99).random()
        formExitoMensaje = "Coordenadas GPS actualizadas"
    }

    fun guardarPuntoTerreno(): Boolean {
        formErrorMensaje = null
        formExitoMensaje = null

        // Validamos que los campos obligatorios no estén vacíos
        if (formNombre.trim().isEmpty()) {
            formErrorMensaje = "El nombre del punto es obligatorio"
            return false
        }
        if (formDescripcion.trim().isEmpty()) {
            formErrorMensaje = "Debes ingresar una breve descripción"
            return false
        }

        // Validamos que las coordenadas sean números
        val lat = formLatitud.toDoubleOrNull()
        val lon = formLongitud.toDoubleOrNull()

        if (lat == null || lon == null) {
            formErrorMensaje = "Las coordenadas deben tener formato numérico (ej: -33.4475)"
            return false
        }

        // Creamos el nuevo punto
        val nuevoPunto = PuntoInteres(
            id = (_puntosSantiago.maxOfOrNull { it.id } ?: 0) + 1,
            nombre = formNombre.trim(),
            descripcion = formDescripcion.trim(),
            comuna = formComuna,
            latitud = lat,
            longitud = lon,
            categoriaIA = formCategoria,
            puntosRecompensa = 50,
            completada = false,
            codigoQR = "QR-TERRENO-" + (100..999).random()
        )

        _puntosSantiago.add(0, nuevoPunto)

        // Limpiamos los campos
        formNombre = ""
        formDescripcion = ""
        formExitoMensaje = "¡Punto registrado con éxito!"
        return true
    }

    fun sincronizarDatos() {
        sincronizando = true
    }

    fun finalizarSincronizacion() {
        sincronizando = false
    }
}
