package com.example.m2ivocabo

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.journeyapps.barcodescanner.*


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private var permissionDenied = false
    private lateinit var map: GoogleMap
    private var scanOptions: ScanOptions? = null
    private var btnaddbeacon: Button? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scanOptions = ScanOptions()
        scanOptions!!.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        scanOptions!!.setPrompt("Scanning Barcode")
        scanOptions!!.setCameraId(0)
        scanOptions!!.setBarcodeImageEnabled(true)
        scanOptions!!.setBeepEnabled(true)


        btnaddbeacon = findViewById(R.id.btnaddbeacon) as Button
        btnaddbeacon!!.setOnClickListener {


            barcodeLauncher.launch(scanOptions)

        }


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG)
                .show()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val sydney = LatLng(-33.852, 151.211)
        googleMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        enableMyLocation()
    }

    private fun enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            return
        } // 2. If if a permission rationale dialog should be shown
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            PermissionUtils.RationaleDialog.newInstance(
                LOCATION_PERMISSION_REQUEST_CODE, true
            ).show(supportFragmentManager, "dialog")
            return
        }

        // 3. Otherwise, request permission
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    //start::Scanner events-->
    private fun ScannerOnClick() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            barcodeLauncher.launch(scanOptions)
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                100
            )
        }
    }


    //end::Scanner events-->
}