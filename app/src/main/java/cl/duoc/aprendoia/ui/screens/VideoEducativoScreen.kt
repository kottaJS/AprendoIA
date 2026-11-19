package cl.duoc.aprendoia.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.VerdeCompletado
import cl.duoc.aprendoia.viewmodel.VideoEducativoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEducativoScreen(
    punto: PuntoInteres? = null,
    viewModel: VideoEducativoViewModel = viewModel(),
    onVolver: () -> Unit,
    onMarcarVisto: (Int) -> Unit = { id -> viewModel.marcarVideoVisto(id) }
) {
    val context = LocalContext.current
    val puntoActivo = punto ?: viewModel.puntoSeleccionado
    val tituloVideo = puntoActivo?.videoTitulo?.ifBlank { puntoActivo.nombre } ?: "Introducción a la Inteligencia Artificial"
    val temaVideo = puntoActivo?.videoTema?.ifBlank { puntoActivo?.categoriaIA ?: "Fundamentos de IA" } ?: "Fundamentos de IA"
    val urlVideo = puntoActivo?.urlVideo ?: "https://www.youtube.com/shorts/sample1"

    var reproduciendo by remember { mutableStateOf(false) }
    var progresoVideo by remember { mutableFloatStateOf(0.45f) }
    var videoYaVisto by remember { mutableStateOf(puntoActivo?.videoVisto == true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Educativo", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Reproductor de Video Emulado (Con Controles)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF141824)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.25f), CircleShape)
                            .clickable {
                                reproduciendo = !reproduciendo
                                if (!videoYaVisto && punto != null) {
                                    videoYaVisto = true
                                    onMarcarVisto(punto.id)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (reproduciendo) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (reproduciendo) "Pausa" else "Reproducir",
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (reproduciendo) "Reproduciendo cápsula…" else "Toca para reproducir",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Barra de control inferior en el video
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = if (reproduciendo) "0:58" else "0:00", color = Color.White, fontSize = 11.sp)
                        Text(text = "2:15", color = Color.White, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { if (reproduciendo) 0.65f else progresoVideo },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = AzulPrimario,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }
            }

            // Información de la Cápsula
            Column {
                Text(
                    text = tituloVideo,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Autor: Triskeledu SpA",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = AzulPrimario
                    )
                    Text(
                        text = " • Duración: 2:15 min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "En este video de microaprendizaje aprenderás conceptos prácticos de Inteligencia Artificial aplicados a la ciudad: cómo funciona el modelo, su impacto urbano y ejemplos cotidianos.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Badge del Tema
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = AzulPrimario.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tema: $temaVideo",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulPrimario
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón de confirmación de visualización (Requisito PDF)
            Button(
                onClick = {
                    if (puntoActivo != null) {
                        videoYaVisto = true
                        onMarcarVisto(puntoActivo.id)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (videoYaVisto) VerdeCompletado else AzulPrimario
                )
            ) {
                Icon(
                    imageVector = if (videoYaVisto) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (videoYaVisto) "✓ ¡Has visto este video!" else "Marcar como visto en mi historial",
                    fontWeight = FontWeight.Bold
                )
            }

            // Botón abrir enlace real
            OutlinedButton(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlVideo))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Abrir video en YouTube", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

