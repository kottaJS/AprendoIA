package cl.duoc.aprendoia.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AnimacionesViewModel : ViewModel() {

    // Contadores de disparo para cada animación
    var triggerLatido by mutableIntStateOf(0)
        private set

    var triggerGiro by mutableIntStateOf(0)
        private set

    var triggerRebote by mutableIntStateOf(0)
        private set

    var triggerDestello by mutableIntStateOf(0)
        private set

    var triggerBalanceo by mutableIntStateOf(0)
        private set

    // Modo bucle continuo
    var bucleContinuo by mutableStateOf(false)

    fun dispararLatido() {
        triggerLatido++
    }

    fun dispararGiro() {
        triggerGiro++
    }

    fun dispararRebote() {
        triggerRebote++
    }

    fun dispararDestello() {
        triggerDestello++
    }

    fun dispararBalanceo() {
        triggerBalanceo++
    }

    fun dispararTodas() {
        dispararLatido()
        dispararGiro()
        dispararRebote()
        dispararDestello()
        dispararBalanceo()
    }

    fun toggleBucleContinuo() {
        bucleContinuo = !bucleContinuo
    }
}
