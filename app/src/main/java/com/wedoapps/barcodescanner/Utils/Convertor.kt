package com.wedoapps.barcodescanner.Utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wedoapps.barcodescanner.Model.ScannedData

class Convertor {
    @TypeConverter
    fun toScannedList(list: String): ArrayList<ScannedData> {
        val type = object : TypeToken<ArrayList<ScannedData>>() {}.type
        return Gson().fromJson(list, type)
    }

    @TypeConverter
    fun toScannedJson(data: ArrayList<ScannedData>): String {
        val type = object : TypeToken<ArrayList<ScannedData>>() {}.type
        return Gson().toJson(data, type)
    }
}