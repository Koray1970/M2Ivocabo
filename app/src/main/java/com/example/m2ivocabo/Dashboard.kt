package com.example.m2ivocabo

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.m2ivocabo.databinding.FragmentDashboardBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class Dashboard : Fragment() {
    private val hideHandler = Handler(Looper.myLooper()!!)

    @Suppress("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        val flags =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        activity?.window?.decorView?.systemUiVisibility = flags
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements

    }
    private var visible: Boolean = false
    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }


    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var permissionDenied = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MaterialAlertDialogBuilder_OnInit()
        GetDeviceList_OnInit()
        visible = true

    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        GetDeviceList_OnInit()
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // Clear the systemUiVisibility flag
        activity?.window?.decorView?.systemUiVisibility = 0
        show()
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    private fun toggle() {
        if (visible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first

        visible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @Suppress("InlinedApi")
    private fun show() {
        // Show the system bar

        visible = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        var TAG: String = Dashboard::class.java.simpleName

        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300

        private var scanOptions: ScanOptions? = null
        private var btnaddbeacon: Button? = null
        var addBeaconBuilder: MaterialAlertDialogBuilder? = null
        var addBeaconDialog: AlertDialog? = null

        var latlng: LatLng? = null

        private val LOCATION_PERMISSION_REQUEST_CODE = 100
        private val gson = Gson()
        var adapter: DeviceRecyclerViewAdapter? = null
        var rvdevice: RecyclerView? = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            Toast.makeText(this.requireActivity(), "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            if (latlng != null) {
                var intent =
                    Intent(this.requireContext(), AddDeviceWithScanResultForm::class.java)
                intent.putExtra("macaddress", result.contents)


                var _latlng = gson.toJson(latlng)
                intent.putExtra("latlng", _latlng)
                val bundle = Bundle()
                bundle.putString("macaddress", result.contents)
                bundle.putString("latlng", _latlng)
                var addDeviceWithScanResultForm = AddDeviceWithScanResultForm()
                addDeviceWithScanResultForm.arguments = bundle

                val fragmentTransaction =
                    this.requireActivity().supportFragmentManager.beginTransaction()
                fragmentTransaction.add(
                    R.id.flmainframe,
                    addDeviceWithScanResultForm,
                    "AddDeviceScanResultForm"
                )
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
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
                this.requireActivity(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            barcodeLauncher.launch(scanOptions)
        else {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
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

        btnaddbeacon = this.view?.findViewById(R.id.btnaddbeacon) as Button
        btnaddbeacon!!.setOnClickListener {
            addBeaconBuilder =
                MaterialAlertDialogBuilder(this.requireContext(), R.style.appAlertDialogStyle)
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
        var dbDeviceHelper = DBDeviceHelper(requireContext())
        val devicelist = dbDeviceHelper.deviceList()
        if (devicelist != null && devicelist.size > 0) {
            adapter = DeviceRecyclerViewAdapter(requireContext(), devicelist)
            rvdevice = requireActivity().findViewById<RecyclerView>(R.id.rcvdevicelist)
            rvdevice?.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            rvdevice?.adapter = adapter
            adapter?.notifyDataSetChanged()
            enableSwipeToDeleteAndUndo()
        }
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeToDeleteCallback: SwipeToDeleteCallback =
            object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: ViewHolder, i: Int) {
                    val position = viewHolder.adapterPosition
                    val item: DeviceItem = adapter!!.getData()!!.get(position)
                    item.id?.let { adapter!!.removeItem(position, it) }
                }
            }
        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchhelper.attachToRecyclerView(rvdevice)
    }


}