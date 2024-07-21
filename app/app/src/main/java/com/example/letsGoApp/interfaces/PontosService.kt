package com.example.letsGoApp.interfaces

import com.example.letsGoApp.models.AuthResponse
import com.example.letsGoApp.models.PontoOrdenado
import com.example.letsGoApp.models.pontos
import com.example.letsGoApp.models.usuario
import com.example.letsGoApp.models.usuarioAuth
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

    @POST("auth/")
    fun authenticateUsuario(@Body usuario: usuarioAuth): Call<AuthResponse>

    @POST("usuarios/")
    fun createUsuario(@Body usuario: usuario): Call<usuario>

    @POST("pontos/{ponto_id}/entrar")
    fun entrarNoPonto(@Path("ponto_id") pontoId: Int, @Query("usuario_email") usuarioEmail: String): Call<Void>

    @POST("pontos/{ponto_id}/sair")
    fun sairDoPonto(@Path("ponto_id") pontoId: Int, @Query("usuario_email") usuarioEmail: String): Call<Void>

    @GET("pontos/{ponto_id}/usuarios")
    fun getUsuariosDoPonto(@Path("ponto_id") pontoId: Int): Call<List<usuario>>

}
