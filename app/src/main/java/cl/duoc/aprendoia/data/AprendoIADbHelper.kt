package cl.duoc.aprendoia.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duoc.aprendoia.model.HistorialItem
import cl.duoc.aprendoia.model.NotificacionItem
import cl.duoc.aprendoia.model.PuntoInteres
import cl.duoc.aprendoia.model.Usuario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AprendoIADbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "aprendo_ia_v5.db"
        const val DATABASE_VERSION = 1

        // Tabla Puntos
        const val TABLE_PUNTOS = "puntos"
        const val COL_PUNTO_ID = "id"
        const val COL_PUNTO_NOMBRE = "nombre"
        const val COL_PUNTO_DESCRIPCION = "descripcion"
        const val COL_PUNTO_COMUNA = "comuna"
        const val COL_PUNTO_LATITUD = "latitud"
        const val COL_PUNTO_LONGITUD = "longitud"
        const val COL_PUNTO_CATEGORIA = "categoria_ia"
        const val COL_PUNTO_PUNTOS = "puntos_recompensa"
        const val COL_PUNTO_CODIGO_QR = "codigo_qr"
        const val COL_PUNTO_URL_VIDEO = "url_video"
        const val COL_PUNTO_VIDEO_TITULO = "video_titulo"
        const val COL_PUNTO_VIDEO_TEMA = "video_tema"
        const val COL_PUNTO_DISTANCIA = "distancia_aprox"
        const val COL_PUNTO_ES_HITO = "es_hito"

        // Tabla Usuarios
        const val TABLE_USUARIO = "usuario"
        const val COL_USER_ID = "id"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_NOMBRE = "nombre"
        const val COL_USER_ALIAS = "alias"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_TELEFONO = "telefono"
        const val COL_USER_DIRECCION = "direccion"
        const val COL_USER_AVATAR = "avatar"
        const val COL_USER_ROL = "rol"
        const val COL_USER_PUNTAJE = "puntaje"
        const val COL_USER_QR_ENCONTRADOS = "qr_encontrados"

        // Tabla Descubrimientos por Usuario
        const val TABLE_DESCUBRIMIENTOS = "descubrimientos"
        const val COL_DESC_ID = "id"
        const val COL_DESC_USUARIO_ID = "usuario_id"
        const val COL_DESC_PUNTO_ID = "punto_id"
        const val COL_DESC_FECHA = "fecha"
        const val COL_DESC_SELFIE_TOMADA = "selfie_tomada"
        const val COL_DESC_SELFIE_URI = "selfie_uri"
        const val COL_DESC_VIDEO_VISTO = "video_visto"
        const val COL_DESC_PUNTOS_GANADOS = "puntos_ganados"

        // Tabla Historial
        const val TABLE_HISTORIAL = "historial"
        const val COL_HIST_ID = "id"
        const val COL_HIST_USUARIO_ID = "usuario_id"
        const val COL_HIST_TIPO = "tipo" // "QR", "Video", "Selfie"
        const val COL_HIST_TITULO = "titulo"
        const val COL_HIST_LUGAR = "lugar"
        const val COL_HIST_FECHA = "fecha"
        const val COL_HIST_HORA = "hora"
        const val COL_HIST_PUNTOS = "puntos"

        // Tabla Notificaciones
        const val TABLE_NOTIFICACIONES = "notificaciones"
        const val COL_NOTIF_ID = "id"
        const val COL_NOTIF_TITULO = "titulo"
        const val COL_NOTIF_MENSAJE = "mensaje"
        const val COL_NOTIF_TIEMPO = "tiempo"
        const val COL_NOTIF_TIPO = "tipo"
        const val COL_NOTIF_DESTINO = "punto_id_destino"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTablePuntos = """
            CREATE TABLE $TABLE_PUNTOS (
                $COL_PUNTO_ID INTEGER PRIMARY KEY,
                $COL_PUNTO_NOMBRE TEXT NOT NULL,
                $COL_PUNTO_DESCRIPCION TEXT NOT NULL,
                $COL_PUNTO_COMUNA TEXT NOT NULL,
                $COL_PUNTO_LATITUD REAL NOT NULL,
                $COL_PUNTO_LONGITUD REAL NOT NULL,
                $COL_PUNTO_CATEGORIA TEXT NOT NULL,
                $COL_PUNTO_PUNTOS INTEGER NOT NULL DEFAULT 10,
                $COL_PUNTO_CODIGO_QR TEXT NOT NULL,
                $COL_PUNTO_URL_VIDEO TEXT NOT NULL,
                $COL_PUNTO_VIDEO_TITULO TEXT NOT NULL,
                $COL_PUNTO_VIDEO_TEMA TEXT NOT NULL,
                $COL_PUNTO_DISTANCIA TEXT NOT NULL DEFAULT '250 m',
                $COL_PUNTO_ES_HITO INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()

        val createTableUsuario = """
            CREATE TABLE $TABLE_USUARIO (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_EMAIL TEXT UNIQUE NOT NULL,
                $COL_USER_NOMBRE TEXT NOT NULL,
                $COL_USER_ALIAS TEXT NOT NULL,
                $COL_USER_PASSWORD TEXT NOT NULL DEFAULT '123',
                $COL_USER_TELEFONO TEXT NOT NULL DEFAULT '+56 9 1234 5678',
                $COL_USER_DIRECCION TEXT NOT NULL DEFAULT 'Santiago',
                $COL_USER_AVATAR TEXT NOT NULL DEFAULT 'avatar_1',
                $COL_USER_ROL TEXT NOT NULL DEFAULT 'Cliente',
                $COL_USER_PUNTAJE INTEGER NOT NULL DEFAULT 0,
                $COL_USER_QR_ENCONTRADOS INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        val createTableDescubrimientos = """
            CREATE TABLE $TABLE_DESCUBRIMIENTOS (
                $COL_DESC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_DESC_USUARIO_ID INTEGER NOT NULL,
                $COL_DESC_PUNTO_ID INTEGER NOT NULL,
                $COL_DESC_FECHA TEXT NOT NULL,
                $COL_DESC_SELFIE_TOMADA INTEGER NOT NULL DEFAULT 0,
                $COL_DESC_SELFIE_URI TEXT,
                $COL_DESC_VIDEO_VISTO INTEGER NOT NULL DEFAULT 0,
                $COL_DESC_PUNTOS_GANADOS INTEGER NOT NULL DEFAULT 10
            )
        """.trimIndent()

        val createTableHistorial = """
            CREATE TABLE $TABLE_HISTORIAL (
                $COL_HIST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_HIST_USUARIO_ID INTEGER NOT NULL,
                $COL_HIST_TIPO TEXT NOT NULL,
                $COL_HIST_TITULO TEXT NOT NULL,
                $COL_HIST_LUGAR TEXT NOT NULL,
                $COL_HIST_FECHA TEXT NOT NULL,
                $COL_HIST_HORA TEXT NOT NULL,
                $COL_HIST_PUNTOS INTEGER NOT NULL
            )
        """.trimIndent()

        val createTableNotificaciones = """
            CREATE TABLE $TABLE_NOTIFICACIONES (
                $COL_NOTIF_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOTIF_TITULO TEXT NOT NULL,
                $COL_NOTIF_MENSAJE TEXT NOT NULL,
                $COL_NOTIF_TIEMPO TEXT NOT NULL,
                $COL_NOTIF_TIPO TEXT NOT NULL,
                $COL_NOTIF_DESTINO INTEGER
            )
        """.trimIndent()

        db.execSQL(createTablePuntos)
        db.execSQL(createTableUsuario)
        db.execSQL(createTableDescubrimientos)
        db.execSQL(createTableHistorial)
        db.execSQL(createTableNotificaciones)

        // Seed data
        poblarDatosIniciales(db)
    }

    private fun poblarDatosIniciales(db: SQLiteDatabase) {
        // 1. Roles requeridos por el caso
        // Administrador: Nombre Administrador, Alias Admin, pass 123
        insertarUsuarioDb(db, "admin@triskeledu.cl", "Administrador", "Admin", "123", "+56 9 9999 8888", "Casa Matriz Triskeledu", "avatar_admin", "Admin", 9999, 15)
        // Creador de Contenido: Nombre Contenido, Alias GenCont, pass 123
        insertarUsuarioDb(db, "contenido@triskeledu.cl", "Contenido", "GenCont", "123", "+56 9 7777 6666", "Santiago Centro", "avatar_gencont", "GenCont", 5000, 10)

        // 2. Usuario Estudiante (Cliente principal)
        insertarUsuarioDb(db, "test@duocuc.cl", "Estudiante", "Estudiante", "123", "+56 9 1234 5678", "Santiago Centro", "avatar_1", "Cliente", 150, 1)

        // 3. Puntos de Santiago con hitos turísticos y cápsulas de IA
        insertarPuntoDb(db, 1, "Palacio de La Moneda", "Sede de gobierno chileno. Descubre cómo la Visión Artificial permite reconocer fachadas y monumentos históricos.", "Santiago Centro", -33.4429, -70.6540, "Visión Artificial", 10, "QR-MONEDA-001", "https://www.youtube.com/shorts/sample2", "Visión Artificial y Monumentos", "Visión Artificial", "600 m", 1)
        insertarPuntoDb(db, 2, "Plaza de Armas", "Centro histórico y cultural de Santiago. Aquí encontrarás un código QR que te permitirá aprender sobre Machine Learning y la historia patrimonial.", "Santiago Centro", -33.4378, -70.6504, "Machine Learning", 10, "QR-PLAZA-002", "https://www.youtube.com/shorts/sample1", "Machine Learning en Ciudades", "Machine Learning", "250 m", 1)
        insertarPuntoDb(db, 3, "Cerro Santa Lucía", "Hito urbano y mirador tradicional de Santiago. Reconoce el paisaje sonoro y el canto de aves con procesamiento de audio e IA.", "Santiago Centro", -33.4402, -70.6439, "Audio y Sonido", 10, "QR-LUCIA-003", "https://www.youtube.com/shorts/sample3", "Audio y Bioacústica con IA", "Audio y Sonido", "850 m", 1)
        insertarPuntoDb(db, 4, "Museo de Bellas Artes", "Epicentro del arte en el Parque Forestal. Aprende sobre IA Generativa y creación de arte digital.", "Santiago Centro", -33.4357, -70.6443, "IA Generativa", 10, "QR-BELLASARTES-004", "https://www.youtube.com/shorts/sample4", "IA y Arte Generativo", "IA Generativa", "1.2 km", 1)
        insertarPuntoDb(db, 5, "Barrio Lastarria", "Barrio gastronómico y cultural. Conoce los avances de la robótica y el transporte autónomo en el entorno urbano.", "Santiago Centro", -33.4385, -70.6415, "Robótica", 10, "QR-LASTARRIA-005", "https://www.youtube.com/shorts/sample5", "Robótica en la Vida Diaria", "Robótica", "1.0 km", 1)
        insertarPuntoDb(db, 6, "Duoc UC Alonso de Ovalle", "Sede Padre Alonso de Ovalle. Taller de robótica, sensores y desarrollo móvil con IA en Santiago.", "Santiago Centro", -33.4475, -70.6558, "Fundamentos de IA", 10, "QR-DUOC-006", "https://www.youtube.com/shorts/sample6", "Introducción a la Inteligencia Artificial", "Fundamentos de IA", "400 m", 1)

        // 4. Descubrimiento inicial para el Estudiante (id = 3)
        registrarDescubrimientoDirecto(db, 3, 6, "12/09/2026", 1, 1, 110)

        // 5. Historial inicial para el Estudiante
        insertarHistorialDirecto(db, 3, "Selfie", "Selfie turística", "Duoc UC Alonso de Ovalle", "12/09/2026", "10:30", 100)
        insertarHistorialDirecto(db, 3, "QR", "QR escaneado", "Duoc UC Alonso de Ovalle", "12/09/2026", "10:25", 10)
        insertarHistorialDirecto(db, 3, "Video", "Video visto", "Introducción a la IA", "12/09/2026", "10:28", 0)

        // 6. Notificaciones iniciales
        insertarNotificacionDirecto(db, "Nuevo punto cercano", "Hay un QR en Palacio de La Moneda a 200 m de tu ubicación. ¡No te lo pierdas!", "Ahora", "punto", 1)
        insertarNotificacionDirecto(db, "Desafío especial", "Visita Palacio de La Moneda y gana 100 puntos extra con una selfie.", "2 h", "desafio", 1)
        insertarNotificacionDirecto(db, "Nuevo video disponible", "Se ha agregado nuevo contenido de IA: Fundamentos y Ética.", "1 d", "video", 6)
        insertarNotificacionDirecto(db, "¡Felicidades!", "Has completado varios puntos turísticos de Santiago. ¡Sigue así!", "2 d", "felicitacion", null)
        insertarNotificacionDirecto(db, "Evento en Santiago", "Mañana habrá nuevos puntos QR en el casco histórico.", "3 d", "evento", null)
    }

    private fun insertarUsuarioDb(
        db: SQLiteDatabase, email: String, nombre: String, alias: String, pass: String,
        tel: String, dir: String, avatar: String, rol: String, puntaje: Int, qrCount: Int
    ) {
        val values = ContentValues().apply {
            put(COL_USER_EMAIL, email)
            put(COL_USER_NOMBRE, nombre)
            put(COL_USER_ALIAS, alias)
            put(COL_USER_PASSWORD, pass)
            put(COL_USER_TELEFONO, tel)
            put(COL_USER_DIRECCION, dir)
            put(COL_USER_AVATAR, avatar)
            put(COL_USER_ROL, rol)
            put(COL_USER_PUNTAJE, puntaje)
            put(COL_USER_QR_ENCONTRADOS, qrCount)
        }
        db.insert(TABLE_USUARIO, null, values)
    }

    private fun insertarPuntoDb(
        db: SQLiteDatabase, id: Int, nombre: String, desc: String, comuna: String,
        lat: Double, lon: Double, cat: String, pts: Int, qr: String, url: String,
        vTitulo: String, vTema: String, dist: String, esHito: Int
    ) {
        val values = ContentValues().apply {
            put(COL_PUNTO_ID, id)
            put(COL_PUNTO_NOMBRE, nombre)
            put(COL_PUNTO_DESCRIPCION, desc)
            put(COL_PUNTO_COMUNA, comuna)
            put(COL_PUNTO_LATITUD, lat)
            put(COL_PUNTO_LONGITUD, lon)
            put(COL_PUNTO_CATEGORIA, cat)
            put(COL_PUNTO_PUNTOS, pts)
            put(COL_PUNTO_CODIGO_QR, qr)
            put(COL_PUNTO_URL_VIDEO, url)
            put(COL_PUNTO_VIDEO_TITULO, vTitulo)
            put(COL_PUNTO_VIDEO_TEMA, vTema)
            put(COL_PUNTO_DISTANCIA, dist)
            put(COL_PUNTO_ES_HITO, esHito)
        }
        db.insert(TABLE_PUNTOS, null, values)
    }

    private fun registrarDescubrimientoDirecto(
        db: SQLiteDatabase, uId: Int, pId: Int, fecha: String, selfie: Int, visto: Int, pts: Int
    ) {
        val values = ContentValues().apply {
            put(COL_DESC_USUARIO_ID, uId)
            put(COL_DESC_PUNTO_ID, pId)
            put(COL_DESC_FECHA, fecha)
            put(COL_DESC_SELFIE_TOMADA, selfie)
            put(COL_DESC_VIDEO_VISTO, visto)
            put(COL_DESC_PUNTOS_GANADOS, pts)
        }
        db.insert(TABLE_DESCUBRIMIENTOS, null, values)
    }

    private fun insertarHistorialDirecto(
        db: SQLiteDatabase, uId: Int, tipo: String, titulo: String, lugar: String, fecha: String, hora: String, pts: Int
    ) {
        val values = ContentValues().apply {
            put(COL_HIST_USUARIO_ID, uId)
            put(COL_HIST_TIPO, tipo)
            put(COL_HIST_TITULO, titulo)
            put(COL_HIST_LUGAR, lugar)
            put(COL_HIST_FECHA, fecha)
            put(COL_HIST_HORA, hora)
            put(COL_HIST_PUNTOS, pts)
        }
        db.insert(TABLE_HISTORIAL, null, values)
    }

    private fun insertarNotificacionDirecto(
        db: SQLiteDatabase, titulo: String, mensaje: String, tiempo: String, tipo: String, destino: Int?
    ) {
        val values = ContentValues().apply {
            put(COL_NOTIF_TITULO, titulo)
            put(COL_NOTIF_MENSAJE, mensaje)
            put(COL_NOTIF_TIEMPO, tiempo)
            put(COL_NOTIF_TIPO, tipo)
            if (destino != null) put(COL_NOTIF_DESTINO, destino)
        }
        db.insert(TABLE_NOTIFICACIONES, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DESCUBRIMIENTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORIAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTIFICACIONES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PUNTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIO")
        onCreate(db)
    }

    // =========================================================================
    // USUARIOS / JUGADORES
    // =========================================================================
    fun obtenerTodosLosUsuarios(): List<Usuario> {
        val lista = mutableListOf<Usuario>()
        val db = readableDatabase
        val cursor = db.query(TABLE_USUARIO, null, null, null, null, null, "$COL_USER_ID ASC")
        cursor.use {
            while (it.moveToNext()) {
                lista.add(mapearUsuario(it))
            }
        }
        return lista
    }

    fun obtenerUsuarioPorId(id: Int): Usuario? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USUARIO, null, "$COL_USER_ID = ?", arrayOf(id.toString()), null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                return mapearUsuario(it)
            }
        }
        return null
    }

    fun obtenerUsuarioPorEmail(email: String): Usuario? {
        val db = readableDatabase
        val cursor = db.query(TABLE_USUARIO, null, "$COL_USER_EMAIL = ? OR $COL_USER_ALIAS = ?", arrayOf(email, email), null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                return mapearUsuario(it)
            }
        }
        return null
    }

    fun insertarUsuario(usuario: Usuario): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_EMAIL, usuario.email)
            put(COL_USER_NOMBRE, usuario.nombre)
            put(COL_USER_ALIAS, usuario.alias.ifBlank { usuario.nombre.substringBefore(" ") })
            put(COL_USER_PASSWORD, usuario.password)
            put(COL_USER_TELEFONO, usuario.telefono)
            put(COL_USER_DIRECCION, usuario.direccion)
            put(COL_USER_AVATAR, usuario.avatar)
            put(COL_USER_ROL, usuario.rol)
            put(COL_USER_PUNTAJE, usuario.puntaje)
            put(COL_USER_QR_ENCONTRADOS, usuario.qrEncontrados)
        }
        return db.insert(TABLE_USUARIO, null, values)
    }

    fun actualizarUsuario(usuario: Usuario): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_NOMBRE, usuario.nombre)
            put(COL_USER_ALIAS, usuario.alias)
            put(COL_USER_PASSWORD, usuario.password)
            put(COL_USER_TELEFONO, usuario.telefono)
            put(COL_USER_DIRECCION, usuario.direccion)
            put(COL_USER_AVATAR, usuario.avatar)
            put(COL_USER_ROL, usuario.rol)
            put(COL_USER_PUNTAJE, usuario.puntaje)
            put(COL_USER_QR_ENCONTRADOS, usuario.qrEncontrados)
        }
        return db.update(TABLE_USUARIO, values, "$COL_USER_ID = ?", arrayOf(usuario.id.toString()))
    }

    fun eliminarUsuario(id: Int): Int {
        val db = writableDatabase
        // Eliminar descubrimientos e historial asociados
        db.delete(TABLE_DESCUBRIMIENTOS, "$COL_DESC_USUARIO_ID = ?", arrayOf(id.toString()))
        db.delete(TABLE_HISTORIAL, "$COL_HIST_USUARIO_ID = ?", arrayOf(id.toString()))
        return db.delete(TABLE_USUARIO, "$COL_USER_ID = ?", arrayOf(id.toString()))
    }

    fun guardarOActualizarUsuario(usuario: Usuario) {
        if (usuario.id > 0) {
            actualizarUsuario(usuario)
        } else {
            val existente = obtenerUsuarioPorEmail(usuario.email)
            if (existente != null) {
                actualizarUsuario(usuario.copy(id = existente.id))
            } else {
                insertarUsuario(usuario)
            }
        }
    }

    fun obtenerUsuario(): Usuario? {
        val lista = obtenerTodosLosUsuarios()
        // Prefiere el primer jugador tipo Cliente (ej. Ana)
        return lista.firstOrNull { it.rol == "Cliente" } ?: lista.firstOrNull()
    }

    private fun mapearUsuario(cursor: android.database.Cursor): Usuario {
        return Usuario(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NOMBRE)),
            alias = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ALIAS)),
            password = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)),
            telefono = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_TELEFONO)),
            direccion = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_DIRECCION)),
            avatar = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_AVATAR)),
            rol = cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ROL)),
            puntaje = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_PUNTAJE)),
            qrEncontrados = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_QR_ENCONTRADOS))
        )
    }

    // =========================================================================
    // PUNTOS DE INTERÉS
    // =========================================================================
    fun obtenerTodosLosPuntos(usuarioId: Int? = null): List<PuntoInteres> {
        val lista = mutableListOf<PuntoInteres>()
        val db = readableDatabase
        val cursor = db.query(TABLE_PUNTOS, null, null, null, null, null, "$COL_PUNTO_ID ASC")

        cursor.use {
            while (it.moveToNext()) {
                val pId = it.getInt(it.getColumnIndexOrThrow(COL_PUNTO_ID))
                val descInfo = if (usuarioId != null) obtenerDescubrimiento(usuarioId, pId) else null

                val completada = descInfo != null
                val selfieTomada = descInfo?.first == true
                val selfieUri = descInfo?.second
                val videoVisto = descInfo?.third == true

                lista.add(
                    PuntoInteres(
                        id = pId,
                        nombre = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_NOMBRE)),
                        descripcion = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_DESCRIPCION)),
                        comuna = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_COMUNA)),
                        latitud = it.getDouble(it.getColumnIndexOrThrow(COL_PUNTO_LATITUD)),
                        longitud = it.getDouble(it.getColumnIndexOrThrow(COL_PUNTO_LONGITUD)),
                        categoriaIA = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_CATEGORIA)),
                        puntosRecompensa = it.getInt(it.getColumnIndexOrThrow(COL_PUNTO_PUNTOS)),
                        completada = completada,
                        codigoQR = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_CODIGO_QR)),
                        selfieTomada = selfieTomada,
                        selfieUri = selfieUri,
                        urlVideo = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_URL_VIDEO)),
                        videoTitulo = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_VIDEO_TITULO)),
                        videoTema = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_VIDEO_TEMA)),
                        distanciaAprox = it.getString(it.getColumnIndexOrThrow(COL_PUNTO_DISTANCIA)),
                        esHitoTuristico = it.getInt(it.getColumnIndexOrThrow(COL_PUNTO_ES_HITO)) == 1,
                        videoVisto = videoVisto
                    )
                )
            }
        }
        return lista
    }

    fun obtenerPuntoPorId(puntoId: Int, usuarioId: Int? = null): PuntoInteres? {
        val todos = obtenerTodosLosPuntos(usuarioId)
        return todos.firstOrNull { it.id == puntoId }
    }

    fun insertarPunto(punto: PuntoInteres): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PUNTO_ID, punto.id)
            put(COL_PUNTO_NOMBRE, punto.nombre)
            put(COL_PUNTO_DESCRIPCION, punto.descripcion)
            put(COL_PUNTO_COMUNA, punto.comuna)
            put(COL_PUNTO_LATITUD, punto.latitud)
            put(COL_PUNTO_LONGITUD, punto.longitud)
            put(COL_PUNTO_CATEGORIA, punto.categoriaIA)
            put(COL_PUNTO_PUNTOS, punto.puntosRecompensa)
            put(COL_PUNTO_CODIGO_QR, punto.codigoQR)
            put(COL_PUNTO_URL_VIDEO, punto.urlVideo)
            put(COL_PUNTO_VIDEO_TITULO, punto.videoTitulo.ifBlank { punto.nombre })
            put(COL_PUNTO_VIDEO_TEMA, punto.videoTema.ifBlank { punto.categoriaIA })
            put(COL_PUNTO_DISTANCIA, punto.distanciaAprox)
            put(COL_PUNTO_ES_HITO, if (punto.esHitoTuristico) 1 else 0)
        }
        val result = db.insertWithOnConflict(TABLE_PUNTOS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        return result != -1L
    }

    fun actualizarPuntoCompletado(id: Int, completada: Boolean, selfieTomada: Boolean) {
        // Compatibilidad con código previo
    }

    // =========================================================================
    // DESCUBRIMIENTOS POR USUARIO
    // =========================================================================
    // Retorna Triple(selfieTomada, selfieUri, videoVisto)
    private fun obtenerDescubrimiento(usuarioId: Int, puntoId: Int): Triple<Boolean, String?, Boolean>? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_DESCUBRIMIENTOS,
            arrayOf(COL_DESC_SELFIE_TOMADA, COL_DESC_SELFIE_URI, COL_DESC_VIDEO_VISTO),
            "$COL_DESC_USUARIO_ID = ? AND $COL_DESC_PUNTO_ID = ?",
            arrayOf(usuarioId.toString(), puntoId.toString()),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                val selfie = it.getInt(it.getColumnIndexOrThrow(COL_DESC_SELFIE_TOMADA)) == 1
                val uri = it.getString(it.getColumnIndexOrThrow(COL_DESC_SELFIE_URI))
                val video = it.getInt(it.getColumnIndexOrThrow(COL_DESC_VIDEO_VISTO)) == 1
                return Triple(selfie, uri, video)
            }
        }
        return null
    }

    fun esPuntoDescubiertoPorUsuario(usuarioId: Int, puntoId: Int): Boolean {
        return obtenerDescubrimiento(usuarioId, puntoId) != null
    }

    fun registrarDescubrimiento(usuarioId: Int, puntoId: Int, puntosGanados: Int, selfieTomada: Boolean = false, selfieUri: String? = null): Boolean {
        val db = writableDatabase
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date())
        val hora = SimpleDateFormat("HH:mm", Locale.US).format(Date())

        val existente = obtenerDescubrimiento(usuarioId, puntoId)
        if (existente == null) {
            // Nuevo descubrimiento
            val values = ContentValues().apply {
                put(COL_DESC_USUARIO_ID, usuarioId)
                put(COL_DESC_PUNTO_ID, puntoId)
                put(COL_DESC_FECHA, fecha)
                put(COL_DESC_SELFIE_TOMADA, if (selfieTomada) 1 else 0)
                put(COL_DESC_SELFIE_URI, selfieUri)
                put(COL_DESC_VIDEO_VISTO, 0)
                put(COL_DESC_PUNTOS_GANADOS, puntosGanados)
            }
            db.insert(TABLE_DESCUBRIMIENTOS, null, values)

            // Actualizar puntaje y QR count del usuario
            val user = obtenerUsuarioPorId(usuarioId)
            if (user != null) {
                actualizarUsuario(user.copy(
                    puntaje = user.puntaje + puntosGanados,
                    qrEncontrados = user.qrEncontrados + 1
                ))
            }

            // Registrar en historial
            val punto = obtenerPuntoPorId(puntoId)
            val nombreLugar = punto?.nombre ?: "Punto Santiago #$puntoId"
            insertarHistorial(HistorialItem(
                usuarioId = usuarioId,
                tipo = "QR",
                titulo = "QR escaneado",
                lugar = nombreLugar,
                fecha = fecha,
                hora = hora,
                puntos = puntosGanados
            ))
            return true
        } else if (selfieTomada && !existente.first) {
            // Bonificación de selfie a punto ya descubierto
            val values = ContentValues().apply {
                put(COL_DESC_SELFIE_TOMADA, 1)
                put(COL_DESC_SELFIE_URI, selfieUri)
                put(COL_DESC_PUNTOS_GANADOS, 110)
            }
            db.update(TABLE_DESCUBRIMIENTOS, values, "$COL_DESC_USUARIO_ID = ? AND $COL_DESC_PUNTO_ID = ?", arrayOf(usuarioId.toString(), puntoId.toString()))

            val user = obtenerUsuarioPorId(usuarioId)
            if (user != null) {
                actualizarUsuario(user.copy(puntaje = user.puntaje + 100))
            }

            val punto = obtenerPuntoPorId(puntoId)
            val nombreLugar = punto?.nombre ?: "Hito Turístico"
            insertarHistorial(HistorialItem(
                usuarioId = usuarioId,
                tipo = "Selfie",
                titulo = "Selfie turística",
                lugar = nombreLugar,
                fecha = fecha,
                hora = hora,
                puntos = 100
            ))
            return true
        }
        return false
    }

    fun registrarSelfiePunto(usuarioId: Int, puntoId: Int, uri: String?): Boolean {
        return registrarDescubrimiento(usuarioId, puntoId, puntosGanados = 10, selfieTomada = true, selfieUri = uri)
    }

    fun marcarVideoVisto(usuarioId: Int, puntoId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_DESC_VIDEO_VISTO, 1)
        }
        val rows = db.update(TABLE_DESCUBRIMIENTOS, values, "$COL_DESC_USUARIO_ID = ? AND $COL_DESC_PUNTO_ID = ?", arrayOf(usuarioId.toString(), puntoId.toString()))

        val punto = obtenerPuntoPorId(puntoId)
        val nombreVideo = punto?.videoTitulo ?: punto?.nombre ?: "Cápsula IA"
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date())
        val hora = SimpleDateFormat("HH:mm", Locale.US).format(Date())

        insertarHistorial(HistorialItem(
            usuarioId = usuarioId,
            tipo = "Video",
            titulo = "Video visto",
            lugar = nombreVideo,
            fecha = fecha,
            hora = hora,
            puntos = 0
        ))
        return rows > 0
    }

    // =========================================================================
    // HISTORIAL Y NOTIFICACIONES
    // =========================================================================
    fun obtenerHistorialPorUsuario(usuarioId: Int): List<HistorialItem> {
        val lista = mutableListOf<HistorialItem>()
        val db = readableDatabase
        val cursor = db.query(TABLE_HISTORIAL, null, "$COL_HIST_USUARIO_ID = ?", arrayOf(usuarioId.toString()), null, null, "$COL_HIST_ID DESC")
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    HistorialItem(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_HIST_ID)),
                        usuarioId = it.getInt(it.getColumnIndexOrThrow(COL_HIST_USUARIO_ID)),
                        tipo = it.getString(it.getColumnIndexOrThrow(COL_HIST_TIPO)),
                        titulo = it.getString(it.getColumnIndexOrThrow(COL_HIST_TITULO)),
                        lugar = it.getString(it.getColumnIndexOrThrow(COL_HIST_LUGAR)),
                        fecha = it.getString(it.getColumnIndexOrThrow(COL_HIST_FECHA)),
                        hora = it.getString(it.getColumnIndexOrThrow(COL_HIST_HORA)),
                        puntos = it.getInt(it.getColumnIndexOrThrow(COL_HIST_PUNTOS))
                    )
                )
            }
        }
        return lista
    }

    fun insertarHistorial(item: HistorialItem): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_HIST_USUARIO_ID, item.usuarioId)
            put(COL_HIST_TIPO, item.tipo)
            put(COL_HIST_TITULO, item.titulo)
            put(COL_HIST_LUGAR, item.lugar)
            put(COL_HIST_FECHA, item.fecha)
            put(COL_HIST_HORA, item.hora)
            put(COL_HIST_PUNTOS, item.puntos)
        }
        return db.insert(TABLE_HISTORIAL, null, values)
    }

    fun obtenerTodasLasNotificaciones(): List<NotificacionItem> {
        val lista = mutableListOf<NotificacionItem>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NOTIFICACIONES, null, null, null, null, null, "$COL_NOTIF_ID DESC")
        cursor.use {
            while (it.moveToNext()) {
                val destinoCol = it.getColumnIndexOrThrow(COL_NOTIF_DESTINO)
                val destino = if (it.isNull(destinoCol)) null else it.getInt(destinoCol)
                lista.add(
                    NotificacionItem(
                        id = it.getInt(it.getColumnIndexOrThrow(COL_NOTIF_ID)),
                        titulo = it.getString(it.getColumnIndexOrThrow(COL_NOTIF_TITULO)),
                        mensaje = it.getString(it.getColumnIndexOrThrow(COL_NOTIF_MENSAJE)),
                        tiempo = it.getString(it.getColumnIndexOrThrow(COL_NOTIF_TIEMPO)),
                        tipo = it.getString(it.getColumnIndexOrThrow(COL_NOTIF_TIPO)),
                        puntoIdDestino = destino
                    )
                )
            }
        }
        return lista
    }
}
