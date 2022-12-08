package com.wedoapps.barcodescanner.Ui.Report

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wedoapps.barcodescanner.Adapter.ChoiceAdapter
import com.wedoapps.barcodescanner.Model.ChoiceModel
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.Report.BuyerReport.BuyerReportActivity
import com.wedoapps.barcodescanner.Ui.Report.SingleReport.SingleReportActivity
import com.wedoapps.barcodescanner.databinding.ActivityReportBinding

class ReportActivity : AppCompatActivity(), ChoiceAdapter.OnChoiceClick {

    private lateinit var binding: ActivityReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
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

        val choiceAdapter = ChoiceAdapter(this)

        val choiceData = mutableListOf(
            ChoiceModel(
                1,
                getString(R.string.vendor_report),
                ContextCompat.getDrawable(this, R.drawable.generate_bill)
            ),
            ChoiceModel(
                2,
                getString(R.string.single_report),
                ContextCompat.getDrawable(this, R.drawable.add_user)
            ),
        )

        choiceAdapter.differ.submitList(choiceData)

        binding.rvReport.apply {
            setHasFixedSize(true)
            adapter = choiceAdapter
        }

    }

    override fun onClick(model: ChoiceModel) {
        when (model.id) {
            1 -> {
                startActivity(Intent(this, BuyerReportActivity::class.java))
            }

            2 -> {
                startActivity(Intent(this, SingleReportActivity::class.java))
            }
        }
    }
}