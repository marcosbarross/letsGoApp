package com.example.letsGoApp.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.letsGoApp.interfaces.PontosService
import com.example.letsGoApp.controllers.apiUtils.Companion.getPathString
import com.example.letsGoApp.controllers.PontosAdapter
import com.example.letsGoApp.controllers.LocationController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.letsGoApp.models.PontoOrdenado
import com.example.letsGoApp.databinding.FragmentListaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListaFragment : Fragment() {

    private var _binding: FragmentListaBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationController: LocationController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val retrofit = Retrofit.Builder()
            .baseUrl(getPathString())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(PontosService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            locationController = LocationController(this@ListaFragment, object : LocationController.LocationCallback {
                override fun onLocationReceived(latitude: Double, longitude: Double) {
                }
                override fun onLocationFailed() {
                }
            })

            val latitude = withContext(Dispatchers.Main) {
                locationController.getLatitude()
            }
            val longitude = withContext(Dispatchers.Main) {
                locationController.getLongitude()
            }
            service.getPontosOrdenados(latitude, longitude).enqueue(object : Callback<List<PontoOrdenado>> {
                override fun onResponse(
                    call: Call<List<PontoOrdenado>>,
                    response: Response<List<PontoOrdenado>>
                ) {
                    if (response.isSuccessful) {
                        val pontos = response.body()
                        pontos?.let {
                            exibirPontos(pontos)
                        }
                    } else {
                        // TODO: Lidar com erro de resposta da API
                    }
                }
                override fun onFailure(call: Call<List<PontoOrdenado>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Falha na requisição: " + t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
        return root
    }

    private fun exibirPontos(pontos: List<PontoOrdenado>) {
        val adapter = PontosAdapter(pontos, locationController)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}