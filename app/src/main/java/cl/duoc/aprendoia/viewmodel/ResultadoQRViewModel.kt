package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.VideoIA

class ResultadoQRViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val puntoSeleccionado: PuntoInteres?
        get() = repository.puntoSeleccionado

    val videoQREscaneado: VideoIA?
        get() = repository.videoQREscaneado
}
