package com.wedoapps.barcodescanner.Repository

import com.wedoapps.barcodescanner.Db.BarcodeDatabase
import com.wedoapps.barcodescanner.Model.*


class BarcodeRepository(private val db: BarcodeDatabase) {

    suspend fun insertItem(data: ScannedData) = db.getBarcodeDao().insertItem(data)

    suspend fun updateItem(data: ScannedData) = db.getBarcodeDao().updateItem(data)

    suspend fun deleteItem(data: ScannedData) = db.getBarcodeDao().deleteItem(data)

    fun getAllItems() = db.getBarcodeDao().getAllItems()

    suspend fun getAllItemsWithoutObservers() = db.getBarcodeDao().getAllItemsWithoutObservers()

    suspend fun insertBarcodeData(item: BarcodeEntryItem) =
        db.getBarcodeDao().insertBarcodeData(item)

    suspend fun updateBarcodeData(item: BarcodeEntryItem) =
        db.getBarcodeDao().updateBarcodeData(item)

    suspend fun deleteBarcodeData(item: BarcodeEntryItem) =
        db.getBarcodeDao().deleteBarcodeData(item)

    suspend fun getBarcodeCodeWO(barcodeNumber: String) =
        db.getBarcodeDao().getBarcodeDataWO(barcodeNumber)

    suspend fun getScannedDataWO(barcodeNumber: String) =
        db.getBarcodeDao().getScannedDataWO(barcodeNumber)

    fun getBarcodeDataList() = db.getBarcodeDao().getAllBarcodeDataList()

    suspend fun insertUser(users: Users) = db.getBarcodeDao().addUser(users)

    suspend fun updateUser(users: Users) = db.getBarcodeDao().updateUser(users)

    suspend fun deleteUser(users: Users) = db.getBarcodeDao().deleteUser(users)

    fun getAllUserList() = db.getBarcodeDao().getAllUserList()


    suspend fun deleteScannedData() = db.getBarcodeDao().deleteScannedData()

    suspend fun addHistoryItem(pdfData: PDFData) = db.getBarcodeDao().addHistoryItem(pdfData)

    suspend fun deleteHistoryItem(pdfData: PDFData) = db.getBarcodeDao().deleteHistoryItem(pdfData)

    fun getAllHistoryList() = db.getBarcodeDao().getAllHistoryList()

    suspend fun getAllHistoryListWO() = db.getBarcodeDao().getAllHistoryListWO()

    suspend fun getHistoryDate(toDate: String, fromDate: String) =
        db.getBarcodeDao().getHistoryFromDate(toDate, fromDate)

    fun getAllVendorList() = db.getBarcodeDao().getAllVendorList()

    suspend fun addVendorItem(vendorModel: VendorModel) = db.getBarcodeDao().addVendor(vendorModel)

    suspend fun updateVendorItem(vendorModel: VendorModel) =
        db.getBarcodeDao().updateVendor(vendorModel)

    suspend fun deleteVendorItem(vendorModel: VendorModel) =
        db.getBarcodeDao().deleteVendor(vendorModel)

    suspend fun getVendorById(id: Int) = db.getBarcodeDao().getVendorById(id)

    suspend fun insertSingleReport(reportModel: SingleReportModel) =
        db.getBarcodeDao().insertSingleReport(reportModel)

    suspend fun updateSingleReport(reportModel: SingleReportModel) =
        db.getBarcodeDao().updateSingleReport(reportModel)

    suspend fun deleteSingleReport(reportModel: SingleReportModel) =
        db.getBarcodeDao().deleteSingleReport(reportModel)

    suspend fun getAllSingleReport() = db.getBarcodeDao().getAllSingleReport()

    suspend fun getSingleReportDateWise(toDate: String, fromDate: String) =
        db.getBarcodeDao().getSingleReportFromDate(toDate, fromDate)

    suspend fun insertBuyerReport(buyerReportModal: BuyerReportModal) =
        db.getBarcodeDao().insertBuyerReport(buyerReportModal)

    suspend fun updateBuyerReport(buyerReportModal: BuyerReportModal) =
        db.getBarcodeDao().updateBuyerReport(buyerReportModal)

    suspend fun deleteBuyerReport(buyerReportModal: BuyerReportModal) =
        db.getBarcodeDao().deleteBuyerReport(buyerReportModal)

    suspend fun getAllBuyerReport() = db.getBarcodeDao().getAllBuyerReport()

    suspend fun getLastDateReport() = db.getBarcodeDao().getLastDayReport()

    suspend fun getLastWeekReport() = db.getBarcodeDao().getLastWeekReport()

    suspend fun getLastMonthReport() = db.getBarcodeDao().getLastMonthReport()

    suspend fun getCurrentMonthReport() = db.getBarcodeDao().getCurrentMonthReport()
}
