package com.example.m2ivocabo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat

class BluetoothTrackService : Service() {
    override fun onCreate() {
        var bluetoothManager:BluetoothManager=getSystemService(BluetoothManager::class.java)
        bluetoothAdapter= bluetoothManager.adapter
        if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(applicationContext,"Herşey yolunda",Toast.LENGTH_SHORT).show()
        }

    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY_COMPATIBILITY
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
    companion object{
        val REQUEST_ENABLE_BT=1
        lateinit var bluetoothAdapter:BluetoothAdapter
    }
}