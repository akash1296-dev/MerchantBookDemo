package com.wedoapps.barcodescanner.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wedoapps.barcodescanner.Model.Users
import com.wedoapps.barcodescanner.databinding.LayoutUserItemBinding

class UserDataRecyclerAdapter(
    private var dataList: ArrayList<Users>?,
    private val listener: OnClick
) :
    RecyclerView.Adapter<UserDataRecyclerAdapter.DataViewHolder>() {

    inner class DataViewHolder(private val binding: LayoutUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun binding(data: Users) {
            binding.apply {
                tvUserName.text = data.name
                tvUserMobile.text = data.mobileNo

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

                /* root.setOnClickListener {
                     val position = bindingAdapterPosition
                     if (position != RecyclerView.NO_POSITION) {
                         val item = dataList?.get(position)
                         if (item != null) {
                             listener.onClick(item)
                         }
                     }
                 }*/
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding =
            LayoutUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val data = dataList?.get(position)

        if (data != null) {
            holder.binding(data)
        }
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    interface OnClick {
        fun onDeleteClick(data: Users)
        fun onEditClick(data: Users)
    }

    fun filterList(filteredList: ArrayList<Users>) {
        dataList = filteredList
        notifyDataSetChanged()
    }
}