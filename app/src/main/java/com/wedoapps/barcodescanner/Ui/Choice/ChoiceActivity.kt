package com.wedoapps.barcodescanner.Ui.Choice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wedoapps.barcodescanner.Adapter.ChoiceAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.ChoiceModel
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.History.HistoryActivity
import com.wedoapps.barcodescanner.Ui.Report.ReportActivity
import com.wedoapps.barcodescanner.Ui.Scanner.MainActivity
import com.wedoapps.barcodescanner.Ui.Stock.StockActivity
import com.wedoapps.barcodescanner.Ui.Users.UserListActivity
import com.wedoapps.barcodescanner.Ui.Vendor.VendorActivity
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants
import com.wedoapps.barcodescanner.Utils.Constants.DIRECT_MAIN
import com.wedoapps.barcodescanner.Utils.Constants.FROM
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivityChoiceBinding
import com.wedoapps.barcodescanner.databinding.ActivityHomeBinding

class ChoiceActivity : AppCompatActivity(), ChoiceAdapter.OnChoiceClick {

    private lateinit var binding: ActivityChoiceBinding

    private val permission = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isLoading.value }
        }
        super.onCreate(savedInstanceState)
        binding = ActivityChoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar.customToolbar
        setSupportActionBar(toolbar)

        checkPermission(100)

        binding.toolbar.ivBack.visibility = View.GONE
        binding.toolbar.ivAddUsers.visibility = View.GONE

        val choiceAdapter = ChoiceAdapter(this)

        val choiceData = mutableListOf(
            ChoiceModel(
                1,
                getString(R.string.generate_bill),
                ContextCompat.getDrawable(this, R.drawable.generate_bill)
            ),
            ChoiceModel(
                2,
                getString(R.string.add_user_info),
                ContextCompat.getDrawable(this, R.drawable.add_user)
            ),
            ChoiceModel(
                3,
                getString(R.string.add_update_stock),
                ContextCompat.getDrawable(this, R.drawable.add_stock)
            ),
            ChoiceModel(
                4,
                getString(R.string.vendor),
                ContextCompat.getDrawable(this, R.drawable.vendor)
            ),
            ChoiceModel(
                5,
                getString(R.string.billing_history),
                ContextCompat.getDrawable(this, R.drawable.history)
            ),
            ChoiceModel(
                6,
                getString(R.string.report),
                ContextCompat.getDrawable(this, R.drawable.report)
            ),
        )

        choiceAdapter.differ.submitList(choiceData)

        binding.rvChoice.apply {
            setHasFixedSize(true)
            adapter = choiceAdapter
        }
    }

    private fun checkPermission(requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission[0]
            ) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(
                this,
                permission[1]
            ) == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(
                this,
                permission[2]
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(
                this,
                permission.toTypedArray(), requestCode
            )
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i(Constants.TAG, "Permission: Granted")
        } else {
            Log.i(Constants.TAG, "Permission: Denied")
        }
    }

    override fun onClick(model: ChoiceModel) {
        when (model.menuItem) {
            getString(R.string.generate_bill) -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(FROM, DIRECT_MAIN)
                startActivity(intent)
            }

            getString(R.string.add_user_info) -> {
                startActivity(Intent(this, UserListActivity::class.java))
            }

            getString(R.string.add_update_stock) -> {
                val intent = Intent(this, StockActivity::class.java)
                intent.putExtra(Constants.FROM, "choice")
                startActivity(intent)
            }

            getString(R.string.billing_history) -> {
                startActivity(Intent(this, HistoryActivity::class.java))
            }

            getString(R.string.vendor) -> {
                startActivity(Intent(this, VendorActivity::class.java))
            }

            getString(R.string.report) -> {
                startActivity(Intent(this, ReportActivity::class.java))
            }
        }
    }
}