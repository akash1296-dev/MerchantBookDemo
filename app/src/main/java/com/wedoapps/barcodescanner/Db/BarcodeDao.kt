package com.wedoapps.barcodescanner.Db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.wedoapps.barcodescanner.Model.*

@Dao
interface BarcodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(data: ScannedData)

    @Update
    suspend fun updateItem(data: ScannedData)

    @Query("SELECT * FROM ScannedData")
    fun getAllItems(): LiveData<List<ScannedData>>

    @Query("SELECT * FROM ScannedData")
    suspend fun getAllItemsWithoutObservers(): List<ScannedData>

    @Query("SELECT * FROM ScannedData where barcodeNumber = :barcodeNumber")
    suspend fun getScannedDataWO(barcodeNumber: String): ScannedData

    @Delete
    suspend fun deleteItem(data: ScannedData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBarcodeData(item: BarcodeEntryItem)

    @Update
    suspend fun updateBarcodeData(item: BarcodeEntryItem)

    @Delete
    suspend fun deleteBarcodeData(item: BarcodeEntryItem)

    @Query("SELECT * FROM BarcodeEntryItem")
    fun getAllBarcodeDataList(): LiveData<List<BarcodeEntryItem>>

    @Query("SELECT * FROM BarcodeEntryItem where barcodeNumber = :barcodeNumber")
    suspend fun getBarcodeDataWO(barcodeNumber: String): BarcodeEntryItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: Users)

    @Update
    suspend fun updateUser(user: Users)

    @Delete
    suspend fun deleteUser(user: Users)

    @Query("SELECT * FROM users")
    fun getAllUserList(): LiveData<List<Users>>

    @Query("DELETE FROM scanneddata")
    suspend fun deleteScannedData()

    @Query("SELECT * FROM HistoryList")
    fun getAllHistoryList(): LiveData<List<PDFData>>

    @Query("SELECT * FROM HistoryList")
    suspend fun getAllHistoryListWO(): List<PDFData>

    @Query("SELECT * FROM HistoryList where date between :toDate and :fromDate")
    suspend fun getHistoryFromDate(toDate: String, fromDate: String): List<PDFData>

    @Delete
    suspend fun deleteHistoryItem(pdfData: PDFData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHistoryItem(pdfData: PDFData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVendor(vendorModel: VendorModel)

    @Update
    suspend fun updateVendor(vendorModel: VendorModel)

    @Delete
    suspend fun deleteVendor(vendorModel: VendorModel)

    @Query("SELECT * FROM vendor")
    fun getAllVendorList(): LiveData<List<VendorModel>>

    @Query("SELECT * FROM vendor where id =:id")
    suspend fun getVendorById(id: Int): List<VendorModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSingleReport(report: SingleReportModel)

    @Update
    suspend fun updateSingleReport(report: SingleReportModel)

    @Delete
    suspend fun deleteSingleReport(report: SingleReportModel)

    @Query("SELECT * FROM SingleReportModal")
    suspend fun getAllSingleReport(): List<SingleReportModel>

    @Query("SELECT * FROM SingleReportModal where date between :toDate and :fromDate")
    suspend fun getSingleReportFromDate(toDate: String, fromDate: String): List<SingleReportModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuyerReport(buyerReportModal: BuyerReportModal)

    @Update
    suspend fun updateBuyerReport(buyerReportModal: BuyerReportModal)

    @Delete
    suspend fun deleteBuyerReport(buyerReportModal: BuyerReportModal)

    @Query("SELECT * FROM BuyerReport")
    suspend fun getAllBuyerReport(): List<BuyerReportModal>

    @Query("SELECT * FROM singlereportmodal WHERE date >= date('now', '-1 days') AND date <  date('now')")
    suspend fun getLastDayReport(): List<SingleReportModel>

    @Query("SELECT * FROM singlereportmodal WHERE date >= date('now', '-7 days') AND date <  date('now')")
    suspend fun getLastWeekReport(): List<SingleReportModel>

    @Query("SELECT * FROM singlereportmodal WHERE date >= date('now','start of month','-1 month') AND date < date('now','start of month')")
    suspend fun getLastMonthReport(): List<SingleReportModel>

    @Query("SELECT * FROM singlereportmodal WHERE strftime('%m', date) = strftime('%m', 'now')")
    suspend fun getCurrentMonthReport(): List<SingleReportModel>


}
