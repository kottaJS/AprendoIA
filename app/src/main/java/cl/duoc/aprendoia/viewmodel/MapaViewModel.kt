package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres

class MapaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    var busqueda by mutableStateOf("")
    var filtroActual by mutableStateOf("Todos") // "Todos", "Pendientes", "Completados"

    val puntoSeleccionado: PuntoInteres?
        get() = repository.puntoSeleccionado

    val puntosSantiago: List<PuntoInteres>
        get() = repository.puntosSantiago

    val puntosFiltrados: List<PuntoInteres>
        get() {
            val listaBase = when (filtroActual) {
                "Pendientes" -> repository.puntosSantiago.filter { !it.completada }
                "Completados" -> repository.puntosSantiago.filter { it.completada }
                else -> repository.puntosSantiago
            }

            return if (busqueda.isBlank()) {
                listaBase
            } else {
                listaBase.filter {
                    it.nombre.contains(busqueda, ignoreCase = true) ||
                    it.categoriaIA.contains(busqueda, ignoreCase = true) ||
                    it.comuna.contains(busqueda, ignoreCase = true)
                }
            }
        }

    fun onBusquedaChange(nuevaBusqueda: String) {
        busqueda = nuevaBusqueda
    }

    fun onFiltroChange(nuevoFiltro: String) {
        filtroActual = nuevoFiltro
    }

    fun seleccionarPunto(punto: PuntoInteres?) {
        repository.seleccionarPunto(punto)
    }
}
