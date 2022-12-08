package com.wedoapps.barcodescanner.Ui.Cart

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.wedoapps.barcodescanner.Adapter.DataRecyclerAdapter
import com.wedoapps.barcodescanner.Adapter.PDFFooterAdapter
import com.wedoapps.barcodescanner.Adapter.PDFHeaderAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.BarcodeEntryItem
import com.wedoapps.barcodescanner.Model.PDFData
import com.wedoapps.barcodescanner.Model.ScannedData
import com.wedoapps.barcodescanner.Model.Users
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.Fragments.AddUserFragment
import com.wedoapps.barcodescanner.Ui.PdfGenerate.PDFActivity
import com.wedoapps.barcodescanner.Ui.Scanner.MainActivity
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants.IS_NEW
import com.wedoapps.barcodescanner.Utils.Constants.PDF_DATA
import com.wedoapps.barcodescanner.Utils.SharedPreferenceManager
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.Utils.searchablespinner.interfaces.IStatusListener
import com.wedoapps.barcodescanner.Utils.searchablespinner.interfaces.OnItemSelectedListener
import com.wedoapps.barcodescanner.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity(), DataRecyclerAdapter.OnClick, AddUserFragment.OnWorkDone {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }
    private var total = 0
    private val userList = arrayListOf<Users>()
    private lateinit var prefs: SharedPreferenceManager
    private var userAdapter: ArrayAdapter<String>? = null
    private var isSpinnerOpen = false
    private var barcodeNumber: String? = ""
    private var name: String? = null
    private var partyNameUpdate: String? = ""
    private var dataList = mutableListOf<ScannedData>()
    private var barcodeList = mutableListOf<BarcodeEntryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar.customToolbar
        setSupportActionBar(toolbar)

        prefs = SharedPreferenceManager(this)

        setupSpinnerItem()

        binding.toolbar.apply {
            ivBack.visibility = View.VISIBLE
            ivAddUsers.visibility = View.GONE
        }

        binding.toolbar.ivBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.toolbar.ivAddUsers.setImageResource(R.drawable.ic_user_add)

        binding.toolbar.ivAddUsers.setOnClickListener {
            openUserSheet()
        }

        binding.spinnerUsername.setOnClickListener {
            openUserSheet()
        }

        binding.btnCheckoutTotal.setOnClickListener {
            if (partyNameUpdate.isNullOrEmpty()) {
                Toast.makeText(this, "Please Select a User", Toast.LENGTH_SHORT).show()
            } else {
                handlePDFShare()
            }
        }
    }

    override fun onDeleteClick(data: ScannedData, position: Int) {
        viewModel.deleteScannedData(data)
    }

    override fun onSubCount(data: ScannedData) {
    }

    override fun onAddCount(data: ScannedData) {
    }

    private fun hideEmpty() {
        binding.apply {
            ivNoData.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE
            btnCheckoutTotal.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        binding.apply {
            ivNoData.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
            btnCheckoutTotal.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        viewModel.getScannedDataList().observe(this) {
            if (it.isNullOrEmpty()) {
                showEmpty()
            } else {
                dataList = it as MutableList<ScannedData>
                hideEmpty()
                it.forEach { data ->
                    barcodeNumber = data.barcodeNumber
                    name = data.item
                    total += data.price!!
                    if (data.storeQuantity!! <= 0) {
                        Toast.makeText(this, "Please Check Stock Inventory", Toast.LENGTH_SHORT)
                            .show()
                        binding.btnCheckoutTotal.isClickable = false
                    }
                }

                binding.rvCartItems.apply {
                    setHasFixedSize(true)
                    val headerAdapter = PDFHeaderAdapter()
                    val itemAdapter = DataRecyclerAdapter(it)
                    val footerAdapter = PDFFooterAdapter(total.toString())
                    val concatAdapter = ConcatAdapter(headerAdapter, itemAdapter, footerAdapter)
                    adapter = concatAdapter
                }
            }
        }
    }

    private fun openUserSheet() {
        val addUserBottomSheet = AddUserFragment()
        addUserBottomSheet.show(supportFragmentManager, addUserBottomSheet.tag)
    }

    override fun onBackPressed() {
        if (isSpinnerOpen) {
            binding.spinnerUsername.hideEdit()
        } else {
            super.onBackPressed()
        }
    }

    private fun setupSpinnerItem() {
        val tempList = arrayListOf<String>()
        viewModel.getUserList().observe(this) {
            if (it.isNullOrEmpty()) {
                prefs.clearNames()
            } else {
                userList.addAll(it)
                for (i in it.indices) {
                    tempList.add(it[i].name.toString())
                }
            }
            val hashSet = hashSetOf<String>()
            hashSet.addAll(tempList)
            val newList = arrayListOf<String>()
            newList.add(0, getString(R.string.select_user_name))
            newList.add(1, getString(R.string.add_party))
            newList.addAll(hashSet)
            setupAdatper(newList)
        }
    }

    private fun setupAdatper(userList: ArrayList<String>) {
        userAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            userList
        )
        binding.spinnerUsername.setAdapter(userAdapter)
        userAdapter?.filter
        userAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUsername.setSelectedItem(0)
        userAdapter?.notifyDataSetChanged()
        if (prefs.getName().isNullOrEmpty() || prefs.getName()
                .isNullOrBlank() || prefs.getName() == null || prefs.getName() == ""
        ) {
            binding.spinnerUsername.setSelectedItem(0)
            prefs.clearNames()
            partyNameUpdate = ""
        } else {
            binding.spinnerUsername.selectedItem = prefs.getName()
            partyNameUpdate = prefs.getName()
            userAdapter?.notifyDataSetChanged()
        }
        binding.spinnerUsername.setStatusListener(object :
            IStatusListener {
            override fun spinnerIsOpening() {
                isSpinnerOpen = true
                binding.spinnerUsername.hideEdit()
            }

            override fun spinnerIsClosing() {
                isSpinnerOpen = false
            }
        })

        binding.spinnerUsername.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(view: View?, position: Int, id: Long) {
                val tvview = view as TextView
                tvview.setTextColor(Color.WHITE)
                partyNameUpdate = if (position == -1) {
                    return
                } else {
                    userAdapter?.getItem(position)
                }
                if (partyNameUpdate!!.contentEquals(resources.getString(R.string.select_user_name))) {
                    partyNameUpdate = ""
                } else if (partyNameUpdate!!.contentEquals(resources.getString(R.string.add_party))) {
                    partyNameUpdate = ""
                    openUserSheet()
                }
            }

            override fun onNothingSelected() {
                partyNameUpdate = ""
            }
        })
    }

    override fun onSubmit() {
        setupSpinnerItem()
    }

    private fun handlePDFShare() {
        viewModel.getBarcodeDataList().observe(this) {
            barcodeList.addAll(it)
        }

        /* if (count!! <= 0) {
             Toast.makeText(
                 this,
                 "Item Quantity Exceding for $name",
                 Toast.LENGTH_SHORT,
             ).show()
         } else {*/

        val foundItem = userList.find { user -> user.name == partyNameUpdate }
        val pdfData = PDFData(
            null,
            partyNameUpdate,
            dataList as ArrayList<ScannedData>,
            foundItem?.mobileNo.toString(),
            total.toString()
        )
        val pdf = Intent(this, PDFActivity::class.java)
        pdf.putExtra(IS_NEW, true)
        pdf.putExtra(PDF_DATA, pdfData)
        startActivity(pdf)
        finish()

        val scannedBarcode = dataList.map { sd -> sd.barcodeNumber }.toSet()
        scannedBarcode.toMutableList().let { it1 -> viewModel.removeStocks(it1) }
    }
}