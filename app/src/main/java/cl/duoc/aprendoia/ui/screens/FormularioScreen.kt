package cl.duoc.aprendoia.ui.screens

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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.aprendoia.ui.components.animacionDestelloOpacidad
import cl.duoc.aprendoia.ui.components.animacionLatidoInfinito
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
            text = "Ingresa los datos para registrar un nuevo punto de interés en Santiago.",
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

                // Nombre
                OutlinedTextField(
                    value = viewModel.formNombre,
                    onValueChange = {
                        viewModel.formNombre = it
                        viewModel.formErrorMensaje = null
                    },
                    label = { Text("Nombre del Lugar *") },
                    placeholder = { Text("Ej: Cerro San Cristóbal") },
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
                        viewModel.formErrorMensaje = null
                    },
                    label = { Text("Comuna *") },
                    placeholder = { Text("Santiago Centro, Providencia") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Categoria
                OutlinedTextField(
                    value = viewModel.formCategoria,
                    onValueChange = {
                        viewModel.formCategoria = it
                        viewModel.formErrorMensaje = null
                    },
                    label = { Text("Área de IA *") },
                    placeholder = { Text("Visión Artificial, Robótica, etc.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Coordenadas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = viewModel.formLatitud,
                        onValueChange = {
                            viewModel.formLatitud = it
                            viewModel.formErrorMensaje = null
                        },
                        label = { Text("Latitud") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = viewModel.formLongitud,
                        onValueChange = {
                            viewModel.formLongitud = it
                            viewModel.formErrorMensaje = null
                        },
                        label = { Text("Longitud") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Simular GPS
                OutlinedButton(
                    onClick = { viewModel.simularCapturaGps() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MoradoIA)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Obtener Coordenadas GPS", color = MoradoIA)
                }

                // Descripcion
                OutlinedTextField(
                    value = viewModel.formDescripcion,
                    onValueChange = {
                        viewModel.formDescripcion = it
                        viewModel.formErrorMensaje = null
                    },
                    label = { Text("Descripción de la Actividad *") },
                    placeholder = { Text("Describe la actividad a realizar en este punto.") },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = AzulPrimario)
                    },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                viewModel.formErrorMensaje?.let { error ->
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
                        text = "Guardar Punto",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

