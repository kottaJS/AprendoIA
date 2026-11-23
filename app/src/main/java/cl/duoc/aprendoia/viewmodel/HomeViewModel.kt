package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val usuarioActual: Usuario?
        get() = repository.usuarioActual

    val puntosSantiago: List<PuntoInteres>
        get() = repository.puntosSantiago

    val puntosDescubiertos: Int
        get() = repository.puntosSantiago.count { it.completada }

    val totalPuntos: Int
        get() = repository.puntosSantiago.size

    val videosVistos: Int
        get() = repository.puntosSantiago.count { it.videoVisto }

    val selfiesTomadas: Int
        get() = repository.puntosSantiago.count { it.selfieTomada }

    val progresoPorcentaje: Float
        get() = if (totalPuntos > 0) (puntosDescubiertos.toFloat() / totalPuntos) else 0f

    val climaActual: String
        get() = repository.climaActual

    val estadoConexionBackend: String
        get() = repository.estadoConexionBackend

    val sincronizando: Boolean
        get() = repository.sincronizando

    fun sincronizarConBackend(onResultado: (Boolean, String) -> Unit = { _, _ -> }) {
        repository.sincronizarConBackend(onResultado)
    }

    val desafioPunto: PuntoInteres?
        get() = repository.puntosSantiago.firstOrNull { it.nombre.contains("Moneda", ignoreCase = true) }
            ?: repository.puntosSantiago.firstOrNull { !it.completada }
            ?: repository.puntosSantiago.firstOrNull()

    fun seleccionarPunto(punto: PuntoInteres) {
        repository.seleccionarPunto(punto)
    }

    fun recargarDatos() {
        repository.recargarPuntos()
    }
}
