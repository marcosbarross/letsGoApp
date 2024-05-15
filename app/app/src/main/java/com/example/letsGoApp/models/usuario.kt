package com.example.letsGoApp.models

import com.google.gson.annotations.SerializedName

data class usuario(
    @SerializedName("nome")
    val nome: String,
    @SerializedName("sobrenome")
    val sobrenome: String,
    @SerializedName("sexo")
    val sexo: String,
    @SerializedName("data_nascimento")
    val dataNascimento: String,
    @SerializedName("cep")
    val cep: String,
    @SerializedName("endereco")
    val endereco: String,
    @SerializedName("cpf")
    val cpf: String,
    @SerializedName("senha")
    val senha: String,
    @SerializedName("email")
    val email: String
)

data class PontoDeEncontro(
    @SerializedName("id")
    val id: Int,
    @SerializedName("cep")
    val cep: String,
    @SerializedName("rua")
    val rua: String,
    @SerializedName("numero")
    val numero: Int,
    @SerializedName("complemento")
    val complemento: String?,
    @SerializedName("atividade")
    val atividade: String
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

