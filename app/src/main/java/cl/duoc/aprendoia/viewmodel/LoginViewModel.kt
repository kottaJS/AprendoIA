package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    var emailOrAlias by mutableStateOf("test@duocuc.cl")
    var password by mutableStateOf("123")
    var passwordVisible by mutableStateOf(false)
    var errorMensaje by mutableStateOf<String?>(null)

    fun onEmailChange(valor: String) {
        emailOrAlias = valor
        errorMensaje = null
    }

    fun onPasswordChange(valor: String) {
        password = valor
        errorMensaje = null
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun autenticar(): Boolean {
        errorMensaje = null
        if (emailOrAlias.isBlank()) {
            errorMensaje = "Ingresa tu correo o alias"
            return false
        }
        if (password.length < 3) {
            errorMensaje = "La contraseña debe tener al menos 3 caracteres"
            return false
        }

        val exito = repository.iniciarSesion(emailOrAlias, password)
        if (!exito) {
            errorMensaje = "Credenciales incorrectas"
        }
        return exito
    }

    fun setAccesoRapido(email: String, pass: String = "123") {
        emailOrAlias = email
        password = pass
        errorMensaje = null
    }
}
