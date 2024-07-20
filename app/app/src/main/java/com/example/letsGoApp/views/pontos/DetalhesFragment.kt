package com.example.letsGoApp.views.pontos

import com.example.letsGoApp.controllers.UsuarioAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.letsGoApp.R
import com.example.letsGoApp.interfaces.PontosService
import com.example.letsGoApp.models.PontoOrdenado
import com.example.letsGoApp.views.usuario.SharedViewModel
import com.example.letsGoApp.controllers.apiUtils
import com.example.letsGoApp.models.usuario
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalhesFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var nomeAtividadeDetalhes: TextView
    private lateinit var infoTextView: TextView
    private lateinit var buttonParticipar: Button
    private lateinit var buttonSair: Button
    private lateinit var listViewUsuarios: ListView
    private lateinit var mapViewDetalhes: MapView
    private lateinit var mMap: GoogleMap

    private lateinit var pontosService: PontosService

    private lateinit var ponto: PontoOrdenado

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalhes, container, false)

        nomeAtividadeDetalhes = view.findViewById(R.id.nomeAtividadeDetalhes)
        infoTextView = view.findViewById(R.id.infoTextView)
        buttonParticipar = view.findViewById(R.id.buttonParticipar)
        buttonSair = view.findViewById(R.id.buttonSair)
        listViewUsuarios = view.findViewById(R.id.listViewUsuarios)
        mapViewDetalhes = view.findViewById(R.id.mapViewDetalhes)

        mapViewDetalhes.onCreate(savedInstanceState)
        mapViewDetalhes.getMapAsync { googleMap ->
            mMap = googleMap
            mMap.uiSettings.isZoomControlsEnabled = true

            val pontoLatLng = LatLng(ponto.latitude, ponto.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pontoLatLng, 15f))
            mMap.addMarker(MarkerOptions().position(pontoLatLng).title(ponto.nome))
        }

        pontosService = apiUtils.getRetrofitInstance(apiUtils.getPathString()).create(PontosService::class.java)

        ponto = arguments?.getParcelable("ponto") ?: return view

        nomeAtividadeDetalhes.text = ponto.nome
        infoTextView.text = "${ponto.atividade} a ${Math.round(ponto.distancia)}km \n ${ponto.diasSemana} às ${ponto.horario}"

        if (sharedViewModel.isLogged.value == true) {
            val userEmail = sharedViewModel.getUserEmail()
            if (userEmail != null) {
                verificarParticipacao(userEmail)
                carregarUsuariosDoPonto()
            }
        }

        buttonParticipar.setOnClickListener {
            if (sharedViewModel.isLogged.value == true) {
                val userEmail = sharedViewModel.getUserEmail() ?: return@setOnClickListener
                entrarNoGrupo(userEmail)
            } else {
                Toast.makeText(context, "Você precisa estar logado para participar do grupo", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSair.setOnClickListener {
            if (sharedViewModel.isLogged.value == true) {
                val userEmail = sharedViewModel.getUserEmail() ?: return@setOnClickListener
                sairDoGrupo(userEmail)
            } else {
                Toast.makeText(context, "Você precisa estar logado para sair do grupo", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        mapViewDetalhes.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapViewDetalhes.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapViewDetalhes.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewDetalhes.onLowMemory()
    }

    private fun verificarParticipacao(userEmail: String) {
        pontosService.getUsuariosDoPonto(ponto.id).enqueue(object : Callback<List<usuario>> {
            override fun onResponse(call: Call<List<usuario>>, response: Response<List<usuario>>) {
                if (response.isSuccessful) {
                    val usuarios = response.body()
                    val usuarioParticipa = usuarios?.any { it.email == userEmail } ?: false
                    if (usuarioParticipa) {
                        buttonParticipar.visibility = View.GONE
                        buttonSair.visibility = View.VISIBLE
                    } else {
                        buttonParticipar.visibility = View.VISIBLE
                        buttonSair.visibility = View.GONE
                    }
                } else {
                    Log.e("DetalhesFragment", "Falha na resposta: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<usuario>>, t: Throwable) {
                Log.e("DetalhesFragment", "Erro ao verificar participação", t)
                Toast.makeText(context, "Erro ao verificar participação", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun entrarNoGrupo(userEmail: String) {
        pontosService.entrarNoPonto(ponto.id, userEmail).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    buttonParticipar.visibility = View.GONE
                    buttonSair.visibility = View.VISIBLE
                    Toast.makeText(context, "Você entrou no grupo", Toast.LENGTH_SHORT).show()
                    carregarUsuariosDoPonto()
                } else {
                    Log.e("DetalhesFragment", "Falha na resposta: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("DetalhesFragment", "Erro ao entrar no grupo", t)
                Toast.makeText(context, "Erro ao entrar no grupo", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sairDoGrupo(userEmail: String) {
        pontosService.sairDoPonto(ponto.id, userEmail).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    buttonParticipar.visibility = View.VISIBLE
                    buttonSair.visibility = View.GONE
                    Toast.makeText(context, "Você saiu do grupo", Toast.LENGTH_SHORT).show()
                    carregarUsuariosDoPonto()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Erro ao sair do grupo", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun carregarUsuariosDoPonto() {
        pontosService.getUsuariosDoPonto(ponto.id).enqueue(object : Callback<List<usuario>> {
            override fun onResponse(call: Call<List<usuario>>, response: Response<List<usuario>>) {
                if (response.isSuccessful) {
                    val usuarios = response.body()
                    if (usuarios != null) {
                        val adapter = UsuarioAdapter(requireContext(), usuarios)
                        listViewUsuarios.adapter = adapter
                    }
                } else {
                    Log.e("DetalhesFragment", "Falha na resposta: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<usuario>>, t: Throwable) {
                Log.e("DetalhesFragment", "Erro ao carregar usuários do ponto", t)
                Toast.makeText(context, "Erro ao carregar usuários do ponto", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
