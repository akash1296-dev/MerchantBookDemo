package com.wedoapps.barcodescanner.Ui.Report.SingleReport

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.wedoapps.barcodescanner.Adapter.ReportSingleItemAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.SingleReportModel
import com.wedoapps.barcodescanner.Ui.Report.SingleReport.BottomSheets.FilterBottomSheet
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants.ALL
import com.wedoapps.barcodescanner.Utils.Constants.DATE_RANGE
import com.wedoapps.barcodescanner.Utils.Constants.FILTER_TYPE
import com.wedoapps.barcodescanner.Utils.Constants.LAST_MONTH
import com.wedoapps.barcodescanner.Utils.Constants.LAST_WEEK
import com.wedoapps.barcodescanner.Utils.Constants.TAG
import com.wedoapps.barcodescanner.Utils.Constants.THIS_DAY
import com.wedoapps.barcodescanner.Utils.Constants.THIS_MONTH
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivitySingleReportBinding
import java.text.SimpleDateFormat
import java.util.*

class SingleReportActivity : AppCompatActivity(), FilterBottomSheet.OnFilterOptionSelected {

    private lateinit var binding: ActivitySingleReportBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }

    @SuppressLint("SimpleDateFormat")
    var dfDate = SimpleDateFormat("dd/MM/yyyy")
    private var dfDateForDB = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var adapterSingleItem: ReportSingleItemAdapter
    private var itemList = arrayListOf<SingleReportModel>()
    private lateinit var datePickerStart: MaterialDatePicker<Long>
    private lateinit var datePickerTO: MaterialDatePicker<Long>
    private var fromDate: String? = null ?: ""
    private var toDate: String? = null ?: ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar.customToolbar
        setSupportActionBar(toolbar)

        binding.toolbar.apply {
            ivSearch.visibility = View.GONE
            ivSetting.visibility = View.GONE
            ivInsertItem.visibility = View.GONE
            ivBack.visibility = View.VISIBLE
            ivUserAdd.visibility = View.GONE
        }

        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        val calendar = Calendar.getInstance().timeInMillis

        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
        datePickerStart =
            MaterialDatePicker.Builder.datePicker().setTitleText("Start Date")
                .setSelection(calendar).build()
        datePickerTO =
            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(calendar)
                .setTitleText("End Date").build()

        binding.tvStartDate.setOnClickListener {
            datePickerStart()
        }

        binding.tvEndDate.setOnClickListener {
            datePickerTO.show(supportFragmentManager, datePickerTO.tag)
            datePickerTO.addOnPositiveButtonClickListener {
                toDate = dfDateForDB.format(Date(it))
                val date = dfDate .format(Date(it))
                binding.tvEndDate.text = date
                Log.d(TAG, "EndDate: $date")
                sendDataRequest(DATE_RANGE)
                datePickerTO.dismiss()
            }
        }

        fromDate = dfDateForDB.format(System.currentTimeMillis())
        toDate = dfDateForDB.format(System.currentTimeMillis())

        binding.tvStartDate.text = dfDate.format(System.currentTimeMillis())
        binding.tvEndDate.text = dfDate.format(System.currentTimeMillis())

        binding.btnSearch.setOnClickListener {
            sendDataRequest(DATE_RANGE)
        }
        loadData()

        binding.tvSearch.setOnClickListener {
            showSearch()
        }

        binding.btnClose.setOnClickListener {
            hideSearch()
        }

        binding.tvFilter.setOnClickListener {
            val filter = FilterBottomSheet()
            val bundle = Bundle()
            bundle.putString(FILTER_TYPE, binding.tvFilter.text.toString())
            filter.arguments = bundle
            filter.show(supportFragmentManager, filter.tag)
        }

        binding.edtSearch.addTextChangedListener {
            filter(it.toString())
        }

    }

    private fun hideSearch() {
        binding.apply {
            frameSearch.visibility = View.GONE
            tvSearch.visibility = View.VISIBLE
            tvFilter.visibility = View.VISIBLE
        }
    }


    private fun hideEmpty() {
        binding.apply {
            ivNoData.visibility = View.GONE
            rvSingleItem.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        binding.apply {
            ivNoData.visibility = View.VISIBLE
            rvSingleItem.visibility = View.GONE
        }
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<SingleReportModel> = ArrayList()
        for (item in itemList) {
            if (item.itemName?.lowercase(Locale.getDefault())!!
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                hideEmpty()
                filteredList.add(item)
            } else {
                showEmpty()
            }
        }
        adapterSingleItem.filterList(filteredList)
    }

    private fun loadData() {
        adapterSingleItem = ReportSingleItemAdapter(arrayListOf())
        viewModel.singleReportItemLiveData.observe(this) {
            Log.d(TAG, "loadData: $it")
            if (it.isNullOrEmpty()) {
                showEmpty()
            } else {
                hideEmpty()
                itemList =
                    it as ArrayList<SingleReportModel>
                adapterSingleItem.filterList(itemList)
                binding.rvSingleItem.apply {
                    setHasFixedSize(true)
                    adapter = adapterSingleItem
                }
                Log.d(TAG, "loadData: $it")
            }
        }

    }

    private fun showSearch() {
        binding.apply {
            tvSearch.visibility = View.GONE
            tvFilter.visibility = View.GONE
            frameSearch.visibility = View.VISIBLE
            edtSearch.requestFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        sendDataRequest(ALL)
    }

    private fun sendDataRequest(type: String) {
        viewModel.singleReportItem(
            type,
            fromDate.toString(),
            toDate.toString()
        )

        Log.d(TAG, "sendDataRequest: $type, $fromDate, $toDate")
    }

    private fun datePickerStart() {
        datePickerStart.show(supportFragmentManager, datePickerStart.tag)
        datePickerStart.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy")
            val dateFormatterForDB = SimpleDateFormat("yyyy-MM-dd")
            fromDate = dateFormatterForDB.format(Date(it))
            val date = dateFormatter.format(Date(it))
            binding.tvStartDate.text = date
            Log.d(TAG, "StartDate: $date")
            sendDataRequest(DATE_RANGE)
        }
    }

    override fun onSelect(type: String) {
        when (type) {
            ALL -> {
                sendDataRequest(ALL)
                binding.tvFilter.text = ALL
            }
            THIS_MONTH -> {
                sendDataRequest(THIS_MONTH)
                binding.tvFilter.text = THIS_MONTH
            }
            THIS_DAY -> {
                sendDataRequest(THIS_DAY)
                binding.tvFilter.text = THIS_DAY
            }
            LAST_WEEK -> {
                sendDataRequest(LAST_WEEK)
                binding.tvFilter.text = LAST_WEEK
            }
            LAST_MONTH -> {
                sendDataRequest(LAST_MONTH)
                binding.tvFilter.text = LAST_MONTH
            }
            DATE_RANGE -> {
                datePickerStart()
                binding.tvFilter.text = DATE_RANGE
            }
        }
    }
}