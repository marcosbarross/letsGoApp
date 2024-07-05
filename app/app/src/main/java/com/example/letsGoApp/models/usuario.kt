package com.example.letsGoApp.models

import com.google.gson.annotations.SerializedName

data class usuario(
    @SerializedName("nome") val nome: String,
    @SerializedName("sobrenome") val sobrenome: String,
    @SerializedName("sexo") val sexo: String,
    @SerializedName("data_nascimento") val dataNascimento: String,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)

data class usuarioAuth(
    @SerializedName("email")
    var email: String,
    @SerializedName("senha")
    var senha: String
)

data class AuthResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("id_usuario") val userId: Int
)
