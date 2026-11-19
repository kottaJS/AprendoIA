package cl.duoc.aprendoia.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.HistorialItem
import cl.duoc.aprendoia.model.NotificacionItem
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario
import cl.duoc.aprendoia.model.VideoIA
import java.util.Locale

class AprendoIAViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val usuarios: List<Usuario>
        get() = repository.usuarios

    val usuarioActual: Usuario?
        get() = repository.usuarioActual

    val estaAutenticado: Boolean
        get() = repository.estaAutenticado

    val puntosSantiago: List<PuntoInteres>
        get() = repository.puntosSantiago

    val historial: List<HistorialItem>
        get() = repository.historial

    val notificaciones: List<NotificacionItem>
        get() = repository.notificaciones

    var puntoSeleccionado: PuntoInteres?
        get() = repository.puntoSeleccionado
        set(value) { repository.seleccionarPunto(value) }

    val videoQREscaneado: VideoIA?
        get() = repository.videoQREscaneado

    val mensajeErrorQR: String?
        get() = repository.mensajeErrorQR

    val mensajeExitoQR: String?
        get() = repository.mensajeExitoQR

    var sincronizando: Boolean
        get() = repository.sincronizando
        set(value) { repository.sincronizando = value }

    // Campos del formulario Admin de nuevo punto
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

    fun sincronizarDatos() {
        repository.sincronizarDatos()
    }

    fun finalizarSincronizacion() {
        repository.finalizarSincronizacion()
    }

    fun recargarUsuarios() {
        repository.recargarUsuarios()
    }

    fun recargarPuntos() {
        repository.recargarPuntos()
    }

    fun recargarHistorial() {
        repository.recargarHistorial()
    }

    fun recargarNotificaciones() {
        repository.recargarNotificaciones()
    }

    fun iniciarSesionConCredenciales(emailOrAlias: String, clave: String): Boolean {
        return repository.iniciarSesion(emailOrAlias, clave)
    }

    fun iniciarSesionUsuario(usuario: Usuario) {
        repository.iniciarSesionDirecto(usuario)
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
    }

    fun registrarNuevoJugador(
        nombre: String,
        email: String,
        telefono: String,
        clave: String,
        direccion: String,
        avatar: String
    ): Boolean {
        return repository.registrarNuevoJugador(nombre, email, telefono, clave, direccion, avatar)
    }

    fun actualizarPerfilActual(
        nombre: String,
        email: String,
        telefono: String,
        direccion: String,
        avatar: String
    ): Boolean {
        return repository.actualizarPerfil(nombre, email, telefono, direccion, avatar)
    }

    fun eliminarJugadorAdmin(usuarioId: Int): Boolean {
        return repository.eliminarJugador(usuarioId)
    }

    fun procesarContenidoQR(rawText: String, puntoIdSugerido: Int? = null): Boolean {
        return repository.procesarContenidoQR(rawText, puntoIdSugerido)
    }

    fun registrarSelfie(puntoId: Int, bitmap: Bitmap? = null) {
        repository.registrarSelfie(puntoId, null)
    }

    fun registrarSelfieTuristica(puntoId: Int, uriFoto: String?): Boolean {
        return repository.registrarSelfie(puntoId, uriFoto)
    }

    fun marcarVideoVisto(puntoId: Int): Boolean {
        return repository.marcarVideoVisto(puntoId)
    }

    fun limpiarEstadoQR() {
        repository.limpiarEstadoQR()
    }

    fun obtenerJsonEjemplo(punto: PuntoInteres): String {
        return """{"titulo":"${punto.nombre}","descripcion":"${punto.descripcion}","url":"${punto.urlVideo}"}"""
    }

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

        val nuevoId = (repository.puntosSantiago.maxOfOrNull { it.id } ?: 0) + 1
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

        val insertado = repository.guardarPuntoTerreno(nuevoPunto)
        if (insertado) {
            formNombre = ""
            formDescripcion = ""
            formVideoTitulo = ""
            formExitoMensaje = "¡Punto creado exitosamente!"
            return true
        }
        return false
    }
}
