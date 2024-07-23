package com.example.letsGoApp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsGoApp.R
import com.example.letsGoApp.databinding.FragmentListaBinding
import com.example.letsGoApp.interfaces.PontosService
import com.example.letsGoApp.controllers.Utils
import com.example.letsGoApp.views.pontos.PontosAdapter
import com.example.letsGoApp.controllers.LocationController
import com.example.letsGoApp.models.PontoOrdenado
import com.example.letsGoApp.views.usuario.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaFragment : Fragment(), PontosAdapter.OnItemClickListener {

    private var _binding: FragmentListaBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationController: LocationController
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val retrofit = Utils.getRetrofitInstance(Utils.getPathString())
        val service = retrofit.create(PontosService::class.java)

        locationController = LocationController(this@ListaFragment, object : LocationController.LocationCallback {
            override fun onLocationReceived(latitude: Double, longitude: Double) {
                service.getPontosOrdenados(latitude, longitude).enqueue(object : Callback<List<PontoOrdenado>> {
                    override fun onResponse(
                        call: Call<List<PontoOrdenado>>,
                        response: Response<List<PontoOrdenado>>
                    ) {
                        if (response.isSuccessful) {
                            val pontos = response.body()
                            pontos?.let {
                                CoroutineScope(Dispatchers.Main).launch {
                                    exibirPontos(pontos)
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Erro na resposta da API", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<List<PontoOrdenado>>, t: Throwable) {
                        Toast.makeText(requireContext(), "Falha na requisição: " + t.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onLocationFailed() {
                Toast.makeText(requireContext(), "Falha ao obter localização", Toast.LENGTH_SHORT).show()
            }
        })

        CoroutineScope(Dispatchers.Main).launch {
            locationController.start()
        }

        return root
    }

    private fun exibirPontos(pontos: List<PontoOrdenado>) {
        val adapter = PontosAdapter(pontos, locationController, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onItemClick(ponto: PontoOrdenado) {
        val bundle = Bundle().apply {
            putParcelable("ponto", ponto)
        }
        sharedViewModel.isLogged.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged) {
                findNavController().navigate(R.id.action_navigation_lista_to_detalhesFragment, bundle)
            } else {
                Toast.makeText(context, "É preciso fazer login", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_to_login)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
