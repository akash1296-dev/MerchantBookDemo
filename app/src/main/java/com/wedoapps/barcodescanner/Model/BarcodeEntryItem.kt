package com.wedoapps.barcodescanner.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "BarcodeEntryItem")

@Parcelize
data class BarcodeEntryItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var barcodeNumber: String? = "",
    var itemCode: String? = "",
    var itemName: String? = "",
    var sellingPrice: Int? = null,
    var purchasePrice: Int? = null,
    var count: Int? = null,
    var minQuantity: Int? = null
) : Parcelable {
    constructor(
        id: Int?,
        barcodeNumber: String?,
        itemCode: String?,
        itemName: String?,
        sellingPrice: Int?,
        purchasePrice: Int?,
        count: Int?,
        minQuantity: Int
    ) : this() {
        this.id = id
        this.barcodeNumber = barcodeNumber
        this.itemCode = itemCode
        this.itemName = itemName
        this.sellingPrice = sellingPrice
        this.purchasePrice = purchasePrice
        this.count = count
        this.minQuantity = minQuantity
    }
}
