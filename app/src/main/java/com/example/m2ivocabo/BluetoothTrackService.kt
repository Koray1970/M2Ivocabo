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
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat

class BluetoothTrackService : Service() {
    override fun onCreate() {
        var bluetoothManager:BluetoothManager=getSystemService(BluetoothManager::class.java)
        bluetoothAdapter= bluetoothManager.adapter
        if(applicationContext.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN)==PackageManager.PERMISSION_GRANTED) {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                var ractivity = application.baseContext as Activity
                startActivityForResult(ractivity, enableBtIntent, REQUEST_ENABLE_BT, null)
                stopSelf()
            }
        }
        else{
           ActivityCompat.requestPermissions(applicationContext as Activity, arrayOf(android.Manifest.permission.BLUETOOTH_SCAN,android.Manifest.permission.BLUETOOTH_CONNECT),100)
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