package cl.duoc.aprendoia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aprendoia.ui.components.animacionLatidoInfinito
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.VerdeCompletado
import cl.duoc.aprendoia.viewmodel.PuntajeProgresoViewModel

private const val PUNTAJE_MAXIMO_META = 10000

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuntajeProgresoScreen(
    viewModel: PuntajeProgresoViewModel = viewModel(),
    onVolver: () -> Unit
) {
    val usuario = viewModel.usuario
    val puntajeTotal = viewModel.puntaje
    val qrCount = viewModel.puntosCompletados
    val videosCount = viewModel.videosVistos
    val selfiesCount = viewModel.selfiesTomadas

    val puntosPorQR = qrCount * 10
    val puntosPorSelfie = selfiesCount * 100
    val puntosOtros = maxOf(0, puntajeTotal - puntosPorQR - puntosPorSelfie)

    val porcentajeMeta = (puntajeTotal.toFloat() / PUNTAJE_MAXIMO_META).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Progreso", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Círculo Central de Puntaje
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { porcentajeMeta },
                    modifier = Modifier.size(190.dp),
                    strokeWidth = 14.dp,
                    color = AzulPrimario,
                    trackColor = AzulPrimario.copy(alpha = 0.15f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AmarilloPuntos,
                        modifier = Modifier
                            .size(32.dp)
                            .animacionLatidoInfinito(true)
                    )
                    Text(
                        text = "$puntajeTotal",
                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = AzulPrimario
                    )
                    Text(
                        text = "Puntos totales",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Fila de Estadísticas Rápidas
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatPill(valor = "$qrCount", etiqueta = "QR encontrados", icono = Icons.Default.QrCodeScanner, color = AzulPrimario)
                    StatPill(valor = "$videosCount", etiqueta = "Videos vistos", icono = Icons.Default.PlayCircleOutline, color = Color(0xFF7E57C2))
                    StatPill(valor = "$selfiesCount", etiqueta = "Selfies turísticas", icono = Icons.Default.CameraAlt, color = VerdeCompletado)
                }
            }

            // Barra de Progreso General hacia 10.000 pts (Exigencia del Anexo)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Progreso general", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${(porcentajeMeta * 100).toInt()}%", fontWeight = FontWeight.Bold, color = AzulPrimario)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$puntajeTotal de $PUNTAJE_MAXIMO_META puntos meta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { porcentajeMeta },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = AzulPrimario,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Desglose: Tipo de Puntos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "Desglose por tipo de puntos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                    DesgloseFila(color = AzulPrimario, titulo = "Puntos por QR", puntos = "$puntosPorQR pts")
                    DesgloseFila(color = VerdeCompletado, titulo = "Puntos por selfies turísticas", puntos = "$puntosPorSelfie pts")
                    DesgloseFila(color = Color(0xFFFF9800), titulo = "Otros desafíos y actividades", puntos = "$puntosOtros pts")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatPill(valor: String, etiqueta: String, icono: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icono, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = valor, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Text(text = etiqueta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DesgloseFila(color: Color, titulo: String, puntos: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = titulo, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = puntos, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
    }
}

