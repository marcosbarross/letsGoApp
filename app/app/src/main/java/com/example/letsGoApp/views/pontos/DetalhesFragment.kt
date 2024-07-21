package com.example.letsGoApp.views.pontos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.letsGoApp.R
import com.example.letsGoApp.controllers.DetalhesController
import com.example.letsGoApp.views.usuario.UsuarioAdapter
import com.example.letsGoApp.models.PontoOrdenado
import com.example.letsGoApp.views.usuario.SharedViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetalhesFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val detalhesViewModel: DetalhesController by viewModels()

    private lateinit var nomeAtividadeDetalhes: TextView
    private lateinit var infoTextView: TextView
    private lateinit var buttonParticipar: Button
    private lateinit var buttonSair: Button
    private lateinit var listViewUsuarios: ListView
    private lateinit var mapViewDetalhes: MapView
    private lateinit var mMap: GoogleMap

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

        ponto = arguments?.getParcelable("ponto") ?: return view

        nomeAtividadeDetalhes.text = ponto.nome
        infoTextView.text = "${ponto.atividade} a ${Math.round(ponto.distancia)}km \n ${ponto.diasSemana} às ${ponto.horario}"

        if (sharedViewModel.isLogged.value == true) {
            val userEmail = sharedViewModel.getUserEmail()
            if (userEmail != null) {
                detalhesViewModel.verificarParticipacao(ponto.id, userEmail)
                detalhesViewModel.carregarUsuariosDoPonto(ponto.id)
            }
        }

        buttonParticipar.setOnClickListener {
            if (sharedViewModel.isLogged.value == true) {
                val userEmail = sharedViewModel.getUserEmail() ?: return@setOnClickListener
                detalhesViewModel.entrarNoGrupo(ponto.id, userEmail)
            } else {
                Toast.makeText(context, "Você precisa estar logado para participar do grupo", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSair.setOnClickListener {
            if (sharedViewModel.isLogged.value == true) {
                val userEmail = sharedViewModel.getUserEmail() ?: return@setOnClickListener
                detalhesViewModel.sairDoGrupo(ponto.id, userEmail)
            } else {
                Toast.makeText(context, "Você precisa estar logado para sair do grupo", Toast.LENGTH_SHORT).show()
            }
        }

        detalhesViewModel.usuarios.observe(viewLifecycleOwner, Observer { usuarios ->
            val adapter = UsuarioAdapter(requireContext(), usuarios)
            listViewUsuarios.adapter = adapter
        })

        detalhesViewModel.isParticipando.observe(viewLifecycleOwner, Observer { isParticipando ->
            if (isParticipando) {
                buttonParticipar.visibility = View.GONE
                buttonSair.visibility = View.VISIBLE
            } else {
                buttonParticipar.visibility = View.VISIBLE
                buttonSair.visibility = View.GONE
            }
        })

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
}