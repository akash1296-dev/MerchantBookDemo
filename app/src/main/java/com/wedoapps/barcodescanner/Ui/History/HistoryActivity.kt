package com.wedoapps.barcodescanner.Ui.History

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wedoapps.barcodescanner.Adapter.HistoryAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.PDFData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants.HISTORY_DATA
import com.wedoapps.barcodescanner.Utils.Constants.TAG
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivityHistoryBinding
import java.text.SimpleDateFormat
import java.util.*


// Here in this the textView for (to and from) are opposite in XML..
class HistoryActivity : AppCompatActivity(), HistoryAdapter.OnHistoryItemClick {

    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }

    @SuppressLint("SimpleDateFormat")
    var dfDate = SimpleDateFormat("dd/MM/yyyy")

    private lateinit var datePickerTO: DatePickerDialog
    private lateinit var datePickerFROM: DatePickerDialog

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar.customToolbar
        setSupportActionBar(toolbar)

        binding.toolbar.apply {
            ivBack.visibility = View.VISIBLE
            ivAddUsers.visibility = View.GONE
        }

        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val okColor = ContextCompat.getColor(this, R.color.gd_center)
        val cancelColor = ContextCompat.getColor(this, R.color.gd_center)
        binding.tvToDate.text = dfDate.format(System.currentTimeMillis())
        binding.tvFromDate.text = dfDate.format(System.currentTimeMillis())

        datePickerFROM =
            DatePickerDialog(
                this,
                R.style.DialogTheme,
                { _, yearDP, monthOfYear, dayOfMonth ->
                    val fmonth = monthOfYear + 1
                    var fm = "" + fmonth
                    var fd = "" + dayOfMonth
                    if (fmonth < 10) {
                        fm = "0$fmonth"
                    }
                    if (dayOfMonth < 10) {
                        fd = "0$dayOfMonth"
                    }
                    binding.tvFromDate.text = "$fd/$fm/$yearDP"
                },
                year,
                month,
                day
            )

        datePickerTO =
            DatePickerDialog(
                this,
                R.style.DialogTheme,
                { _, yearDP, monthOfYear, dayOfMonth ->
                    val fmonth = monthOfYear + 1
                    var fm = "" + fmonth
                    var fd = "" + dayOfMonth
                    if (fmonth < 10) {
                        fm = "0$fmonth"
                    }
                    if (dayOfMonth < 10) {
                        fd = "0$dayOfMonth"
                    }
                    val toDate = "$fd/$fm/$yearDP"
                    binding.tvToDate.text = toDate
                },
                year,
                month,
                day
            )
        datePickerFROM.datePicker.minDate = calendar.timeInMillis

        binding.tvToDate.setOnClickListener {
            datePickerTO.show()
            datePickerTO.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(okColor)
            datePickerTO.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(cancelColor)
        }

        binding.tvFromDate.setOnClickListener {
            datePickerFROM.show()
            datePickerFROM.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(okColor)
            datePickerFROM.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(cancelColor)
        }

        binding.btnSearch.setOnClickListener {
            sendDataRequest()
        }

        loadData()

    }

    override fun onClick(data: PDFData) {
        val intent = Intent(this, SingleHistoryActivity::class.java)
        intent.putExtra(HISTORY_DATA, data)
        startActivity(intent)

    }

    private fun hideEmpty() {
        binding.apply {
            ivNoData.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
            btnShareTotal.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        binding.apply {
            ivNoData.visibility = View.VISIBLE
            rvHistory.visibility = View.GONE
            btnShareTotal.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        sendDataRequest()
    }

    private fun loadData() {
        val adapterHistory = HistoryAdapter(this)
        viewModel.historyDataLiveData.observe(this) {
            Log.d(TAG, "loadData: $it")
            if (it.isNullOrEmpty()) {
                showEmpty()
            } else {
                hideEmpty()
                adapterHistory.differ.submitList(it)
                binding.rvHistory.apply {
                    setHasFixedSize(true)
                    adapter = adapterHistory
                }
            }
        }
    }

    private fun sendDataRequest() {
        viewModel.historyDateWise(
            binding.tvToDate.text as String,
            binding.tvFromDate.text as String
        )
    }
}