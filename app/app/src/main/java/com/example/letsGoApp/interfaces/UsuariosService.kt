package com.example.letsGoApp.interfaces

import com.example.letsGoApp.models.usuario
import com.example.letsGoApp.models.AuthResponse
import com.example.letsGoApp.models.usuarioAuth
import com.example.letsGoApp.models.pontos
import retrofit2.Call
import retrofit2.http.*

interface UsuariosService {

    @GET("usuarios")
    fun getUsuarios(): Call<List<usuario>>

    @GET("/getUsuarioById/{id}")
    fun getUsuario(@Path("id") id: Int): Call<usuario>

    @GET("usuarios/{id}/pontos")
    fun getPontosDoUsuario(@Path("id") id: Int): Call<List<pontos>>

    @POST("usuarios/")
    fun createUsuario(@Body usuario: usuario): Call<usuario>

    @POST("auth/")
    fun autenticarUsuario(@Body usuario: usuarioAuth): Call<AuthResponse>

    @PUT("usuarios/{id}")
    fun updateUsuario(@Path("id") id: Int, @Body usuario: usuario): Call<usuario>

    @DELETE("usuarios/{id}")
    fun deleteUsuario(@Path("id") id: Int): Call<Void>

}
