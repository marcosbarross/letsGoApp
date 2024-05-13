package com.example.letsGoApp.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.letsGoApp.controllers.apiUtils.Companion.getRetrofitInstance
import com.example.letsGoApp.models.pontos
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.location.LocationServices
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.letsGoApp.interfaces.PontosService
import com.example.letsGoApp.controllers.apiUtils.Companion.getPathString
import com.example.letsGoApp.controllers.PermissionController
import com.example.letsGoApp.views.usuario.SharedViewModel
import com.example.letsGoApp.R
import com.example.letsGoApp.databinding.FragmentMapaBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapaFragment : Fragment() {

    private var _binding: FragmentMapaBinding? = null
    private lateinit var mapView: MapView
    private lateinit var mMap: GoogleMap
    private lateinit var floatingActionButton : FloatingActionButton
    private lateinit var permissionController: PermissionController
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapaBinding.inflate(inflater, container, false)
        permissionController = PermissionController(requireActivity())
        val root: View = binding.root

        floatingActionButton = binding.floatingActionButton

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync { googleMap ->
            mMap = googleMap
            mMap.setPadding(0,0,0,200)
            
            permissionController.checkLocationPermission(mMap)

            mMap.uiSettings.apply {
                isZoomControlsEnabled = true
                isMyLocationButtonEnabled = true
            }

            // Move a câmera para a localização atual do dispositivo quando disponível
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    }
                }

            mMap.setOnMapLongClickListener{latLng ->
                latitude = latLng.latitude
                longitude = latLng.longitude

                sharedViewModel.isLogged.observe(viewLifecycleOwner) { isLogged ->
                    if (isLogged) {
                        findNavController().navigate(R.id.action_home_to_cadastro_estacionamentos)
                    } else {
                        findNavController().navigate(R.id.action_to_login)
                    }
                }
            }
        }
        floatingActionButton.setOnClickListener {
            val pontosService = getRetrofitInstance(getPathString()).create(
                PontosService::class.java)
            val call = pontosService.getPoints()

            call.enqueue(object : Callback<List<pontos>> {
                override fun onResponse(call: Call<List<pontos>>, response: Response<List<pontos>>) {
                    if (response.isSuccessful) {
                        val pontos = response.body()
                        pontos?.let {
                            if (it.isNotEmpty()) {
                                val builder = LatLngBounds.Builder()
                                it.forEach { ponto ->
                                    val posicao = LatLng(ponto.latitude, ponto.longitude)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(posicao)
                                            .title(ponto.nome)
                                            .snippet("Preço: R$" + ponto.preco.toString())
                                    )
                                    builder.include(posicao)
                                }
                                val bounds = builder.build()
                                val padding = 100
                                val cameraUpdate = if (it.size == 1) {
                                    CameraUpdateFactory.newLatLngZoom(bounds.center, 16f)
                                } else {
                                    CameraUpdateFactory.newLatLngBounds(bounds, padding)
                                }
                                mMap.animateCamera(cameraUpdate)
                            } else {
                                Toast.makeText(requireContext(), "Não há estacionamentos próximos", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Erro de API: $errorBody")
                        Toast.makeText(requireContext(), "Falha ao obter os pontos", Toast.LENGTH_SHORT).show()
                    }
                }
                
                override fun onFailure(call: Call<List<pontos>>, t: Throwable) {
                    Log.e("NETWORK_ERROR", "Erro de conexão: ${t.message}", t)
                    Toast.makeText(requireContext(), "Erro de conexão", Toast.LENGTH_SHORT).show()
                }
            })
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var longitude: Double = 0.00
        var latitude : Double = 0.00
    }
}
