package com.example.letsGoApp.interfaces

import com.example.letsGoApp.models.DistanciaResponse
import com.example.letsGoApp.models.pontos
import com.example.letsGoApp.models.pontosOrdenados
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PontosService {
    @GET("/GetEstacionamentos/")
    fun getPoints(): Call<List<pontos>>

    @POST("/AddEstacionamento/")
    fun addPoint(@Body ponto: pontos): Call<Void>
    @GET("/CalcularDistancia/")
    fun CalcularDistancia(
        @Query("lat1") lat1: Double,
        @Query("lon1") lon1: Double,
        @Query("lat2") lat2: Double,
        @Query("lon2") lon2: Double
    ): Call<DistanciaResponse>

    @GET("/GetEstacionamentosOrdenados/")
    fun getEstacionamentosOrdenados(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double
    ) : Call <List<pontosOrdenados>>
}
