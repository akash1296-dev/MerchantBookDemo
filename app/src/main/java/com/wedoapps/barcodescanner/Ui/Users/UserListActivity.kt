package com.wedoapps.barcodescanner.Ui.Users

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Adapter.UserDataRecyclerAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.Users
import com.wedoapps.barcodescanner.Ui.Fragments.AddUserFragment
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants.USER_DATA
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivityItemListBinding


class UserListActivity : AppCompatActivity(), UserDataRecyclerAdapter.OnClick,
    AddUserFragment.OnWorkDone {

    private lateinit var binding: ActivityItemListBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }
    private lateinit var itemListAdapter: UserDataRecyclerAdapter
    private var userEntryList: java.util.ArrayList<Users> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemListBinding.inflate(layoutInflater)
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

        binding.fabAddUser.setOnClickListener {
            val addUserBottomSheet = AddUserFragment()
            addUserBottomSheet.show(supportFragmentManager, addUserBottomSheet.tag)
        }

        itemListAdapter = UserDataRecyclerAdapter(userEntryList, this)

        viewModel.getUserList().observe(this) {
            if (it.isNullOrEmpty()) {
                binding.recyclerViewList.visibility = View.GONE
                binding.ivNoData.visibility = View.VISIBLE
            } else {
                binding.ivNoData.visibility = View.GONE
                binding.recyclerViewList.visibility = View.VISIBLE
                userEntryList = it as java.util.ArrayList<Users>
                itemListAdapter.filterList(userEntryList)
                setupRv()
            }
        }

        binding.recyclerViewList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.fabAddUser.isShown) {
                    binding.fabAddUser.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.fabAddUser.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    override fun onDeleteClick(data: Users) {
        getDeleteItemDialog(data)
    }

    private fun getDeleteItemDialog(data: Users) {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure you want to Delete User ${data.name} ?")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, _ ->
            viewModel.deleteUser(data)
            userEntryList.remove(data)
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

    override fun onEditClick(data: Users) {
        val addUserSheet = AddUserFragment()
        val bundle = Bundle()
        bundle.putParcelable(USER_DATA, data)
        addUserSheet.arguments = bundle
        addUserSheet.show(supportFragmentManager, addUserSheet.tag)
    }

    private fun setupRv() {
        binding.recyclerViewList.apply {
            setHasFixedSize(true)
            adapter = itemListAdapter
        }
    }

    override fun onSubmit() {
    }
}