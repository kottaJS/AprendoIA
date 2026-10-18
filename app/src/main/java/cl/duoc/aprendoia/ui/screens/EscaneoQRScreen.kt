package cl.duoc.aprendoia.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import cl.duoc.aprendoia.ui.components.TarjetaVideo
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.MoradoIA
import cl.duoc.aprendoia.ui.theme.ErrorRojo
import cl.duoc.aprendoia.util.QRScannerHelper
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel

/**
 * Pantalla de Escaneo de Códigos QR.
 * Diseñada de acuerdo al modelo explicado por el docente:
 * - Utiliza GmsBarcodeScanning (Google Code Scanner)
 * - Valida la estructura JSON del video { titulo, descripcion, url }
 * - Muestra la Tarjeta de Video al ser válido
 * - Despliega mensaje de rechazo si el QR no contiene un JSON válido
 */
@Composable
fun EscaneoQRScreen(
    viewModel: AprendoIAViewModel
) {
    val context = LocalContext.current
    var mostrarDialogoManual by remember { mutableStateOf(false) }
    var textoManual by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Encabezado
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(AzulPrimario.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = AzulPrimario,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Lector de Códigos QR",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AzulPrimario
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Toca el botón y apunta la cámara a un código QR educativo para acceder a la cápsula de video de IA.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón principal de escaneo con Google Code Scanner
        Button(
            onClick = {
                QRScannerHelper.iniciarEscaneo(
                    context = context,
                    onSuccess = { barcodeText ->
                        val valido = viewModel.procesarContenidoQR(barcodeText)
                        if (!valido) {
                            Toast.makeText(
                                context,
                                "Se leyó el QR, pero no contiene un JSON válido",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    onCanceled = {
                        Toast.makeText(context, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        // Fallback seguro si el dispositivo no tiene Google Play Services instalado
                        mostrarDialogoManual = true
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
        ) {
            Icon(Icons.Default.QrCodeScanner, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Escanear Código QR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón de prueba rápida para emuladores
        OutlinedButton(
            onClick = {
                val jsonEjemplo = """{"titulo":"Automatiza Excel con IA","descripcion":"Aprende a integrar modelos de lenguaje para clasificar datos y optimizar tareas.","url":"https://www.youtube.com/shorts/sample1"}"""
                viewModel.procesarContenidoQR(jsonEjemplo)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MoradoIA)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simular QR Válido (Ejemplo del Profe)")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                // Simular un QR no válido para demostrar la validación requerida
                val qrInvalido = "https://www.duoc.cl"
                val resultado = viewModel.procesarContenidoQR(qrInvalido)
                if (!resultado) {
                    Toast.makeText(
                        context,
                        "Se leyó el QR, pero no contiene un JSON válido",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = ErrorRojo)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Probar QR Inválido (Validación)")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Si se leyó un QR exitoso: mostrar la Tarjeta de Video
        viewModel.videoQREscaneado?.let { video ->
            Text(
                text = "Resultado del Escaneo:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AzulPrimario,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TarjetaVideo(
                video = video,
                puntosGanados = 40,
                onCerrar = { viewModel.limpiarEstadoQR() }
            )
        }

        // Si falló la validación del QR
        viewModel.mensajeErrorQR?.let { errorMsg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = ErrorRojo,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = errorMsg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ErrorRojo,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Diálogo manual para emuladores sin cámara física
    if (mostrarDialogoManual) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoManual = false },
            icon = {
                Icon(Icons.Default.Info, contentDescription = null, tint = AzulPrimario)
            },
            title = {
                Text("Lectura en Emulador", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Ingresa el texto o JSON del código QR para validar:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = textoManual,
                        onValueChange = { textoManual = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Pegar JSON o texto...") },
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoManual = false
                        val valido = viewModel.procesarContenidoQR(textoManual)
                        if (!valido) {
                            Toast.makeText(
                                context,
                                "Se leyó el QR, pero no contiene un JSON válido",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                ) {
                    Text("Validar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoManual = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
