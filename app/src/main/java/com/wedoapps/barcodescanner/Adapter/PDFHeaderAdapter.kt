package com.wedoapps.barcodescanner.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.databinding.HeaderLayoutPdfItemBinding

class PDFHeaderAdapter : RecyclerView.Adapter<PDFHeaderAdapter.PdfViewHolder>() {
    inner class PdfViewHolder(val binding: HeaderLayoutPdfItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val binding =
            HeaderLayoutPdfItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1
}