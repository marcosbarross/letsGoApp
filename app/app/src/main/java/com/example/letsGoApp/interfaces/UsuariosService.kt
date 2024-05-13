package com.example.letsGoApp.interfaces

import com.example.letsGoApp.models.AuthResponse
import com.example.letsGoApp.models.usuarioAuth
import com.example.letsGoApp.models.usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UsuariosService {
    @POST("/AutenticarUsuario/")
    fun autenticarUsuario(@Body usuario: usuarioAuth): Call<AuthResponse>

    @POST("AddUsuario/")
    fun addUsuario(@Body usuario : usuario): Call<Void>
    @GET("/UsuarioPorId/{usuarioId}")
    fun getUsuarioPorId(
        @Path("usuarioId") usuarioId: Int
    ): Call<usuario>
}
