package cl.duoc.aprendoia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(

    primary = AzulPrimario,
    secondary = MoradoIA,
    tertiary = AmarilloPuntos,
    background = FondoClaro,
    surface = SuperficieTarjeta,
    onPrimary = SuperficieTarjeta,
    onSecondary = SuperficieTarjeta,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal,
    error = ErrorRojo
)

@Composable
fun AprendoIATheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
