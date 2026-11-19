package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres

class EscaneoQRViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val mensajeError: String?
        get() = repository.mensajeErrorQR

    val mensajeExito: String?
        get() = repository.mensajeExitoQR

    val puntoSeleccionado: PuntoInteres?
        get() = repository.puntoSeleccionado

    fun procesarQR(rawText: String): Boolean {
        return repository.procesarContenidoQR(rawText, repository.puntoSeleccionado?.id)
    }

    fun limpiarEstado() {
        repository.limpiarEstadoQR()
    }
}
