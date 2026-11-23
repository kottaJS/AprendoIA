package cl.duoc.aprendoia

import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias de modelos y lógica de negocio en la App Móvil.
 * Cumplimiento IE 3.2.1 e IE 3.2.2 de la Evaluación Parcial 5.
 */
class PuntoInteresTest {

    private lateinit var punto: PuntoInteres

    @Before
    fun setUp() {
        punto = PuntoInteres(
            id = 1,
            nombre = "Palacio de La Moneda",
            descripcion = "Hito histórico y centro de gobernanza.",
            comuna = "Santiago Centro",
            latitud = -33.4429,
            longitud = -70.6539,
            categoriaIA = "Ética y Sociedad",
            puntosRecompensa = 15,
            codigoQR = "QR-MONEDA-IA-001"
        )
    }

    @Test
    fun testCreacionPuntoValoresPorDefecto() {
        assertEquals(1, punto.id)
        assertEquals("Palacio de La Moneda", punto.nombre)
        assertFalse("El punto nuevo no debe estar completado", punto.completada)
        assertFalse("No debe tener selfie tomada al inicio", punto.selfieTomada)
        assertEquals(15, punto.puntosRecompensa)
    }

    @Test
    fun testCompletarPuntoYMisiones() {
        val puntoCompletado = punto.copy(completada = true, selfieTomada = true)
        assertTrue(puntoCompletado.completada)
        assertTrue(puntoCompletado.selfieTomada)
    }

    @Test
    fun testCalculoPuntajeUsuarioConRecompensa() {
        val usuario = Usuario(
            id = 1,
            email = "maxi@duocuc.cl",
            nombre = "Maximiliano Dinamarca",
            puntaje = 100,
            qrEncontrados = 2
        )

        // Simular suma de recompensa al descubrir La Moneda (+15 pts)
        val usuarioActualizado = usuario.copy(
            puntaje = usuario.puntaje + punto.puntosRecompensa,
            qrEncontrados = usuario.qrEncontrados + 1
        )

        assertEquals(115, usuarioActualizado.puntaje)
        assertEquals(3, usuarioActualizado.qrEncontrados)
    }

    @Test
    fun testValidacionCoordenadasSantiago() {
        // Coordenadas válidas en Santiago deben estar en rango aproximado lat (-34 a -33) y lon (-71 a -70)
        assertTrue("Latitud debe estar en rango de Santiago", punto.latitud in -34.0..-33.0)
        assertTrue("Longitud debe estar en rango de Santiago", punto.longitud in -71.5..-70.0)
    }
}
