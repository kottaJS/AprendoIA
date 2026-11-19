package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository

class RegistroJugadorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var telefono by mutableStateOf("")
    var password by mutableStateOf("")
    var direccion by mutableStateOf("")
    var avatarSeleccionado by mutableStateOf("avatar_1")
    var passwordVisible by mutableStateOf(false)

    var errorNombre by mutableStateOf<String?>(null)
    var errorEmail by mutableStateOf<String?>(null)
    var errorPassword by mutableStateOf<String?>(null)
    var errorGeneral by mutableStateOf<String?>(null)

    fun onNombreChange(valor: String) {
        nombre = valor
        errorNombre = null
        errorGeneral = null
    }

    fun onEmailChange(valor: String) {
        email = valor
        errorEmail = null
        errorGeneral = null
    }

    fun onTelefonoChange(valor: String) {
        telefono = valor
    }

    fun onPasswordChange(valor: String) {
        password = valor
        errorPassword = null
        errorGeneral = null
    }

    fun onDireccionChange(valor: String) {
        direccion = valor
    }

    fun onAvatarChange(valor: String) {
        avatarSeleccionado = valor
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun registrar(): Boolean {
        var valido = true

        if (nombre.trim().length < 3) {
            errorNombre = "El nombre debe tener al menos 3 caracteres"
            valido = false
        }
        if (email.isBlank() || !email.contains("@")) {
            errorEmail = "Ingresa un correo institucional o personal válido"
            valido = false
        }
        if (password.trim().length < 3) {
            errorPassword = "La contraseña debe tener al menos 3 caracteres"
            valido = false
        }

        if (!valido) return false

        val exito = repository.registrarNuevoJugador(
            nombre = nombre.trim(),
            email = email.trim(),
            telefono = telefono.trim().ifBlank { "+56 9 1234 5678" },
            clave = password.trim(),
            direccion = direccion.trim().ifBlank { "Santiago Centro" },
            avatar = avatarSeleccionado
        )

        if (!exito) {
            errorGeneral = "No se pudo registrar el usuario. El correo podría ya estar registrado."
        }

        return exito
    }
}
