package com.example.letsGoApp.controllers

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class LocationController(private val fragment: Fragment, private val callback: LocationCallback) {

    private val requestPermissionCode = 1
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var mLocation: Location? = null

    init {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(fragment.requireContext())
    }

    suspend fun getLatitude(): Double {
        return if (ActivityCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            0.0
        } else {
            val location = fusedLocationProviderClient.lastLocation.await()
            mLocation = location
            location?.latitude ?: run {
                callback.onLocationFailed()
                0.0
            }
        }
    }

    suspend fun getLongitude(): Double {
        return if (ActivityCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            0.0
        } else {
            val location = fusedLocationProviderClient.lastLocation.await()
            mLocation = location
            location?.longitude ?: run {
                callback.onLocationFailed()
                0.0
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            fragment.requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            requestPermissionCode
        )
    }

    interface LocationCallback {
        fun onLocationReceived(latitude: Double, longitude: Double)
        fun onLocationFailed()
    }
}