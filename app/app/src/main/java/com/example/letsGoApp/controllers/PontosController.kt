package com.example.letsGoApp.controllers

import com.example.letsGoApp.controllers.apiUtils.Companion.getPathString
import com.example.letsGoApp.interfaces.PontosService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object pontosController {
    private val pontosService: PontosService by lazy {
        apiUtils.getRetrofitInstance(getPathString()).create(PontosService::class.java)
    }

    fun retrievePontosService(): PontosService {
        return pontosService
    }

    suspend fun getDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        var distancia = 0.0

        withContext(Dispatchers.IO) {
            val call = pontosService.CalcularDistancia(lat1, lon1, lat2, lon2)
            val response = call.execute()

            if (response.isSuccessful) {
                val distanciaResponse = response.body()
                distancia = distanciaResponse?.distancia_km ?: 0.0
            } else {
                // TODO: Tratar a falha na solicitação
            }
        }

        return distancia
    }
}