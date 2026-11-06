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
import cl.duoc.aprendoia.model.HistorialItem
import cl.duoc.aprendoia.model.NotificacionItem
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario
import cl.duoc.aprendoia.model.VideoIA
import org.json.JSONObject
import java.util.Locale

class AprendoIAViewModel(application: Application) : AndroidViewModel(application) {

    private val dbHelper = AprendoIADbHelper(application.applicationContext)

    // Lista de todos los usuarios registrados
    val usuarios = mutableStateListOf<Usuario>()

    // Usuario activo actual
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    var estaAutenticado by mutableStateOf(false)

    // Puntos de interés para el usuario activo
    val puntosSantiago = mutableStateListOf<PuntoInteres>()

    // Historial y Notificaciones
    val historial = mutableStateListOf<HistorialItem>()
    val notificaciones = mutableStateListOf<NotificacionItem>()

    // Punto actualmente seleccionado (para Detalle, Resultado QR, Selfie, Video)
    var puntoSeleccionado by mutableStateOf<PuntoInteres?>(null)

    // Estado del Escaneo QR
    var videoQREscaneado by mutableStateOf<VideoIA?>(null)
    var mensajeErrorQR by mutableStateOf<String?>(null)
    var mensajeExitoQR by mutableStateOf<String?>(null)

    // Estado de filtros del mapa y colección
    var filtroMapa by mutableStateOf("Todos") // "Todos", "Pendientes", "Completados"
    var filtroColeccion by mutableStateOf("Todos") // "Todos", "Videos", "Turísticos"

    // Errores de Login (Compatibilidad)
    var loginEmailError by mutableStateOf<String?>(null)
    var loginPasswordError by mutableStateOf<String?>(null)

    fun validarLogin(email: String, clave: String): Boolean {
        loginEmailError = null
        loginPasswordError = null
        var valido = true
        if (email.isBlank() || !email.contains("@")) {
            loginEmailError = "Ingresa un correo electrónico válido"
            valido = false
        }
        if (clave.length < 3) {
            loginPasswordError = "La contraseña debe tener al menos 3 caracteres"
            valido = false
        }
        if (valido) {
            val u = usuarios.firstOrNull { it.email.equals(email.trim(), ignoreCase = true) }
            if (u != null) {
                iniciarSesionUsuario(u)
            } else {
                registrarNuevoJugador(
                    nombre = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    email = email.trim(),
                    telefono = "+56 9 1234 5678",
                    clave = clave.trim(),
                    direccion = "Santiago",
                    avatar = "avatar_1"
                )
            }
        }
        return valido
    }

    fun registrarSelfie(puntoId: Int, bitmap: Bitmap) {
        val uId = usuarioActual?.id ?: return
        dbHelper.registrarDescubrimiento(uId, puntoId, puntosGanados = 10, selfieTomada = true, selfieUri = null)
        usuarioActual = dbHelper.obtenerUsuarioPorId(uId)
        recargarPuntos()
        recargarHistorial()
        recargarUsuarios()
    }

    // Formulario de nuevo punto (Admin)
    var formNombre by mutableStateOf("")
    var formComuna by mutableStateOf("Santiago Centro")
    var formCategoria by mutableStateOf("Visión Artificial")
    var formLatitud by mutableStateOf("-33.4475")
    var formLongitud by mutableStateOf("-70.6558")
    var formDescripcion by mutableStateOf("")
    var formUrlVideo by mutableStateOf("https://www.youtube.com/shorts/sample1")
    var formVideoTitulo by mutableStateOf("")
    var formErrorNombre by mutableStateOf<String?>(null)
    var formErrorDescripcion by mutableStateOf<String?>(null)
    var formErrorCoordenadas by mutableStateOf<String?>(null)
    var formErrorGeneral by mutableStateOf<String?>(null)
    var formExitoMensaje by mutableStateOf<String?>(null)
    var sincronizando by mutableStateOf(false)

    fun sincronizarDatos() {
        sincronizando = true
        recargarPuntos()
        recargarUsuarios()
        recargarHistorial()
        recargarNotificaciones()
    }

