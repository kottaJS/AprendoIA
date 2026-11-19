package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres

class VideoEducativoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val puntoSeleccionado: PuntoInteres?
        get() = repository.puntoSeleccionado

    fun marcarVideoVisto(puntoId: Int): Boolean {
        return repository.marcarVideoVisto(puntoId)
    }
}
