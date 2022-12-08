package com.wedoapps.barcodescanner.Model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "SingleReportModal")

@Parcelize
data class SingleReportModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var barcodeNumber: String? = "",
    var itemCode: String? = "",
    var itemName: String? = "",
    var itemPrice: Int? = null,
    var quantity: Int? = null,
    var totalPrice: Int? = null,
    var date: String = getDate(),
    var time: String = getTime(),
    var epochtime: String = getEpochTime()
) : Parcelable {

    constructor(
        id: Int?,
        barcodeNumber: String?,
        itemCode: String?,
        itemName: String?,
        itemPrice: Int?,
        quantity: Int?,
        totalPrice: Int?,
    ) : this() {
        this.id = id
        this.barcodeNumber = barcodeNumber
        this.itemCode = itemCode
        this.itemName = itemName
        this.itemPrice = itemPrice
        this.quantity = quantity
        this.totalPrice = totalPrice
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        private fun getDate(): String {
            val currentTime: Date = Calendar.getInstance().time
//            val simpleFormat = SimpleDateFormat("dd MMM yyyy hh:mm")
            val simpleFormat = SimpleDateFormat("yyyy-MM-dd")
            return simpleFormat.format(currentTime)
        }

        private fun getTime(): String {
            val time: Date = Calendar.getInstance().time
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            return currentTime.format(time)
        }

        private fun getEpochTime(): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val netDate = Date(getDate().toLong() * 1000)
                sdf.format(netDate)
            } catch (e: Exception) {
                e.toString()
            }
        }
    }
}
