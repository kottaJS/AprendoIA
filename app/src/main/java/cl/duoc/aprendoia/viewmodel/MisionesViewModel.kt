package cl.duoc.aprendoia.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres

class MisionesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    var filtroCategoria by mutableStateOf("Todas")

    val puntosSantiago: List<PuntoInteres>
        get() = repository.puntosSantiago

    val videoQREscaneado
        get() = repository.videoQREscaneado

    val misionesFiltradas: List<PuntoInteres>
        get() = if (filtroCategoria == "Todas") {
            repository.puntosSantiago
        } else {
            repository.puntosSantiago.filter { it.categoriaIA.equals(filtroCategoria, ignoreCase = true) }
        }

    fun onFiltroCategoriaChange(cat: String) {
        filtroCategoria = cat
    }

    fun seleccionarPunto(punto: PuntoInteres) {
        repository.seleccionarPunto(punto)
    }

    fun registrarSelfie(puntoId: Int, bitmap: Bitmap? = null) {
        repository.registrarSelfie(puntoId, null)
    }

    fun procesarContenidoQR(rawText: String, puntoId: Int? = null): Boolean {
        return repository.procesarContenidoQR(rawText, puntoId)
    }

    fun limpiarEstadoQR() {
        repository.limpiarEstadoQR()
    }

    fun obtenerJsonEjemplo(punto: PuntoInteres): String {
        return """{"titulo":"${punto.nombre}","descripcion":"${punto.descripcion}","url":"${punto.urlVideo}"}"""
    }
}
