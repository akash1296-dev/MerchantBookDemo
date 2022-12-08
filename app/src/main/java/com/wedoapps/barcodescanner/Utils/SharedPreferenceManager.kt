package com.wedoapps.barcodescanner.Utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.wedoapps.barcodescanner.Utils.Constants.NAME
import com.wedoapps.barcodescanner.Utils.Constants.SP_NAME
import com.wedoapps.barcodescanner.Utils.Constants.VENDOR_NAME

class SharedPreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SP_NAME, MODE_PRIVATE)

    fun setName(name: String) {
        sharedPreferences.edit().putString(NAME, name).apply()
    }

    fun setVendorName(name: String) {
        sharedPreferences.edit().putString(VENDOR_NAME, name).apply()
    }

    fun getName(): String? {
        return sharedPreferences.getString(NAME, "")
    }

    fun getVendorName(): String? {
        return sharedPreferences.getString(VENDOR_NAME, "")
    }

    fun clearNames() {
        sharedPreferences.edit().clear().apply()
    }
}