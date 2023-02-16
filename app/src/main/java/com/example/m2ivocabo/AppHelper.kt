package com.example.m2ivocabo

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import java.util.regex.Pattern

class AppHelper {
    fun StringToMacaddress(str: String): String? {
        try {
            return str.replace("(..)(?!$)".toRegex(), "$1:")
            //return str.replace("..(?!\$)".toRegex(), "\$&:")
        } catch (ex: Exception) {
        }
        return null
    }
    fun CheckMacAddress(macaddress:String):Boolean{
        try{
            return BluetoothAdapter.checkBluetoothAddress(macaddress)
        }
        catch (ex:Exception){

        }
        return false
    }
}