package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres

class ColeccionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    var filtroSeleccionado by mutableStateOf("Todos") // "Todos", "Videos", "Turísticos"

    val puntosDescubiertos: List<PuntoInteres>
        get() = repository.puntosSantiago.filter { it.completada }

    val totalVideos: Int
        get() = puntosDescubiertos.count { it.videoVisto }

    val totalTuristicos: Int
        get() = puntosDescubiertos.count { it.esHitoTuristico }

    val puntosFiltrados: List<PuntoInteres>
        get() = when (filtroSeleccionado) {
            "Videos" -> puntosDescubiertos.filter { it.videoVisto }
            "Turísticos" -> puntosDescubiertos.filter { it.esHitoTuristico }
            else -> puntosDescubiertos
        }

    fun onFiltroChange(nuevoFiltro: String) {
        filtroSeleccionado = nuevoFiltro
    }

    fun seleccionarPunto(punto: PuntoInteres) {
        repository.seleccionarPunto(punto)
    }
}
