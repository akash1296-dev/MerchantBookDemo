package com.wedoapps.barcodescanner.Ui.Choice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.Report.ReportActivity
import com.wedoapps.barcodescanner.Ui.Scanner.MainActivity
import com.wedoapps.barcodescanner.Ui.Stock.StockActivity
import com.wedoapps.barcodescanner.Ui.Vendor.VendorActivity
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

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
        /*installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.isLoading.value }
        }*/
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPermission(100)

        binding.bottomBar.itemIconTintList = null
        handleClickListener()
        bottomNavigationListener()
    }

    private fun bottomNavigationListener() {
        binding.bottomBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {

                }

                R.id.action_supplier -> {
                    startActivity(Intent(this, VendorActivity::class.java))
                }

                R.id.action_new_bill -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(Constants.FROM, Constants.DIRECT_MAIN)
                    startActivity(intent)
                }

                R.id.action_stock -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(Constants.FROM, Constants.DIRECT_MAIN)
                    startActivity(intent)
                }

                R.id.action_sales -> {
                    startActivity(Intent(this, ReportActivity::class.java))
                }

            }
            true
        }
    }

    private fun handleClickListener() {
        binding.ivAddItem.setOnClickListener {
            val intent = Intent(this, StockActivity::class.java)
            intent.putExtra(Constants.FROM, "choice")
            startActivity(intent)
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

    /*override fun onClick(model: ChoiceModel) {
        when (model.menuItem) {
            getString(R.string.generate_bill) -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra(Constants.FROM, Constants.DIRECT_MAIN)
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
    }*/

}