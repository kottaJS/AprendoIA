package cl.duoc.aprendoia.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer

// 1. Latido (Pulse)
@Composable
fun Modifier.animacionLatidoInfinito(activo: Boolean = true): Modifier {
    if (!activo) return this

    val infiniteTransition = rememberInfiniteTransition(label = "LatidoTransition")
    val escala by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.33f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EscalaLatido"
    )

    return this.scale(escala)
}

@Composable
fun Modifier.animacionLatidoDisparo(disparador: Int): Modifier {
    val escala = remember { Animatable(1.0f) }

    LaunchedEffect(disparador) {
        if (disparador > 0) {
            escala.animateTo(
                targetValue = 1.33f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
            escala.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
    }

    return this.scale(escala.value)
}

// =============================================================================
// 2. GIRO (SPIN) - Rotación 360°
// =============================================================================
@Composable
fun Modifier.animacionGiroInfinito(activo: Boolean = true): Modifier {
    if (!activo) return this

    val infiniteTransition = rememberInfiniteTransition(label = "GiroTransition")
    val angulo by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "AnguloGiro"
    )

    return this.graphicsLayer {
        rotationZ = angulo
    }
}

@Composable
fun Modifier.animacionRotacionInfinita(rotando: Boolean): Modifier {
    return animacionGiroInfinito(activo = rotando)
}

@Composable
fun Modifier.animacionGiroDisparo(disparador: Int): Modifier {
    val angulo = remember { Animatable(0f) }

    LaunchedEffect(disparador) {
        if (disparador > 0) {
            angulo.snapTo(0f)
            angulo.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
            )
        }
    }

    return this.graphicsLayer {
        rotationZ = angulo.value
    }
}

// =============================================================================
// 3. REBOTE (BOUNCE) - Traslación vertical
// =============================================================================
@Composable
fun Modifier.animacionReboteInfinito(activo: Boolean = true): Modifier {
    if (!activo) return this

    val infiniteTransition = rememberInfiniteTransition(label = "ReboteTransition")
    val desplazamientoY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DesplazamientoRebote"
    )

    return this.graphicsLayer {
        translationY = desplazamientoY
    }
}

@Composable
fun Modifier.animacionReboteDisparo(disparador: Int): Modifier {
    val posY = remember { Animatable(0f) }

    LaunchedEffect(disparador) {
        if (disparador > 0) {
            posY.animateTo(
                targetValue = -70f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
            posY.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = 0.45f, stiffness = 400f)
            )
        }
    }

    return this.graphicsLayer {
        translationY = posY.value
    }
}

// =============================================================================
// 4. DESTELLO (FADE) - Alpha / Opacidad
// =============================================================================
@Composable
fun Modifier.animacionDestelloOpacidad(activo: Boolean = true): Modifier {
    if (!activo) return this

    val infiniteTransition = rememberInfiniteTransition(label = "DestelloTransition")
    val opacidad by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 0.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OpacidadDestello"
    )

    return this.alpha(opacidad)
}

@Composable
fun Modifier.animacionDestelloDisparo(disparador: Int): Modifier {
    val alphaAnim = remember { Animatable(1.0f) }

    LaunchedEffect(disparador) {
        if (disparador > 0) {
            alphaAnim.animateTo(
                targetValue = 0.15f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
            alphaAnim.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing)
            )
        }
    }

    return this.alpha(alphaAnim.value)
}

// =============================================================================
// 5. BALANCEO (SWING) - Rotación pendular
// =============================================================================
@Composable
fun Modifier.animacionBalanceoInfinito(activo: Boolean = true): Modifier {
    if (!activo) return this

    val infiniteTransition = rememberInfiniteTransition(label = "BalanceoTransition")
    val rotacion by infiniteTransition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RotacionBalanceo"
    )

    return this.graphicsLayer {
        rotationZ = rotacion
    }
}

@Composable
fun Modifier.animacionBalanceoDisparo(disparador: Int): Modifier {
    val rotacion = remember { Animatable(0f) }

    LaunchedEffect(disparador) {
        if (disparador > 0) {
            rotacion.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 1100
                    0f at 0 using FastOutSlowInEasing
                    -7f at 200 using FastOutSlowInEasing
                    7f at 400 using FastOutSlowInEasing
                    -4f at 650 using FastOutSlowInEasing
                    4f at 850 using FastOutSlowInEasing
                    0f at 1100
                }
            )
        }
    }

    return this.graphicsLayer {
        rotationZ = rotacion.value
    }
}

// =============================================================================
// Efecto complementario: Botón Presionado (Click Feedback)
// =============================================================================
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
