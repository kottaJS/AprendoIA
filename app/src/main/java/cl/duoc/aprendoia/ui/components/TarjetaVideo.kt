package cl.duoc.aprendoia.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.aprendoia.model.VideoIA
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.MoradoIA
import cl.duoc.aprendoia.ui.theme.VerdeExito

/**
 * Componente Tarjeta de Video Educativo, renderizado al validar un código QR
 * según el diseño y código presentado en la clase.
 */
@Composable
fun TarjetaVideo(
    video: VideoIA,
    modifier: Modifier = Modifier,
    puntosGanados: Int = 40,
    onCerrar: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8FAFC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(MoradoIA.copy(alpha = 0.15f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Video IA",
                            tint = MoradoIA,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Video Educativo IA",
                            style = MaterialTheme.typography.labelMedium,
                            color = MoradoIA,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = video.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulPrimario
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(VerdeExito.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AmarilloPuntos,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+$puntosGanados pts",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = VerdeExito
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = video.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // En emuladores sin navegador instalado
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ver Video en YouTube")
                }

                if (onCerrar != null) {
                    Button(
                        onClick = onCerrar,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cerrar", color = Color.Black)
                    }
                }
            }
        }
    }
}
