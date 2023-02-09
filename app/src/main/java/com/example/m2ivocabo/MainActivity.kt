package com.example.m2ivocabo

import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        var latlng: LatLng? = null
        var mapFragment: SupportMapFragment? = null
        private var fusedLocationProviderClient: FusedLocationProviderClient? = null
        private var locationManager: LocationManager? = null
        val minTimeMs: Long = 0
        val minDistanceM: Float = 0f
        var currentRSSI: Int? = null
        var locationIntent: Intent? = null
        var map:GoogleMap?=null
    }

    //@RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationIntent = Intent(this@MainActivity, AppLocationService::class.java)
        startService(locationIntent);

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(AppLocationServiceReceiver(),
            IntentFilter("location")
        )

        //device list fragments
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.flmainframe, Dashboard())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @RequiresPermission(
        allOf = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.clear()
        map=googleMap
        googleMap.clear()
        if (latlng != null) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(latlng!!)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng!!, 15f))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(locationIntent)
    }
    class AppLocationServiceReceiver : BroadcastReceiver() {
        var gson= Gson()
        override fun onReceive(context: Context?, intent: Intent?) {
            var latLng=gson.fromJson( intent?.getStringExtra("loc"),LatLng::class.java)
            MainActivity.latlng =latLng
            MainActivity.map?.clear()
            MainActivity.map?.addMarker(MarkerOptions().position(latLng))
            MainActivity.map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,20f))
        }
    }
}