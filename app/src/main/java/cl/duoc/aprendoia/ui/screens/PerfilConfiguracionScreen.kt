package cl.duoc.aprendoia.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.viewmodel.PerfilViewModel

@Composable
fun PerfilConfiguracionScreen(
    viewModel: PerfilViewModel = viewModel(),
    onCerrarSesion: () -> Unit,
    onIrAFormularioAdmin: () -> Unit
) {
    val context = LocalContext.current
    val usuario = viewModel.usuarioActual

    var notificacionesActivadas by remember { mutableStateOf(true) }
    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var mostrarDialogoAvatar by remember { mutableStateOf(false) }
    var mostrarDialogoAyuda by remember { mutableStateOf(false) }

    var editNombre by remember { mutableStateOf(usuario?.nombre ?: "") }
    var editEmail by remember { mutableStateOf(usuario?.email ?: "") }
    var editTelefono by remember { mutableStateOf(usuario?.telefono ?: "") }
    var editDireccion by remember { mutableStateOf(usuario?.direccion ?: "") }
    var editAvatar by remember { mutableStateOf(usuario?.avatar ?: "avatar_1") }

    val tieneCamara = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    val tieneUbicacion = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    val pedirPermisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    val colorAvatar = when (usuario?.avatar) {
        "avatar_1" -> Color(0xFFE91E63)
        "avatar_2" -> Color(0xFF2196F3)
        "avatar_3" -> Color(0xFF4CAF50)
        "avatar_4" -> Color(0xFFFF9800)
        "avatar_5" -> Color(0xFF9C27B0)
        else -> Color(0xFF00BCD4)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = AzulPrimario
        )

        // Cabecera de Usuario
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(colorAvatar)
                        .clickable { mostrarDialogoAvatar = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = usuario?.nombre ?: "Jugador",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "${usuario?.puntaje ?: 0} puntos acumulados",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AzulPrimario,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Rol: ${usuario?.rol ?: "Cliente"} • ${usuario?.email ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Mini estadísticas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${usuario?.qrEncontrados ?: 0}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "QR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${viewModel.puntosSantiago.count { it.videoVisto }}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Videos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${viewModel.puntosSantiago.count { it.selfieTomada }}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "Selfies", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Opciones de Configuración (Estilo del PDF Page 6)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                // Editar Perfil
                OpcionItem(
                    icono = Icons.Default.Edit,
                    titulo = "Editar perfil",
                    subtitulo = "Modifica tus datos de contacto en SQLite",
                    onClick = {
                        editNombre = usuario?.nombre ?: ""
                        editEmail = usuario?.email ?: ""
                        editTelefono = usuario?.telefono ?: ""
                        editDireccion = usuario?.direccion ?: ""
                        mostrarDialogoEditar = true
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Cambiar Avatar
                OpcionItem(
                    icono = Icons.Default.Face,
                    titulo = "Cambiar avatar",
                    subtitulo = "Elige tu icono representativo",
                    onClick = { mostrarDialogoAvatar = true }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Switch de Notificaciones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = AzulPrimario)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(text = "Notificaciones", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = "Avisos de puntos cercanos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = notificacionesActivadas,
                        onCheckedChange = { notificacionesActivadas = it }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Permisos de Cámara y Ubicación
                OpcionItem(
                    icono = Icons.Default.Security,
                    titulo = "Permisos de Cámara y GPS",
                    subtitulo = "Cámara: ${if (tieneCamara) "Concedido" else "Por conceder"} • GPS: ${if (tieneUbicacion) "Concedido" else "Por conceder"}",
                    onClick = {
                        pedirPermisosLauncher.launch(arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ))
                    }
                )

                // Panel extra si es Administrador
                if (usuario?.rol == "Admin") {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    OpcionItem(
                        icono = Icons.Default.AddLocation,
                        titulo = "Registrar nuevo punto en terreno",
                        subtitulo = "Formulario de coordenadas GPS y QR (Admin)",
                        onClick = onIrAFormularioAdmin
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Ayuda y Soporte
                OpcionItem(
                    icono = Icons.Default.HelpOutline,
                    titulo = "Ayuda y soporte",
                    subtitulo = "Información sobre Triskeledu SpA",
                    onClick = { mostrarDialogoAyuda = true }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Cerrar Sesión / Cambiar de jugador
                OpcionItem(
                    icono = Icons.Default.ExitToApp,
                    titulo = "Cerrar sesión",
                    subtitulo = "Cambiar de jugador en este teléfono",
                    colorTexto = MaterialTheme.colorScheme.error,
                    onClick = onCerrarSesion
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal Editar Perfil
    if (mostrarDialogoEditar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEditar = false },
            title = { Text("Editar Perfil") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editNombre,
                        onValueChange = { editNombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editTelefono,
                        onValueChange = { editTelefono = it },
                        label = { Text("Teléfono") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDireccion,
                        onValueChange = { editDireccion = it },
                        label = { Text("Dirección") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.actualizarPerfil(
                            nombre = editNombre,
                            email = editEmail,
                            telefono = editTelefono,
                            direccion = editDireccion,
                            avatar = usuario?.avatar ?: "avatar_1"
                        )
                        mostrarDialogoEditar = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEditar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Cambiar Avatar
    if (mostrarDialogoAvatar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAvatar = false },
            title = { Text("Elige tu Avatar") },
            text = {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AVATARES_DISPONIBLES) { (id, color) ->
                        val sel = editAvatar == id
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (sel) 3.dp else 0.dp,
                                    color = if (sel) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { editAvatar = id },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        usuario?.let {
                            viewModel.actualizarPerfil(it.nombre, it.email, it.telefono, it.direccion, editAvatar)
                        }
                        mostrarDialogoAvatar = false
                    }
                ) {
                    Text("Aplicar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoAvatar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal Ayuda
    if (mostrarDialogoAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAyuda = false },
            title = { Text("Aprendo IA Jugando en Santiago") },
            text = {
                Text(
                    text = "Desarrollado para Triskeledu SpA en Duoc UC.\n\n" +
                            "Esta plataforma conecta geolocalización, gamificación y microaprendizaje de IA a través de códigos QR distribuidos en los puntos más emblemáticos de la ciudad de Santiago.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = { mostrarDialogoAyuda = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun OpcionItem(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    colorTexto: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icono, contentDescription = null, tint = if (colorTexto != MaterialTheme.colorScheme.onSurface) colorTexto else AzulPrimario)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = colorTexto)
            Text(text = subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
    }
}

