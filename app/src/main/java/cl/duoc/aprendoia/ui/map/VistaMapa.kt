package cl.duoc.aprendoia.ui.map

import android.graphics.Rect
import android.os.Build
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

@Composable
fun rememberMapView(): MapView {
    val context = LocalContext.current

    val mapView = remember {
        Configuration.getInstance().userAgentValue = context.packageName
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            isTilesScaledToDpi = true
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            minZoomLevel = 10.0
            maxZoomLevel = 19.0
            controller.setZoom(ZOOM_SANTIAGO_INICIAL)
            controller.setCenter(PUNTO_SANTIAGO_CENTRO)

            // Evitar que contenedores padre (drawer, scroll, etc.) capturen los gestos del mapa
            setOnTouchListener { v, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_POINTER_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        v.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_UP,
                    MotionEvent.ACTION_CANCEL -> {
                        v.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
                false // Permite que MapView procese normalmente el paneo y pinch zoom
            }

            // Excluir gestos de navegación del sistema Android (evita volver atrás al tocar los bordes)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                addOnLayoutChangeListener { v, left, top, right, bottom, _, _, _, _ ->
                    val ancho = right - left
                    val alto = bottom - top
                    if (ancho > 0 && alto > 0) {
                        v.systemGestureExclusionRects = listOf(Rect(0, 0, ancho, alto))
                    }
                }
            }
        }
    }

    val cicloDeVida = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(cicloDeVida) {
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        cicloDeVida.addObserver(observador)

        onDispose {
            cicloDeVida.removeObserver(observador)
            mapView.onDetach()
        }
    }

    return mapView
}

@Composable
fun VistaMapa(mapView: MapView, modifier: Modifier = Modifier) {
    AndroidView(factory = { mapView }, modifier = modifier)
}

