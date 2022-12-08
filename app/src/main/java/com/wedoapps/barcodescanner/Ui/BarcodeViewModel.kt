package com.wedoapps.barcodescanner

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wedoapps.barcodescanner.Model.*
import com.wedoapps.barcodescanner.Repository.BarcodeRepository
import com.wedoapps.barcodescanner.Utils.Constants.ALL
import com.wedoapps.barcodescanner.Utils.Constants.DATE_RANGE
import com.wedoapps.barcodescanner.Utils.Constants.LAST_MONTH
import com.wedoapps.barcodescanner.Utils.Constants.LAST_WEEK
import com.wedoapps.barcodescanner.Utils.Constants.TAG
import com.wedoapps.barcodescanner.Utils.Constants.THIS_DAY
import com.wedoapps.barcodescanner.Utils.Constants.THIS_MONTH
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BarcodeViewModel(
    app: Application,
    private val repository: BarcodeRepository
) : AndroidViewModel(app) {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            delay(1500L)
            _isLoading.value = false
        }
    }

    private val _historyDataMutableLiveData = MutableLiveData<List<PDFData>>()
    val historyDataLiveData: LiveData<List<PDFData>>
        get() = _historyDataMutableLiveData

    val barcodeDataMutableLiveData = MutableLiveData<BarcodeEntryItem?>()
    val scannedDataInsertAndUpdateResponseLiveData = MutableLiveData<String>()
    private var itemList = mutableListOf<ScannedData>()

    private val _singleReportItem = MutableLiveData<List<SingleReportModel>>()
    val singleReportItemLiveData: LiveData<List<SingleReportModel>>
        get() = _singleReportItem

    private val _buyerReport = MutableLiveData<List<BuyerReportModal>>()
    val buyerReportLiveData: LiveData<List<BuyerReportModal>>
        get() = _buyerReport

    private fun insertScannedItem(
        barcodeNumber: String,
        itemCode: String,
        item: String,
        price: Int,
        originalPrice: Int?,
        storeQuantity: Int?,
        showDialog: Boolean?,
        minCount: Int?,
    ) = viewModelScope.launch {
        val scannedData = ScannedData(
            null,
            barcodeNumber,
            itemCode,
            item,
            price,
            originalPrice,
            storeQuantity,
            minCount,
            showDialog,
            1
        )
        repository.insertItem(scannedData)
    }

    fun updateScannedData(
        id: Int,
        barcodeNumber: String,
        itemCode: String,
        item: String,
        price: Int?,
        originalPrice: Int?,
        storeQuantity: Int?,
        minCount: Int?,
        showDialog: Boolean?,
        count: Int?
    ) = viewModelScope.launch {
        val scannedData = ScannedData(
            id,
            barcodeNumber,
            itemCode,
            item,
            price,
            originalPrice,
            storeQuantity,
            minCount,
            showDialog,
            count
        )
        repository.updateItem(scannedData)
    }

    fun deleteScannedData(scannedData: ScannedData) = viewModelScope.launch {
        repository.deleteItem(scannedData)
    }

    fun addBarcodeItem(
        barcodeNumber: String,
        itemCode: String,
        item: String,
        sellingPrice: Int,
        purchasePrice: Int,
        count: Int,
        minCount: Int?
    ) = viewModelScope.launch {
        val barcodeEntryItem = BarcodeEntryItem(
            null,
            barcodeNumber,
            itemCode,
            item,
            sellingPrice,
            purchasePrice,
            count,
            minCount
        )

        repository.insertBarcodeData(barcodeEntryItem)
    }

    fun updateBarcodeItem(
        id: Int?,
        barcodeNumber: String,
        itemCode: String,
        item: String,
        sellingPrice: Int?,
        purchasePrice: Int?,
        count: Int?,
        minCount: Int?
    ) = viewModelScope.launch {
        val barcodeEntryItem = BarcodeEntryItem(
            id,
            barcodeNumber,
            itemCode,
            item,
            sellingPrice,
            purchasePrice,
            count,
            minCount
        )
        repository.updateBarcodeData(barcodeEntryItem)
    }

    fun deleteBarcodeItem(barcodeEntryItem: BarcodeEntryItem) = viewModelScope.launch {
        repository.deleteBarcodeData(barcodeEntryItem)
    }

    fun getScannedDataList() = repository.getAllItems()

    fun getBarcodeDataList() = repository.getBarcodeDataList()

    fun getSingleBarcodeData(barcodeNumber: String) = viewModelScope.launch {
        handleBarcodeItemEntry(barcodeNumber)
    }

    private suspend fun handleBarcodeItemEntry(barcodeNumber: String) {
        val barcodeEntryList = repository.getBarcodeCodeWO(barcodeNumber)

        if (barcodeEntryList == null) {
            barcodeDataMutableLiveData.postValue(
                BarcodeEntryItem(
                    null,
                    barcodeNumber,
                    "",
                    "",
                    null,
                    null,
                    null,
                    null
                )
            )
        } else if (barcodeEntryList.barcodeNumber == barcodeNumber) {
            insertAndUpdateScannedData(
                barcodeEntryList.barcodeNumber.toString(),
                barcodeEntryList.itemCode.toString(),
                barcodeEntryList.itemName.toString(),
                barcodeEntryList.sellingPrice!!,
                barcodeEntryList.count!!,
                barcodeEntryList.minQuantity!!,
                barcodeEntryList.sellingPrice!!,
                true
            )
            barcodeDataMutableLiveData.postValue(barcodeEntryList)
        }
    }

    fun insertAndUpdateScannedData(
        barcodeNumber: String,
        itemCode: String,
        item: String,
        price: Int,
        storeQuantity: Int?,
        minCount: Int?,
        originalPrice: Int?,
        showDialog: Boolean?
    ) =
        viewModelScope.launch {
            handleInsertAndUpdateScannedData(
                barcodeNumber,
                itemCode,
                item,
                price,
                storeQuantity,
                minCount,
                originalPrice,
                showDialog
            )
        }

    private suspend fun handleInsertAndUpdateScannedData(
        barcodeNumber: String?,
        itemCode: String?,
        item: String,
        price: Int,
        storeQuantity: Int?,
        minCount: Int?,
        originalPrice: Int?,
        showDialog: Boolean?
    ) {
        itemList = repository.getAllItemsWithoutObservers().toMutableList()
        val foundItem = itemList.find { fItem -> fItem.barcodeNumber.equals(barcodeNumber) }
        var isUpdated = 0       // 0 == NotUpdated,  1 == Updated
        if (foundItem != null) {
            val totalPrice = foundItem.price?.plus(price)
            val totalCount = foundItem.count?.plus(1)
            isUpdated = 1
            updateScannedData(
                id = foundItem.id!!,
                barcodeNumber = barcodeNumber ?: foundItem.barcodeNumber.toString(),
                itemCode = itemCode ?: foundItem.itemCode.toString(),
                item = foundItem.item.toString(),
                price = if (showDialog == true) totalPrice else foundItem.price,
                originalPrice = originalPrice ?: foundItem.originalPrice,
                storeQuantity = storeQuantity ?: foundItem.storeQuantity,
                minCount = foundItem.minCount,
                showDialog = showDialog,
                count = if (showDialog == true) totalCount else foundItem.count
            )
            scannedDataInsertAndUpdateResponseLiveData.postValue("$item Updated")
        }

        if (isUpdated == 0) {
            insertScannedItem(
                barcodeNumber.toString(),
                itemCode.toString(),
                item,
                price,
                originalPrice = price,
                storeQuantity,
                showDialog,
                minCount
            )
            scannedDataInsertAndUpdateResponseLiveData.postValue("$item Added")
        }
    }

    fun addUser(
        name: String, mobileNumber: String,
        address1: String, city: String, pincode: String
    ) = viewModelScope.launch {
        val user = Users(
            null,
            name,
            mobileNumber,
            address1,
            city,
            pincode
        )
        repository.insertUser(user)
    }

    fun updateUser(
        id: Int?, name: String, mobileNumber: String,
        address1: String, address2: String, pincode: String
    ) = viewModelScope.launch {
        val user = Users(
            id,
            name,
            mobileNumber,
            address1,
            address2,
            pincode
        )
        repository.updateUser(user)
    }

    fun deleteUser(user: Users) = viewModelScope.launch {
        repository.deleteUser(user)
    }

    fun getUserList() = repository.getAllUserList()

    fun deleteScannedData() = viewModelScope.launch {
        repository.deleteScannedData()
    }

    fun removeStocks(barcodeNumber: MutableList<String?>) = viewModelScope.launch {
        safeHandleRemoveStocks(barcodeNumber)
    }

    private suspend fun safeHandleRemoveStocks(barcodeNumber: MutableList<String?>) {
        if (barcodeNumber.isNotEmpty()) {
            for (i in barcodeNumber) {
                val findBarcodeData = repository.getBarcodeCodeWO(i.toString())
                val findScannedData = repository.getScannedDataWO(i.toString())

                Log.d(
                    TAG,
                    "safeHandleRemoveStocks: ${findBarcodeData.barcodeNumber} ${findScannedData.barcodeNumber}"
                )

                if (findBarcodeData.barcodeNumber.equals(findScannedData.barcodeNumber)) {
                    val newQuantity = findBarcodeData.count?.minus(findScannedData.count ?: 0)
                    Log.d(TAG, "safeHandleRemoveStocks: $newQuantity")
                    updateBarcodeItem(
                        findBarcodeData.id,
                        findBarcodeData.barcodeNumber.toString(),
                        findBarcodeData.itemCode.toString(),
                        findBarcodeData.itemName.toString(),
                        findBarcodeData.sellingPrice,
                        findBarcodeData.purchasePrice,
                        newQuantity,
                        findBarcodeData.minQuantity
                    )
                    insertAndUpdateScannedData(
                        findBarcodeData.barcodeNumber.toString(),
                        findBarcodeData.itemCode.toString(),
                        findBarcodeData.itemName.toString(),
                        findBarcodeData.sellingPrice!!,
                        newQuantity,
                        findBarcodeData.minQuantity,
                        findBarcodeData.purchasePrice,
                        false
                    )
                }

                if (findScannedData.storeQuantity!! <= 0) {
                    deleteScannedData(findScannedData)
                }
            }
        }
    }

    fun updateScannedItem(
        barcodeNumber: String,
        itemCode: String,
        item: String,
        originalPrice: Int?,
        storeQuantity: Int?,
    ) = viewModelScope.launch {
        handleUpdate(
            barcodeNumber, itemCode, item, originalPrice, storeQuantity
        )
    }

    private suspend fun handleUpdate(
        barcodeNumber: String,
        itemCode: String,
        item: String,
        originalPrice: Int?,
        storeQuantity: Int?,
    ) {
        val scannedList = repository.getAllItemsWithoutObservers()
        val foundItem = scannedList.find { it.barcodeNumber == barcodeNumber }

        if (foundItem != null) {
            updateScannedData(
                foundItem.id!!,
                barcodeNumber,
                itemCode,
                item,
                foundItem.price,
                originalPrice,
                storeQuantity,
                foundItem.minCount,
                foundItem.showDialog,
                foundItem.count
            )
        }
    }

    fun addHistoryItem(
        name: String,
        itemList: ArrayList<ScannedData>?,
        phoneNumber: String,
        total: String,
        date: String,
        time: String
    ) = viewModelScope.launch {
        val pdfData = PDFData(
            null, name, itemList, phoneNumber, total, date, time
        )
        repository.addHistoryItem(pdfData)
    }

    /* fun deleteHistoryItem(pdfData: PDFData) = viewModelScope.launch {
         repository.deleteHistoryItem(pdfData)
     }

     fun getHistoryList() = repository.getAllHistoryList()*/

    fun historyDateWise(toDate: String, fromDate: String) = viewModelScope.launch {
        handleHistoryDateWise(toDate, fromDate)
    }

    private suspend fun handleHistoryDateWise(toDate: String, fromDate: String) {
        Log.d(TAG, "view: $toDate $fromDate")
        val list = repository.getHistoryDate(toDate, fromDate)
        Log.d(TAG, "view LIST: $list")
        _historyDataMutableLiveData.postValue(list)
    }

    fun addVendor(
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        city: String,
        pincode: String,
        payment: Int?,
        paidPayment: Int?,
        duePayment: Int?
    ) = viewModelScope.launch {
        val vendor = VendorModel(
            null,
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            address = address,
            city = city,
            pincode = pincode,
            payment = payment,
            paidPayment = paidPayment,
            duePayment = duePayment
        )
        repository.addVendorItem(vendor)
    }

    fun updateVendor(
        id: Int,
        name: String,
        email: String,
        phoneNumber: String,
        address: String,
        city: String,
        pincode: String,
        payment: Int,
        paidPayment: Int,
        duePayment: Int
    ) = viewModelScope.launch {
        val vendor = VendorModel(
            id = id,
            name = name,
            email = email,
            phoneNumber = phoneNumber,
            address = address,
            city = city,
            pincode = pincode,
            payment = payment,
            paidPayment = paidPayment,
            duePayment = duePayment
        )
        repository.updateVendorItem(vendor)
    }

    fun deleteVendor(vendorModel: VendorModel) = viewModelScope.launch {
        repository.deleteVendorItem(vendorModel)
    }

    fun vendorList() = repository.getAllVendorList()

    fun updatePayment(
        id: Int,
        totalPayment: Int,
        paidPayment: Int
    ) = viewModelScope.launch {
        safeHandleUpdatePayment(id, totalPayment, paidPayment)
    }

    private suspend fun safeHandleUpdatePayment(
        id: Int,
        totalPayment: Int,
        paidPayment: Int
    ) {
        val foundVendor = repository.getVendorById(id).find { it.id == id }

        val paid = foundVendor?.paidPayment?.plus(paidPayment)
        val due = foundVendor?.duePayment?.minus(paidPayment)

        val updateVendorModel = VendorModel(
            id,
            foundVendor?.name,
            foundVendor?.email,
            foundVendor?.phoneNumber,
            foundVendor?.address,
            foundVendor?.city,
            foundVendor?.pincode,
            totalPayment,
            paid,
            due
        )
        repository.updateVendorItem(updateVendorModel)
    }

    private fun insertSingleReport(
        barcodeNumber: String,
        itemCode: String,
        itemName: String,
        itemPrice: Int,
        quantity: Int,
        totalPrice: Int
    ) = viewModelScope.launch {
        handleInsertSingleReport(
            barcodeNumber = barcodeNumber,
            itemCode = itemCode,
            itemName = itemName,
            itemPrice = itemPrice,
            quantity = quantity,
            totalPrice = totalPrice
        )
    }

    private suspend fun handleInsertSingleReport(
        barcodeNumber: String,
        itemCode: String,
        itemName: String,
        itemPrice: Int,
        quantity: Int,
        totalPrice: Int
    ) {
        val singleReportModel = SingleReportModel(
            null,
            barcodeNumber = barcodeNumber,
            itemCode = itemCode,
            itemName = itemName,
            itemPrice = itemPrice,
            quantity = quantity,
            totalPrice = totalPrice
        )

        repository.insertSingleReport(singleReportModel)
    }

    private fun updateSingleReport(
        id: Int,
        barcodeNumber: String,
        itemCode: String,
        itemName: String,
        itemPrice: Int,
        quantity: Int,
        totalPrice: Int
    ) = viewModelScope.launch {
        val singleReportModel = SingleReportModel(
            id,
            barcodeNumber = barcodeNumber,
            itemCode = itemCode,
            itemName = itemName,
            itemPrice = itemPrice,
            quantity = quantity,
            totalPrice = totalPrice
        )
        repository.updateSingleReport(singleReportModel)
    }

    /* fun deleteSingleReport(reportModel: SingleReportModel) = viewModelScope.launch {
         repository.deleteSingleReport(reportModel)
     }*/

    fun singleReportItem(type: String, fromDate: String, toDate: String) = viewModelScope.launch {
        when (type) {
            ALL -> {
                safeHandleAllSingleReport()
            }
            THIS_MONTH -> {
                safeHandleCurrentMonthReport()
            }
            THIS_DAY -> {
                handleLastDateReportData()
            }
            LAST_WEEK -> {
                safeHandleLastWeekReport()
            }
            LAST_MONTH -> {
                safeHandleLastMonthReport()
            }
            DATE_RANGE -> {
                safeHandleSingleReportItem(fromDate, toDate)
            }
        }
    }

    fun updateAndInsertSingleReport(
        barcodeNumber: String,
        itemCode: String,
        itemName: String,
        itemPrice: Int,
        quantity: Int,
        totalPrice: Int
    ) = viewModelScope.launch {
        handleUpdateAndInsertSingleReport(
            barcodeNumber,
            itemCode,
            itemName,
            itemPrice,
            quantity,
            totalPrice
        )
    }

    private suspend fun handleUpdateAndInsertSingleReport(
        barcodeNumber: String,
        itemCode: String,
        itemName: String,
        itemPrice: Int,
        quantity: Int,
        totalPrice: Int
    ) {
        val itemList = repository.getAllSingleReport()
        val foundItem = itemList.find { it.barcodeNumber.equals(barcodeNumber) }
        var isUpdated = 0       // 0 == NotUpdated,  1 == Updated
        if (foundItem != null) {
            val totalPriceAll = foundItem.itemPrice?.plus(itemPrice)
            val totalCount = foundItem.quantity?.plus(quantity)
            isUpdated = 1
            updateSingleReport(
                id = foundItem.id!!,
                barcodeNumber = barcodeNumber,
                itemCode = itemCode,
                itemName = foundItem.itemName.toString(),
                itemPrice = itemPrice,
                quantity = totalCount!!,
                totalPrice = totalPriceAll!!,
            )
        }

        if (isUpdated == 0) {
            insertSingleReport(
                barcodeNumber,
                itemCode,
                itemName,
                itemPrice,
                quantity,
                totalPrice,
            )
        }

    }

    private suspend fun safeHandleSingleReportItem(fromDate: String, toDate: String) {
        val list = repository.getSingleReportDateWise(fromDate, toDate)
        _singleReportItem.postValue(list)
    }

    private suspend fun safeHandleBuyerReport() {
        val list = repository.getAllBuyerReport()
        _buyerReport.postValue(list)
    }

    fun buyerReport() = viewModelScope.launch {
        safeHandleBuyerReport()
    }

    private suspend fun handleInsertBuyerReport(
        name: String,
        cartList: ArrayList<ScannedData>?,
        phoneNumber: String,
        total: String
    ) {
        val buyerReport = BuyerReportModal(
            id = null,
            name = name,
            cartList = cartList,
            phoneNumber = phoneNumber,
            total = total,
        )
        repository.insertBuyerReport(buyerReport)
    }

    private suspend fun handleUpdateBuyerReport(
        id: Int,
        name: String,
        cartList: ArrayList<ScannedData>?,
        phoneNumber: String,
        total: String
    ) {
        val buyerReport = BuyerReportModal(
            id,
            name,
            cartList,
            phoneNumber,
            total
        )

        repository.updateBuyerReport(buyerReport)
    }

    private fun insertBuyerReport(
        name: String,
        cartList: ArrayList<ScannedData>?,
        phoneNumber: String,
        total: String
    ) = viewModelScope.launch {
        handleInsertBuyerReport(
            name, cartList, phoneNumber, total
        )
    }

    private fun updateBuyerReport(
        id: Int,
        name: String,
        cartList: ArrayList<ScannedData>?,
        phoneNumber: String,
        total: String
    ) = viewModelScope.launch {
        handleUpdateBuyerReport(
            id, name, cartList, phoneNumber, total
        )
    }

    private suspend fun handleUpdateAndInsertBuyerReport(
        name: String,
        cartList: ArrayList<ScannedData>?,
        phoneNumber: String,
        total: String
    ) {
        val itemList = repository.getAllBuyerReport()
        val foundItem = itemList.find { it.name.equals(name) }
        var isUpdated = 0       // 0 == NotUpdated,  1 == Updated
        if (foundItem != null) {
            val totalPriceAll = foundItem.total?.toInt()?.plus(total.toInt())
            cartList?.let { foundItem.cartList?.addAll(it) }
            isUpdated = 1
            updateBuyerReport(
                id = foundItem.id!!,
                name = name,
                cartList = foundItem.cartList,
                phoneNumber = phoneNumber,
                total = totalPriceAll.toString()
            )
        }

        if (isUpdated == 0) {
            insertBuyerReport(
                name, cartList, phoneNumber, total
            )
        }
    }

    fun insertAndUpdateBuyerReport(
        name: String,
        cartList: ArrayList<ScannedData>?,
        phoneNumber: String,
        total: String
    ) = viewModelScope.launch {
        handleUpdateAndInsertBuyerReport(
            name, cartList, phoneNumber, total
        )
    }

    private suspend fun handleLastDateReportData() {
        val list = repository.getLastDateReport()
        _singleReportItem.postValue(list)
    }

    private suspend fun safeHandleAllSingleReport() {
        val list = repository.getAllSingleReport()
        _singleReportItem.postValue(list)
    }

    private suspend fun safeHandleLastWeekReport() {
        val list = repository.getLastWeekReport()
        _singleReportItem.postValue(list)
    }

    private suspend fun safeHandleLastMonthReport() {
        val list = repository.getLastMonthReport()
        _singleReportItem.postValue(list)
    }

    private suspend fun safeHandleCurrentMonthReport() {
        val list = repository.getCurrentMonthReport()
        _singleReportItem.postValue(list)
    }
}