package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.HistorialItem

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    var filtroTipo by mutableStateOf("Todos") // "Todos", "QR", "Videos", "Selfies"

    val historial: List<HistorialItem>
        get() = repository.historial

    val itemsFiltrados: List<HistorialItem>
        get() = when (filtroTipo) {
            "QR" -> repository.historial.filter { it.tipo.equals("QR", ignoreCase = true) }
            "Videos" -> repository.historial.filter { it.tipo.equals("Video", ignoreCase = true) }
            "Selfies" -> repository.historial.filter { it.tipo.equals("Selfie", ignoreCase = true) }
            else -> repository.historial
        }

    fun onFiltroChange(nuevoFiltro: String) {
        filtroTipo = nuevoFiltro
    }
}
