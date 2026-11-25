package cl.duoc.aprendoia.network

import cl.duoc.aprendoia.network.dto.ApiResponseDto
import cl.duoc.aprendoia.network.dto.ClimaResponseDto
import cl.duoc.aprendoia.network.dto.PuntoInteresDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * 1. Interface Retrofit para el Microservicio Propio (Spring Boot)
 */
interface AprendoIABackendApi {

    @GET("api/puntos")
    suspend fun obtenerPuntos(): Response<ApiResponseDto<List<PuntoInteresDto>>>

    @GET("api/puntos/{id}")
    suspend fun obtenerPuntoPorId(@Path("id") id: Long): Response<ApiResponseDto<PuntoInteresDto>>

    @GET("api/puntos/qr/{codigoQr}")
    suspend fun buscarPorQr(@Path("codigoQr") codigoQr: String): Response<ApiResponseDto<PuntoInteresDto>>

    @POST("api/puntos")
    suspend fun crearPunto(@Body nuevoPunto: PuntoInteresDto): Response<ApiResponseDto<PuntoInteresDto>>

    @PUT("api/puntos/{id}")
    suspend fun actualizarPunto(@Path("id") id: Long, @Body datos: PuntoInteresDto): Response<ApiResponseDto<PuntoInteresDto>>

    @DELETE("api/puntos/{id}")
    suspend fun eliminarPunto(@Path("id") id: Long): Response<ApiResponseDto<Void>>

    @PATCH("api/puntos/{id}/completar")
    suspend fun completarPunto(
        @Path("id") id: Long,
        @Query("selfie") selfie: Boolean = false
    ): Response<ApiResponseDto<PuntoInteresDto>>

    @PUT("api/usuarios/{id}/puntaje")
    suspend fun actualizarPuntaje(
        @Path("id") id: Long,
        @Body body: Map<String, Any>
    ): Response<ApiResponseDto<Any>>
}

/**
 * 2. Interface Retrofit para la API Externa Pública (Open-Meteo Clima Santiago)
 */
interface ClimaExternalApi {

    @GET("v1/forecast?latitude=-33.4489&longitude=-70.6693&current_weather=true")
    suspend fun obtenerClimaSantiago(): Response<ClimaResponseDto>
}

/**
 * 3. Cliente Singleton de Retrofit
 */
object RetrofitClient {

    // Detección automática: En emulador usa 10.0.2.2; en celular físico por cable USB (adb reverse) usa 127.0.0.1
    private val isEmulator: Boolean
        get() {
            val fp = android.os.Build.FINGERPRINT ?: ""
            val model = android.os.Build.MODEL ?: ""
            val brand = android.os.Build.BRAND ?: ""
            val device = android.os.Build.DEVICE ?: ""
            val product = android.os.Build.PRODUCT ?: ""
            return fp.startsWith("generic") ||
                    fp.startsWith("unknown") ||
                    model.contains("google_sdk") ||
                    model.contains("Emulator") ||
                    model.contains("Android SDK built for x86") ||
                    (brand.startsWith("generic") && device.startsWith("generic")) ||
                    "google_sdk" == product
        }

    private val BACKEND_BASE_URL: String
        get() = if (isEmulator) "http://10.0.2.2:8080/" else "http://127.0.0.1:8080/"

    private const val EXTERNAL_API_BASE_URL = "https://api.open-meteo.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    // Instancia para el Backend Spring Boot
    val backendApi: AprendoIABackendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AprendoIABackendApi::class.java)
    }

    // Instancia para la API Externa
    val climaApi: ClimaExternalApi by lazy {
        Retrofit.Builder()
            .baseUrl(EXTERNAL_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClimaExternalApi::class.java)
    }
}
