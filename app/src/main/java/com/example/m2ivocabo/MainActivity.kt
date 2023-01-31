package com.example.m2ivocabo

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks

abstract class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var TAG = MainActivity::class.java.simpleName
    private var currentLocation:Location?=null
    lateinit var locationManager: LocationManager

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var latlang: LatLng?=null
    private var hasGPS=false
    private var hasNetwork=false
    private var mapFragment:SupportMapFragment?=null
    private var locationByGps:Location?=null
    private var locationByNetwork:Location?=null
    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment



        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mapFragment?.getMapAsync(this)
            locationManager=getSystemService(Context.LOCALE_SERVICE) as LocationManager
            hasGPS=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            hasNetwork=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (hasGPS) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }
            else if(hasNetwork){
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
                )
            }

            val lastKnownLocationByGps =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }

            val lastKnownLocationByNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }

            if (locationByGps != null && locationByNetwork != null) {
                if (locationByGps!!.accuracy > locationByNetwork!!.accuracy) {
                    currentLocation = locationByGps
                } else {
                    currentLocation = locationByNetwork
                }
                latlang=LatLng(currentLocation!!.latitude,currentLocation!!.longitude)
            }





        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        if(latlang!=null) {
            p0.addMarker(
                MarkerOptions()
                    .position(latlang!!)
                    .title("Marker in Sydney")

            )

            p0.moveCamera(CameraUpdateFactory.newLatLngZoom(latlang!!, 15f))
        }
    }

    val gpsLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByGps= location


        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    //------------------------------------------------------//
    val networkLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            locationByNetwork= location

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

}