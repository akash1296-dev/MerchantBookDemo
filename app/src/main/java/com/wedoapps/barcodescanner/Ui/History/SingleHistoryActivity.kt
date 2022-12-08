package com.wedoapps.barcodescanner.Ui.History

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.wedoapps.barcodescanner.Adapter.DataRecyclerAdapter
import com.wedoapps.barcodescanner.Adapter.PDFFooterAdapter
import com.wedoapps.barcodescanner.Adapter.PDFHeaderAdapter
import com.wedoapps.barcodescanner.Model.PDFData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.PdfGenerate.PDFActivity
import com.wedoapps.barcodescanner.Utils.Constants.HISTORY_DATA
import com.wedoapps.barcodescanner.Utils.Constants.IS_NEW
import com.wedoapps.barcodescanner.Utils.Constants.PDF_DATA
import com.wedoapps.barcodescanner.databinding.ActivitySingleHistoryBinding

class SingleHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySingleHistoryBinding
    private var pdfData: PDFData? = PDFData()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfData = intent.getParcelableExtra(HISTORY_DATA)

        val toolbar = binding.toolbar.customToolbar
        setSupportActionBar(toolbar)

        binding.toolbar.ivAddUsers.setImageResource(R.drawable.ic_share)

        binding.toolbar.apply {
            ivBack.visibility = View.VISIBLE
        }

        binding.tvShName.text = "Name: ${pdfData?.name}"
        binding.tvShDate.text = "Date: ${pdfData?.date}"

        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        binding.toolbar.ivAddUsers.setOnClickListener {
            val pdf = Intent(this, PDFActivity::class.java)
            pdf.putExtra(IS_NEW, false)
            pdf.putExtra("Screen", "SingleHistory")
            pdf.putExtra(PDF_DATA, pdfData)
            startActivity(pdf)
            finish()
        }

        if (pdfData?.cartList.isNullOrEmpty()) {
            showEmpty()
        } else {
            hideEmpty()
            binding.rvHistoryItems.apply {
                setHasFixedSize(true)
                val headerAdapter = PDFHeaderAdapter()
                val itemAdapter = DataRecyclerAdapter(pdfData?.cartList)
                val footerAdapter = PDFFooterAdapter(pdfData?.total.toString())
                val concatAdapter = ConcatAdapter(headerAdapter, itemAdapter, footerAdapter)
                adapter = concatAdapter
            }
        }

    }

    private fun hideEmpty() {
        binding.apply {
            ivNoData.visibility = View.GONE
            rvHistoryItems.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        binding.apply {
            ivNoData.visibility = View.VISIBLE
            rvHistoryItems.visibility = View.GONE
        }
    }


}