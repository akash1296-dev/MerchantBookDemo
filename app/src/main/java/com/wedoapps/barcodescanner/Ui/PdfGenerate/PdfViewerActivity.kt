package com.wedoapps.barcodescanner.Ui.PdfGenerate

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.tejpratapsingh.pdfcreator.activity.PDFViewerActivity
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.PDFData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants
import com.wedoapps.barcodescanner.Utils.Constants.REQUEST_CODE
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLConnection

class PdfViewerActivity : PDFViewerActivity() {
    private var createInvoiceActivityResultLauncher: ActivityResultLauncher<Intent>? = null
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }
    private var pdfData: PDFData? = PDFData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Pdf Viewer"
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary))
        )

        pdfData = intent.getParcelableExtra(Constants.PDF_DATA)

        createInvoiceActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val uri: Uri?
                if (result.data != null) {
                    uri = result.data!!.data
                    Log.d("BarcodeScanner", "onCreate: $uri")
                    createInvoice(uri)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_pdf_viewer, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.download -> {
                val fileName = "${pdfData?.name}_${pdfData?.date}_${pdfData?.time}"
                createFile(fileName)

            }
            R.id.share -> {
                val fileToShare: File? = pdfFile
                if (fileToShare == null || !fileToShare.exists()) {
                    Toast.makeText(this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show()
                    return super.onOptionsItemSelected(item)
                }
                val intentShareFile = Intent(Intent.ACTION_SEND)
                val apkURI: Uri = FileProvider.getUriForFile(
                    applicationContext,
                    applicationContext
                        .packageName + ".provider", fileToShare
                )
                Log.d("BarcodeScanner", "onOptionsItemSelected: $apkURI")
                intentShareFile.setDataAndType(
                    apkURI,
                    URLConnection.guessContentTypeFromName(fileToShare.name)
                )
                intentShareFile.setPackage("com.whatsapp")
                intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intentShareFile.putExtra(
                    Intent.EXTRA_STREAM,
                    apkURI
                )
                try {
                    startActivity(intentShareFile)
                    deleteScannedData()
                    createInvoiceActivityResultLauncher?.launch(intentShareFile)
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "WhatsApp Not Found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createFile(title: String) {
        val apkURI = Uri.fromFile(pdfFile)
        Log.d("TAG", "createFile: $apkURI")
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_TITLE, title)
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, apkURI)
        createInvoiceActivityResultLauncher?.launch(intent)
        viewModel.deleteScannedData()
    }

    private fun createInvoice(uri: Uri?) {
        try {
            val pfd = uri?.let { contentResolver.openFileDescriptor(it, "w") }
            if (pfd != null) {
                val fileOutputStream = FileOutputStream(pfd.fileDescriptor)
                fileOutputStream.write(pdfFile.readBytes())
                fileOutputStream.close()
                pfd.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteScannedData() {
        viewModel.deleteScannedData()
        val intent = Intent()
        setResult(REQUEST_CODE, intent)
        finish()
    }
}

