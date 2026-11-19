package cl.duoc.aprendoia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.ui.components.animacionBalanceoInfinito
import cl.duoc.aprendoia.ui.components.animacionLatidoInfinito
import cl.duoc.aprendoia.ui.components.animacionRotacionInfinita
import cl.duoc.aprendoia.ui.screens.AnimacionesScreen
import cl.duoc.aprendoia.ui.screens.BienvenidaScreen
import cl.duoc.aprendoia.ui.screens.ColeccionScreen
import cl.duoc.aprendoia.ui.screens.DetallePuntoScreen
import cl.duoc.aprendoia.ui.screens.EscaneoQRScreen
import cl.duoc.aprendoia.ui.screens.FormularioScreen
import cl.duoc.aprendoia.ui.screens.HistorialScreen
import cl.duoc.aprendoia.ui.screens.HomeScreen
import cl.duoc.aprendoia.ui.screens.LoginScreen
import cl.duoc.aprendoia.ui.screens.MapaScreen
import cl.duoc.aprendoia.ui.screens.MisionesScreen
import cl.duoc.aprendoia.ui.screens.NotificacionesScreen
import cl.duoc.aprendoia.ui.screens.PerfilConfiguracionScreen
import cl.duoc.aprendoia.ui.screens.PuntajeProgresoScreen
import cl.duoc.aprendoia.ui.screens.RegistroJugadorScreen
import cl.duoc.aprendoia.ui.screens.ResultadoQRScreen
import cl.duoc.aprendoia.ui.screens.SelfieTuristicaScreen
import cl.duoc.aprendoia.ui.screens.VideoEducativoScreen
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AprendoIATheme
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration

enum class DestinoApp {
    BIENVENIDA,              // 1. Bienvenida / Inicio
    LOGIN,                   // 2. Inicio de Sesión
    REGISTRO_JUGADOR,        // 3. Registro de Jugador
    DASHBOARD,               // 4. Principal / Dashboard
    MAPA,                    // 5. Mapa de Santiago
    DETALLE_PUNTO,           // 6. Detalle del Punto
    ESCANER_QR,              // 7. Escáner QR
    RESULTADO_QR,            // 8. Resultado del QR
    VIDEO_EDUCATIVO,         // 9. Video Educativo
    SELFIE_TURISTICA,        // 10. Selfie Turística
    COLECCION,               // 11. Colección de Descubrimientos
    PUNTAJE_PROGRESO,        // 12. Puntaje y Progreso
    HISTORIAL,               // 13. Historial de Actividades
    NOTIFICACIONES,          // 14. Notificaciones
    PERFIL_CONFIGURACION,    // 15. Perfil y Configuración
    MISIONES_LISTA,          // 16. Lista de misiones
    FORMULARIO_ADMIN,        // 17. Formulario nuevo punto (Admin)
    ANIMACIONES              // 18. Laboratorio de Animaciones (Ejemplo 106 de Clases)
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración crítica de osmdroid para permitir descargar las calles de OpenStreetMap
        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        enableEdgeToEdge()

        setContent {
            AprendoIATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val appViewModel: AprendoIAViewModel = viewModel()
                    EnrutadorPrincipal(viewModel = appViewModel)
                }
            }
        }
    }
}

