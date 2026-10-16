package cl.duoc.aprendoia.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario

class AprendoIADbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "aprendo_ia.db"
        const val DATABASE_VERSION = 1

        const val TABLE_PUNTOS = "puntos"
        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_COMUNA = "comuna"
        const val COL_LATITUD = "latitud"
        const val COL_LONGITUD = "longitud"
        const val COL_CATEGORIA = "categoria_ia"
        const val COL_PUNTOS = "puntos_recompensa"
        const val COL_COMPLETADA = "completada"
        const val COL_CODIGO_QR = "codigo_qr"
        const val COL_SELFIE_TOMADA = "selfie_tomada"
        const val COL_URL_VIDEO = "url_video"

        const val TABLE_USUARIO = "usuario"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_NOMBRE = "nombre"
        const val COL_USER_ROL = "rol"
        const val COL_USER_PUNTAJE = "puntaje"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTablePuntos = """
            CREATE TABLE $TABLE_PUNTOS (
                $COL_ID INTEGER PRIMARY KEY,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_DESCRIPCION TEXT NOT NULL,
                $COL_COMUNA TEXT NOT NULL,
                $COL_LATITUD REAL NOT NULL,
                $COL_LONGITUD REAL NOT NULL,
                $COL_CATEGORIA TEXT NOT NULL,
                $COL_PUNTOS INTEGER NOT NULL,
                $COL_COMPLETADA INTEGER NOT NULL DEFAULT 0,
                $COL_CODIGO_QR TEXT NOT NULL,
                $COL_SELFIE_TOMADA INTEGER NOT NULL DEFAULT 0,
                $COL_URL_VIDEO TEXT NOT NULL
            )
        """.trimIndent()

        val createTableUsuario = """
            CREATE TABLE $TABLE_USUARIO (
                $COL_USER_EMAIL TEXT PRIMARY KEY,
                $COL_USER_NOMBRE TEXT NOT NULL,
                $COL_USER_ROL TEXT NOT NULL,
                $COL_USER_PUNTAJE INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createTablePuntos)
        db.execSQL(createTableUsuario)

        // Poblado inicial de datos (seed data)
        insertarPuntoInicial(db, 1, "Duoc UC Alonso de Ovalle", "Taller de robótica y sensores para monitorear el centro de Santiago.", "Santiago Centro", -33.4475, -70.6558, "Robótica", 50, 1, "QR-DUOC-001", 1, "https://www.youtube.com/shorts/sample1")
        insertarPuntoInicial(db, 2, "Palacio de La Moneda", "Identificación de fachadas y monumentos con fotos y visión artificial.", "Santiago Centro", -33.4429, -70.6540, "Visión Artificial", 40, 0, "QR-MONEDA-002", 0, "https://www.youtube.com/shorts/sample2")
        insertarPuntoInicial(db, 3, "Plaza de Armas", "Conteo y estimación de personas en el centro usando machine learning.", "Santiago Centro", -33.4378, -70.6504, "Machine Learning", 35, 0, "QR-PLAZA-003", 0, "https://www.youtube.com/shorts/sample3")
        insertarPuntoInicial(db, 4, "Cerro Santa Lucía", "Grabación de audio ambiente para reconocer sonidos y aves del cerro.", "Santiago Centro", -33.4402, -70.6439, "Audio y Sonido", 45, 0, "QR-LUCIA-004", 0, "https://www.youtube.com/shorts/sample4")
        insertarPuntoInicial(db, 5, "Museo de Bellas Artes", "Actividad de arte digital y generación de imágenes con IA.", "Santiago Centro", -33.4357, -70.6443, "IA Generativa", 60, 0, "QR-BELLASARTES-005", 0, "https://www.youtube.com/shorts/sample5")

        // Usuario inicial
        val valuesUser = ContentValues().apply {
            put(COL_USER_EMAIL, "estudiante@duocuc.cl")
            put(COL_USER_NOMBRE, "Maximiliano Dinamarca")
            put(COL_USER_ROL, "Estudiante")
            put(COL_USER_PUNTAJE, 150)
        }
        db.insert(TABLE_USUARIO, null, valuesUser)
    }

    private fun insertarPuntoInicial(
        db: SQLiteDatabase,
        id: Int,
        nombre: String,
        descripcion: String,
        comuna: String,
        lat: Double,
        lon: Double,
        categoria: String,
        puntos: Int,
        completada: Int,
        qr: String,
        selfie: Int,
        urlVideo: String
    ) {
        val values = ContentValues().apply {
            put(COL_ID, id)
            put(COL_NOMBRE, nombre)
            put(COL_DESCRIPCION, descripcion)
            put(COL_COMUNA, comuna)
            put(COL_LATITUD, lat)
            put(COL_LONGITUD, lon)
            put(COL_CATEGORIA, categoria)
            put(COL_PUNTOS, puntos)
            put(COL_COMPLETADA, completada)
            put(COL_CODIGO_QR, qr)
            put(COL_SELFIE_TOMADA, selfie)
            put(COL_URL_VIDEO, urlVideo)
        }
        db.insert(TABLE_PUNTOS, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PUNTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        onCreate(db)
    }

    fun obtenerTodosLosPuntos(): List<PuntoInteres> {
        val lista = mutableListOf<PuntoInteres>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PUNTOS, null, null, null, null, null, "$COL_ID DESC")

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(COL_ID))
                    val nombre = it.getString(it.getColumnIndexOrThrow(COL_NOMBRE))
                    val descripcion = it.getString(it.getColumnIndexOrThrow(COL_DESCRIPCION))
                    val comuna = it.getString(it.getColumnIndexOrThrow(COL_COMUNA))
                    val latitud = it.getDouble(it.getColumnIndexOrThrow(COL_LATITUD))
                    val longitud = it.getDouble(it.getColumnIndexOrThrow(COL_LONGITUD))
                    val categoria = it.getString(it.getColumnIndexOrThrow(COL_CATEGORIA))
                    val puntos = it.getInt(it.getColumnIndexOrThrow(COL_PUNTOS))
                    val completada = it.getInt(it.getColumnIndexOrThrow(COL_COMPLETADA)) == 1
                    val codigoQR = it.getString(it.getColumnIndexOrThrow(COL_CODIGO_QR))
                    val selfieTomada = it.getInt(it.getColumnIndexOrThrow(COL_SELFIE_TOMADA)) == 1
                    val urlVideo = it.getString(it.getColumnIndexOrThrow(COL_URL_VIDEO))

                    lista.add(
                        PuntoInteres(
                            id = id,
                            nombre = nombre,
                            descripcion = descripcion,
                            comuna = comuna,
                            latitud = latitud,
                            longitud = longitud,
                            categoriaIA = categoria,
                            puntosRecompensa = puntos,
                            completada = completada,
                            codigoQR = codigoQR,
                            selfieTomada = selfieTomada,
                            urlVideo = urlVideo
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return lista
    }

    fun insertarPunto(punto: PuntoInteres): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ID, punto.id)
            put(COL_NOMBRE, punto.nombre)
            put(COL_DESCRIPCION, punto.descripcion)
            put(COL_COMUNA, punto.comuna)
            put(COL_LATITUD, punto.latitud)
            put(COL_LONGITUD, punto.longitud)
            put(COL_CATEGORIA, punto.categoriaIA)
            put(COL_PUNTOS, punto.puntosRecompensa)
            put(COL_COMPLETADA, if (punto.completada) 1 else 0)
            put(COL_CODIGO_QR, punto.codigoQR)
            put(COL_SELFIE_TOMADA, if (punto.selfieTomada) 1 else 0)
            put(COL_URL_VIDEO, punto.urlVideo)
        }
        val result = db.insert(TABLE_PUNTOS, null, values)
        return result != -1L
    }

    fun actualizarPuntoCompletado(id: Int, completada: Boolean, selfieTomada: Boolean) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_COMPLETADA, if (completada) 1 else 0)
            put(COL_SELFIE_TOMADA, if (selfieTomada) 1 else 0)
        }
        db.update(TABLE_PUNTOS, values, "$COL_ID = ?", arrayOf(id.toString()))
    }

    fun obtenerUsuario(): Usuario? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USUARIO, null, null, null, null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                val email = it.getString(it.getColumnIndexOrThrow(COL_USER_EMAIL))
                val nombre = it.getString(it.getColumnIndexOrThrow(COL_USER_NOMBRE))
                val rol = it.getString(it.getColumnIndexOrThrow(COL_USER_ROL))
                val puntaje = it.getInt(it.getColumnIndexOrThrow(COL_USER_PUNTAJE))
                return Usuario(email, nombre, rol, puntaje)
            }
        }
        return null
    }

    fun guardarOActualizarUsuario(usuario: Usuario) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_EMAIL, usuario.email)
            put(COL_USER_NOMBRE, usuario.nombre)
            put(COL_USER_ROL, usuario.rol)
            put(COL_USER_PUNTAJE, usuario.puntaje)
        }
        val rows = db.update(TABLE_USUARIO, values, "$COL_USER_EMAIL = ?", arrayOf(usuario.email))
        if (rows == 0) {
            db.insert(TABLE_USUARIO, null, values)
        }
    }
}
