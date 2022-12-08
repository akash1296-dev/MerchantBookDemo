package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.wedoapps.barcodescanner.Model.ScannedData
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.databinding.LayoutItemBinding

class MainDataRecyclerAdapter(private var list: List<ScannedData>?, private val listener: OnClick) :
    RecyclerView.Adapter<MainDataRecyclerAdapter.DataViewHolder>() {

    inner class DataViewHolder(private val binding: LayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun binding(data: ScannedData) {
            binding.apply {
                tvItemName.text = data.item
                tvItemCount.text = data.count.toString()
                tvPrice.text = itemView.context.getString(R.string.Rs) + data.price.toString()

                val least = data.minCount?.let { data.storeQuantity?.minus(it) }
                if (data.count!! > least!!) {
                    TransitionManager.beginDelayedTransition(
                        binding.root,
                        AutoTransition()
                    )
                    tvMinQuantity.visibility = View.VISIBLE
                } else {
                    TransitionManager.beginDelayedTransition(
                        binding.root,
                        AutoTransition()
                    )
                    tvMinQuantity.visibility = View.GONE
                }

                ivDelete.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = list?.get(position)
                        if (item != null) {
                            listener.onDeleteClick(data)
                        }
                    }
                }

                subCount.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = list?.get(position)
                        if (item != null) {
                            val count = item.count?.minus(1)
                            val price = count?.let { it1 -> item.originalPrice?.times(it1) }
                            val newData = ScannedData(
                                item.id,
                                item.barcodeNumber,
                                item.itemCode.toString(),
                                item.item,
                                price,
                                item.originalPrice,
                                item.storeQuantity,
                                item.minCount,
                                item.showDialog,
                                count
                            )
                            listener.onSubCount(newData)
                        }
                    }
                }

                if (data.count!! >= data.storeQuantity!!) {
                    addCount.isClickable = false
                } else {
                    addCount.isClickable = true
                    addCount.setOnClickListener {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val item = list?.get(position)
                            if (item != null) {
                                val count = item.count?.plus(1)
                                val price = count?.let { it1 -> item.originalPrice?.times(it1) }
                                val newData = ScannedData(
                                    item.id,
                                    item.barcodeNumber,
                                    item.itemCode,
                                    item.item,
                                    price,
                                    item.originalPrice,
                                    item.storeQuantity,
                                    item.minCount,
                                    item.showDialog,
                                    count
                                )
                                listener.onAddCount(newData)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        fun onDeleteClick(data: ScannedData)
        fun onSubCount(data: ScannedData)
        fun onAddCount(data: ScannedData)
    }
}