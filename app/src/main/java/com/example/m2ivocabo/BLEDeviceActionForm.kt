package com.example.m2ivocabo

import android.Manifest
import android.Manifest.permission.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.m2ivocabo.MainActivity.Companion.fusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.gson.Gson


class BLEDeviceActionForm : AppCompatActivity(), OnMapReadyCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bledevice_action_form)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)

        var bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        var bluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            ActivityCompat.startActivityForResult(
                this@BLEDeviceActionForm,
                enableBtIntent,
                BluetoothTrackService.REQUEST_ENABLE_BT,
                null
            )
        }



        btnTracking = findViewById<Button>(R.id.btntrack)
        btnTracking!!.isClickable = false

        btnTracking!!.setOnClickListener {
            /*if (checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN))
                    requestPermissions(arrayOf(android.Manifest.permission.BLUETOOTH_SCAN), 300)
            }*/
            runNotification()
        }
        getCurrentLocation()
    }

    private fun runNotification() {
        val intBleTrack = Intent(this@BLEDeviceActionForm, BluetoothTrackService::class.java)
        startService(intBleTrack)
        /*startForegroundService(intBleTrack)*/
    }

    private fun mapOnPrepare() {
        if (map?.cameraPosition!!.zoom >= ZOOM_LEVEL)
            ZOOM_LEVEL = map?.cameraPosition!!.zoom
        Log.v(TAG, "Zoom Level : ${ZOOM_LEVEL}")

        mapmarker?.remove()
        mapmarker = map?.addMarker(MarkerOptions().position(latlng!!))

        map?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latlng!!,
                ZOOM_LEVEL
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    //start::Current Location Init
    private fun getCurrentLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient!!.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                CancellationTokenSource().token
            )
                .addOnSuccessListener {
                    latlng = LatLng(it.latitude, it.longitude)
                    mapOnPrepare()
                }
        }
    }
//end::Current Location Init

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val i =
                    Intent(this@BLEDeviceActionForm, MainActivity::class.java)
                startActivity(i)
            }
        }

    companion object {
        var TAG: String = BLEDeviceActionForm::class.java.simpleName
        var map: GoogleMap? = null
        var mapFragment: SupportMapFragment? = null
        var ZOOM_LEVEL: Float = 20f
        var mapmarker: Marker? = null
        var latlng: LatLng? = null
        var btnTracking: Button? = null
        private val BLE_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        private var ANDROID_12_BLE_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        private const val ANDROID_12_BLE_PERMISSIONSCODE = 200
        private const val BLE_PERMISSIONSCODE = 205
        private const val POST_NOTIFICATIONS_PERMISSIONCODE = 90
        private const val BLUETOOTH_PERMISSIONCODE = 100
        private const val BLUETOOTH_CONNECTION_PERMISSIONCODE = 110
        private const val BLUETOOTH_SCAN_PERMISSIONCODE = 120

        private val gson = Gson()
    }
}