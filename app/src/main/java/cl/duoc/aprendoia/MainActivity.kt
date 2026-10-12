package cl.duoc.aprendoia

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.aprendoia.ui.components.animacionRotacionInfinita
import cl.duoc.aprendoia.ui.screens.FormularioScreen
import cl.duoc.aprendoia.ui.screens.HomeScreen
import cl.duoc.aprendoia.ui.screens.LoginScreen
import cl.duoc.aprendoia.ui.screens.MisionesScreen
import cl.duoc.aprendoia.ui.theme.AmarilloPuntos
import cl.duoc.aprendoia.ui.theme.AprendoIATheme
import cl.duoc.aprendoia.ui.theme.AzulPrimario
import cl.duoc.aprendoia.viewmodel.AprendoIAViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PantallaApp {
    INICIO,
    MISIONES,
    FORMULARIO
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AprendoIATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val appViewModel: AprendoIAViewModel = viewModel()

                    // Si no ha iniciado sesion mostramos el Login
                    if (!appViewModel.estaAutenticado) {
                        LoginScreen(
                            onLoginExitoso = { email, clave ->
                                appViewModel.iniciarSesion(email, clave)
                            }
                        )
                    } else {
                        MainContainer(viewModel = appViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(viewModel: AprendoIAViewModel) {
    var pantallaActual by remember { mutableStateOf(PantallaApp.INICIO) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AzulPrimario)
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "Aprendo IA",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Duoc UC • Santiago",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = viewModel.usuarioActual?.nombre ?: "Estudiante",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Opciones del menu
                NavigationDrawerItem(
                    label = { Text("Inicio") },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    selected = pantallaActual == PantallaApp.INICIO,
                    onClick = {
                        pantallaActual = PantallaApp.INICIO
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Misiones en Santiago") },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    selected = pantallaActual == PantallaApp.MISIONES,
                    onClick = {
                        pantallaActual = PantallaApp.MISIONES
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Registrar Nuevo Punto") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    selected = pantallaActual == PantallaApp.FORMULARIO,
                    onClick = {
                        pantallaActual = PantallaApp.FORMULARIO
                        coroutineScope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                    icon = { Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                            viewModel.cerrarSesion()
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
                        Column {
                            Text(
                                text = when (pantallaActual) {
                                    PantallaApp.INICIO -> "Aprendo IA"
                                    PantallaApp.MISIONES -> "Misiones en Santiago"
                                    PantallaApp.FORMULARIO -> "Nuevo Punto"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .background(AmarilloPuntos, shape = RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
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
                                    text = "${viewModel.usuarioActual?.puntaje ?: 0} pts",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.sincronizarDatos()
                                    delay(1200)
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

            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = pantallaActual == PantallaApp.INICIO,
                        onClick = { pantallaActual = PantallaApp.INICIO },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = pantallaActual == PantallaApp.MISIONES,
                        onClick = { pantallaActual = PantallaApp.MISIONES },
                        icon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = { Text("Misiones") }
                    )
                    NavigationBarItem(
                        selected = pantallaActual == PantallaApp.FORMULARIO,
                        onClick = { pantallaActual = PantallaApp.FORMULARIO },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        label = { Text("Formulario") }
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (pantallaActual) {
                    PantallaApp.INICIO -> HomeScreen(
                        viewModel = viewModel,
                        onIrAMisiones = { pantallaActual = PantallaApp.MISIONES },
                        onIrAFormulario = { pantallaActual = PantallaApp.FORMULARIO }
                    )
                    PantallaApp.MISIONES -> MisionesScreen(
                        viewModel = viewModel
                    )
                    PantallaApp.FORMULARIO -> FormularioScreen(
                        viewModel = viewModel,
                        onRegistroCompletado = {
                            pantallaActual = PantallaApp.MISIONES
                        }
                    )
                }
            }
        }
    }
}
