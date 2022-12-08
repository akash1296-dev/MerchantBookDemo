package com.wedoapps.barcodescanner.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Users")

@Parcelize
data class Users(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String? = "",
    var mobileNo: String? = "",
    var address1: String? = "",
    var city: String? = "",
    var pincode: String? = ""
) : Parcelable {

}