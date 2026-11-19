package cl.duoc.aprendoia.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val CARPETA_FOTOS_TURISTICAS = "AprendoIA_Selfies"

fun Context.crearArchivoSelfieTemporal(nombre: String = "selfie_temp.jpg"): File {
    val carpeta = File(cacheDir, "fotos")
    if (!carpeta.exists()) {
        carpeta.mkdirs()
    }
    val archivo = File(carpeta, nombre)
    try {
        if (archivo.exists()) {
            archivo.delete()
        }
        archivo.createNewFile()
    } catch (e: Exception) {
        // Fallback en cache directo si falla la subcarpeta
        return File(cacheDir, nombre)
    }
    return archivo
}

fun Context.uriCompartible(archivo: File): Uri {
    return FileProvider.getUriForFile(this, "$packageName.fileprovider", archivo)
}

fun Context.guardarSelfieEnGaleria(archivo: File): Uri? {
    if (!archivo.exists() || archivo.length() == 0L) {
        return null
    }
    val fecha = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val nombre = "SELFIE_$fecha.jpg"

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val valores = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$CARPETA_FOTOS_TURISTICAS")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores) ?: return null

            contentResolver.openOutputStream(uri)?.use { salida ->
                FileInputStream(archivo).use { entrada ->
                    entrada.copyTo(salida)
                }
            }

            valores.clear()
            valores.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, valores, null, null)
            uri
        } else {
            val carpeta = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CARPETA_FOTOS_TURISTICAS)
            carpeta.mkdirs()
            val destino = File(carpeta, nombre)
            FileInputStream(archivo).use { entrada ->
                FileOutputStream(destino).use { salida ->
                    entrada.copyTo(salida)
                }
            }
            Uri.fromFile(destino)
        }
    } catch (e: Exception) {
        null
    }
}

