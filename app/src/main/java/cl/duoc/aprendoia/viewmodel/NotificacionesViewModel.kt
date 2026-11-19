package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.NotificacionItem

class NotificacionesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val notificaciones: List<NotificacionItem>
        get() = repository.notificaciones

    val cantidadNoLeidas: Int
        get() = repository.notificaciones.size

    fun recargar() {
        repository.recargarNotificaciones()
    }
}
