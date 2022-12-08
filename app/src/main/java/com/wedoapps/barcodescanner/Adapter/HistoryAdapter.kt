package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.PDFData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.LayoutHistoryItemBinding

class HistoryAdapter(private val listener: OnHistoryItemClick) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(private val binding: LayoutHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: PDFData) {
            binding.apply {
                var total = data.total?.toInt()
                total = data.total?.toInt()?.let { total?.plus(it) }
                tvUserNameHistory.text = data.name
                tvDateHistory.text = "${data.date}-${data.time}"
                tvTotalAmtHistory.text = itemView.context.getString(R.string.Rs) + data.total

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding =
            LayoutHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<PDFData>() {
        override fun areItemsTheSame(oldItem: PDFData, newItem: PDFData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PDFData, newItem: PDFData): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnHistoryItemClick {
        fun onClick(data: PDFData)
    }
}