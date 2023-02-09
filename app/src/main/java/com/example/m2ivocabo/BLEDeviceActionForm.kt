package com.example.m2ivocabo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback

class BLEDeviceActionForm : AppCompatActivity(),OnMapReadyCallback {
    var map:GoogleMap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bledevice_action_form)
    }

    override fun onMapReady(p0: GoogleMap) {
        map=p0
    }
}