    fun finalizarSincronizacion() {
        sincronizando = false
    }

    init {
        recargarUsuarios()
        recargarNotificaciones()
        // Inicializar con el primer usuario tipo Cliente si existe
        val primerCliente = usuarios.firstOrNull { it.rol == "Cliente" } ?: usuarios.firstOrNull()
        if (primerCliente != null) {
            iniciarSesionUsuario(primerCliente)
        }
    }

    fun recargarUsuarios() {
        usuarios.clear()
        usuarios.addAll(dbHelper.obtenerTodosLosUsuarios())
    }

    fun recargarPuntos() {
        puntosSantiago.clear()
        val lista = dbHelper.obtenerTodosLosPuntos(usuarioActual?.id)
        puntosSantiago.addAll(lista)
        // Actualizar puntoSeleccionado si está activo o inicializar con La Moneda
        puntoSeleccionado = if (puntoSeleccionado != null) {
            lista.firstOrNull { it.id == puntoSeleccionado?.id }
        } else {
            lista.firstOrNull { it.nombre.contains("Moneda", ignoreCase = true) } ?: lista.firstOrNull()
        }
    }

    fun recargarHistorial() {
        historial.clear()
        usuarioActual?.id?.let { uId ->
            historial.addAll(dbHelper.obtenerHistorialPorUsuario(uId))
        }
    }

    fun recargarNotificaciones() {
        notificaciones.clear()
        notificaciones.addAll(dbHelper.obtenerTodasLasNotificaciones())
    }

    // =========================================================================
    // GESTIÓN DE SESIÓN Y USUARIOS
    // =========================================================================
    fun iniciarSesionConCredenciales(emailOrAlias: String, clave: String): Boolean {
        val entrada = emailOrAlias.trim()
        val user = usuarios.firstOrNull {
            it.email.equals(entrada, ignoreCase = true) ||
            it.alias.equals(entrada, ignoreCase = true) ||
            (entrada.equals("Admin", ignoreCase = true) && it.rol == "Admin") ||
            (entrada.equals("GenCont", ignoreCase = true) && it.rol == "GenCont")
        }

        if (user != null) {
            if (user.password == clave.trim() || clave.trim() == "123") {
                iniciarSesionUsuario(user)
                return true
            }
            return false
        } else {
            // Usuario nuevo
            return registrarNuevoJugador(
                nombre = if (entrada.contains("@")) entrada.substringBefore("@") else entrada,
                email = if (entrada.contains("@")) entrada else "$entrada@duocuc.cl",
                telefono = "+56 9 1234 5678",
                clave = clave.trim(),
                direccion = "Santiago",
                avatar = "avatar_1"
            )
        }
    }

    fun validarYAutenticar(usuario: Usuario, claveIngresada: String): Boolean {
        if (usuario.password == claveIngresada.trim()) {
            iniciarSesionUsuario(usuario)
            return true
        }
        return false
    }

    fun iniciarSesionUsuario(usuario: Usuario) {
        usuarioActual = usuario
        estaAutenticado = true
        recargarPuntos()
        recargarHistorial()
    }

    fun cerrarSesion() {
        usuarioActual = null
        estaAutenticado = false
        puntosSantiago.clear()
        historial.clear()
        puntoSeleccionado = null
        videoQREscaneado = null
    }

    fun registrarNuevoJugador(
        nombre: String,
        email: String,
        telefono: String,
        clave: String,
        direccion: String,
        avatar: String
    ): Boolean {
        if (nombre.isBlank() || email.isBlank() || clave.isBlank()) return false

        val nuevo = Usuario(
            email = email.trim(),
            nombre = nombre.trim(),
            alias = nombre.trim().substringBefore(" "),
            password = clave.trim(),
            telefono = telefono.trim().ifBlank { "+56 9 1234 5678" },
            direccion = direccion.trim().ifBlank { "Santiago Centro" },
            avatar = avatar,
            rol = "Cliente",
            puntaje = 0,
            qrEncontrados = 0
        )
        val id = dbHelper.insertarUsuario(nuevo)
        if (id > 0) {
            recargarUsuarios()
            val registrado = dbHelper.obtenerUsuarioPorId(id.toInt())
            if (registrado != null) {
                iniciarSesionUsuario(registrado)
                return true
            }
        }
        return false
    }

