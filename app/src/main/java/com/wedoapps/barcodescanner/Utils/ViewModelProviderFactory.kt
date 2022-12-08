package com.wedoapps.barcodescanner.Utils

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Repository.BarcodeRepository

class ViewModelProviderFactory(
    private val app: Application,
    private val repository: BarcodeRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BarcodeViewModel(app, repository) as T
    }
}