@Composable
fun EnrutadorPrincipal(viewModel: AprendoIAViewModel) {
    // Si ya está autenticado, va al Dashboard; si no, empieza en Bienvenida
    var destinoActual by remember {
        mutableStateOf(if (viewModel.estaAutenticado) DestinoApp.DASHBOARD else DestinoApp.BIENVENIDA)
    }

    when (destinoActual) {
        DestinoApp.BIENVENIDA -> {
            BienvenidaScreen(
                onComenzar = { destinoActual = DestinoApp.LOGIN },
                onYaTengoCuenta = { destinoActual = DestinoApp.LOGIN }
            )
        }
        DestinoApp.LOGIN -> {
            LoginScreen(
                onLoginExitoso = { destinoActual = DestinoApp.DASHBOARD },
                onIrARegistro = { destinoActual = DestinoApp.REGISTRO_JUGADOR }
            )
        }
        DestinoApp.REGISTRO_JUGADOR -> {
            RegistroJugadorScreen(
                onRegistroExitoso = { destinoActual = DestinoApp.DASHBOARD },
                onVolver = { destinoActual = DestinoApp.LOGIN }
            )
        }
        else -> {
            // Contenedor principal con Navegación para las pantallas 4 a 18
            ContenedorConNavegacion(
                viewModel = viewModel,
                destinoActual = destinoActual,
                onCambiarDestino = { destinoActual = it },
                onCerrarSesion = {
                    viewModel.cerrarSesion()
                    destinoActual = DestinoApp.LOGIN
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContenedorConNavegacion(
    viewModel: AprendoIAViewModel,
    destinoActual: DestinoApp,
    onCambiarDestino: (DestinoApp) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val usuario = viewModel.usuarioActual

    BackHandler(enabled = drawerState.isOpen || destinoActual != DestinoApp.DASHBOARD) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            onCambiarDestino(DestinoApp.DASHBOARD)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet {
                // Cabecera del usuario
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AzulPrimario)
                        .padding(22.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = usuario?.nombre ?: "Jugador",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Rol: ${usuario?.rol ?: "Cliente"} • ${usuario?.puntaje ?: 0} pts",
                                    color = Color.White.copy(alpha = 0.85f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Items del Drawer
                NavigationDrawerItem(
                    label = { Text("Inicio / Dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    selected = destinoActual == DestinoApp.DASHBOARD,
                    onClick = {
                        onCambiarDestino(DestinoApp.DASHBOARD)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Mapa de Santiago (osmdroid)") },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    selected = destinoActual == DestinoApp.MAPA,
                    onClick = {
                        onCambiarDestino(DestinoApp.MAPA)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Escanear Código QR") },
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                    selected = destinoActual == DestinoApp.ESCANER_QR,
                    onClick = {
                        onCambiarDestino(DestinoApp.ESCANER_QR)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Colección de Descubrimientos") },
                    icon = { Icon(Icons.Default.Collections, contentDescription = null) },
                    selected = destinoActual == DestinoApp.COLECCION,
                    onClick = {
                        onCambiarDestino(DestinoApp.COLECCION)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Puntaje y Progreso") },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                    selected = destinoActual == DestinoApp.PUNTAJE_PROGRESO,
                    onClick = {
                        onCambiarDestino(DestinoApp.PUNTAJE_PROGRESO)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Historial de Actividades") },
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    selected = destinoActual == DestinoApp.HISTORIAL,
                    onClick = {
                        onCambiarDestino(DestinoApp.HISTORIAL)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Notificaciones") },
                    icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
                    selected = destinoActual == DestinoApp.NOTIFICACIONES,
                    onClick = {
                        onCambiarDestino(DestinoApp.NOTIFICACIONES)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Mi Perfil y Ajustes") },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    selected = destinoActual == DestinoApp.PERFIL_CONFIGURACION,
                    onClick = {
                        onCambiarDestino(DestinoApp.PERFIL_CONFIGURACION)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Misiones en Lista") },
                    icon = { Icon(Icons.Default.Place, contentDescription = null) },
                    selected = destinoActual == DestinoApp.MISIONES_LISTA,
                    onClick = {
                        onCambiarDestino(DestinoApp.MISIONES_LISTA)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Item de Animaciones
                NavigationDrawerItem(
                    label = { Text("Animaciones") },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AzulPrimario) },
                    selected = destinoActual == DestinoApp.ANIMACIONES,
                    onClick = {
                        onCambiarDestino(DestinoApp.ANIMACIONES)
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                if (usuario?.rol == "Admin") {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    NavigationDrawerItem(
                        label = { Text("Registrar Punto GPS (Admin)") },
                        icon = { Icon(Icons.Default.AddLocation, contentDescription = null) },
                        selected = destinoActual == DestinoApp.FORMULARIO_ADMIN,
                        onClick = {
                            onCambiarDestino(DestinoApp.FORMULARIO_ADMIN)
                            coroutineScope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            onCerrarSesion()
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (destinoActual) {
                                DestinoApp.DASHBOARD -> "Aprendo IA"
                                DestinoApp.MAPA -> "Mapa de Santiago"
                                DestinoApp.DETALLE_PUNTO -> "Detalle del Punto"
                                DestinoApp.ESCANER_QR -> "Escanear QR"
                                DestinoApp.RESULTADO_QR -> "Resultado QR"
                                DestinoApp.VIDEO_EDUCATIVO -> "Video Educativo"
                                DestinoApp.SELFIE_TURISTICA -> "Selfie Turística"
                                DestinoApp.COLECCION -> "Mis Descubrimientos"
                                DestinoApp.PUNTAJE_PROGRESO -> "Mi Progreso"
                                DestinoApp.HISTORIAL -> "Historial"
                                DestinoApp.NOTIFICACIONES -> "Notificaciones"
                                DestinoApp.PERFIL_CONFIGURACION -> "Mi Perfil"
                                DestinoApp.MISIONES_LISTA -> "Misiones en Santiago"
                                DestinoApp.FORMULARIO_ADMIN -> "Nuevo Punto en Terreno"
                                DestinoApp.ANIMACIONES -> "Animaciones"
                                else -> "Aprendo IA"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    actions = {
                        // Badge de Notificaciones con animación de BALANCEO (SWING)
                        IconButton(onClick = { onCambiarDestino(DestinoApp.NOTIFICACIONES) }) {
                            BadgedBox(
                                badge = {
                                    if (viewModel.notificaciones.isNotEmpty()) {
                                        Badge { Text("${viewModel.notificaciones.size}") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notificaciones",
                                    tint = Color.White,
                                    modifier = Modifier.animacionBalanceoInfinito(true)
                                )
                            }
                        }

                        // Badge de Puntos con animación de LATIDO (PULSE)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AmarilloPuntos)
                                .animacionLatidoInfinito(true)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.Transparent,
                            onClick = { onCambiarDestino(DestinoApp.PUNTAJE_PROGRESO) }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${usuario?.puntaje ?: 0} pts",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        // Botón de sincronización con animación de GIRO (SPIN)
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.sincronizarDatos()
                                    delay(800)
                                    viewModel.finalizarSincronizacion()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sincronizar",
                                tint = Color.White,
                                modifier = Modifier.animacionRotacionInfinita(rotando = viewModel.sincronizando)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AzulPrimario)
                )
            },

            // Barra Inferior de 5 Ítems según el PDF (Inicio, Mapa, Escanear QR, Colección, Perfil)
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = destinoActual == DestinoApp.DASHBOARD,
                        onClick = { onCambiarDestino(DestinoApp.DASHBOARD) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = destinoActual == DestinoApp.MAPA,
                        onClick = { onCambiarDestino(DestinoApp.MAPA) },
                        icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                        label = { Text("Mapa") }
                    )
                    NavigationBarItem(
                        selected = destinoActual == DestinoApp.ESCANER_QR,
                        onClick = { onCambiarDestino(DestinoApp.ESCANER_QR) },
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "QR") },
                        label = { Text("Escanear QR") }
                    )
                    NavigationBarItem(
                        selected = destinoActual == DestinoApp.COLECCION,
                        onClick = { onCambiarDestino(DestinoApp.COLECCION) },
                        icon = { Icon(Icons.Default.Collections, contentDescription = "Colección") },
                        label = { Text("Colección") }
                    )
                    NavigationBarItem(
                        selected = destinoActual == DestinoApp.PERFIL_CONFIGURACION,
                        onClick = { onCambiarDestino(DestinoApp.PERFIL_CONFIGURACION) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                        label = { Text("Perfil") }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (destinoActual) {
                    DestinoApp.DASHBOARD -> HomeScreen(
                        onNavegarAMapa = { onCambiarDestino(DestinoApp.MAPA) },
                        onNavegarAEscanerQR = { onCambiarDestino(DestinoApp.ESCANER_QR) },
                        onNavegarAColeccion = { onCambiarDestino(DestinoApp.COLECCION) },
                        onNavegarAPuntoDetalle = { p ->
                            viewModel.puntoSeleccionado = p
                            onCambiarDestino(DestinoApp.DETALLE_PUNTO)
                        }
                    )

                    DestinoApp.MAPA -> MapaScreen(
                        onVerDetallePunto = { p ->
                            viewModel.puntoSeleccionado = p
                            onCambiarDestino(DestinoApp.DETALLE_PUNTO)
                        }
                    )

                    DestinoApp.DETALLE_PUNTO -> {
                        val punto = viewModel.puntoSeleccionado ?: viewModel.puntosSantiago.firstOrNull()
                        if (punto != null) {
                            DetallePuntoScreen(
                                punto = punto,
                                onVolver = { onCambiarDestino(DestinoApp.MAPA) },
                                onIrAEscanerQR = { onCambiarDestino(DestinoApp.ESCANER_QR) },
                                onIrASelfie = { onCambiarDestino(DestinoApp.SELFIE_TURISTICA) },
                                onIrAVideo = { onCambiarDestino(DestinoApp.VIDEO_EDUCATIVO) },
                                onVerEnMapa = { onCambiarDestino(DestinoApp.MAPA) }
                            )
                        } else {
                            onCambiarDestino(DestinoApp.MAPA)
                        }
                    }

                    DestinoApp.ESCANER_QR -> EscaneoQRScreen(
                        onEscaneoExitoso = { onCambiarDestino(DestinoApp.RESULTADO_QR) },
                        onVolver = { onCambiarDestino(DestinoApp.DASHBOARD) }
                    )

                    DestinoApp.RESULTADO_QR -> ResultadoQRScreen(
                        punto = viewModel.puntoSeleccionado,
                        video = viewModel.videoQREscaneado,
                        onVerVideo = { onCambiarDestino(DestinoApp.VIDEO_EDUCATIVO) },
                        onTomarSelfie = { onCambiarDestino(DestinoApp.SELFIE_TURISTICA) },
                        onVolverAlMapa = { onCambiarDestino(DestinoApp.MAPA) }
                    )

                    DestinoApp.VIDEO_EDUCATIVO -> VideoEducativoScreen(
                        punto = viewModel.puntoSeleccionado,
                        onVolver = { onCambiarDestino(DestinoApp.COLECCION) },
                        onMarcarVisto = { pId -> viewModel.marcarVideoVisto(pId) }
                    )

                    DestinoApp.SELFIE_TURISTICA -> SelfieTuristicaScreen(
                        punto = viewModel.puntoSeleccionado,
                        onSelfieRegistrada = { uri ->
                            viewModel.puntoSeleccionado?.let { p ->
                                viewModel.registrarSelfieTuristica(p.id, uri?.toString())
                            }
                            onCambiarDestino(DestinoApp.PUNTAJE_PROGRESO)
                        },
                        onVolver = { onCambiarDestino(DestinoApp.DETALLE_PUNTO) }
                    )

                    DestinoApp.COLECCION -> ColeccionScreen(
                        onAbrirVideo = { p ->
                            viewModel.puntoSeleccionado = p
                            onCambiarDestino(DestinoApp.VIDEO_EDUCATIVO)
                        }
                    )

                    DestinoApp.PUNTAJE_PROGRESO -> PuntajeProgresoScreen(
                        onVolver = { onCambiarDestino(DestinoApp.DASHBOARD) }
                    )

                    DestinoApp.HISTORIAL -> HistorialScreen()

                    DestinoApp.NOTIFICACIONES -> NotificacionesScreen(
                        notificaciones = viewModel.notificaciones,
                        onVolver = { onCambiarDestino(DestinoApp.DASHBOARD) },
                        onSeleccionarNotificacion = { notif ->
                            if (notif.puntoIdDestino != null) {
                                val p = viewModel.puntosSantiago.firstOrNull { it.id == notif.puntoIdDestino }
                                if (p != null) {
                                    viewModel.puntoSeleccionado = p
                                    onCambiarDestino(DestinoApp.DETALLE_PUNTO)
                                } else {
                                    onCambiarDestino(DestinoApp.MAPA)
                                }
                            } else {
                                onCambiarDestino(DestinoApp.MAPA)
                            }
                        }
                    )

                    DestinoApp.PERFIL_CONFIGURACION -> PerfilConfiguracionScreen(
                        onCerrarSesion = onCerrarSesion,
                        onIrAFormularioAdmin = { onCambiarDestino(DestinoApp.FORMULARIO_ADMIN) }
                    )

                    DestinoApp.MISIONES_LISTA -> MisionesScreen()

                    DestinoApp.FORMULARIO_ADMIN -> FormularioScreen(
                        onRegistroCompletado = { onCambiarDestino(DestinoApp.MAPA) }
                    )

                    DestinoApp.ANIMACIONES -> AnimacionesScreen()

                    else -> Unit
                }
            }
        }
    }
}
