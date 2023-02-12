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
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        var TAG: String = MainActivity::class.java.simpleName
        var locationIntent: Intent? = null

        private var scanOptions: ScanOptions? = null
        private var btnaddbeacon: Button? = null
        var addBeaconBuilder: MaterialAlertDialogBuilder? = null
        var addBeaconDialog: AlertDialog? = null

        var map: GoogleMap? = null
        var mapFragment: SupportMapFragment? = null

        var latlng: LatLng? = null

        private val LOCATION_PERMISSION_REQUEST_CODE = 100
        private val gson = Gson()
        var adapter: DeviceRecyclerViewAdapter? = null
        var rvdevice: RecyclerView? = null
        var mapmarker: Marker? = null
        var ZOOM_LEVEL: Float = 20f
        val googlemapCancelableCallback: CancelableCallback? = null
    }

    //@RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationPermissionRequest.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )



        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        MaterialAlertDialogBuilder_OnInit()
        GetDeviceList_OnInit()

    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            (permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false)
                    || permissions.getOrDefault(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                false
            ))
            -> {
                // Precise location access granted.
                // Only approximate location access granted.
                locationIntent = Intent(this@MainActivity, AppLocationService::class.java)
                startService(locationIntent);
                LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(
                    AppLocationServiceReceiver(),
                    IntentFilter("location")
                )
            }
            else -> {
                MaterialAlertDialogBuilder(this, R.style.appAlertDialogStyle)
                    .setTitle(R.string.dismissedlocationpermission_title)
                    .setMessage(R.string.dismissedlocationpermission_message)
                    .setPositiveButton(
                        R.string.btnok,
                        DialogInterface.OnClickListener { dialog, which ->
                            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        })
            }
        }
    }


    @RequiresPermission(
        allOf = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    override fun onDestroy() {
        super.onDestroy()
        stopService(locationIntent)
    }

    override fun onResume() {
        super.onResume()
        GetDeviceList_OnInit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


    }


    //start::Barcode Scanner event init
    @SuppressLint("MissingPermission")
    @RequiresPermission(
        anyOf = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            if (latlng != null) {
                var intent = Intent(this, AddDeviceAfterScanResultForm::class.java)
                intent.putExtra("macaddress", result.contents)

                var _latlng = gson.toJson(latlng)
                intent.putExtra("latlng", _latlng)
                startActivity(intent)
            }
        }
    }

    private fun SetBarcodeScanning() {
        scanOptions = ScanOptions()
        scanOptions!!.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        scanOptions!!.setPrompt("Scanning Barcode")
        scanOptions!!.setCameraId(0)
        scanOptions!!.setBarcodeImageEnabled(true)
        scanOptions!!.setBeepEnabled(true)

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

    //end::Barcode Scanner event init
//start::MaterialAlertDialog events
    fun MaterialAlertDialogBuilder_OnInit() {
        var addDeviceChooseType =
            layoutInflater.inflate(R.layout.fragment_add_device_choose_type, null)

        var btnscancode: Button =
            addDeviceChooseType.findViewById<Button>(R.id.btnchoosescan)
        btnscancode.setOnClickListener {
            SetBarcodeScanning()
            addBeaconDialog?.dismiss()
        }

        btnaddbeacon = findViewById(R.id.btnaddbeacon) as Button
        btnaddbeacon!!.setOnClickListener {
            addBeaconBuilder =
                MaterialAlertDialogBuilder(this, R.style.appAlertDialogStyle)
                    .setView(addDeviceChooseType)
                    //.setNegativeButton("Cancel", null)
                    .setOnDismissListener(DialogInterface.OnDismissListener {
                        (addDeviceChooseType.parent as ViewGroup).removeView(addDeviceChooseType)
                    })
            addBeaconDialog = addBeaconBuilder!!.create()
            addBeaconDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            addBeaconDialog!!.show()
        };
    }
//end::MaterialAlertDialog events

    fun GetDeviceList_OnInit() {
        var dbDeviceHelper = DBDeviceHelper(this)
        val devicelist = dbDeviceHelper.deviceList()
        if (devicelist != null && devicelist.size > 0) {
            adapter = DeviceRecyclerViewAdapter(this, devicelist)
            rvdevice = this.findViewById<RecyclerView>(R.id.rcvdevicelist)
            rvdevice?.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            rvdevice?.adapter = adapter
            adapter?.notifyDataSetChanged()
            enableSwipeToDeleteAndUndo()
        }
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(this) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition
                    val item: DeviceItem = adapter!!.getData()!!.get(position)
                    item.id?.let { adapter!!.removeItem(position, it) }
                }
            }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(rvdevice)
    }


    class AppLocationServiceReceiver : BroadcastReceiver() {
        var gson = Gson()
        override fun onReceive(context: Context?, intent: Intent?) {
            var latLng = gson.fromJson(intent?.getStringExtra("loc"), LatLng::class.java)
            if (map?.cameraPosition!!.zoom >= ZOOM_LEVEL)
                ZOOM_LEVEL = map?.cameraPosition!!.zoom
            Log.v(TAG, "Zoom Level : " + ZOOM_LEVEL)
            latlng = latLng
            latlng = latLng
            mapmarker?.remove()
            mapmarker = map?.addMarker(MarkerOptions().position(latLng))

            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL))


        }
    }
}