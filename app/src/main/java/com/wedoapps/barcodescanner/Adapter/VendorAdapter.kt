package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.VendorModel
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.LayoutVendorItemBinding
import com.wedoapps.barcodescanner.databinding.LayoutVendorMenuBinding

class VendorAdapter(
    var dataList: ArrayList<VendorModel>?,
    val listener: OnVendorClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class VendorViewHolder(private val binding: LayoutVendorItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(vendor: VendorModel) {
            binding.apply {
                tvVendorName.text = vendor.name
                tvVendorPrice.text = itemView.context.getString(R.string.Rs) + vendor.payment
                tvVendorPaidPrice.text =
                    itemView.context.getString(R.string.Rs) + vendor.paidPayment
                tvVendorDuePrice.text = itemView.context.getString(R.string.Rs) + vendor.duePayment

                btnAddPayment.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = dataList?.get(position)
                        if (item != null) {
                            listener.onAddPayment(item)
                        }
                    }
                }


            }
        }
    }

    inner class VendorMenuViewHolder(private val binding: LayoutVendorMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.apply {
                llEdit.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = dataList?.get(position)
                        if (item != null) {
                            listener.onEdit(item)
                        }
                    }
                }

                llDelete.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = dataList?.get(position)
                        if (item != null) {
                            listener.onDelete(item)
                        }
                    }
                }

                ivBack.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onBackClick()
                    }
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (dataList?.get(position)?.showMenu == true) {
            SHOW_MENU
        } else {
            HIDE_MENU
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SHOW_MENU) {
            val binding =
                LayoutVendorMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VendorMenuViewHolder(binding)
        } else {
            val binding =
                LayoutVendorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            VendorViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = dataList?.get(position)
        if (holder is VendorViewHolder) {
            currentItem?.let { holder.bind(it) }
        }

        if (holder is VendorMenuViewHolder) {
            holder.bind()
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    fun showMenu(position: Int) {
        for (i in 0 until dataList!!.size) {
            dataList!![i].showMenu = false
        }
        dataList!![position].showMenu = true
        notifyDataSetChanged()
    }


    fun isMenuShown(): Boolean {
        for (i in 0 until dataList!!.size) {
            if (dataList!![i].showMenu == true) {
                return true
            }
        }
        return false
    }

    fun closeMenu() {
        for (i in 0 until dataList!!.size) {
            dataList!![i].showMenu = false
        }
        notifyDataSetChanged()
    }

    /* private val differCallback = object : DiffUtil.ItemCallback<VendorModel>() {
         override fun areItemsTheSame(oldItem: VendorModel, newItem: VendorModel): Boolean {
             return oldItem.id == newItem.id
         }

         override fun areContentsTheSame(oldItem: VendorModel, newItem: VendorModel): Boolean {
             return oldItem == newItem
         }
     }

     val differ = AsyncListDiffer(this, differCallback)*/

    interface OnVendorClick {
        fun onAddPayment(vendor: VendorModel)
        fun onEdit(vendor: VendorModel)
        fun onDelete(vendor: VendorModel)
        fun onBackClick()
    }

    fun filterList(filteredList: ArrayList<VendorModel>) {
        dataList = filteredList
        notifyDataSetChanged()
    }

    companion object {
        const val SHOW_MENU = 1
        const val HIDE_MENU = 2
    }
}