package com.wedoapps.barcodescanner.Ui.Scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.wedoapps.barcodescanner.Adapter.MainDataRecyclerAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.ScannedData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.Cart.CartActivity
import com.wedoapps.barcodescanner.Ui.Fragments.AddItemAlertDialog
import com.wedoapps.barcodescanner.Ui.Stock.StockActivity
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants.BARCODE
import com.wedoapps.barcodescanner.Utils.Constants.FROM
import com.wedoapps.barcodescanner.Utils.Constants.TAG
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), MainDataRecyclerAdapter.OnClick,
    AddItemAlertDialog.OnSheetWork {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }

    //    private lateinit var searchItem: MenuItem
    private val permission = listOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val quantityHashMap = hashMapOf<String, String>()
    private var scannedDataList = mutableListOf<ScannedData>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private var dataAdapter = MainDataRecyclerAdapter(listOf(), this)
    private lateinit var layout: View
    private var beepManager: BeepManager? = null
    private val mainScope = CoroutineScope(Dispatchers.Main)
    private lateinit var from: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.mainLayout
        setContentView(view)

        from = intent.getStringExtra(FROM).toString()

//        onClickRequestPermission(view)
        val toolbar = binding.toolbar.customToolbar
        setSupportActionBar(toolbar)

        dataAdapter = MainDataRecyclerAdapter(listOf(), this)

        checkPermission(100)

