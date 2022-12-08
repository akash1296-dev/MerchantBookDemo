package com.wedoapps.barcodescanner.Ui.Vendor.BottomSheets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.VendorModel
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.Constants.VENDOR_DATA
import com.wedoapps.barcodescanner.Utils.SharedPreferenceManager
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.FragmentBottomAddVendorBinding

class AddVendorBottomSheet : BottomSheetDialogFragment() {

    private lateinit var listener: OnWorkDone
    private lateinit var binding: FragmentBottomAddVendorBinding
    private val userList = mutableListOf<VendorModel>()
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            requireActivity().application,
            (requireActivity().application as BarcodeApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_add_vendor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBottomAddVendorBinding.bind(view)

        val bundle = arguments
        val data = bundle?.getParcelable<VendorModel>(VENDOR_DATA)

        val prefs = SharedPreferenceManager(requireContext())

        if (data != null) {
            binding.inputUser.editText?.isEnabled = false
            binding.inputUser.editText?.setText(data.name)
            binding.inputEmail.editText?.setText(data.email)
            binding.inputMobileNo.editText?.setText(data.phoneNumber)
            binding.inputAddress1.editText?.setText(data.address)
            binding.inputCity.editText?.setText(data.city)
            binding.inputPincode.editText?.setText(data.pincode)
            binding.inputPayment.editText?.setText(data.payment.toString())
            binding.inputPaidPay.editText?.setText(data.paidPayment.toString())
        }

        viewModel.vendorList().observe(this) {
            userList.addAll(it)
        }


        binding.btnSubmit.setOnClickListener {
            if (validate()) {
                if (data != null) {
                    viewModel.updateVendor(
                        data.id!!,
                        binding.inputUser.editText?.text.toString(),
                        binding.inputEmail.editText?.text.toString(),
                        binding.inputMobileNo.editText?.text.toString(),
                        binding.inputAddress1.editText?.text.toString(),
                        binding.inputCity.editText?.text.toString(),
                        binding.inputPincode.editText?.text.toString(),
                        binding.inputPayment.editText?.text.toString().toInt(),
                        binding.inputPaidPay.editText?.text.toString().toInt(),
                        getDuePayment()
                    )
                    listener.onSubmit()
                    dismiss()
                } else {
                    if (userList.any { data -> data.name == binding.inputUser.editText?.text.toString() }) {
                        binding.inputUser.editText?.error = "Name Already Exists"
                    } else {
                        viewModel.addVendor(
                            binding.inputUser.editText?.text.toString(),
                            binding.inputEmail.editText?.text.toString(),
                            binding.inputMobileNo.editText?.text.toString(),
                            binding.inputAddress1.editText?.text.toString(),
                            binding.inputCity.editText?.text.toString(),
                            binding.inputPincode.editText?.text.toString(),
                            binding.inputPayment.editText?.text.toString().toInt(),
                            binding.inputPaidPay.editText?.text.toString().toInt(),
                            getDuePayment()
                        )
                        prefs.setVendorName(binding.inputUser.editText?.text.toString())
                        listener.onSubmit()
                        dismiss()
                    }
                }
            }
        }

        binding.inputUser.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (userList.any { data -> data.name == p0.toString() }) {
                    binding.inputUser.editText?.error = "Name Already Exists"
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun validate(): Boolean {
        val userName = binding.inputUser.editText
        val mobileNumber = binding.inputMobileNo.editText
        val email = binding.inputEmail.editText
        val totalPayment = binding.inputPayment.editText
        val paidPayment = binding.inputPaidPay.editText
        /*   val address = binding.inputAddress1.editText
           val city = binding.inputCity.editText
           val pincode = binding.inputPincode.editText*/

        // UserName
        if (userName?.text.toString().isEmpty()) {
            userName?.error = "Field Cannot be Empty"
            return false
        }

        if (totalPayment?.text.toString().isBlank()) {
            totalPayment?.error = "Field Cannot be Empty"
            return false
        }

        if (paidPayment?.text.toString().isBlank()) {
            paidPayment?.error = "Field Cannot be Empty"
            return false
        }

        if (mobileNumber?.text.toString().isEmpty()) {
            return true
        } else {
            if (mobileNumber?.text.toString().length != 10) {
                mobileNumber?.error = "Please Check your Mobile Number"
                return false
            }
        }

        if (email?.text.toString().isEmpty()) {
            return true
        } else {
            if (!Patterns.EMAIL_ADDRESS.matcher(email?.text.toString()).matches()) {
                email?.error = "Enter Correct Email"
                return false
            }
        }

        return true
    }

    interface OnWorkDone {
        fun onSubmit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnWorkDone
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnWorkDone")
        }
    }

    private fun getDuePayment(): Int {
        return binding.inputPayment.editText?.text.toString()
            .toInt() - binding.inputPaidPay.editText?.text.toString().toInt()
    }

}