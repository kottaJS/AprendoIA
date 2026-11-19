package cl.duoc.aprendoia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.WbIncandescent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aprendoia.ui.components.animacionBalanceoDisparo
import cl.duoc.aprendoia.ui.components.animacionBalanceoInfinito
import cl.duoc.aprendoia.ui.components.animacionDestelloDisparo
import cl.duoc.aprendoia.ui.components.animacionDestelloOpacidad
import cl.duoc.aprendoia.ui.components.animacionGiroDisparo
import cl.duoc.aprendoia.ui.components.animacionGiroInfinito
import cl.duoc.aprendoia.ui.components.animacionLatidoDisparo
import cl.duoc.aprendoia.ui.components.animacionLatidoInfinito
import cl.duoc.aprendoia.ui.components.animacionReboteDisparo
import cl.duoc.aprendoia.ui.components.animacionReboteInfinito
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.MoradoIA
import cl.duoc.aprendoia.ui.theme.VerdeCompletado
import cl.duoc.aprendoia.viewmodel.AnimacionesViewModel

@Composable
fun AnimacionesScreen(
    viewModel: AnimacionesViewModel = viewModel()
) {
    val bucle = viewModel.bucleContinuo

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Cabecera descriptiva
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPrimario.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AzulPrimario,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Animaciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulPrimario
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Animaciones disponibles en la app: Latido, Giro, Rebote, Destello y Balanceo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Barra de control general
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { viewModel.dispararTodas() },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ejecutar las 5 a la vez", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Bucle", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = bucle,
                        onCheckedChange = { viewModel.toggleBucleContinuo() }
                    )
                }
            }
        }

        // 1. Latido (Pulse)
        item {
            TarjetaAnimacion(
                numero = "1",
                titulo = "Latido (Pulse)",
                propiedad = "Escala (scaleX, scaleY)",
                descripcion = "Efecto de pulso en escala. Usado en el trofeo y badges.",
                icono = Icons.Default.Favorite,
                colorIcono = Color(0xFFE91E63),
                modifierAnimado = if (bucle) {
                    Modifier.animacionLatidoInfinito(true)
                } else {
                    Modifier.animacionLatidoDisparo(viewModel.triggerLatido)
                },
                onProbar = { viewModel.dispararLatido() }
            )
        }

        // 2. Giro (Spin)
        item {
            TarjetaAnimacion(
                numero = "2",
                titulo = "Giro (Spin)",
                propiedad = "Rotación (rotationZ)",
                descripcion = "Rotación en 360°. Usado en el botón de sincronización.",
                icono = Icons.Default.ChangeCircle,
                colorIcono = AzulPrimario,
                modifierAnimado = if (bucle) {
                    Modifier.animacionGiroInfinito(true)
                } else {
                    Modifier.animacionGiroDisparo(viewModel.triggerGiro)
                },
                onProbar = { viewModel.dispararGiro() }
            )
        }

        // 3. Rebote (Bounce)
        item {
            TarjetaAnimacion(
                numero = "3",
                titulo = "Rebote (Bounce)",
                propiedad = "Traslación Y (translationY)",
                descripcion = "Desplazamiento vertical elástico. Usado en marcadores y desafíos.",
                icono = Icons.Default.RocketLaunch,
                colorIcono = AmarilloPuntos,
                modifierAnimado = if (bucle) {
                    Modifier.animacionReboteInfinito(true)
                } else {
                    Modifier.animacionReboteDisparo(viewModel.triggerRebote)
                },
                onProbar = { viewModel.dispararRebote() }
            )
        }

        // 4. Destello (Fade)
        item {
            TarjetaAnimacion(
                numero = "4",
                titulo = "Destello (Fade)",
                propiedad = "Opacidad (alpha)",
                descripcion = "Variación de opacidad. Usado en el visor del escáner.",
                icono = Icons.Default.WbIncandescent,
                colorIcono = MoradoIA,
                modifierAnimado = if (bucle) {
                    Modifier.animacionDestelloOpacidad(true)
                } else {
                    Modifier.animacionDestelloDisparo(viewModel.triggerDestello)
                },
                onProbar = { viewModel.dispararDestello() }
            )
        }

        // 5. Balanceo (Swing)
        item {
            TarjetaAnimacion(
                numero = "5",
                titulo = "Balanceo (Swing)",
                propiedad = "Oscilación (rotationZ)",
                descripcion = "Efecto pendular oscilante. Usado en la campana de notificaciones.",
                icono = Icons.Default.NotificationsActive,
                colorIcono = VerdeCompletado,
                modifierAnimado = if (bucle) {
                    Modifier.animacionBalanceoInfinito(true)
                } else {
                    Modifier.animacionBalanceoDisparo(viewModel.triggerBalanceo)
                },
                onProbar = { viewModel.dispararBalanceo() }
            )
        }
    }
}

@Composable
private fun TarjetaAnimacion(
    numero: String,
    titulo: String,
    propiedad: String,
    descripcion: String,
    icono: ImageVector,
    colorIcono: Color,
    modifierAnimado: Modifier,
    onProbar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elemento animado visual
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(colorIcono.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = colorIcono,
                    modifier = Modifier
                        .size(36.dp)
                        .then(modifierAnimado)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información técnica y botón
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$numero. $titulo",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = propiedad,
                    style = MaterialTheme.typography.labelSmall,
                    color = AzulPrimario,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onProbar,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Probar $titulo", fontSize = 12.sp)
                }
            }
        }
    }
}
