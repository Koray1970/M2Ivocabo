package com.example.m2ivocabo

import android.content.Context
import android.content.DialogInterface
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
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        var latlng: LatLng? = null
        var mapFragment: SupportMapFragment? = null
        private var fusedLocationProviderClient: FusedLocationProviderClient? = null
        private var locationManager: LocationManager? = null
        val minTimeMs: Long = 0
        val minDistanceM: Float = 0f
        var currentRSSI: Int? = null
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        //locationManager =getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        val locationRequiresPermissionBuilder =
            com.google.android.gms.location.LocationRequest.Builder(minTimeMs)
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequiresPermissionBuilder.build(),
                locationCallback,
                Looper.myLooper()
            )
        } else {
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }


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

        //googleMap.isMyLocationEnabled = true
        //Dashboard.latlng = LatLng(-33.852, 151.211)
        if (latlng != null) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(latlng!!)
                    .title("Marker in Sydney")
            )

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng!!, 15f))
        }
    }

    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            latlng = LatLng(location?.latitude!!, location?.longitude!!)
            mapFragment?.getMapAsync(this@MainActivity)
        }
    }
/*override fun onLocationChanged(p0: Location) {
    Log.v(Dashboard.TAG,"Enlem : "+p0.latitude+" Boylam : "+p0.longitude)
    Dashboard.latlng = LatLng(p0.latitude, p0.longitude)

}
override fun oocationChanged(locations: MutableList<Location>) {
    for (r: Location in locations) {
        Dashboard.latlng = LatLng(r.latitude, r.longitude)
        Log.v(Dashboard.TAG,"Enlem : "+r.latitude+" Boylam : "+r.longitude)
    }
}
override fun onProviderDisabled(provider: String) {
    Toast.makeText(this, "$provider is disabled", Toast.LENGTH_SHORT).show()
}

override fun onProviderEnabled(provider: String) {
    Toast.makeText(this, "$provider is enabled", Toast.LENGTH_SHORT).show()
}*/
}