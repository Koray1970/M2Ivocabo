package com.example.m2ivocabo

class AppHelper {
    fun StringToMacaddress(str: String): String? {
        try {
            return str.replace("(..)(?!$)".toRegex(), "$1:")
        } catch (ex: Exception) {
        }
        return null
    }
}