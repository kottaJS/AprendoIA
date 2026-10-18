package cl.duoc.aprendoia.util

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

/**
 * Helper para invocar Google Play Services Code Scanner (GmsBarcodeScanning),
 * exactamente como fue explicado por el docente en la clase.
 */
object QRScannerHelper {

    fun iniciarEscaneo(
        context: Context,
        onSuccess: (String) -> Unit,
        onCanceled: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        try {
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()

            val scanner = GmsBarcodeScanning.getClient(context, options)

            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val rawValue = barcode.rawValue ?: barcode.displayValue ?: ""
                    onSuccess(rawValue)
                }
                .addOnCanceledListener {
                    onCanceled()
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
