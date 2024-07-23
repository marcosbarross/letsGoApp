package com.example.letsGoApp.controllers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.letsGoApp.interfaces.PontosService
import com.example.letsGoApp.models.usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalhesController : ViewModel() {

    private val _usuarios = MutableLiveData<List<usuario>>()
    val usuarios: LiveData<List<usuario>> get() = _usuarios

    private val _isParticipando = MutableLiveData<Boolean>()
    val isParticipando: LiveData<Boolean> get() = _isParticipando

    private val pontosService: PontosService = Utils.getRetrofitInstance(Utils.getPathString()).create(PontosService::class.java)

    fun carregarUsuariosDoPonto(pontoId: Int) {
        pontosService.getUsuariosDoPonto(pontoId).enqueue(object : Callback<List<usuario>> {
            override fun onResponse(call: Call<List<usuario>>, response: Response<List<usuario>>) {
                if (response.isSuccessful) {
                    _usuarios.value = response.body()
                }
            }

            override fun onFailure(call: Call<List<usuario>>, t: Throwable) {
                // Lidar com falha
            }
        })
    }

    fun verificarParticipacao(pontoId: Int, userEmail: String) {
        pontosService.getUsuariosDoPonto(pontoId).enqueue(object : Callback<List<usuario>> {
            override fun onResponse(call: Call<List<usuario>>, response: Response<List<usuario>>) {
                if (response.isSuccessful) {
                    val usuarios = response.body()
                    val usuarioParticipa = usuarios?.any { it.email == userEmail } ?: false
                    _isParticipando.value = usuarioParticipa
                }
            }

            override fun onFailure(call: Call<List<usuario>>, t: Throwable) {
                // Lidar com falha
            }
        })
    }

    fun entrarNoGrupo(pontoId: Int, userEmail: String) {
        pontosService.entrarNoPonto(pontoId, userEmail).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _isParticipando.value = true
                    carregarUsuariosDoPonto(pontoId)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Lidar com falha
            }
        })
    }

    fun sairDoGrupo(pontoId: Int, userEmail: String) {
        pontosService.sairDoPonto(pontoId, userEmail).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    _isParticipando.value = false
                    carregarUsuariosDoPonto(pontoId)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Lidar com falha
            }
        })
    }
}