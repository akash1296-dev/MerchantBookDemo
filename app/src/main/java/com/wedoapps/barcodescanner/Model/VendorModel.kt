package com.wedoapps.barcodescanner.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Vendor")
@Parcelize
data class VendorModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String? = "",
    var email: String? = "",
    var phoneNumber: String? = "",
    var address: String? = "",
    var city: String? = "",
    var pincode: String? = "",
    var payment: Int? = null,
    var paidPayment: Int? = null,
    var duePayment: Int? = null,
    var showMenu: Boolean? = false
) : Parcelable {


}