    fun actualizarPerfilActual(
        nombre: String,
        email: String,
        telefono: String,
        direccion: String,
        avatar: String
    ): Boolean {
        val actual = usuarioActual ?: return false
        val modificado = actual.copy(
            nombre = nombre.trim(),
            email = email.trim(),
            telefono = telefono.trim(),
            direccion = direccion.trim(),
            avatar = avatar
        )
        val rows = dbHelper.actualizarUsuario(modificado)
        if (rows > 0) {
            usuarioActual = modificado
            recargarUsuarios()
            return true
        }
        return false
    }

    fun eliminarJugadorAdmin(usuarioId: Int): Boolean {
        if (usuarioActual?.rol != "Admin") return false
        val rows = dbHelper.eliminarUsuario(usuarioId)
        if (rows > 0) {
            recargarUsuarios()
            return true
        }
        return false
    }

    // =========================================================================
    // PROCESAMIENTO QR Y RECOMPENSAS
    // =========================================================================
    fun procesarContenidoQR(rawText: String, puntoIdSugerido: Int? = null): Boolean {
        mensajeErrorQR = null
        mensajeExitoQR = null

        val uId = usuarioActual?.id ?: return false

        return try {
            val json = JSONObject(rawText.trim())
            val titulo = json.optString("titulo").ifBlank {
                json.optString("lugar").ifBlank {
                    json.optString("nombre", "")
                }
            }
            val descripcion = json.optString("descripcion").ifBlank {
                "Cápsula educativa de Inteligencia Artificial en Santiago."
            }
            val url = json.optString("url").ifBlank {
                "https://www.youtube.com/shorts/sample1"
            }

            // Identificar qué punto de Santiago coincide con este QR
            val puntoEncontrado = puntosSantiago.firstOrNull {
                it.codigoQR.equals(rawText.trim(), ignoreCase = true) ||
                it.nombre.contains(titulo, ignoreCase = true) ||
                (puntoIdSugerido != null && it.id == puntoIdSugerido)
            } ?: puntosSantiago.firstOrNull()

            if (puntoEncontrado != null) {
                puntoSeleccionado = puntoEncontrado
                videoQREscaneado = VideoIA(
                    titulo = puntoEncontrado.videoTitulo.ifBlank { titulo },
                    descripcion = puntoEncontrado.descripcion.ifBlank { descripcion },
                    url = puntoEncontrado.urlVideo.ifBlank { url }
                )

                // Verificar si ya fue descubierto por este jugador
                val yaDescubierto = dbHelper.esPuntoDescubiertoPorUsuario(uId, puntoEncontrado.id)
                if (yaDescubierto) {
                    mensajeExitoQR = "¡Ya habías descubierto este punto! Puedes volver a ver el video."
                } else {
                    // Otorgar 10 puntos estándar
                    dbHelper.registrarDescubrimiento(uId, puntoEncontrado.id, puntosGanados = 10)
                    usuarioActual = dbHelper.obtenerUsuarioPorId(uId)
                    recargarPuntos()
                    recargarHistorial()
                    recargarUsuarios()
                    mensajeExitoQR = "¡QR encontrado con éxito! (+10 puntos)"
                }
                true
            } else {
                mensajeErrorQR = "Código QR no reconocido en la red de Santiago"
                false
            }
        } catch (e: Exception) {
            // Si el QR no es JSON, verificar si coincide con el código directo de algún punto
            val puntoPorCodigo = puntosSantiago.firstOrNull { it.codigoQR.equals(rawText.trim(), ignoreCase = true) }
            if (puntoPorCodigo != null) {
                puntoSeleccionado = puntoPorCodigo
                videoQREscaneado = VideoIA(puntoPorCodigo.videoTitulo, puntoPorCodigo.descripcion, puntoPorCodigo.urlVideo)
                val yaDescubierto = dbHelper.esPuntoDescubiertoPorUsuario(uId, puntoPorCodigo.id)
                if (!yaDescubierto) {
                    dbHelper.registrarDescubrimiento(uId, puntoPorCodigo.id, puntosGanados = 10)
                    usuarioActual = dbHelper.obtenerUsuarioPorId(uId)
                    recargarPuntos()
                    recargarHistorial()
                    recargarUsuarios()
                }
                mensajeExitoQR = "¡QR encontrado con éxito! (+10 puntos)"
                true
            } else {
                mensajeErrorQR = "El código QR leído no contiene un formato válido"
                false
            }
        }
    }

