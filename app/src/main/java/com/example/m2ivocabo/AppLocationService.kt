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
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

class AppLocationService : Service(), LocationListener {
    var TAG: String = AppLocationService::class.java.simpleName
    var locationManager: LocationManager? = null
    var latLng: LatLng? = null
    var gson:Gson=Gson()
    override fun onCreate() {
        locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(applicationContext, "GPS disabled!", Toast.LENGTH_SHORT).show()
        } else {
            if (application.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 240, 10f, this,
                     Looper.myLooper())

            } else {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    Activity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        }
    }

    override fun onDestroy() {
        locationManager!!.removeUpdates(this)
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

    override fun onLocationChanged(location: Location) {
        latLng = LatLng(location.latitude, location.longitude)
        var intsrv=Intent("location")
        intsrv.putExtra("loc",gson.toJson(latLng))
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intsrv)
        Log.v(TAG, "Enlem : " + location.latitude + " Boylam : " + location.longitude)
    }
}