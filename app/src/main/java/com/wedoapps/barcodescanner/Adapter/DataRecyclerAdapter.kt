package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.ScannedData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.LayoutPdfItemBinding

class DataRecyclerAdapter(private var list: List<ScannedData>?) :
    RecyclerView.Adapter<DataRecyclerAdapter.DataViewHolder>() {

    inner class DataViewHolder(private val binding: LayoutPdfItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun binding(data: ScannedData) {
            binding.apply {
                tvPdfIndex.text = ("${bindingAdapterPosition + 1}.").toString()
                tvPdfItem.text = data.item
                tvPdfCount.text = data.count.toString()
                tvPdfPriceSingle.text =
                    itemView.context.getString(R.string.Rs) + (data.price!! / data.count!!)
                tvPdfPrice.text = itemView.context.getString(R.string.Rs) + data.price.toString()

                /*ivDelete.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = list?.get(position)
                        if (item != null) {
                            listener.onDeleteClick(data, position)
                        }
                    }
                }

                subCount.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = list?.get(position)
                        if (item != null) {
                            listener.onSubCount(data)
                        }
                    }
                }

                addCount.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = list?.get(position)
                        if (item != null) {
                            listener.onAddCount(data)
                        }
                    }
                }*/
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            LayoutPdfItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = list?.get(position)

        if (data != null) {
            holder.binding(data)
        }

    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    /*  private val differCallback = object : DiffUtil.ItemCallback<ScannedData>() {
          override fun areItemsTheSame(oldItem: ScannedData, newItem: ScannedData): Boolean {
              return true
          }

          override fun areContentsTheSame(oldItem: ScannedData, newItem: ScannedData): Boolean {
              return true
          }
      }

      val differ = AsyncListDiffer(this, differCallback)*/

    fun updateData(payload: List<ScannedData>) {
        list = payload
        notifyDataSetChanged()
    }

    interface OnClick {
        fun onDeleteClick(data: ScannedData, position: Int)
        fun onSubCount(data: ScannedData)
        fun onAddCount(data: ScannedData)
    }
}