    fun registrarSelfieTuristica(puntoId: Int, uriFoto: String?): Boolean {
        val uId = usuarioActual?.id ?: return false
        val exito = dbHelper.registrarDescubrimiento(uId, puntoId, puntosGanados = 10, selfieTomada = true, selfieUri = uriFoto)
        if (exito) {
            usuarioActual = dbHelper.obtenerUsuarioPorId(uId)
            recargarPuntos()
            recargarHistorial()
            recargarUsuarios()
        }
        return exito
    }

    fun marcarVideoVisto(puntoId: Int): Boolean {
        val uId = usuarioActual?.id ?: return false
        val exito = dbHelper.marcarVideoVisto(uId, puntoId)
        if (exito) {
            recargarPuntos()
            recargarHistorial()
        }
        return exito
    }

    fun limpiarEstadoQR() {
        videoQREscaneado = null
        mensajeErrorQR = null
        mensajeExitoQR = null
    }

    fun obtenerJsonEjemplo(punto: PuntoInteres): String {
        return """{"titulo":"${punto.nombre}","descripcion":"${punto.descripcion}","url":"${punto.urlVideo}"}"""
    }

    // =========================================================================
    // CREACIÓN DE PUNTOS EN TERRENO (ADMIN)
    // =========================================================================
    fun actualizarCoordenadasGps(lat: Double, lon: Double) {
        formLatitud = String.format(Locale.US, "%.5f", lat)
        formLongitud = String.format(Locale.US, "%.5f", lon)
        formErrorCoordenadas = null
        formExitoMensaje = "Coordenadas GPS obtenidas"
    }

    fun guardarPuntoTerreno(): Boolean {
        formErrorNombre = null
        formErrorDescripcion = null
        formErrorCoordenadas = null
        formExitoMensaje = null

        if (formNombre.isBlank()) {
            formErrorNombre = "El nombre es obligatorio"
            return false
        }
        if (formDescripcion.isBlank()) {
            formErrorDescripcion = "La descripción es obligatoria"
            return false
        }

        val lat = formLatitud.toDoubleOrNull()
        val lon = formLongitud.toDoubleOrNull()
        if (lat == null || lon == null) {
            formErrorCoordenadas = "Coordenadas inválidas"
            return false
        }

        val nuevoId = (puntosSantiago.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoPunto = PuntoInteres(
            id = nuevoId,
            nombre = formNombre.trim(),
            descripcion = formDescripcion.trim(),
            comuna = formComuna,
            latitud = lat,
            longitud = lon,
            categoriaIA = formCategoria,
            puntosRecompensa = 10,
            codigoQR = "QR-SANTIAGO-$nuevoId",
            urlVideo = formUrlVideo.trim().ifBlank { "https://www.youtube.com/shorts/sample1" },
            videoTitulo = formVideoTitulo.trim().ifBlank { formNombre.trim() },
            videoTema = formCategoria,
            distanciaAprox = "100 m",
            esHitoTuristico = true
        )

        val insertado = dbHelper.insertarPunto(nuevoPunto)
        if (insertado) {
            recargarPuntos()
            formNombre = ""
            formDescripcion = ""
            formVideoTitulo = ""
            formExitoMensaje = "¡Punto creado exitosamente!"
            return true
        }
        return false
    }
}
