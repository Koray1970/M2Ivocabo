package com.example.m2ivocabo

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class GetCurrentLocationAsync {
    companion object {
        var fusedLocationProviderClient: FusedLocationProviderClient? = null
    }
    var latLng: LatLng? = null
    suspend fun CurrentLocation_Result(activity: Activity) {
        getCurrentLocation(activity)
        Thread.sleep(1000)
        if (latLng == null)
            CurrentLocation_Result(activity)
    }

    private fun getCurrentLocation(activity: Activity) {
        if (fusedLocationProviderClient == null)
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        if (activity.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (activity.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                fusedLocationProviderClient!!.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener {
                    latLng = LatLng(it.latitude, it.longitude)
                }
        }
    }
}