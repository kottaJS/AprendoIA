package cl.duoc.aprendoia.ui.map

import android.content.Context
import androidx.core.content.ContextCompat
import cl.duoc.aprendoia.R
import cl.duoc.aprendoia.model.PuntoInteres
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

val PUNTO_SANTIAGO_CENTRO = GeoPoint(-33.4429, -70.6540)
const val ZOOM_SANTIAGO_INICIAL = 15.0
private const val DURACION_ANIMACION_MAPA = 1500L

fun MapView.irA(punto: GeoPoint, zoom: Double = 16.5) {
    controller.animateTo(punto, zoom, DURACION_ANIMACION_MAPA)
}

fun MapView.crearCapaMiUbicacion(): MyLocationNewOverlay {
    val capa = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
    overlays.add(capa)
    return capa
}

fun MapView.actualizarMarcadores(
    puntos: List<PuntoInteres>,
    onPuntoSeleccionado: (PuntoInteres) -> Unit
) {
    // Retener solo la capa de ubicación si existe
    val capasConservar = overlays.filterIsInstance<MyLocationNewOverlay>()
    overlays.clear()
    overlays.addAll(capasConservar)

    for (punto in puntos) {
        val marcador = Marker(this)
        marcador.position = GeoPoint(punto.latitud, punto.longitud)
        marcador.title = punto.nombre
        marcador.subDescription = "${punto.categoriaIA} • ${if (punto.completada) "Completado" else "Pendiente"}"

        val iconRes = if (punto.completada) R.drawable.ic_marcador else R.drawable.ic_marcador
        marcador.icon = ContextCompat.getDrawable(context, iconRes)
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        marcador.setOnMarkerClickListener { m, _ ->
            irA(m.position, 17.0)
            onPuntoSeleccionado(punto)
            true
        }

        overlays.add(marcador)
    }
    invalidate()
}

