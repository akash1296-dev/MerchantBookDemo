package com.wedoapps.barcodescanner.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "ScannedData")

@Parcelize
data class ScannedData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var barcodeNumber: String? = "",
    var itemCode: String? = "",
    var item: String? = "",
    var price: Int? = null,
    var originalPrice: Int? = null,
    var storeQuantity: Int? = null,
    var minCount: Int? = null,
    var showDialog: Boolean? = true,
    var count: Int? = null
) : Parcelable {

    constructor(
        id: Int?,
        item: String?,
        price: Int?,
        originalPrice: Int?,
        storeQuantity: Int?,
        minCount: Int?,
        showDialog: Boolean?,
        count: Int
    ) : this() {
        this.id = id
        this.item = item
        this.price = price
        this.originalPrice = originalPrice
        this.storeQuantity = storeQuantity
        this.minCount = minCount
        this.showDialog = showDialog
        this.count = count
    }

}
