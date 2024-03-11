package com.example.letsgoapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.letsgoapp.R
import com.example.letsgoapp.databinding.FragmentHomeBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return root
    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        val ifpe = LatLng(-7.945590983202563, -34.85879696004683)
        map.addMarker(MarkerOptions().position(ifpe).title("IFPE").draggable(true))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ifpe, 13.5f))

        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isMyLocationButtonEnabled = true
        }

        map.setPadding(0,0,0,150)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

}
