package cl.duoc.aprendoia.network.dto

import com.google.gson.annotations.SerializedName

data class ApiResponseDto<T>(
    @SerializedName("exito") val exito: Boolean,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("datos") val datos: T?,
    @SerializedName("timestamp") val timestamp: String?
)

data class PuntoInteresDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("comuna") val comuna: String? = "Santiago Centro",
    @SerializedName("latitud") val latitud: Double,
    @SerializedName("longitud") val longitud: Double,
    @SerializedName("categoriaIA") val categoriaIA: String? = "General",
    @SerializedName("puntosRecompensa") val puntosRecompensa: Int? = 10,
    @SerializedName("completada") val completada: Boolean? = false,
    @SerializedName("codigoQR") val codigoQR: String? = null,
    @SerializedName("selfieTomada") val selfieTomada: Boolean? = false,
    @SerializedName("selfieUri") val selfieUri: String? = null,
    @SerializedName("urlVideo") val urlVideo: String? = "https://www.youtube.com/shorts/dQw4w9WgXcQ",
    @SerializedName("videoTitulo") val videoTitulo: String? = "",
    @SerializedName("videoTema") val videoTema: String? = "",
    @SerializedName("distanciaAprox") val distanciaAprox: String? = "250 m",
    @SerializedName("esHitoTuristico") val esHitoTuristico: Boolean? = true,
    @SerializedName("videoVisto") val videoVisto: Boolean? = false
)

data class ClimaResponseDto(
    @SerializedName("current_weather") val currentWeather: CurrentWeatherDto?
)

data class CurrentWeatherDto(
    @SerializedName("temperature") val temperature: Double,
    @SerializedName("windspeed") val windspeed: Double,
    @SerializedName("weathercode") val weathercode: Int,
    @SerializedName("time") val time: String
)
