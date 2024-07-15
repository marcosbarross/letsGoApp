package com.example.letsGoApp.interfaces

import com.example.letsGoApp.models.PontoOrdenado
import com.example.letsGoApp.models.pontos
import retrofit2.Call
import retrofit2.http.*

interface PontosService {
    @GET("pontos")
    fun getPontos(): Call<List<pontos>>

    @GET("pontos/{id}")
    fun getPonto(@Path("id") id: Int): Call<pontos>

    @POST("pontos")
    fun createPonto(@Body pontos: pontos): Call<pontos>

    @PUT("pontos/{id}")
    fun updatePonto(@Path("id") id: Int, @Body pontos: pontos): Call<pontos>

    @DELETE("pontos/{id}")
    fun deletePonto(@Path("id") id: Int): Call<Void>

    @GET("pontos/ordenados")
    fun getPontosOrdenados(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Call<List<PontoOrdenado>>
}


