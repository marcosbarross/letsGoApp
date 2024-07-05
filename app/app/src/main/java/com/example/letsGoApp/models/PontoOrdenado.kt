package com.example.letsGoApp.models

import com.google.gson.annotations.SerializedName

data class PontoOrdenado(
    @SerializedName("id") val id: Int,
    @SerializedName("nome") val nome: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("distancia") val distancia: Double
)
