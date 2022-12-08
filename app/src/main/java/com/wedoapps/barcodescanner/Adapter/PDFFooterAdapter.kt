package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.PdfFooterLayoutBinding

class PDFFooterAdapter(val total: String) : RecyclerView.Adapter<PDFFooterAdapter.PdfViewHolder>() {
    inner class PdfViewHolder(val binding: PdfFooterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind() {
            binding.apply {
                tvTotal.text = itemView.context.getString(R.string.Rs) + total
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val binding =
            PdfFooterLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1
}