package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres
import java.util.Locale

class FormularioAdminViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

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

        val exito = repository.guardarPuntoTerreno(nuevoPunto)
        if (exito) {
            formNombre = ""
            formDescripcion = ""
            formVideoTitulo = ""
            formExitoMensaje = "¡Punto creado exitosamente!"
            return true
        } else {
            formErrorGeneral = "Error al insertar punto en base de datos"
            return false
        }
    }
}
