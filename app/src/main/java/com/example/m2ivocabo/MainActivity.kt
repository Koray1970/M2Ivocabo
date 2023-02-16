package com.example.m2ivocabo

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
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
        @SuppressLint("StaticFieldLeak")
        var btnaddbeacon: Button? = null
        var addBeaconDialog: AlertDialog? = null
        var map: GoogleMap? = null
        var mapFragment: SupportMapFragment? = null
        var latlng: LatLng? = null
        var fusedLocationProviderClient: FusedLocationProviderClient? = null
        private val gson = Gson()

        @SuppressLint("StaticFieldLeak")
        var adapter: DeviceRecyclerViewAdapter? = null
        var rvdevice: RecyclerView? = null
        var mapmarker: Marker? = null
        var ZOOM_LEVEL: Float = 20f
    }

    //@RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        addDeviceBottomSheetOnInit()
        getDeviceListOnInit()
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

    private fun mapOnPrepare() {
        if (map?.cameraPosition!!.zoom >= ZOOM_LEVEL)
            ZOOM_LEVEL = map?.cameraPosition!!.zoom
        Log.v(TAG, "Zoom Level : $ZOOM_LEVEL")

        mapmarker?.remove()
        mapmarker = map?.addMarker(MarkerOptions().position(latlng!!))

        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng!!, ZOOM_LEVEL))
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        getDeviceListOnInit()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }


    //start::Barcode Scanner event init
    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            if (latlng != null) {
                val intent = Intent(this, AddDeviceAfterScanResultForm::class.java)
                intent.putExtra("macaddress", result.contents)

                val llatlng = gson.toJson(latlng)
                intent.putExtra("latlng", llatlng)
                startActivity(intent)
            }
        }
    }

    public final fun setBarcodeScanning() {
        scanOptions = ScanOptions()
        scanOptions!!.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        scanOptions!!.setPrompt("Scanning Barcode")
        scanOptions!!.setCameraId(0)
        scanOptions!!.setBarcodeImageEnabled(true)
        scanOptions!!.setBeepEnabled(true)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            barcodeLauncher.launch(scanOptions)
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }

    //end::Barcode Scanner event init
    // start::MaterialAlertDialog events
    @SuppressLint("InflateParams", "PrivateResource")
    fun addDeviceBottomSheetOnInit() {
        val addDeviceChooseType =
            layoutInflater.inflate(R.layout.fragment_add_device_choose_type, null)



        btnaddbeacon = findViewById(R.id.btnaddbeacon)
        btnaddbeacon!!.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                this@MainActivity,
                com.google.android.material.R.style.Base_Theme_Material3_Dark_BottomSheetDialog
            )
            //start::bottom sheet events
            val bottomsheetviewgroup =
                findViewById<ViewGroup>(R.id.choosedevicebottomSheetContainer)
            val bottomSheetView = LayoutInflater.from(applicationContext)
                .inflate(R.layout.fragment_add_device_choose_bottom_sheet, bottomsheetviewgroup)
            bottomSheetDialog.setContentView(bottomSheetView)

            //start::Manual Device Add Form Button
            val bntmanualdeviceadd:Button=bottomSheetView.findViewById(R.id.btnchoosemacaddress)
            bntmanualdeviceadd.setOnClickListener{
                var manualadddeviceformint=Intent(this@MainActivity,AddDeviceAfterScanResultForm::class.java)
                manualadddeviceformint.putExtra("ismanual",true)
                startActivity(manualadddeviceformint)
            }
            //end::Manual Device Add Form Button



            val btnscancode: Button = bottomSheetView.findViewById(R.id.btnchoosescan)
            btnscancode.setOnClickListener {
                setBarcodeScanning()
            }
            /*val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    *//*if(newState.equals(BottomSheetBehavior.STATE_EXPANDED)){
                        val btnscancode: Button = bottomSheet.findViewById(R.id.btnchoosescan)
                        btnscancode.setOnClickListener {
                            setBarcodeScanning()
                        }
                    }*//*
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    TODO("Not yet implemented")
                }
            })*/

            bottomSheetDialog.show()


            /*addBeaconBuilder =
                MaterialAlertDialogBuilder(this, R.style.appAlertDialogStyle)
                    .setView(addDeviceChooseType)
                    //.setNegativeButton("Cancel", null)
                    .setOnDismissListener(DialogInterface.OnDismissListener {
                        (addDeviceChooseType.parent as ViewGroup).removeView(addDeviceChooseType)
                    })
            addBeaconDialog = addBeaconBuilder!!.create()
            addBeaconDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            addBeaconDialog!!.show()*/
        }
        //start::bottom sheet events
    }
//end::MaterialAlertDialog events

    @SuppressLint("NotifyDataSetChanged")
    fun getDeviceListOnInit() {
        val dbDeviceHelper = DBDeviceHelper(this)
        val devicelist = dbDeviceHelper.deviceList()
        if (devicelist != null && devicelist.size > 0) {
            adapter = DeviceRecyclerViewAdapter(this, devicelist)
            rvdevice = this.findViewById(R.id.rcvdevicelist)
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
                    val item: DeviceItem = adapter!!.getData()[position]
                    item.id?.let { adapter!!.removeItem(position, it) }
                }
            }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(rvdevice)
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

}