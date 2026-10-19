package cl.duoc.aprendoia.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIADbHelper
import cl.duoc.aprendoia.model.CapsulaIA
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario
import cl.duoc.aprendoia.model.VideoIA
import org.json.JSONObject

class AprendoIAViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = AprendoIADbHelper(application.applicationContext)

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    var estaAutenticado by mutableStateOf(true)

    var videoQREscaneado by mutableStateOf<VideoIA?>(null)
    var mensajeErrorQR by mutableStateOf<String?>(null)
    var mensajeExitoQR by mutableStateOf<String?>(null)

    private val _puntosSantiago = mutableStateListOf<PuntoInteres>()
    val puntosSantiago: List<PuntoInteres> get() = _puntosSantiago

    val capsulasDisponibles = listOf(
        CapsulaIA(1, "¿Qué es una Red Neuronal?", "Fundamentos", 3, 20, "Explicación básica de cómo aprende un modelo a reconocer patrones."),
        CapsulaIA(2, "Reconocimiento de Imágenes", "Visión Artificial", 5, 30, "Cómo una app puede identificar lugares históricos usando la cámara."),
        CapsulaIA(3, "Ética y Datos en Santiago", "Ética en IA", 4, 25, "Cuidado con los datos personales al usar cámaras y sensores en la calle.")
    )

    var loginEmailError by mutableStateOf<String?>(null)
    var loginPasswordError by mutableStateOf<String?>(null)

    var formNombre by mutableStateOf("")
    var formComuna by mutableStateOf("Santiago Centro")
    var formCategoria by mutableStateOf("Visión Artificial")
    var formLatitud by mutableStateOf("-33.4475")
    var formLongitud by mutableStateOf("-70.6558")
    var formDescripcion by mutableStateOf("")

    var formErrorNombre by mutableStateOf<String?>(null)
    var formErrorDescripcion by mutableStateOf<String?>(null)
    var formErrorCoordenadas by mutableStateOf<String?>(null)
    var formErrorGeneral by mutableStateOf<String?>(null)
    var formExitoMensaje by mutableStateOf<String?>(null)

    var sincronizando by mutableStateOf(false)

    init {
        cargarDatosDesdeSQLite()
    }

    private fun cargarDatosDesdeSQLite() {
        val usuarioDb = dbHelper.obtenerUsuario()
        if (usuarioDb != null) {
            usuarioActual = usuarioDb
        } else {
            val userDefault = Usuario(
                email = "test@duocuc.cl",
                nombre = "Estudiante",
                rol = "Estudiante",
                puntaje = 150
            )
            usuarioActual = userDefault
            dbHelper.guardarOActualizarUsuario(userDefault)
        }

        _puntosSantiago.clear()
        val puntosDb = dbHelper.obtenerTodosLosPuntos()
        _puntosSantiago.addAll(puntosDb)
    }

    fun validarLogin(email: String, clave: String): Boolean {
        loginEmailError = null
        loginPasswordError = null

        var esValido = true

        if (email.isBlank()) {
            loginEmailError = "El correo no puede estar vacío"
            esValido = false
        } else if (!email.contains("@") || !email.contains(".")) {
            loginEmailError = "Ingresa un formato de correo válido (ej: usuario@duocuc.cl)"
            esValido = false
        }

        if (clave.isBlank()) {
            loginPasswordError = "La contraseña es obligatoria"
            esValido = false
        } else if (clave.length < 3) {
            loginPasswordError = "La contraseña debe tener al menos 3 caracteres"
            esValido = false
        }

        if (esValido) {
            iniciarSesion(email, clave)
        }
        return esValido
    }

    fun iniciarSesion(email: String, clave: String): Boolean {
        val nombreUsuario = if (email.contains("@")) {
            email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
        } else {
            "Estudiante"
        }

        val usuario = Usuario(
            email = email.trim(),
            nombre = nombreUsuario,
            rol = "Estudiante",
            puntaje = usuarioActual?.puntaje ?: 150
        )
        usuarioActual = usuario
        estaAutenticado = true
        dbHelper.guardarOActualizarUsuario(usuario)
        return true
    }

    fun cerrarSesion() {
        estaAutenticado = false
        usuarioActual = null
    }

    fun completarMision(puntoId: Int): Boolean {
        val index = _puntosSantiago.indexOfFirst { it.id == puntoId }
        if (index != -1 && !_puntosSantiago[index].completada) {
            val punto = _puntosSantiago[index]
            val puntoActualizado = punto.copy(completada = true)
            _puntosSantiago[index] = puntoActualizado

            usuarioActual = usuarioActual?.let {
                val nuevoPuntaje = it.puntaje + punto.puntosRecompensa
                val userActualizado = it.copy(puntaje = nuevoPuntaje)
                dbHelper.guardarOActualizarUsuario(userActualizado)
                userActualizado
            }

            dbHelper.actualizarPuntoCompletado(puntoId, completada = true, selfieTomada = punto.selfieTomada)
            return true
        }
        return false
    }

    fun procesarContenidoQR(rawText: String, puntoId: Int? = null): Boolean {
        mensajeErrorQR = null
        mensajeExitoQR = null

        return try {
            val json = JSONObject(rawText.trim())
            val titulo = json.optString("titulo").ifBlank {
                json.optString("lugar").ifBlank {
                    json.optString("nombre", "")
                }
            }
            val descripcion = json.optString("descripcion").ifBlank {
                "Cápsula educativa y validación turística de Santiago con IA."
            }
            val url = json.optString("url").ifBlank {
                "https://www.youtube.com/shorts/sample1"
            }

            if (titulo.isNotBlank()) {
                val video = VideoIA(titulo = titulo, descripcion = descripcion, url = url)
                videoQREscaneado = video
                mensajeExitoQR = "¡Código QR validado exitosamente! (+40 pts)"

                if (puntoId != null) {
                    completarMision(puntoId)
                } else {
                    usuarioActual = usuarioActual?.let {
                        val nuevoPuntaje = it.puntaje + 40
                        val act = it.copy(puntaje = nuevoPuntaje)
                        dbHelper.guardarOActualizarUsuario(act)
                        act
                    }
                }
                true
            } else {
                mensajeErrorQR = "Se leyó el QR, pero no contiene un JSON válido"
                false
            }
        } catch (e: Exception) {
            mensajeErrorQR = "Se leyó el QR, pero no contiene un JSON válido"
            false
        }
    }

    fun limpiarEstadoQR() {
        videoQREscaneado = null
        mensajeErrorQR = null
        mensajeExitoQR = null
    }

    fun obtenerJsonEjemplo(punto: PuntoInteres): String {
        return """{"titulo":"${punto.nombre}","descripcion":"${punto.descripcion}","url":"${punto.urlVideo}"}"""
    }

    fun registrarSelfie(puntoId: Int, bitmap: Bitmap) {
        val index = _puntosSantiago.indexOfFirst { it.id == puntoId }
        if (index != -1) {
            val punto = _puntosSantiago[index]
            val puntosExtra = 30
            val puntoActualizado = punto.copy(
                completada = true,
                selfieTomada = true,
                selfieBitmap = bitmap
            )
            _puntosSantiago[index] = puntoActualizado

            usuarioActual = usuarioActual?.let {
                val nuevoPuntaje = it.puntaje + if (!punto.completada) punto.puntosRecompensa + puntosExtra else puntosExtra
                val userActualizado = it.copy(puntaje = nuevoPuntaje)
                dbHelper.guardarOActualizarUsuario(userActualizado)
                userActualizado
            }

            dbHelper.actualizarPuntoCompletado(puntoId, completada = true, selfieTomada = true)
        }
    }

    fun actualizarCoordenadasGps(latitud: Double, longitud: Double) {
        formLatitud = String.format(java.util.Locale.US, "%.5f", latitud)
        formLongitud = String.format(java.util.Locale.US, "%.5f", longitud)
        formErrorCoordenadas = null
        formExitoMensaje = "Coordenadas GPS reales obtenidas con éxito"
    }

    fun guardarPuntoTerreno(): Boolean {
        formErrorNombre = null
        formErrorDescripcion = null
        formErrorCoordenadas = null
        formErrorGeneral = null
        formExitoMensaje = null

        var esValido = true

        if (formNombre.trim().isEmpty()) {
            formErrorNombre = "El nombre del lugar es obligatorio"
            esValido = false
        }

        if (formDescripcion.trim().isEmpty()) {
            formErrorDescripcion = "Debes ingresar una descripción de la actividad"
            esValido = false
        } else if (formDescripcion.trim().length < 10) {
            formErrorDescripcion = "La descripción debe tener al menos 10 caracteres"
            esValido = false
        }

        val lat = formLatitud.toDoubleOrNull()
        val lon = formLongitud.toDoubleOrNull()
        if (lat == null || lon == null) {
            formErrorCoordenadas = "Las coordenadas deben ser números válidos (ej: -33.4475)"
            esValido = false
        }

        if (!esValido) {
            formErrorGeneral = "Corrige los errores antes de guardar"
            return false
        }

        val nuevoPunto = PuntoInteres(
            id = (_puntosSantiago.maxOfOrNull { it.id } ?: 0) + 1,
            nombre = formNombre.trim(),
            descripcion = formDescripcion.trim(),
            comuna = formComuna,
            latitud = lat!!,
            longitud = lon!!,
            categoriaIA = formCategoria,
            puntosRecompensa = 50,
            completada = false,
            codigoQR = "QR-TERRENO-" + (100..999).random(),
            selfieTomada = false,
            selfieBitmap = null,
            urlVideo = "https://www.youtube.com/shorts/sample"
        )

        val guardadoEnDb = dbHelper.insertarPunto(nuevoPunto)
        if (guardadoEnDb) {
            _puntosSantiago.add(0, nuevoPunto)
            formNombre = ""
            formDescripcion = ""
            formExitoMensaje = "¡Punto guardado en SQLite con éxito!"
            return true
        } else {
            formErrorGeneral = "Error al guardar en la base de datos local"
            return false
        }
    }

    fun sincronizarDatos() {
        sincronizando = true
    }

    fun finalizarSincronizacion() {
        sincronizando = false
    }
}
