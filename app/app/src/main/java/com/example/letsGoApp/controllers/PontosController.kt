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
}