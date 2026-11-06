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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.aprendoia.model.HistorialItem
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.VerdeCompletado
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel

@Composable
fun HistorialScreen(
    viewModel: AprendoIAViewModel
) {
    var filtroTipo by remember { mutableStateOf("Todos") } // "Todos", "QR", "Videos", "Selfies"

    val itemsHistorial = viewModel.historial
    val itemsFiltrados = remember(itemsHistorial, filtroTipo) {
        when (filtroTipo) {
            "QR" -> itemsHistorial.filter { it.tipo.equals("QR", ignoreCase = true) }
            "Videos" -> itemsHistorial.filter { it.tipo.equals("Video", ignoreCase = true) }
            "Selfies" -> itemsHistorial.filter { it.tipo.equals("Selfie", ignoreCase = true) }
            else -> itemsHistorial
        }
    }

    // Agrupar por fecha
    val agrupadosPorFecha = remember(itemsFiltrados) {
        itemsFiltrados.groupBy { it.fecha }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Historial de Actividades",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AzulPrimario
        )
        Text(
            text = "Registro cronológico de tus descubrimientos en Santiago",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Filtros
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "QR", "Videos", "Selfies").forEach { f ->
                val sel = filtroTipo == f
                FilterChip(
                    selected = sel,
                    onClick = { filtroTipo = f },
                    label = { Text(f, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AzulPrimario,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (agrupadosPorFecha.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No hay actividades registradas aún",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                agrupadosPorFecha.forEach { (fecha, actividades) ->
                    item {
                        Text(
                            text = fecha,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(actividades) { item ->
                        HistorialFila(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorialFila(item: HistorialItem) {
    val icono = when (item.tipo) {
        "Selfie" -> Icons.Default.CameraAlt
        "Video" -> Icons.Default.PlayCircleOutline
        else -> Icons.Default.QrCodeScanner
    }
    val colorIcono = when (item.tipo) {
        "Selfie" -> VerdeCompletado
        "Video" -> Color(0xFF7E57C2)
        else -> AzulPrimario
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(colorIcono.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.lugar,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${item.fecha} • ${item.hora}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            if (item.puntos > 0) {
                Text(
                    text = "+${item.puntos} pts",
                    fontWeight = FontWeight.Bold,
                    color = colorIcono,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}
