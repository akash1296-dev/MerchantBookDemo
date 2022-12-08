package com.wedoapps.barcodescanner.Ui.Fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.BarcodeEntryItem
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants.BARCODE
import com.wedoapps.barcodescanner.Utils.Constants.BARCODE_DATA
import com.wedoapps.barcodescanner.Utils.Constants.BY_MANUAL
import com.wedoapps.barcodescanner.Utils.Constants.DIRECT_MAIN
import com.wedoapps.barcodescanner.Utils.Constants.FROM
import com.wedoapps.barcodescanner.Utils.Constants.MANUALLY
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.LayoutAddItemAlertBinding

class AddItemAlertDialog : DialogFragment() {

    private lateinit var listener: OnSheetWork
    private lateinit var binding: LayoutAddItemAlertBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            requireActivity().application,
            (requireActivity().application as BarcodeApplication).repository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_add_item_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LayoutAddItemAlertBinding.bind(view)

        val bundle = arguments
        val barcodeNumber = bundle?.getString(BARCODE)
        val by = bundle?.getString(BY_MANUAL)
        val from = bundle?.getString(FROM)
        val data: BarcodeEntryItem? = bundle?.getParcelable(BARCODE_DATA)

        // Barcode Only
        binding.inputBarcode.editText?.setText(barcodeNumber)

        if (data != null) {
            // Complete ScannedData
            binding.inputBarcode.editText?.setText(data.barcodeNumber)
            binding.inputBarcodeCode.editText?.setText(data.itemCode)
            binding.inputItemName.editText?.setText(data.itemName)
            binding.inputPrice.editText?.setText(data.purchasePrice.toString())
            binding.inputSellingPrice.editText?.setText(data.sellingPrice.toString())
            binding.inputQuantity.editText?.setText(data.count.toString())
            binding.inputMinimumQuantity.editText?.setText(data.minQuantity.toString())
        }


        binding.btnAddItem.setOnClickListener {
            if (validate()) {
                if (binding.inputSellingPrice.editText?.text.toString()
                        .toInt() > binding.inputPrice.editText?.text.toString().toInt()
                ) {
                    if (data != null) {
                        viewModel.updateBarcodeItem(
                            data.id!!,
                            barcodeNumber = binding.inputBarcode.editText?.text.toString(),
                            itemCode = binding.inputBarcodeCode.editText?.text.toString(),
                            item = binding.inputItemName.editText?.text.toString(),
                            purchasePrice = binding.inputPrice.editText?.text.toString().toInt(),
                            sellingPrice = binding.inputSellingPrice.editText?.text.toString()
                                .toInt(),
                            count = binding.inputQuantity.editText?.text.toString().toInt(),
                            minCount = emptyMin()
                        )
                        viewModel.updateScannedItem(
                            binding.inputBarcode.editText?.text.toString(),
                            binding.inputBarcodeCode.editText?.text.toString(),
                            binding.inputItemName.editText?.text.toString(),
                            binding.inputSellingPrice.editText?.text.toString()
                                .toInt(),
                            binding.inputQuantity.editText?.text.toString().toInt(),
                        )
                        updateScannedData(from.toString(), "", false)
                        dismiss()
                        listener.onSheetClose(false)
                    } else {
                        viewModel.addBarcodeItem(
                            barcodeNumber = binding.inputBarcode.editText?.text.toString(),
                            itemCode = binding.inputBarcodeCode.editText?.text.toString(),
                            item = binding.inputItemName.editText?.text.toString(),
                            purchasePrice = binding.inputPrice.editText?.text.toString().toInt(),
                            sellingPrice = binding.inputSellingPrice.editText?.text.toString()
                                .toInt(),
                            count = binding.inputQuantity.editText?.text.toString().toInt(),
                            minCount = emptyMin()
                        )
                        updateScannedData(from.toString(), by.toString(), false)
                        dismiss()
                        listener.onSheetClose(false)
                    }

                } else {
                    binding.inputSellingPrice.editText?.error =
                        "Price Cannot be Lower Than Purchase Price"
                }
            }
        }

        binding.ivClose.setOnClickListener {
            dismiss()
            listener.onSheetClose(false)
        }
    }

    private fun validate(): Boolean {
        val barcodeNumber = binding.inputBarcode.editText
        val barcodeCode = binding.inputBarcodeCode.editText
        val itemName = binding.inputItemName.editText
        val price = binding.inputPrice.editText
        val sellingPrice = binding.inputSellingPrice.editText
        val quantity = binding.inputQuantity.editText
        /*   // Barcode
           if (barcodeNumber?.text.toString().isEmpty()) {
               barcodeNumber?.error = "Field Cannot be Empty"
               return false
           }*/

        // ItemCode
        if (barcodeCode?.text.toString().isEmpty()) {
            barcodeCode?.error = "Field Cannot be Empty"
            return false
        }

        // ItemName
        if (itemName?.text.toString().isEmpty()) {
            itemName?.error = "Field Cannot be Empty"
            return false
        }

        // Purchase Price
        if (price?.text.toString().isEmpty()) {
            price?.error = "Field Cannot be Empty"
            return false
        }

        // Selling Price
        if (sellingPrice?.text.toString().isEmpty()) {
            sellingPrice?.error = "Field Cannot be Empty"
            return false
        }

        // Quantity
        if (quantity?.text.toString().isEmpty()) {
            quantity?.error = "Field Cannot be Empty"
            return false
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog?.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    interface OnSheetWork {
        fun onSheetClose(inserted: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnSheetWork
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString()
                        + " must implement AddItemDialog"
            )
        }
    }

    private fun emptyMin(): Int {
        return if (binding.inputMinimumQuantity.editText?.text.toString().isEmpty()) {
            0
        } else {
            binding.inputMinimumQuantity.editText?.text.toString().toInt()
        }
    }

    private fun updateScannedData(from: String, by: String, inserted: Boolean) {
        //"by" here to add stock manually and do not add it in scanner activity
        if (from == DIRECT_MAIN) {
            if (by != MANUALLY) {
                viewModel.insertAndUpdateScannedData(
                    binding.inputBarcode.editText?.text.toString(),
                    binding.inputBarcodeCode.editText?.text.toString(),
                    binding.inputItemName.editText?.text.toString(),
                    binding.inputSellingPrice.editText?.text.toString().toInt(),
                    binding.inputQuantity.editText?.text.toString().toInt(),
                    emptyMin(),
                    binding.inputSellingPrice.editText?.text.toString().toInt(),
                    inserted
                )
            }
        }
    }
}