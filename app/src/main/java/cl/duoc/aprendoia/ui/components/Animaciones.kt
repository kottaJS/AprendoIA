package cl.duoc.aprendoia.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

// Modificadores de animacion reutilizables con Jetpack Compose

@Composable
fun Modifier.animacionLatidoInfinito(activo: Boolean = true): Modifier {

    if (!activo) return this

    val infiniteTransition = rememberInfiniteTransition(label = "LatidoTransition")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EscalaLatido"
    )

    return this.scale(escala)
}

// Efecto de escala al presionar
@Composable
fun Modifier.animacionBotonPresionado(
    onClick: () -> Unit
): Modifier {
    var presionado by remember { mutableStateOf(false) }

    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 150),
        finishedListener = {
            if (presionado) {
                presionado = false
                onClick()
            }
        },
        label = "EscalaBoton"
    )

    return this
        .scale(escala)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            presionado = true
        }
}

// Rotacion continua para el boton de refrescar o sincronizar
@Composable
fun Modifier.animacionRotacionInfinita(rotando: Boolean): Modifier {
    if (!rotando) return this

    val infiniteTransition = rememberInfiniteTransition(label = "RotacionTransition")
    val angulo by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "AnguloRotacion"
    )

    return this.graphicsLayer {
        rotationZ = angulo
    }
}

// Rebote vertical para destacar elementos
@Composable
fun Modifier.animacionReboteInfinito(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "ReboteTransition")
    val desplazamientoY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DesplazamientoRebote"
    )

    return this.graphicsLayer {
        translationY = desplazamientoY
    }
}

// Efecto de opacidad o parpadeo
@Composable
fun Modifier.animacionDestelloOpacidad(): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "OpacidadTransition")
    val opacidad by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OpacidadFade"
    )

    return this.alpha(opacidad)
}