//        binding.toolbar.toolbarTitle.gravity = Gravity.START

        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        binding.toolbar.ivAddUsers.setOnClickListener {
            val intent = Intent(this, StockActivity::class.java)
            intent.putExtra(FROM, "main")
            startActivity(intent)
        }



        beepManager = BeepManager(this)

        val viewIncluded = binding.includeContent

        addData()

        viewModel.barcodeDataMutableLiveData.observe(this) {
            Log.d(TAG, "barcodeScan: $it")
            if (it?.id == null && it?.count == null && it?.itemName.equals("") && it?.sellingPrice == null) {
                Log.d(TAG, "barcodeScan IF: $it")
                val addItemDialog = AddItemAlertDialog()
                val bundle = Bundle()
                bundle.putString(BARCODE, it?.barcodeNumber)
                bundle.putString(FROM, from)
                addItemDialog.arguments = bundle
                addItemDialog.show(supportFragmentManager, addItemDialog.tag)
                binding.scannerData.pause()
            } else if (it?.id != null && it.count != null && it.itemName != "" && it.sellingPrice != null) {
                Log.d(TAG, "barcodeScan ELSE: $it")
                binding.scannerData.resume()
                mainScope.launch {
                    delay(2000)
                    barcodeScan()
                }
            }
        }

        viewModel.scannedDataInsertAndUpdateResponseLiveData.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(viewIncluded.modalBottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.halfExpandedRatio = 0.7f
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.scannerData.pause()

                } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    binding.scannerData.resume()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        viewIncluded.btnCheckout.setOnClickListener {
            if (quantityHashMap.isNotEmpty()) {
                alertDialog("Minimum Quantity Exceeding For Items", 0)
            } else {
                startActivity(Intent(this, CartActivity::class.java))
                finish()
            }
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        val drawable = searchItem.icon
        if (drawable != null) {
            drawable.setColorFilter(
                ContextCompat.getColor(this, R.color.ColorPrimaryTextColor),
                PorterDuff.Mode.SRC_IN
            )
            searchItem.icon = drawable
        }
        val searchView: SearchView =
            searchItem.actionView as SearchView
        searchView.isIconified = false
        searchView.requestFocus()
        searchView.queryHint = "Search Item"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!searchView.isIconified) {
                    searchView.isIconified = true
                }
                searchItem.collapseActionView()
                filter(query)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                filter(s)
                return false
            }
        })
        return true
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<ScannedData> = ArrayList()
        for (item in scannedDataList) {
            if (item.item?.lowercase(Locale.getDefault())!!
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(item)
            }
        }
        dataAdapter.updateData(filteredList)
    }*/

    private fun addData() {
        viewModel.getScannedDataList().observe(this) {
            scannedDataList = it as MutableList<ScannedData>
            dataAdapter.updateData(it)
            binding.includeContent.recyclerView.apply {
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                adapter = dataAdapter
            }
            Log.d(TAG, "addData: $it")
        }
    }

    private fun getDeleteItemDialog(data: ScannedData) {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure you want to Delete this ${data.item} ?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            viewModel.deleteScannedData(data)
            dialog.cancel()
        }
        builder1.setNegativeButton(
            "No"
        ) { dialog, _ ->
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }
        val alert11 = builder1.create()
        alert11.show()
    }

    override fun onDeleteClick(data: ScannedData) {
        getDeleteItemDialog(data)
    }


    override fun onSubCount(data: ScannedData) {
        viewModel.updateScannedData(
            id = data.id!!,
            barcodeNumber = data.barcodeNumber.toString(),
            itemCode = data.itemCode.toString(),
            item = data.item.toString(),
            price = data.price!!,
            data.originalPrice!!,
            data.storeQuantity!!,
            data.minCount!!,
            count = data.count!!,
            showDialog = data.showDialog,
        )

        dataAdapter.updateData(scannedDataList)

        if (data.count == 0) {
            viewModel.deleteScannedData(data)
        }

    }

    override fun onAddCount(data: ScannedData) {
        val least = data.minCount?.let { data.storeQuantity?.minus(it) }
        if (data.count!! > least!!) {
            quantityHashMap[data.item.toString()] = least.toString()
        }

        if (data.count!! >= data.storeQuantity!!) {
            alertDialog("${data.item} Quantity Exceeded!!", 1)
        }

        viewModel.updateScannedData(
            id = data.id!!,
            barcodeNumber = data.barcodeNumber.toString(),
            itemCode = data.itemCode.toString(),
            item = data.item.toString(),
            price = data.price!!,
            data.originalPrice!!,
            data.storeQuantity!!,
            data.minCount!!,
            count = data.count!!,
            showDialog = data.showDialog
        )
        dataAdapter.updateData(scannedDataList)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.scannerData.resume()
        barcodeScan()
        addData()
        viewModel.getScannedDataList().observe(this) {
            var price = 0
            if (it.isNullOrEmpty()) {
                binding.includeContent.btnCheckout.isClickable = false
                binding.includeContent.btnCheckout.text =
                    "Check Out With ${getString(R.string.Rs)} $price"
            } else {
                it.forEach { item ->
                    price += item.price!!
                    binding.includeContent.btnCheckout.isClickable = true
                    binding.includeContent.btnCheckout.text =
                        "Check Out With ${getString(R.string.Rs)} $price"
                    if (item.storeQuantity!! <= 0) {
                        viewModel.deleteScannedData(item)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.scannerData.pause()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun barcodeScan() {
        val formats: Collection<BarcodeFormat> =
            mutableListOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_128)
        binding.scannerData.decoderFactory = DefaultDecoderFactory(formats)
        binding.scannerData.initializeFromIntent(Intent())
        binding.scannerData.isSoundEffectsEnabled = true
        binding.scannerData.decodeSingle { result ->
            Log.d("TAG", "Barcode ScannedData: ${result.text}")
            viewModel.getSingleBarcodeData(barcodeNumber = result.text)
            beepManager?.playBeepSoundAndVibrate()
        }
    }


    override fun onSheetClose(inserted: Boolean) {
        binding.scannerData.resume()
        barcodeScan()
        if (inserted) {
            dataAdapter.updateData(scannedDataList)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.deleteScannedData()
    }

    private fun alertDialog(message: String, flag: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setTitle("Alert")
        builder.setCancelable(true)
        builder.setPositiveButton(
            "OK"
        ) { dialog, _ ->
            if (flag == 1) {
                dialog.dismiss()
            } else {
                startActivity(Intent(this, CartActivity::class.java))
            }
        }
        val dialog = builder.create()
        dialog.show()
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
            Log.i(TAG, "Permission: Granted")
        } else {
            Log.i(TAG, "Permission: Denied")
        }
    }

}