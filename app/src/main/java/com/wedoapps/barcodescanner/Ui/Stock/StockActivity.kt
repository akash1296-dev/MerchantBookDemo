package com.wedoapps.barcodescanner.Ui.Stock

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Adapter.BarcodeDataRecyclerAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.BarcodeEntryItem
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.Fragments.AddItemAlertDialog
import com.wedoapps.barcodescanner.Ui.Scanner.MainActivity
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants
import com.wedoapps.barcodescanner.Utils.Constants.BY_MANUAL
import com.wedoapps.barcodescanner.Utils.Constants.FROM
import com.wedoapps.barcodescanner.Utils.Constants.MANUALLY
import com.wedoapps.barcodescanner.Utils.Constants.STOCK_TO_MAIN
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivitySearchBinding
import java.util.*

class StockActivity : AppCompatActivity(), BarcodeDataRecyclerAdapter.OnClick,
    AddItemAlertDialog.OnSheetWork {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }
    private lateinit var itemListAdapter: BarcodeDataRecyclerAdapter
    private var barcodeEntryList: ArrayList<BarcodeEntryItem> = arrayListOf()
    private lateinit var searchItem: MenuItem
    private lateinit var from: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        from = intent.getStringExtra(FROM).toString()

        val toolbar = binding.toolbar.customToolbar
        val title = binding.toolbar.toolbarTitle
        setSupportActionBar(toolbar)
        title.text = toolbar.title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        itemListAdapter = BarcodeDataRecyclerAdapter(arrayListOf(), this)

        viewModel.getBarcodeDataList().observe(this) {
            if (it.isNullOrEmpty()) {
                binding.rvSearch.visibility = View.GONE
                binding.ivNoData.visibility = View.VISIBLE
            } else {
                binding.ivNoData.visibility = View.GONE
                binding.rvSearch.visibility = View.VISIBLE
                barcodeEntryList = it as ArrayList<BarcodeEntryItem>
                itemListAdapter.filterList(barcodeEntryList)
                setUpRv()
            }
        }


        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.fabAdd.isShown) {
                    binding.fabAdd.visibility = View.GONE
                    binding.fabAdd.visibility = View.GONE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.fabAdd.visibility = View.VISIBLE
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        onClickForFabs()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        searchItem = menu.findItem(R.id.action_search)
        val drawable = searchItem.icon
        if (drawable != null) {
            drawable.setTint(ContextCompat.getColor(this, R.color.black))
            searchItem.icon = drawable
        }
        val searchView: SearchView =
            searchItem.actionView as SearchView
        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        val cancelIcon =
            searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        val searchHint =
            searchView.findViewById<SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
        searchHint.setHintTextColor(Color.BLACK)
        searchHint.setTextColor(Color.BLACK)
        searchIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_search))
        cancelIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_close))
        searchView.isIconified = false
        searchView.isFocusable = true
        searchView.requestFocusFromTouch()
        searchView.queryHint = "Search Item"
        if (from == "main") {
            searchItem.expandActionView()
        } else {
            searchItem.collapseActionView()
            searchView.isIconified = true
        }
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
        val filteredList: ArrayList<BarcodeEntryItem> = ArrayList()
        for (item in barcodeEntryList) {
            if (item.itemName?.lowercase(Locale.getDefault())!!
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                filteredList.add(item)
            }
        }
        itemListAdapter.filterList(filteredList)
    }


    override fun onDeleteClick(data: BarcodeEntryItem) {
        getDeleteItemDialog(data)
    }

    private fun getDeleteItemDialog(data: BarcodeEntryItem) {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure you want to Delete this ${data.itemName} ?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            viewModel.deleteBarcodeItem(data)
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

    override fun onEditClick(data: BarcodeEntryItem) {
        val addItemDialog = AddItemAlertDialog()
        val bundle = Bundle()
        bundle.putParcelable(Constants.BARCODE_DATA, data)
        addItemDialog.arguments = bundle
        addItemDialog.show(supportFragmentManager, addItemDialog.tag)
    }

    override fun onClick(data: BarcodeEntryItem) {
        if (data.count!! <= 0) {
            Toast.makeText(this, "Quantity Finished for ${data.itemName}", Toast.LENGTH_SHORT)
                .show()
        } else {
            viewModel.insertAndUpdateScannedData(
                data.barcodeNumber.toString(),
                data.itemCode.toString(),
                data.itemName.toString(),
                data.sellingPrice!!,
                data.count!!,
                data.minQuantity!!,
                data.sellingPrice!!,
                true
            )

            viewModel.scannedDataInsertAndUpdateResponseLiveData.observe(this) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

    }

    private fun setUpRv() {
        binding.rvSearch.apply {
            setHasFixedSize(true)
            adapter = itemListAdapter
        }
    }

    override fun onSheetClose(inserted: Boolean) {

    }

    override fun onErrorMessage(message: String) {
        alertDialog(message)
    }

    private fun alertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.setTitle("Alert")
        builder.setCancelable(true)
        builder.setPositiveButton(
            "OK"
        ) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun onClickForFabs() {
        binding.apply {
            fabManually.setOnClickListener {
                val addItemDialog = AddItemAlertDialog()
                val bundle = Bundle()
                // to add stock manually and do not add it in scanner activity
                bundle.putString(BY_MANUAL, MANUALLY)
                addItemDialog.arguments = bundle
                addItemDialog.show(supportFragmentManager, addItemDialog.tag)
                fabAdd.collapse()
            }

            fabScanner.setOnClickListener {
                val intent = Intent(this@StockActivity, MainActivity::class.java)
                intent.putExtra(FROM, STOCK_TO_MAIN)
                startActivity(intent)
                fabAdd.collapse()
            }
        }
    }
}