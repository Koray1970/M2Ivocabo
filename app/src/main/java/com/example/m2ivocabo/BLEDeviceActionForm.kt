package com.example.m2ivocabo

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class BLEDeviceActionForm : AppCompatActivity(),OnMapReadyCallback {
    var map:GoogleMap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bledevice_action_form)
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onMapReady(p0: GoogleMap) {
        map=p0
    }

    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val i = Intent(this@BLEDeviceActionForm, MainActivity::class.java)
                startActivity(i)
            }
        }
}