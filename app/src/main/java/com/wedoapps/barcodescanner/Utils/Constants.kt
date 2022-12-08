package com.wedoapps.barcodescanner.Utils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Constants {

    const val TAG = "BarcodeScanner"
    const val BARCODE = "Barcode"
    const val BARCODE_DATA = "ScannedData"
    const val USER_DATA = "UserData"
    const val SP_NAME = "Sp"
    const val NAME = "Name"
    const val VENDOR_NAME = "VendorName"
    const val PDF_DATA = "PdfData"
    const val FILTER_TYPE = "FilterType"
    const val IS_NEW = "isNew"
    const val REQUEST_CODE = 1
    const val HISTORY_DATA = "HistoryData"
    const val VENDOR_DATA = "VendorData"
    const val BY_MANUAL = "ByManual"
    const val MANUALLY = "Manually"
    const val DATE_RANGE = "Date Range"
    const val THIS_DAY = "Yesterday"
    const val THIS_MONTH = "This Month"
    const val LAST_WEEK = "Last Week"
    const val LAST_MONTH = "Last Month"
    const val ALL = "All"
    const val FROM = "From"
    const val STOCK_TO_MAIN = "stockToMain"
    const val DIRECT_MAIN = "Main"

    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }
}