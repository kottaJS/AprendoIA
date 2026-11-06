package cl.duoc.aprendoia.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.ui.map.BotonesMapa
import cl.duoc.aprendoia.ui.map.PUNTO_SANTIAGO_CENTRO
import cl.duoc.aprendoia.ui.map.TarjetaPuntoMapa
import cl.duoc.aprendoia.ui.map.VistaMapa
import cl.duoc.aprendoia.ui.map.actualizarMarcadores
import cl.duoc.aprendoia.ui.map.crearCapaMiUbicacion
import cl.duoc.aprendoia.ui.map.irA
import cl.duoc.aprendoia.ui.map.rememberMapView
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    viewModel: AprendoIAViewModel,
    onVerDetallePunto: (PuntoInteres) -> Unit
) {
    val context = LocalContext.current
    val mapView = rememberMapView()
    val capaMiUbicacion = remember { mapView.crearCapaMiUbicacion() }

    var textoBusqueda by remember { mutableStateOf("") }
    var filtroEstado by remember { mutableStateOf("Todos") } // "Todos", "Pendientes", "Completados"
    var puntoSeleccionado by remember { mutableStateOf<PuntoInteres?>(null) }

    // Launcher de permisos de ubicación
    val pedirPermisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            capaMiUbicacion.enableMyLocation()
            capaMiUbicacion.myLocation?.let {
                mapView.irA(GeoPoint(it.latitude, it.longitude), 17.0)
            }
        }
    }

    // Filtrar los puntos según texto y chip
    val puntosFiltrados = remember(viewModel.puntosSantiago, textoBusqueda, filtroEstado) {
        viewModel.puntosSantiago.filter { punto ->
            val coincideTexto = punto.nombre.contains(textoBusqueda, ignoreCase = true) ||
                    punto.comuna.contains(textoBusqueda, ignoreCase = true) ||
                    punto.categoriaIA.contains(textoBusqueda, ignoreCase = true)

            val coincideFiltro = when (filtroEstado) {
                "Pendientes" -> !punto.completada
                "Completados" -> punto.completada
                else -> true
            }
            coincideTexto && coincideFiltro
        }
    }

    // Actualizar marcadores en el mapa osmdroid
    LaunchedEffect(puntosFiltrados) {
        mapView.actualizarMarcadores(puntosFiltrados) { punto ->
            puntoSeleccionado = punto
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Vista nativa del Mapa OpenStreetMap
        VistaMapa(mapView = mapView, modifier = Modifier.fillMaxSize())

        // Barra Superior de Búsqueda y Filtros
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = textoBusqueda,
                        onValueChange = { textoBusqueda = it },
                        placeholder = { Text("Buscar hito, comuna o tema de IA…") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Todos", "Pendientes", "Completados").forEach { filtro ->
                            val seleccionado = filtroEstado == filtro
                            FilterChip(
                                selected = seleccionado,
                                onClick = { filtroEstado = filtro },
                                label = { Text(filtro, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AzulPrimario,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Botones de Zoom y Mi Ubicación
        BotonesMapa(
            alAcercar = { mapView.controller.zoomIn() },
            alAlejar = { mapView.controller.zoomOut() },
            alIrAMiUbicacion = {
                val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (fine || coarse) {
                    capaMiUbicacion.enableMyLocation()
                    val loc = capaMiUbicacion.myLocation
                    if (loc != null) {
                        mapView.irA(GeoPoint(loc.latitude, loc.longitude), 17.0)
                    } else {
                        mapView.irA(PUNTO_SANTIAGO_CENTRO, 16.0)
                    }
                } else {
                    pedirPermisoUbicacion.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = if (puntoSeleccionado != null) 220.dp else 24.dp)
        )

        // Tarjeta inferior del Punto seleccionado
        puntoSeleccionado?.let { punto ->
            TarjetaPuntoMapa(
                punto = punto,
                onCerrar = { puntoSeleccionado = null },
                onVerDetalle = { onVerDetallePunto(punto) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }
}

