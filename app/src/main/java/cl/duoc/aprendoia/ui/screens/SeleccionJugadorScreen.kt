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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.aprendoia.model.Usuario
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AzulPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionJugadorScreen(
    usuarios: List<Usuario>,
    usuarioActual: Usuario?,
    onSeleccionarUsuario: (Usuario, String) -> Boolean,
    onCrearNuevoJugador: () -> Unit,
    onEliminarJugador: (Int) -> Boolean,
    onEditarJugador: (nombre: String, email: String, tel: String, dir: String, avatar: String) -> Boolean
) {
    var usuarioParaAutenticar by remember { mutableStateOf<Usuario?>(null) }
    var claveIngresada by remember { mutableStateOf("") }
    var errorClave by remember { mutableStateOf<String?>(null) }

    var usuarioParaEditar by remember { mutableStateOf<Usuario?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editTelefono by remember { mutableStateOf("") }
    var editDireccion by remember { mutableStateOf("") }

    val esAdmin = usuarioActual?.rol == "Admin"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona tu jugador", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Elige con qué perfil explorar Santiago:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Lista de Jugadores Clientes
            val jugadoresClientes = usuarios.filter { it.rol == "Cliente" }
            items(jugadoresClientes) { jugador ->
                JugadorCard(
                    jugador = jugador,
                    esAdmin = esAdmin,
                    onTocar = {
                        usuarioParaAutenticar = jugador
                        claveIngresada = ""
                        errorClave = null
                    },
                    onEditar = {
                        usuarioParaEditar = jugador
                        editNombre = jugador.nombre
                        editEmail = jugador.email
                        editTelefono = jugador.telefono
                        editDireccion = jugador.direccion
                    },
                    onEliminar = {
                        onEliminarJugador(jugador.id)
                    }
                )
            }

            // Botón Crear Nuevo Jugador
            item {
                OutlinedButton(
                    onClick = onCrearNuevoJugador,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crear nuevo jugador", fontWeight = FontWeight.SemiBold)
                }
            }

            // Sección de Roles Especiales del Sistema (Admin y Creador de Contenido)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cuentas del Sistema (Roles del Caso)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            val cuentasEspeciales = usuarios.filter { it.rol != "Cliente" }
            items(cuentasEspeciales) { especial ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            usuarioParaAutenticar = especial
                            claveIngresada = ""
                            errorClave = null
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (especial.rol == "Admin")
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                        else
                            AzulPrimario.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    if (especial.rol == "Admin") Color(0xFFD32F2F) else AzulPrimario,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (especial.rol == "Admin") Icons.Default.AdminPanelSettings else Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${especial.nombre} (Alias: ${especial.alias})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Rol: ${especial.rol} • Clave en duro: 123",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(Icons.Default.Lock, contentDescription = "Requiere clave", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal para pedir contraseña al seleccionar jugador
    usuarioParaAutenticar?.let { u ->
        AlertDialog(
            onDismissRequest = { usuarioParaAutenticar = null },
            title = {
                Text(text = "Ingresar como ${u.alias.ifBlank { u.nombre }}")
            },
            text = {
                Column {
                    Text(
                        text = "Por favor, introduce tu contraseña para acceder a este perfil:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = claveIngresada,
                        onValueChange = {
                            claveIngresada = it
                            errorClave = null
                        },
                        label = { Text("Contraseña") },
                        placeholder = { Text("Clave (Ej: 123)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        isError = errorClave != null,
                        supportingText = errorClave?.let { { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = onSeleccionarUsuario(u, claveIngresada)
                        if (ok) {
                            usuarioParaAutenticar = null
                        } else {
                            errorClave = "Contraseña incorrecta (recuerda que es 123)"
                        }
                    }
                ) {
                    Text("Ingresar")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioParaAutenticar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal para editar perfil (Solo Administrador)
    usuarioParaEditar?.let { target ->
        AlertDialog(
            onDismissRequest = { usuarioParaEditar = null },
            title = { Text("Editar Jugador: ${target.nombre}") },
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
                        label = { Text("Email") },
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
                        onEditarJugador(editNombre, editEmail, editTelefono, editDireccion, target.avatar)
                        usuarioParaEditar = null
                    }
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { usuarioParaEditar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun JugadorCard(
    jugador: Usuario,
    esAdmin: Boolean,
    onTocar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }

    val colorAvatar = when (jugador.avatar) {
        "avatar_1" -> Color(0xFFE91E63)
        "avatar_2" -> Color(0xFF2196F3)
        "avatar_3" -> Color(0xFF4CAF50)
        "avatar_4" -> Color(0xFFFF9800)
        "avatar_5" -> Color(0xFF9C27B0)
        else -> Color(0xFF00BCD4)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTocar() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(colorAvatar),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = jugador.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${jugador.puntaje} puntos • ${jugador.qrEncontrados} QR encontrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (esAdmin) {
                Box {
                    IconButton(onClick = { menuExpandido = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones Admin")
                    }
                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar perfil") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpandido = false
                                onEditar()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar jugador", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpandido = false
                                onEliminar()
                            }
                        )
                    }
                }
            } else {
                Icon(Icons.Default.Lock, contentDescription = "Requiere clave", tint = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

