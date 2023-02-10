package com.example.m2ivocabo

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
}