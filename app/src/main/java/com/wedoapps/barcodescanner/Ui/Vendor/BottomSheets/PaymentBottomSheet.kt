package com.wedoapps.barcodescanner.Ui.Vendor.BottomSheets

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.FragmentBottomSheetPaymentBinding

class PaymentBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetPaymentBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_payment, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBottomSheetPaymentBinding.bind(view)

        val bundle = arguments
        val data = bundle?.getParcelable<VendorModel>(VENDOR_DATA)

        binding.tvTotal.text = "${getString(R.string.Rs)}${data?.payment}"
        binding.tvDue.text = "${getString(R.string.Rs)}${data?.duePayment}"
        binding.tvPaid.text = "${getString(R.string.Rs)}${data?.paidPayment}"

        val payment = binding.inputPayment.editText
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.inputPayment.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val newPaidValue: Int = if (payment?.text.toString().isEmpty()) {
                    0.plus(data?.paidPayment!!)
                } else {
                    payment?.text.toString().toInt().plus(data?.paidPayment!!)
                }

                val newDueValue: Int = if (payment?.text.toString().isEmpty()) {
                    data?.duePayment!! - 0
                } else {
                    data?.duePayment!!.minus(payment?.text.toString().toInt())
                }


                /* val newDueValue =
                     data?.duePayment!!.minus(payment?.text.toString().toInt())
 */
                binding.tvDue.text = "${getString(R.string.Rs)}${newDueValue}"
                binding.tvPaid.text = "${getString(R.string.Rs)}${newPaidValue}"
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.btnSubmit.setOnClickListener {
            if (payment?.text.toString().isEmpty()) {
                payment?.error = "Field Cannot Be Empty"
            } else {
                viewModel.updatePayment(
                    data?.id!!,
                    data.payment!!,
                    payment?.text.toString().toInt()
                )
                dismiss()
            }
        }
    }
}