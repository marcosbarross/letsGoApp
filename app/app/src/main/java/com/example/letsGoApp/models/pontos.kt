package com.example.letsGoApp.models

import com.google.gson.annotations.SerializedName

data class pontos(
    @SerializedName("id") val id: Int,
    @SerializedName("id_usuario") val idUsuario: Int,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("atividade") val atividade: String,
    @SerializedName("horario") val horario: String,
    @SerializedName("dias_semana") val diasSemana: String
)
