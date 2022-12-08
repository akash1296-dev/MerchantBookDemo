package com.wedoapps.barcodescanner.Ui.Vendor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Adapter.VendorAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.VendorModel
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Ui.Vendor.BottomSheets.AddVendorBottomSheet
import com.wedoapps.barcodescanner.Ui.Vendor.BottomSheets.PaymentBottomSheet
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants
import com.wedoapps.barcodescanner.Utils.Constants.VENDOR_DATA
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivityVendorBinding
import java.util.*


class VendorActivity : AppCompatActivity(), VendorAdapter.OnVendorClick,
    AddVendorBottomSheet.OnWorkDone {

    private lateinit var binding: ActivityVendorBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }
    private var vendorList: ArrayList<VendorModel> = arrayListOf()
    private lateinit var searchItem: MenuItem
    private lateinit var adapterVendor: VendorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVendorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbarVendor.customToolbar
        val title = binding.toolbarVendor.tvTitle
        setSupportActionBar(toolbar)
        title.text = toolbar.title
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbarVendor.ivBack.setOnClickListener {
            finish()
        }
        binding.toolbarVendor.ivAddUsers.visibility = View.GONE

        loadData()

        binding.rvVendorList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.fabAddVendor.isExtended) {
                    binding.fabAddVendor.shrink()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.fabAddVendor.extend()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        binding.fabAddVendor.setOnClickListener {
            val addVendor = AddVendorBottomSheet()
            addVendor.show(supportFragmentManager, addVendor.tag)
        }

        val touchHelperCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                private val background = ColorDrawable(Color.WHITE)

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    adapterVendor.showMenu(viewHolder.bindingAdapterPosition)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    val itemView = viewHolder.itemView
                    if (dX > 0) {
                        background.setBounds(
                            itemView.left,
                            itemView.top,
                            itemView.left + dX.toInt(),
                            itemView.bottom
                        )
                    } else if (dX < 0) {
                        background.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                    } else {
                        background?.setBounds(0, 0, 0, 0)
                    }
                    background?.draw(c)
                }
            }
        val itemTouchHelper = ItemTouchHelper(touchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvVendorList)

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

        searchItem.expandActionView()
        searchView.isIconified = true
        searchView.isFocusable = true
        searchView.requestFocusFromTouch()
        searchView.queryHint = "Search Item"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (!searchView.isIconified) {
                    searchView.isIconified = false
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
        val filteredList: ArrayList<VendorModel> = ArrayList()
        for (item in vendorList) {
            if (item.name?.lowercase(Locale.getDefault())!!
                    .contains(text.lowercase(Locale.getDefault()))
            ) {
                hideEmpty()
                filteredList.add(item)
            } else {
                showEmpty()
            }
        }
        adapterVendor.filterList(filteredList)
    }

    private fun loadData() {
        adapterVendor = VendorAdapter(arrayListOf(), this)
        viewModel.vendorList().observe(this) {
            Log.d(Constants.TAG, "loadData: $it")
            if (it.isNullOrEmpty()) {
                showEmpty()
            } else {
                hideEmpty()
                vendorList = it as ArrayList<VendorModel>
                adapterVendor.filterList(vendorList)
                binding.rvVendorList.apply {
                    setHasFixedSize(true)
                    adapter = adapterVendor
                }
            }
        }
        /*object : SwipeHelper(this, binding.rvVendorList, true) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: MutableList<UnderlayButton>?
            ) {
                underlayButtons?.add(UnderlayButton(
                    "Delete",
                    AppCompatResources.getDrawable(
                        this@VendorActivity,
                        R.drawable.ic_delete
                    ),
                    ContextCompat.getColor(this@VendorActivity, R.color.gd_center),
                    Color.parseColor("#ffffff")
                ) { pos: Int ->
                    getDeleteItemDialog(adapterVendor.dataList!![pos])
                })

                // Flag Button
                underlayButtons?.add(UnderlayButton(
                    "Edit",
                    AppCompatResources.getDrawable(
                        this@VendorActivity,
                        R.drawable.ic_edit
                    ),
                    ContextCompat.getColor(this@VendorActivity, R.color.colorPrimary),
                    Color.parseColor("#000000")
                ) { pos: Int ->

                })
            }
        }*/
    }

    override fun onAddPayment(vendor: VendorModel) {
        val addPayment = PaymentBottomSheet()
        val bundle = Bundle()
        bundle.putParcelable(VENDOR_DATA, vendor)
        addPayment.arguments = bundle
        addPayment.show(supportFragmentManager, addPayment.tag)
    }

    override fun onEdit(vendor: VendorModel) {
        val addVendor = AddVendorBottomSheet()
        val bundle = Bundle()
        bundle.putParcelable(VENDOR_DATA, vendor)
        addVendor.arguments = bundle
        addVendor.show(supportFragmentManager, addVendor.tag)
    }

    override fun onDelete(vendor: VendorModel) {
        getDeleteItemDialog(vendor)
    }

    private fun getDeleteItemDialog(data: VendorModel) {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure you want to Delete User ${data.name} ?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            viewModel.deleteVendor(data)
            Toast.makeText(this, "${data.name} User Deleted", Toast.LENGTH_SHORT).show()
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

    private fun hideEmpty() {
        binding.apply {
            ivNoData.visibility = View.GONE
            rvVendorList.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        binding.apply {
            ivNoData.visibility = View.VISIBLE
            rvVendorList.visibility = View.GONE
        }
    }

    override fun onSubmit() {

    }

    override fun onBackClick() {
        adapterVendor.closeMenu()
    }

    override fun onBackPressed() {
        if (adapterVendor.isMenuShown()) {
            adapterVendor.closeMenu()
        } else {
            super.onBackPressed()
        }
    }

}