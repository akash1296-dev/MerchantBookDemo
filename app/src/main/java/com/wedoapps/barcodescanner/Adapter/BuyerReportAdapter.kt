package com.wedoapps.barcodescanner.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.VendorModel
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.LayoutSingleReportItemBinding

class BuyerReportAdapter(private val listener: OnBuyerClick) :
    RecyclerView.Adapter<BuyerReportAdapter.BuyerReportViewHolder>() {

    inner class BuyerReportViewHolder(private val binding: LayoutSingleReportItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(buyerReportModal: VendorModel) {
            binding.apply {
                tvSingleItemCount.text = ("${bindingAdapterPosition + 1}.").toString()
                tvSingleItemName.text = buyerReportModal.name
                tvSingleItemBarcode.text = if (buyerReportModal.phoneNumber.isNullOrEmpty()) {
                    "NO MOBILE NUMBER AVAILABLE"
                } else {
                    "Mob no.: ${buyerReportModal.phoneNumber}"
                }
                tvSingleItemTotalPrice.text =
                    "Total Amt: ${itemView.context.getString(R.string.Rs)} ${buyerReportModal.payment}"
                tvSingleItemPaidPrice.text =
                    "Paid Amt:${itemView.context.getString(R.string.Rs)} ${buyerReportModal.paidPayment}"
                tvSingleItemDuePrice.text =
                    "Due Amt: ${itemView.context.getString(R.string.Rs)} ${buyerReportModal.duePayment}"

                root.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = differ.currentList[position]
                        if (item != null) {
                            listener.onClick(item)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyerReportViewHolder {
        val binding = LayoutSingleReportItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BuyerReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyerReportViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<VendorModel>() {
        override fun areItemsTheSame(
            oldItem: VendorModel,
            newItem: VendorModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: VendorModel,
            newItem: VendorModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnBuyerClick {
        fun onClick(VendorModel: VendorModel)
    }
}