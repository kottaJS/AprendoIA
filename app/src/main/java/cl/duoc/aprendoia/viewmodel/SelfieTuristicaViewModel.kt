package cl.duoc.aprendoia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres

class SelfieTuristicaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val puntoSeleccionado: PuntoInteres?
        get() = repository.puntoSeleccionado

    var fotoUri by mutableStateOf<Uri?>(null)
    var selfieGuardadaExitosamente by mutableStateOf(false)

    fun onFotoCapturada(uri: Uri?) {
        fotoUri = uri
    }

    fun guardarSelfie(): Boolean {
        val p = repository.puntoSeleccionado ?: return false
        val exito = repository.registrarSelfie(p.id, fotoUri?.toString())
        selfieGuardadaExitosamente = exito
        return exito
    }
}
