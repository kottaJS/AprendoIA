package cl.duoc.aprendoia.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Stars
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.VerdeCompletado
import cl.duoc.aprendoia.viewmodel.ColeccionViewModel

@Composable
fun ColeccionScreen(
    viewModel: ColeccionViewModel = viewModel(),
    onAbrirVideo: (PuntoInteres) -> Unit
) {
    val filtroSeleccionado = viewModel.filtroSeleccionado
    val puntosDescubiertos = viewModel.puntosDescubiertos
    val totalVideos = viewModel.totalVideos
    val totalTuristicos = viewModel.totalTuristicos
    val listaAMostrar = viewModel.puntosFiltrados

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = "Mis Descubrimientos",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AzulPrimario
        )
        Text(
            text = "Lugares de Santiago donde encontraste QR y aprendiste de IA",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Filtros (Pills/Chips)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filtroSeleccionado == "Todos",
                onClick = { viewModel.onFiltroChange("Todos") },
                label = { Text("Todos (${puntosDescubiertos.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AzulPrimario,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = filtroSeleccionado == "Videos",
                onClick = { viewModel.onFiltroChange("Videos") },
                label = { Text("Videos ($totalVideos)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AzulPrimario,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = filtroSeleccionado == "Turísticos",
                onClick = { viewModel.onFiltroChange("Turísticos") },
                label = { Text("Turísticos ($totalTuristicos)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AzulPrimario,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (listaAMostrar.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aún no tienes descubrimientos en este filtro",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Explora el mapa y escanea códigos QR para sumar ítems",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaAMostrar) { punto ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAbrirVideo(punto) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Miniatura o Icono de video
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFF7E57C2).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleOutline,
                                    contentDescription = null,
                                    tint = Color(0xFF7E57C2),
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = punto.videoTitulo.ifBlank { punto.nombre },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = punto.nombre,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (punto.selfieTomada) "+110 pts" else "+10 pts",
                                        color = if (punto.selfieTomada) VerdeCompletado else AzulPrimario,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = " • 12/09/2026",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    if (punto.videoVisto) {
                                        Text(
                                            text = " • Visto ✓",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = VerdeCompletado,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Ver cápsula",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

