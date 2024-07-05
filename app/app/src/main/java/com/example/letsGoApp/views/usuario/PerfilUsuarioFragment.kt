package com.example.letsGoApp.views.usuario

import com.example.letsGoApp.interfaces.UsuariosService
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.letsGoApp.controllers.apiUtils
import com.example.letsGoApp.models.usuario
import com.example.letsGoApp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilUsuarioFragment : Fragment() {
    private lateinit var sairButton: Button
    private lateinit var nomeUsuarioLabel: TextView
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var usuariosService: UsuariosService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)
        sairButton = rootView.findViewById(R.id.logoutUserButton)
        nomeUsuarioLabel = rootView.findViewById(R.id.nomeUsuarioLabel)

        sairButton.setOnClickListener {
            sharedViewModel.setUserId(Int.MIN_VALUE)
            sharedViewModel.setLogged(false)
            findNavController().navigate(R.id.navigation_mapa)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usuariosService = apiUtils.getRetrofitInstance(apiUtils.getPathString()).create(
            UsuariosService::class.java)

        val idUsuario = sharedViewModel.userId.value
        idUsuario?.let { userId ->
            usuariosService.getUsuario(userId).enqueue(object : Callback<usuario> {
                override fun onResponse(call: Call<usuario>, response: Response<usuario>) {
                    if (response.isSuccessful) {
                        val usuario = response.body()
                        if (usuario != null) {
                            nomeUsuarioLabel.text = usuario.nome
                        }
                    }
                }

                override fun onFailure(call: Call<usuario>, t: Throwable) {
                    // TODO: Tratar o caso de falha na comunicação com a API
                }
            })
        } ?: run {
            // TODO: Tratar o caso em que idUsuario é nulo
        }
    }
}
