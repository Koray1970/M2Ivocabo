package com.example.m2ivocabo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.work.*
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.example.m2ivocabo.BLEServices.Companion.ARG_PROGRESS
import java.util.UUID
import java.util.concurrent.TimeUnit


class BLEDeviceActionForm : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        var TAG: String = BLEDeviceActionForm::class.java.simpleName
        var map: GoogleMap? = null
        var mapFragment: SupportMapFragment? = null
        var ZOOM_LEVEL: Float = 20f
        var mapmarker: Marker? = null
        var latlng: LatLng? = null
        var btnTracking: Button? = null
        private val gson = Gson()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bledevice_action_form)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
        btnTracking = findViewById(R.id.btntrack)
        btnTracking!!.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                runNotification()
            } else {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }


    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        runNotification()
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
                    || permissions.getOrDefault(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                false
            ))
            -> {
                getCurrentLocation()
            }
            else -> {
                MaterialAlertDialogBuilder(this, R.style.appAlertDialogStyle)
                    .setTitle(R.string.dismissedlocationpermission_title)
                    .setMessage(R.string.dismissedlocationpermission_message)
                    .setPositiveButton(
                        R.string.btnok
                    ) { _, _ ->
                        shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
            }
        }
    }

    private fun runNotification() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()

        val request =  OneTimeWorkRequestBuilder<BLEServices>().build()
        /*val prequest =PeriodicWorkRequestBuilder<BLEServices>(
           PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,TimeUnit.MILLISECONDS
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR,PeriodicWorkRequest.MAX_BACKOFF_MILLIS,TimeUnit.MILLISECONDS)
            .build()*/

        val workManager = WorkManager.getInstance(applicationContext)


        /*workManager.getWorkInfoByIdLiveData(prequest.id)
             .observe(this, Observer { workInfo: WorkInfo? ->
                 if (workInfo != null) {
                     val progress = workInfo.progress
                     val value = progress.getInt(ARG_PROGRESS, 0)

                 }

             })*/
        //Log.v(TAG, "Nofification Request id : " + prequest.id)
        workManager.enqueue(request)
        //var nname=UUID.randomUUID().toString()
        //workManager.enqueueUniquePeriodicWork("nname", ExistingPeriodicWorkPolicy.KEEP, prequest)
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
                val i = Intent(this@BLEDeviceActionForm, MainActivity::class.java)
                startActivity(i)
            }
        }
}