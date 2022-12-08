package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.BarcodeEntryItem
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.LayoutBarcodeDataBinding

class BarcodeDataRecyclerAdapter(
    private var dataList: ArrayList<BarcodeEntryItem>?,
    private val listener: OnClick
) :
    RecyclerView.Adapter<BarcodeDataRecyclerAdapter.DataViewHolder>() {

    inner class DataViewHolder(private val binding: LayoutBarcodeDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(data: BarcodeEntryItem) {
            binding.apply {
                tvBarcodeNumber.text = data.barcodeNumber
                tvItemCode.text = data.itemCode
                tvItemName.text = data.itemName
                tvQuantity.text = "x" + data.count.toString()
                tvMinQuantity.text =
                    itemView.context.getString(R.string.min_stock) + data.minQuantity.toString()
                tvPurchasePrice.text =
                    itemView.context.getString(R.string.Rs) + data.purchasePrice.toString()
                tvPriceSelling.text =
                    itemView.context.getString(R.string.Rs) + data.sellingPrice.toString()

                deleteBtn.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = dataList?.get(position)
                        if (item != null) {
                            listener.onDeleteClick(item)
                        }
                    }
                }

                editBtn.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = dataList?.get(position)
                        if (item != null) {
                            listener.onEditClick(item)
                        }
                    }
                }

                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = dataList?.get(position)
                        if (data.count!! > 0) {
                            if (item != null) {
                                listener.onClick(item)
                            }
                        } else {
                            listener.onErrorMessage("Item Count Exceeded!!")
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            LayoutBarcodeDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = dataList?.get(position)

        if (data != null) {
            holder.binding(data)
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    interface OnClick {
        fun onDeleteClick(data: BarcodeEntryItem)
        fun onEditClick(data: BarcodeEntryItem)
        fun onClick(data: BarcodeEntryItem)
        fun onErrorMessage(message: String)
    }

    fun filterList(filteredList: ArrayList<BarcodeEntryItem>) {
        dataList = filteredList
        notifyDataSetChanged()
    }
}