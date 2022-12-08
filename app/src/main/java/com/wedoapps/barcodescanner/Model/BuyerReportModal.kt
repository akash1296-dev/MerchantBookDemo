package com.wedoapps.barcodescanner.Model

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity(tableName = "BuyerReport")
data class BuyerReportModal(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String? = "",
    var cartList: ArrayList<ScannedData>? = arrayListOf(),
    var phoneNumber: String? = "",
    var total: String? = "",
    var date: String = getDate(),
    var time: String = getTime()
) : Parcelable {

    constructor(
        id: Int?,
        name: String?,
        cartList: ArrayList<ScannedData>?,
        phoneNumber: String?,
        total: String?,
    ) : this() {
        this.id = id
        this.name = name
        this.cartList = cartList
        this.phoneNumber = phoneNumber
        this.total = total
    }

    companion object {
        @SuppressLint("SimpleDateFormat")
        private fun getDate(): String {
            val currentTime: Date = Calendar.getInstance().time
//            val simpleFormat = SimpleDateFormat("dd MMM yyyy hh:mm")
            val simpleFormat = SimpleDateFormat("dd/MM/yyyy")
            return simpleFormat.format(currentTime)
        }

        private fun getTime(): String {
            val time: Date = Calendar.getInstance().time
            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            return currentTime.format(time)
        }
    }
}
