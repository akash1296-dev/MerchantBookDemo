package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.ScannedData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.LayoutPdfItemBinding

class PDFRecyclerAdapter(private val dataList: ArrayList<ScannedData>) :
    RecyclerView.Adapter<PDFRecyclerAdapter.PDFViewHolder>() {

    inner class PDFViewHolder(private val binding: LayoutPdfItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(scannedData: ScannedData) {
            binding.apply {
                tvPdfItem.text = scannedData.item
                tvPdfCount.text = scannedData.count.toString()
                tvPdfPriceSingle.text =
                    itemView.context.getString(R.string.Rs) + (scannedData.price!! / scannedData.count!!)
                tvPdfIndex.text = (layoutPosition).toString()
                tvPdfPrice.text = itemView.context.getString(R.string.Rs) + scannedData.price
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PDFRecyclerAdapter.PDFViewHolder {
        val binding =
            LayoutPdfItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PDFViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PDFRecyclerAdapter.PDFViewHolder, position: Int) {
        val currentItem = dataList[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}