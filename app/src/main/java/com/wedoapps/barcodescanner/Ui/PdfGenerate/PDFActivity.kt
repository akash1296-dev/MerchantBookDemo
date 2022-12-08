package com.wedoapps.barcodescanner.Ui.PdfGenerate

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity
import com.tejpratapsingh.pdfcreator.activity.PDFViewerActivity
import com.tejpratapsingh.pdfcreator.utils.PDFUtil
import com.tejpratapsingh.pdfcreator.views.PDFBody
import com.tejpratapsingh.pdfcreator.views.PDFFooterView
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView
import com.tejpratapsingh.pdfcreator.views.PDFTableView
import com.tejpratapsingh.pdfcreator.views.PDFTableView.PDFTableRowView
import com.tejpratapsingh.pdfcreator.views.basic.PDFHorizontalView
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.PDFData
import com.wedoapps.barcodescanner.Model.ScannedData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.Scanner.MainActivity
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants.IS_NEW
import com.wedoapps.barcodescanner.Utils.Constants.PDF_DATA
import com.wedoapps.barcodescanner.Utils.Constants.REQUEST_CODE
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import java.io.File
import java.util.*

class PDFActivity : PDFCreatorActivity() {

    private var pdfData: PDFData? = PDFData()
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }
    private var isNew = true
    private var screen = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        pdfData = intent.getParcelableExtra(PDF_DATA)
        isNew = intent.getBooleanExtra(IS_NEW, true)
        screen = intent.getStringExtra("Screen").toString()


        createPDF(pdfData?.name + pdfData?.date + pdfData?.time, object : PDFUtil.PDFUtilListener {
            override fun pdfGenerationSuccess(savedPDFFile: File?) {
            }

            override fun pdfGenerationFailure(exception: Exception?) {
                Toast.makeText(this@PDFActivity, exception?.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun getHeaderView(forPage: Int): PDFHeaderView {
        val headerView = PDFHeaderView(applicationContext)

        val pdfHorizontalView = PDFHorizontalView(applicationContext)

        val pdfHeadingTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.HEADER)
        val headingString = SpannableString("INVOICE")
        headingString.setSpan(
            ForegroundColorSpan(Color.DKGRAY),
            0,
            headingString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        pdfHeadingTextView.text = headingString
        pdfHeadingTextView.setLayout(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1F
            )
        )
        pdfHeadingTextView.view.gravity = Gravity.CENTER_HORIZONTAL
        pdfHeadingTextView.view.setTypeface(pdfHeadingTextView.view.typeface, Typeface.BOLD)

        pdfHorizontalView.addView(pdfHeadingTextView)

        headerView.addView(pdfHorizontalView)

        val lineSeparatorView1 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        headerView.addView(lineSeparatorView1)

        return headerView
    }

    override fun getBodyViews(): PDFBody {
        val pdfBody = PDFBody()

        for (i in 10 until 0) {
            val seperators =
                PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
            pdfBody.addView(seperators)
        }

        val pdfHorizontalView = PDFHorizontalView(applicationContext)

        val pdfCompanyNameView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfCompanyNameView.setText("Name: ${pdfData?.name}")
        pdfCompanyNameView.view.gravity = Gravity.START
        pdfCompanyNameView.setLayout(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1F
            )
        )
        pdfHorizontalView.addView(pdfCompanyNameView)

        val pdfDateAndTime = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfDateAndTime.setText("Date: ${pdfData?.date} ${pdfData?.time}")
        pdfDateAndTime.view.gravity = Gravity.END
        pdfDateAndTime.setLayout(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1F
            )
        )
        pdfHorizontalView.addView(pdfDateAndTime)
        pdfBody.addView(pdfHorizontalView)

        val lineSeparatorView =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        pdfBody.addView(lineSeparatorView)

        val lineSeparatorView1 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        pdfBody.addView(lineSeparatorView1)

        val lineSeparatorView2 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        pdfBody.addView(lineSeparatorView2)

        val tableHeaderView = PDFTableRowView(applicationContext)
        val freeView = PDFTableRowView(applicationContext)
        for (i in 0..4) {
            val pdfHeaderTitle = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            when (i) {
                0 -> {
                    pdfHeaderTitle.setText("S.No")
                }
                1 -> {
                    pdfHeaderTitle.setText("Item Name")
                }
                2 -> {
                    pdfHeaderTitle.setText("Price")
                    pdfHeaderTitle.view.gravity = Gravity.CENTER
                }
                3 -> {
                    pdfHeaderTitle.setText("Quantity")
                    pdfHeaderTitle.view.gravity = Gravity.CENTER
                }
                4 -> {
                    pdfHeaderTitle.setText("Total")
                    pdfHeaderTitle.view.gravity = Gravity.END
                }
            }
            tableHeaderView.addToRow(pdfHeaderTitle)
        }

        val tableView = PDFTableView(applicationContext, tableHeaderView, freeView)

        for (s in pdfData?.cartList!!.indices) {
            val tableRowView = PDFTableRowView(applicationContext)
            for (i in 0..4) {
                val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                when (i) {
                    0 -> {
                        pdfTextView.setText((s + 1).toString())
                    }
                    1 -> {
                        pdfTextView.setText(pdfData?.cartList!![s].item)
                    }
                    2 -> {
                        pdfTextView.setText("${getString(R.string.Rs)}${pdfData?.cartList!![s].originalPrice.toString()}")
                        pdfTextView.view.gravity = Gravity.CENTER
                    }
                    3 -> {
                        pdfTextView.setText(pdfData?.cartList!![s].count.toString())
                        pdfTextView.view.gravity = Gravity.CENTER
                    }
                    4 -> {
                        pdfTextView.setText("${getString(R.string.Rs)}${pdfData?.cartList!![s].price.toString()}")
                        pdfTextView.view.gravity = Gravity.END
                    }
                }
                tableRowView.addToRow(pdfTextView)
            }
            tableView.addRow(tableRowView)
        }

        val lineSeperator = PDFLineSeparatorView(applicationContext)
        lineSeperator.setBackgroundColor(Color.DKGRAY)
        pdfBody.addView(lineSeperator)

        tableView.setColumnWidth(20, 20, 20, 20, 20)
        pdfBody.addView(tableView)

        val lineSeperator1 = PDFLineSeparatorView(applicationContext)
        lineSeperator1.setBackgroundColor(Color.DKGRAY)
        pdfBody.addView(lineSeperator1)

        val lineSeperator2 = PDFLineSeparatorView(applicationContext)
        pdfBody.addView(lineSeperator2)

        val lineSeperator3 = PDFLineSeparatorView(applicationContext)
        pdfBody.addView(lineSeperator3)

        val pdfGrandHorizontalView = PDFHorizontalView(applicationContext)

        val pdfGrandTotalTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfGrandTotalTextView.setText("GrandTotal")
        pdfGrandTotalTextView.view.gravity = Gravity.START
        pdfGrandTotalTextView.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1F
            )
        )

        val pdfGrandTotal = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfGrandTotal.setText(getString(R.string.Rs) + pdfData!!.total.toString())
        pdfGrandTotal.view.gravity = Gravity.END
        pdfGrandTotal.setTextTypeface(Typeface.DEFAULT_BOLD)
        pdfGrandTotal.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1F
            )
        )
        pdfGrandHorizontalView.addView(pdfGrandTotalTextView)
        pdfGrandHorizontalView.addView(pdfGrandTotal)
        pdfBody.addView(pdfGrandHorizontalView)

        return pdfBody
    }

    override fun getFooterView(forPage: Int): PDFFooterView {
        val footerView = PDFFooterView(applicationContext)

        val pdfTextViewPage = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", forPage + 1))
        pdfTextViewPage.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0F
            )
        )
        pdfTextViewPage.view.gravity = Gravity.CENTER_HORIZONTAL
        footerView.addView(pdfTextViewPage)
        return footerView
    }

    override fun onNextClicked(savedPDFFile: File?) {

        if (isNew) {
            addInHistory(
                pdfData?.name.toString(),
                pdfData?.cartList,
                pdfData?.phoneNumber.toString(),
                pdfData?.total.toString(),
                pdfData?.date.toString(),
                pdfData?.time.toString()
            )
        }
        pdfData?.cartList?.forEach { data ->
            viewModel.updateAndInsertSingleReport(
                data.barcodeNumber.toString(),
                data.itemCode.toString(),
                data.item.toString(),
                data.originalPrice!!,
                data.count!!,
                data.price!!
            )
        }
        val pdfUri: Uri = Uri.fromFile(savedPDFFile)

        val intentPdfViewer = Intent(
            this,
            PdfViewerActivity::class.java
        )
        intentPdfViewer.putExtra(PDF_DATA, pdfData)
        intentPdfViewer.putExtra(PDFViewerActivity.PDF_FILE_URI, pdfUri)
        startActivityForResult(intentPdfViewer, REQUEST_CODE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (screen == "SingleHistory") {
                onBackPressed()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                setResult(REQUEST_CODE, intent)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun addInHistory(
        name: String,
        itemList: ArrayList<ScannedData>?,
        phoneNumber: String,
        total: String,
        date: String,
        time: String
    ) {
        viewModel.addHistoryItem(
            name, itemList, phoneNumber, total, date, time
        )

        viewModel.insertAndUpdateBuyerReport(
            name,
            itemList,
            phoneNumber,
            total
        )
    }

}