package com.example.m2ivocabo

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

class AppLocationService : Service(){
    var TAG: String = AppLocationService::class.java.simpleName
    var fusedLocationProviderClient:FusedLocationProviderClient?=null
    var locationRequest:LocationRequest?=null
    var latLng: LatLng? = null
    var gson:Gson=Gson()
    override fun onCreate() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(application)
        LocationManager_OnInit()
    }
    var locationCallback=object:LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            latLng = LatLng(result.locations.last().latitude, result.locations.last().longitude)
            var intsrv=Intent("location")
            intsrv.putExtra("loc",gson.toJson(latLng))

            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intsrv)
            Toast.makeText(applicationContext,"Enlem : " + latLng!!.latitude + " Boylam : " + latLng!!.longitude, Toast.LENGTH_SHORT).show()
            Log.v(TAG, "Enlem : " + latLng!!.latitude + " Boylam : " + latLng!!.longitude)
        }

        override fun onLocationAvailability(result: LocationAvailability) {
            if(!result.isLocationAvailable)
                Toast.makeText(applicationContext,R.string.locationnotavailable,Toast.LENGTH_SHORT).show()
        }
    }
    private fun LocationManager_OnInit(){
        if (application.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (application.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                fusedLocationProviderClient!!.requestLocationUpdates(locationRequest!!,locationCallback, Looper.getMainLooper())

        }
    }
    override fun onDestroy() {
        fusedLocationProviderClient!!.removeLocationUpdates(locationCallback)
    }

    override fun onRebind(intent: Intent) {

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

        return START_STICKY_COMPATIBILITY
    }

    override fun onUnbind(intent: Intent): Boolean {
        return false
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    /*override fun onLocationChanged(location: Location) {
        latLng = LatLng(location.latitude, location.longitude)
        var intsrv=Intent("location")
        intsrv.putExtra("loc",gson.toJson(latLng))

        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intsrv)
        Log.v(TAG, "Enlem : " + location.latitude + " Boylam : " + location.longitude)
    }

    override fun onProviderDisabled(provider: String) {
        Toast.makeText(applicationContext,R.string.gpsprovideisdisabled,Toast.LENGTH_SHORT).show()
        //stopSelf()
    }

    override fun onProviderEnabled(provider: String) {
        LocationManager_OnInit()
        Toast.makeText(applicationContext,R.string.gpsprovideisenabled,Toast.LENGTH_SHORT).show()
    }*/
}