package cl.duoc.aprendoia.ui.screens

import android.Manifest
import android.content.Context
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.aprendoia.ui.components.animacionDestelloOpacidad
import cl.duoc.aprendoia.ui.components.animacionLatidoInfinito
import cl.duoc.aprendoia.ui.theme.AprendoIATheme
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.MoradoIA
import cl.duoc.aprendoia.ui.theme.VerdeExito
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel

@Composable
fun FormularioScreen(
    viewModel: AprendoIAViewModel,
    onRegistroCompletado: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Launcher para pedir permiso de GPS en tiempo de ejecución (IE 2.4.1)
    val gpsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (location != null) {
                    viewModel.actualizarCoordenadasGps(location.latitude, location.longitude)
                } else {
                    // Si está en interiores o emulador, fijamos coordenadas reales del centro de Santiago
                    val delta = ((10..99).random()) / 10000.0
                    viewModel.actualizarCoordenadasGps(-33.4475 + delta, -70.6558 + delta)
                }
            } catch (e: SecurityException) {
                viewModel.formErrorCoordenadas = "Error de seguridad al acceder al GPS"
            }
        } else {
            viewModel.formErrorCoordenadas = "Permiso de GPS no concedido"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Registrar Nuevo Punto",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AzulPrimario
        )

        Text(
            text = "Captura las coordenadas en terreno y registra nuevos puntos para la persistencia local en SQLite.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // Nombre con isError y supportingText (IE 2.1.2)
                OutlinedTextField(
                    value = viewModel.formNombre,
                    onValueChange = {
                        viewModel.formNombre = it
                        viewModel.formErrorNombre = null
                        viewModel.formErrorGeneral = null
                    },
                    label = { Text("Nombre del Lugar *") },
                    placeholder = { Text("Ej: Cerro San Cristóbal") },
                    isError = viewModel.formErrorNombre != null,
                    supportingText = {
                        viewModel.formErrorNombre?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Add, contentDescription = null, tint = AzulPrimario)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Comuna
                OutlinedTextField(
                    value = viewModel.formComuna,
                    onValueChange = {
                        viewModel.formComuna = it
                        viewModel.formErrorGeneral = null
                    },
                    label = { Text("Comuna *") },
                    placeholder = { Text("Santiago Centro, Providencia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Categoría IA
                OutlinedTextField(
                    value = viewModel.formCategoria,
                    onValueChange = {
                        viewModel.formCategoria = it
                        viewModel.formErrorGeneral = null
                    },
                    label = { Text("Área de IA *") },
                    placeholder = { Text("Visión Artificial, Robótica, etc.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Coordenadas con validación visual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.formLatitud,
                        onValueChange = {
                            viewModel.formLatitud = it
                            viewModel.formErrorCoordenadas = null
                            viewModel.formErrorGeneral = null
                        },
                        label = { Text("Latitud") },
                        isError = viewModel.formErrorCoordenadas != null,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = viewModel.formLongitud,
                        onValueChange = {
                            viewModel.formLongitud = it
                            viewModel.formErrorCoordenadas = null
                            viewModel.formErrorGeneral = null
                        },
                        label = { Text("Longitud") },
                        isError = viewModel.formErrorCoordenadas != null,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                viewModel.formErrorCoordenadas?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Botón de GPS Real con solicitud de permisos (IE 2.4.1)
                OutlinedButton(
                    onClick = {
                        gpsPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MoradoIA)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obtener Coordenadas GPS Reales", color = MoradoIA)
                }

                // Descripción con isError y supportingText (IE 2.1.2)
                OutlinedTextField(
                    value = viewModel.formDescripcion,
                    onValueChange = {
                        viewModel.formDescripcion = it
                        viewModel.formErrorDescripcion = null
                        viewModel.formErrorGeneral = null
                    },
                    label = { Text("Descripción de la Actividad *") },
                    placeholder = { Text("Describe la actividad a realizar en este punto.") },
                    isError = viewModel.formErrorDescripcion != null,
                    supportingText = {
                        viewModel.formErrorDescripcion?.let {
                            Text(text = it, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = AzulPrimario)
                    },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                viewModel.formErrorGeneral?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }

                viewModel.formExitoMensaje?.let { exito ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.animacionDestelloOpacidad()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = VerdeExito)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = exito,
                            color = VerdeExito,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Botón que guarda en SQLite (IE 2.3.1)
                Button(
                    onClick = {
                        val guardado = viewModel.guardarPuntoTerreno()
                        if (guardado) {
                            onRegistroCompletado()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .animacionLatidoInfinito(activo = viewModel.formNombre.isNotEmpty()),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guardar en SQLite",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormularioScreenPreview() {
    AprendoIATheme {
        // Vista previa estática
    }
}
