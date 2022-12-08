package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.SingleReportModel
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.LayoutSingleReportItemBinding

class ReportSingleItemAdapter(var dataList: ArrayList<SingleReportModel>?) :
    RecyclerView.Adapter<ReportSingleItemAdapter.ReportSingleItemViewHolder>() {

    inner class ReportSingleItemViewHolder(private val binding: LayoutSingleReportItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(singleReportModel: SingleReportModel) {
            binding.apply {
                tvSingleItemCount.text = ("${bindingAdapterPosition + 1}.").toString()
                tvSingleItemName.text = singleReportModel.itemName
                tvSingleItemBarcode.text = singleReportModel.barcodeNumber
                tvSingleItemDuePrice.text =
                    "${itemView.context.getString(R.string.Rs)}${singleReportModel.itemPrice.toString()}"
                tvSingleItemPaidPrice.text = "qty: ${singleReportModel.quantity}"

                tvSingleItemTotalPrice.text =
                    "Total: ${itemView.context.getString(R.string.Rs)}${
                        (singleReportModel.itemPrice?.times(
                            singleReportModel.quantity!!
                        ))
                    }"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportSingleItemViewHolder {
        val binding = LayoutSingleReportItemBinding.inflate(
            LayoutInflater.from(
                parent.context
            ),
            parent,
            false
        )
        return ReportSingleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportSingleItemViewHolder, position: Int) {
        val currenItem = dataList?.get(position)

        if (currenItem != null) {
            holder.bind(currenItem)
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    /*private val differCallback = object : DiffUtil.ItemCallback<SingleReportModel>() {
        override fun areItemsTheSame(
            oldItem: SingleReportModel,
            newItem: SingleReportModel
        ): Boolean {
            return true
        }

        override fun areContentsTheSame(
            oldItem: SingleReportModel,
            newItem: SingleReportModel
        ): Boolean {
            return true
        }
    }

    val differ = AsyncListDiffer(this, differCallback)*/

    fun filterList(filteredList: ArrayList<SingleReportModel>) {
        dataList = filteredList
        notifyDataSetChanged()
    }
}