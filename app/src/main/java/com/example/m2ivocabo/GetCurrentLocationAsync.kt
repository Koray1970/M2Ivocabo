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
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class GetCurrentLocationAsync {
    companion object {
        var fusedLocationProviderClient: FusedLocationProviderClient? = null
    }

    var loc: Location? = null
    suspend fun CurrentLocationAsync(activity: Activity): Location? {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        if (activity.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (activity.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                fusedLocationProviderClient!!.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener {
                    loc = it
                    return@addOnSuccessListener
                }
        }
        return null
    }

}