package com.example.letsGoApp.views.usuario

import com.example.letsGoApp.interfaces.UsuariosService
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.letsGoApp.controllers.Utils
import com.example.letsGoApp.models.usuario
import com.example.letsGoApp.models.pontos
import com.example.letsGoApp.R
import com.example.letsGoApp.controllers.LocationController
import com.example.letsGoApp.models.PontoOrdenado
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.*

class PerfilUsuarioFragment : Fragment(), LocationController.LocationCallback {

    private lateinit var sairButton: Button
    private lateinit var nomeUsuarioLabel: TextView
    private lateinit var pontosListView: ListView
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var usuariosService: UsuariosService
    private lateinit var locationController: LocationController
    private var userLocationLat: Double? = null
    private var userLocationLon: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)

        sairButton = rootView.findViewById(R.id.logoutUserButton)
        nomeUsuarioLabel = rootView.findViewById(R.id.nomeUsuarioLabel)
        pontosListView = rootView.findViewById(R.id.pontosListView)

        usuariosService = Retrofit.Builder().baseUrl(Utils.getPathString()).addConverterFactory(GsonConverterFactory.create()).build().create(UsuariosService::class.java)

        sairButton.setOnClickListener {
            sharedViewModel.setUserId(Int.MIN_VALUE)
            sharedViewModel.setLogged(false)
            findNavController().navigate(R.id.navigation_mapa)
        }

        locationController = LocationController(this, this)
        lifecycleScope.launch {
            locationController.start()
        }

        val userId = sharedViewModel.getUserId()
        if (userId != null) {
            usuariosService.getUsuario(userId).enqueue(object : Callback<usuario> {
                override fun onResponse(call: Call<usuario>, response: Response<usuario>) {
                    if (response.isSuccessful) {
                        val usuario = response.body()
                        nomeUsuarioLabel.text = Utils.capitalize(usuario?.nome?: "Nome não disponível) ")
                    } else {
                        nomeUsuarioLabel.text = "Erro ao carregar nome"
                    }
                }

                override fun onFailure(call: Call<usuario>, t: Throwable) {
                    nomeUsuarioLabel.text = "Erro ao carregar nome"
                }
            })
        }

        return rootView
    }




    override fun onLocationReceived(latitude: Double, longitude: Double) {
        userLocationLat = latitude
        userLocationLon = longitude
        val idUsuario = sharedViewModel.userId.value
        idUsuario?.let { userId ->
            carregarPontos(userId)
        }
    }

    override fun onLocationFailed() {
        // Trate o caso de falha ao obter a localização
    }

    private fun carregarPontos(userId: Int) {
        usuariosService.getPontosDoUsuario(userId).enqueue(object : Callback<List<pontos>> {
            override fun onResponse(call: Call<List<pontos>>, response: Response<List<pontos>>) {
                if (response.isSuccessful) {
                    val pontos = response.body() ?: emptyList()
                    val pontosComDistancia = pontos.map { ponto ->
                        PontoOrdenado(
                            id = ponto.id_ponto,
                            nome = ponto.atividade,
                            latitude = ponto.latitude,
                            longitude = ponto.longitude,
                            distancia = userLocationLat?.let { lat ->
                                userLocationLon?.let { lon ->
                                    calcularDistancia(lat, lon, ponto.latitude, ponto.longitude)
                                }
                            } ?: 0.0,
                            atividade = ponto.atividade,
                            horario = ponto.horario,
                            diasSemana = ponto.diasSemana
                        )
                    }
                    val pontosAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, pontosComDistancia.map { it.nome })
                    pontosListView.adapter = pontosAdapter

                    pontosListView.setOnItemClickListener { _, _, position, _ ->
                        val ponto = pontosComDistancia[position]
                        val bundle = Bundle().apply {
                            putParcelable("ponto", ponto)
                        }
                        findNavController().navigate(R.id.action_navigation_lista_to_detalhesFragment, bundle)
                    }
                }
            }

            override fun onFailure(call: Call<List<pontos>>, t: Throwable) {
                // TODO: Tratar o caso de falha na comunicação com a API
            }
        })
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Raio da Terra em quilômetros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
