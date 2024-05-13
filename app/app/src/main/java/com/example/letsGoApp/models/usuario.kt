package com.example.letsGoApp.models

import com.google.gson.annotations.SerializedName

data class usuario(
    @SerializedName("nome")
    var nome : String,
    @SerializedName("email")
    var email : String,
    @SerializedName("senha")
    var senha : String
)

data class usuarioAuth(
    @SerializedName("email")
    var email : String,
    @SerializedName("senha")
    var senha : String
)

data class AuthResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("id_usuario") val userId: Int
)

