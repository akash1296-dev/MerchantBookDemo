package com.wedoapps.barcodescanner.Utils

import android.app.Application
import com.wedoapps.barcodescanner.Db.BarcodeDatabase
import com.wedoapps.barcodescanner.Repository.BarcodeRepository

class BarcodeApplication : Application() {

    val database by lazy { BarcodeDatabase.invoke(this) }
    val repository by lazy { BarcodeRepository(database) }
    val pref by lazy { PreferenceHelper.init(this) }

}