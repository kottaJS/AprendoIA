package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val usuarioActual: Usuario?
        get() = repository.usuarioActual

    val usuarios: List<Usuario>
        get() = repository.usuarios

    val puntosSantiago: List<PuntoInteres>
        get() = repository.puntosSantiago

    var mensajeResultado by mutableStateOf<String?>(null)

    fun actualizarPerfil(
        nombre: String,
        email: String,
        telefono: String,
        direccion: String,
        avatar: String
    ): Boolean {
        val exito = repository.actualizarPerfil(nombre, email, telefono, direccion, avatar)
        mensajeResultado = if (exito) "Perfil actualizado" else "Error al actualizar"
        return exito
    }

    fun eliminarJugadorAdmin(usuarioId: Int): Boolean {
        val exito = repository.eliminarJugador(usuarioId)
        mensajeResultado = if (exito) "Usuario eliminado" else "Error al eliminar"
        return exito
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
    }
}

