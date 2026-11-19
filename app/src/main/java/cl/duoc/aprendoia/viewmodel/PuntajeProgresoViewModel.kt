package cl.duoc.aprendoia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cl.duoc.aprendoia.data.AprendoIARepository
import cl.duoc.aprendoia.model.Usuario

class PuntajeProgresoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AprendoIARepository.getInstance(application)

    val usuario: Usuario?
        get() = repository.usuarioActual

    val puntaje: Int
        get() = usuario?.puntaje ?: 0

    val puntosCompletados: Int
        get() = repository.puntosSantiago.count { it.completada }

    val videosVistos: Int
        get() = repository.puntosSantiago.count { it.videoVisto }

    val selfiesTomadas: Int
        get() = repository.puntosSantiago.count { it.selfieTomada }

    val metaPuntaje: Int = 1000

    val porcentajeAvance: Float
        get() = (puntaje.toFloat() / metaPuntaje.toFloat()).coerceIn(0f, 1f)

    val rangoTitulo: String
        get() = when {
            puntaje >= 500 -> "Explorador Experto IA"
            puntaje >= 200 -> "Aventurero Urbano IA"
            puntaje >= 50 -> "Iniciado en Inteligencia Artificial"
            else -> "Novato de Santiago"
        }
}
