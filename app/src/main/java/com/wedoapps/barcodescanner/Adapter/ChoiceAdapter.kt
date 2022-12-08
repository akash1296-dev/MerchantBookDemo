package com.wedoapps.barcodescanner.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.ChoiceModel
import com.wedoapps.barcodescanner.databinding.LayoutChoiceBinding

class ChoiceAdapter(private val listener: OnChoiceClick) :
    RecyclerView.Adapter<ChoiceAdapter.ChoiceViewHolder>() {

    inner class ChoiceViewHolder(private val binding: LayoutChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(choice: ChoiceModel) {
            binding.apply {
                ivChoice.setImageDrawable(choice.menuImage)
                tvChoice.text = choice.menuItem
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceViewHolder {
        val binding =
            LayoutChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChoiceViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<ChoiceModel>() {
        override fun areItemsTheSame(oldItem: ChoiceModel, newItem: ChoiceModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChoiceModel, newItem: ChoiceModel): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnChoiceClick {
        fun onClick(model: ChoiceModel)
    }
}