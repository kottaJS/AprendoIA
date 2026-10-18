package cl.duoc.aprendoia.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.MoradoIA
import cl.duoc.aprendoia.ui.theme.VerdeExito
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel

/**
 * Pantalla de Colección de Descubrimientos e Historial.
 * Corresponde a la pantalla descrita por el docente en el minuto 22:27:
 * "cuáles son los QRs que yo capturé y en qué lugar los capturé...
 * fotito chiquitita del lugar o video short... y de qué se trata el video".
 */
@Composable
fun HistorialScreen(
    viewModel: AprendoIAViewModel
) {
    val context = LocalContext.current
    val puntosDescubiertos = viewModel.puntosSantiago.filter { it.completada || it.selfieTomada }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Colección de Descubrimientos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AzulPrimario
        )

        Text(
            text = "Registro histórico de códigos QR y selfies turísticas validadas en terreno.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tarjeta resumen de hitos del concurso
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AzulPrimario)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${viewModel.puntosSantiago.count { it.completada }}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("QRs Capturados", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${viewModel.puntosSantiago.count { it.selfieTomada }}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text("Selfies Validadas", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${viewModel.usuarioActual?.puntaje ?: 0}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AmarilloPuntos
                    )
                    Text("Puntos Totales", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (puntosDescubiertos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no has completado misiones.\n¡Ve a 'Misiones en Santiago' y captura tu primer QR!",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(puntosDescubiertos, key = { it.id }) { punto ->
                    TarjetaDescubrimiento(
                        punto = punto,
                        onVerVideo = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(punto.urlVideo))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Navegador no disponible
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaDescubrimiento(
    punto: PuntoInteres,
    onVerVideo: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fotito chiquitita del lugar o selfie (Minuto 22:44)
            if (punto.selfieTomada && punto.selfieBitmap != null) {
                Image(
                    bitmap = punto.selfieBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(VerdeExito.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = VerdeExito,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = punto.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AzulPrimario
                )
                Text(
                    text = "${punto.categoriaIA} • ${punto.comuna}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = AmarilloPuntos, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "+${punto.puntosRecompensa} pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = VerdeExito
                    )
                }
            }

            IconButton(onClick = onVerVideo) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Ver Video",
                    tint = MoradoIA,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
