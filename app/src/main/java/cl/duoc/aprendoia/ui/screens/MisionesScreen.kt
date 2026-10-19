package cl.duoc.aprendoia.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import cl.duoc.aprendoia.ui.components.TarjetaVideo
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.ui.theme.MoradoIA
import cl.duoc.aprendoia.ui.theme.VerdeExito
import cl.duoc.aprendoia.util.QRScannerHelper
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel

@Composable
fun MisionesScreen(
    viewModel: AprendoIAViewModel
) {
    var puntoDetalle by remember { mutableStateOf<PuntoInteres?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Misiones en Santiago",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = AzulPrimario
        )

        Text(
            text = "Recorre puntos patrimoniales de Santiago, escanea su código QR educativo y valida tu visita con una selfie.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(viewModel.puntosSantiago, key = { it.id }) { punto ->
                TarjetaPuntoInteres(
                    punto = punto,
                    onRegistrarSelfie = { bitmap -> viewModel.registrarSelfie(punto.id, bitmap) },
                    onCapturarQR = {
                        QRScannerHelper.iniciarEscaneo(
                            context = context,
                            onSuccess = { barcodeText ->
                                val valido = viewModel.procesarContenidoQR(barcodeText, punto.id)
                                if (!valido) {
                                    Toast.makeText(
                                        context,
                                        "Se leyó el QR, pero no contiene un JSON válido",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            },
                            onFailure = {
                                val ejemplo = viewModel.obtenerJsonEjemplo(punto)
                                viewModel.procesarContenidoQR(ejemplo, punto.id)
                            }
                        )
                    },
                    onVerDetalle = {
                        puntoDetalle = punto
                    }
                )
            }
        }
    }

    puntoDetalle?.let { punto ->
        AlertDialog(
            onDismissRequest = { puntoDetalle = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MoradoIA,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = punto.nombre,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulPrimario
                    )
                    Text(
                        text = punto.categoriaIA,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Actividad Educativa:",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = punto.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Código de registro: ${punto.codigoQR}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { puntoDetalle = null },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    viewModel.videoQREscaneado?.let { video ->
        AlertDialog(
            onDismissRequest = { viewModel.limpiarEstadoQR() },
            title = {
                Text("¡Cápsula de Video Desbloqueada!", fontWeight = FontWeight.Bold)
            },
            text = {
                TarjetaVideo(
                    video = video,
                    puntosGanados = 40,
                    onCerrar = { viewModel.limpiarEstadoQR() }
                )
            },
            confirmButton = {}
        )
    }
}

@Composable
fun TarjetaPuntoInteres(
    punto: PuntoInteres,
    onRegistrarSelfie: (Bitmap) -> Unit,
    onCapturarQR: () -> Unit,
    onVerDetalle: () -> Unit
) {
    val context = LocalContext.current

    fun crearSelfieSimulada(): Bitmap {
        val bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.parseColor("#1E3A8A")
            isAntiAlias = true
        }
        canvas.drawCircle(60f, 60f, 55f, paint)
        val textPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 24f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }
        canvas.drawText("SELFIE", 60f, 68f, textPaint)
        return bitmap
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onRegistrarSelfie(bitmap)
            Toast.makeText(context, "Foto de validación registrada (+30 pts)", Toast.LENGTH_SHORT).show()
        } else {
            val simulada = crearSelfieSimulada()
            onRegistrarSelfie(simulada)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                val simulada = crearSelfieSimulada()
                onRegistrarSelfie(simulada)
            }
        } else {
            val simulada = crearSelfieSimulada()
            onRegistrarSelfie(simulada)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (punto.completada && punto.selfieTomada) Color(0xFFF0FDF4) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

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
                            .size(38.dp)
                            .background(
                                if (punto.completada) VerdeExito.copy(alpha = 0.15f) else MoradoIA.copy(alpha = 0.12f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (punto.completada) Icons.Default.Check else Icons.Default.Place,
                            contentDescription = null,
                            tint = if (punto.completada) VerdeExito else MoradoIA,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = punto.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${punto.comuna} • ${punto.categoriaIA}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = AmarilloPuntos,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${punto.puntosRecompensa} pts",
                        fontWeight = FontWeight.Bold,
                        color = AmarilloPuntos
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = punto.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Coordenadas: Lat ${punto.latitud}, Lon ${punto.longitud} • ${punto.codigoQR}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (punto.completada) VerdeExito.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (punto.completada) Icons.Default.Check else Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = if (punto.completada) VerdeExito else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (punto.completada) "Estado del QR: Capturado (+40 pts)" else "Estado del QR: Pendiente de captura",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (punto.completada) VerdeExito else Color.DarkGray,
                    fontWeight = if (punto.completada) FontWeight.Bold else FontWeight.Normal
                )
            }

            if (punto.selfieTomada && punto.selfieBitmap != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFE0F2FE), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        bitmap = punto.selfieBitmap.asImageBitmap(),
                        contentDescription = "Foto de validación",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Evidencia fotográfica registrada",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall,
                            color = AzulPrimario
                        )
                        Text(
                            text = "+30 pts extra guardados en SQLite",
                            style = MaterialTheme.typography.labelSmall,
                            color = VerdeExito
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCapturarQR,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (punto.completada) Icons.Default.Check else Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (punto.completada) VerdeExito else AzulPrimario
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (punto.completada) "QR Listo" else "Capturar QR",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                OutlinedButton(
                    onClick = {
                        try {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } catch (e: Exception) {
                            val simulada = crearSelfieSimulada()
                            onRegistrarSelfie(simulada)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (punto.selfieTomada) VerdeExito else MoradoIA
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (punto.selfieTomada) "Repetir Foto" else "Selfie (+30)",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Button(
                    onClick = onVerDetalle,
                    modifier = Modifier.weight(0.9f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AzulPrimario
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Info",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}
