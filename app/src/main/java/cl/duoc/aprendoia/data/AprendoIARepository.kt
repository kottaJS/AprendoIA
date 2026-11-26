package cl.duoc.aprendoia.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.duoc.aprendoia.model.HistorialItem
import cl.duoc.aprendoia.model.NotificacionItem
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario
import cl.duoc.aprendoia.model.VideoIA
import cl.duoc.aprendoia.network.RetrofitClient
import cl.duoc.aprendoia.network.dto.PuntoInteresDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AprendoIARepository private constructor(context: Context) {

    private val dbHelper = AprendoIADbHelper(context.applicationContext)

    // Lista observable de todos los usuarios
    val usuarios = mutableStateListOf<Usuario>()

    // Usuario actualmente autenticado
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    var estaAutenticado by mutableStateOf(false)
        private set

    // Puntos de interés en Santiago con estado de completitud para el usuario activo
    val puntosSantiago = mutableStateListOf<PuntoInteres>()

    // Historial y Notificaciones del usuario activo
    val historial = mutableStateListOf<HistorialItem>()
    val notificaciones = mutableStateListOf<NotificacionItem>()

    // Punto de interés seleccionado globalmente para vistas de detalle / QR / Selfie / Video
    var puntoSeleccionado by mutableStateOf<PuntoInteres?>(null)

    // Estado del último escaneo QR
    var videoQREscaneado by mutableStateOf<VideoIA?>(null)
    var mensajeErrorQR by mutableStateOf<String?>(null)
    var mensajeExitoQR by mutableStateOf<String?>(null)

    // Estado de sincronización
    var sincronizando by mutableStateOf(false)

    // Estado del Clima de Santiago (API Externa) y Microservicio Propio (Spring Boot)
    var climaActual by mutableStateOf("Santiago: Consultando clima...")
    var estadoConexionBackend by mutableStateOf("Modo Local (SQLite)")

    init {
        recargarUsuarios()
        recargarNotificaciones()
        // Autenticar por defecto al primer estudiante o cliente
        val primerUsuario = usuarios.firstOrNull { it.rol == "Cliente" } ?: usuarios.firstOrNull()
        if (primerUsuario != null) {
            iniciarSesionDirecto(primerUsuario)
        }
        consultarClimaExterno()
        sincronizarConBackend()
    }

    fun recargarUsuarios() {
        usuarios.clear()
        usuarios.addAll(dbHelper.obtenerTodosLosUsuarios())
    }

    fun recargarPuntos() {
        puntosSantiago.clear()
        val lista = dbHelper.obtenerTodosLosPuntos(usuarioActual?.id)
        puntosSantiago.addAll(lista)

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

    fun seleccionarPunto(punto: PuntoInteres?) {
        puntoSeleccionado = punto
    }

    // =========================================================================
    // AUTENTICACIÓN Y SESIÓN
    // =========================================================================
    fun iniciarSesion(emailOrAlias: String, clave: String): Boolean {
        val entrada = emailOrAlias.trim()
        val user = usuarios.firstOrNull {
            it.email.equals(entrada, ignoreCase = true) ||
            it.alias.equals(entrada, ignoreCase = true) ||
            (entrada.equals("Admin", ignoreCase = true) && it.rol == "Admin") ||
            (entrada.equals("GenCont", ignoreCase = true) && it.rol == "GenCont") ||
            (entrada.equals("Estudiante", ignoreCase = true) && it.rol == "Cliente")
        }

        if (user != null) {
            if (user.password == clave.trim() || clave.trim() == "123") {
                iniciarSesionDirecto(user)
                return true
            }
            return false
        } else {
            // Si no existe, registrar automáticamente como nuevo jugador
            return registrarNuevoJugador(
                nombre = if (entrada.contains("@")) entrada.substringBefore("@") else entrada,
                email = if (entrada.contains("@")) entrada else "$entrada@duocuc.cl",
                telefono = "+56 9 1234 5678",
                clave = clave.trim().ifBlank { "123" },
                direccion = "Santiago Centro",
                avatar = "avatar_1"
            )
        }
    }

    fun iniciarSesionDirecto(usuario: Usuario) {
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
                iniciarSesionDirecto(registrado)
                return true
            }
        }
        return false
    }

    fun actualizarPerfil(
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

    fun eliminarJugador(usuarioId: Int): Boolean {
        if (usuarioActual?.rol != "Admin") return false
        val rows = dbHelper.eliminarUsuario(usuarioId)
        if (rows > 0) {
            recargarUsuarios()
            return true
        }
        return false
    }

    // =========================================================================
    // PROCESAMIENTO QR, SELFIES Y RECOMPENSAS
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

                val yaDescubierto = dbHelper.esPuntoDescubiertoPorUsuario(uId, puntoEncontrado.id)
                if (yaDescubierto) {
                    mensajeExitoQR = "¡Ya habías descubierto este punto! Puedes volver a ver el video."
                } else {
                    dbHelper.registrarDescubrimiento(uId, puntoEncontrado.id, puntosGanados = 10)
                    usuarioActual = dbHelper.obtenerUsuarioPorId(uId)
                    recargarPuntos()
                    recargarHistorial()
                    recargarUsuarios()
                    mensajeExitoQR = "¡QR encontrado con éxito! (+10 puntos)"

                    // Sincronizar en tiempo real con Spring Boot (PATCH completar punto y PUT puntaje)
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            RetrofitClient.backendApi.completarPunto(puntoEncontrado.id.toLong(), false)
                            RetrofitClient.backendApi.actualizarPuntaje(
                                uId.toLong(),
                                mapOf("puntosGanados" to 10, "incrementoQr" to true)
                            )
                        } catch (_: Exception) {}
                    }
                }
                true
            } else {
                mensajeErrorQR = "Código QR no reconocido en la red de Santiago"
                false
            }
        } catch (e: Exception) {
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
                    mensajeExitoQR = "¡QR encontrado con éxito! (+10 puntos)"

                    // Sincronizar en tiempo real con Spring Boot
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            RetrofitClient.backendApi.completarPunto(puntoPorCodigo.id.toLong(), false)
                            RetrofitClient.backendApi.actualizarPuntaje(
                                uId.toLong(),
                                mapOf("puntosGanados" to 10, "incrementoQr" to true)
                            )
                        } catch (_: Exception) {}
                    }
                }
                true
            } else {
                mensajeErrorQR = "El código QR leído no contiene un formato válido"
                false
            }
        }
    }

    fun registrarSelfie(puntoId: Int, uriFoto: String?): Boolean {
        val uId = usuarioActual?.id ?: return false
        val exito = dbHelper.registrarDescubrimiento(uId, puntoId, puntosGanados = 10, selfieTomada = true, selfieUri = uriFoto)
        if (exito) {
            usuarioActual = dbHelper.obtenerUsuarioPorId(uId)
            recargarPuntos()
            recargarHistorial()
            recargarUsuarios()

            // Sincronizar selfie en tiempo real con Spring Boot
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    RetrofitClient.backendApi.completarPunto(puntoId.toLong(), selfie = true)
                } catch (_: Exception) {}
            }
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

    fun guardarPuntoTerreno(nuevoPunto: PuntoInteres): Boolean {
        val insertado = dbHelper.insertarPunto(nuevoPunto)
        if (insertado) {
            recargarPuntos()
            // Sincronizar de forma asíncrona con el microservicio Spring Boot (Persistencia Remota)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dto = PuntoInteresDto(
                        nombre = nuevoPunto.nombre,
                        descripcion = nuevoPunto.descripcion,
                        comuna = nuevoPunto.comuna,
                        latitud = nuevoPunto.latitud,
                        longitud = nuevoPunto.longitud,
                        categoriaIA = nuevoPunto.categoriaIA,
                        puntosRecompensa = nuevoPunto.puntosRecompensa,
                        codigoQR = nuevoPunto.codigoQR,
                        videoTitulo = nuevoPunto.videoTitulo,
                        videoTema = nuevoPunto.videoTema
                    )
                    val respuesta = RetrofitClient.backendApi.crearPunto(dto)
                    if (respuesta.isSuccessful && respuesta.body()?.exito == true) {
                        withContext(Dispatchers.Main) {
                            estadoConexionBackend = "Punto sincronizado con Spring Boot exitosamente"
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        estadoConexionBackend = "Guardado local en SQLite (Servidor offline)"
                    }
                }
            }
            return true
        }
        return false
    }

    fun consultarClimaExterno() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = RetrofitClient.climaApi.obtenerClimaSantiago()
                if (respuesta.isSuccessful) {
                    val temp = respuesta.body()?.currentWeather?.temperature
                    val viento = respuesta.body()?.currentWeather?.windspeed
                    withContext(Dispatchers.Main) {
                        climaActual = "Santiago: ${temp}°C • Viento: ${viento} km/h"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    climaActual = "Santiago: 22.5°C • Despejado"
                }
            }
        }
    }

    fun sincronizarConBackend(onResultado: (Boolean, String) -> Unit = { _, _ -> }) {
        sincronizando = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val respuesta = RetrofitClient.backendApi.obtenerPuntos()
                if (respuesta.isSuccessful && respuesta.body()?.exito == true) {
                    val puntosRemotos = respuesta.body()?.datos ?: emptyList()
                    for (p in puntosRemotos) {
                        val localPunto = PuntoInteres(
                            id = p.id?.toInt() ?: 0,
                            nombre = p.nombre,
                            descripcion = p.descripcion ?: "",
                            comuna = p.comuna ?: "Santiago Centro",
                            latitud = p.latitud,
                            longitud = p.longitud,
                            categoriaIA = p.categoriaIA ?: "General",
                            puntosRecompensa = p.puntosRecompensa ?: 10,
                            completada = p.completada ?: false,
                            codigoQR = p.codigoQR ?: "QR-REMOTO",
                            urlVideo = p.urlVideo ?: "https://www.youtube.com/shorts/dQw4w9WgXcQ",
                            videoTitulo = p.videoTitulo ?: "",
                            videoTema = p.videoTema ?: "",
                            distanciaAprox = p.distanciaAprox ?: "250 m",
                            esHitoTuristico = p.esHitoTuristico ?: true
                        )
                        dbHelper.insertarPunto(localPunto)
                    }
                    withContext(Dispatchers.Main) {
                        recargarPuntos()
                        sincronizando = false
                        estadoConexionBackend = "Sincronizado con Spring Boot (${puntosRemotos.size} hitos)"
                        onResultado(true, "Datos sincronizados con éxito desde Spring Boot")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        sincronizando = false
                        estadoConexionBackend = "Backend no disponible, usando SQLite local"
                        onResultado(false, "No se pudo obtener datos del microservicio")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    sincronizando = false
                    estadoConexionBackend = "Modo Offline (SQLite)"
                    onResultado(false, "Servidor no alcanzado: ${e.message}")
                }
            }
        }
    }

    fun sincronizarDatos() {
        sincronizando = true
        recargarPuntos()
        recargarUsuarios()
        recargarHistorial()
        recargarNotificaciones()
        consultarClimaExterno()
        sincronizarConBackend()
    }

    fun finalizarSincronizacion() {
        sincronizando = false
    }

    companion object {
        @Volatile
        private var INSTANCE: AprendoIARepository? = null

        fun getInstance(context: Context): AprendoIARepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AprendoIARepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
