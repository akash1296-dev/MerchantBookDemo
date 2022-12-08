package com.wedoapps.barcodescanner.Ui.Report.SingleReport.BottomSheets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wedoapps.barcodescanner.R
import com.wedoapps.barcodescanner.Utils.Constants.ALL
import com.wedoapps.barcodescanner.Utils.Constants.DATE_RANGE
import com.wedoapps.barcodescanner.Utils.Constants.FILTER_TYPE
import com.wedoapps.barcodescanner.Utils.Constants.LAST_MONTH
import com.wedoapps.barcodescanner.Utils.Constants.LAST_WEEK
import com.wedoapps.barcodescanner.Utils.Constants.THIS_DAY
import com.wedoapps.barcodescanner.Utils.Constants.THIS_MONTH
import com.wedoapps.barcodescanner.databinding.FragmentBottomSheetFilterBinding

class FilterBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetFilterBinding
    private lateinit var listener: OnFilterOptionSelected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_filter, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBottomSheetFilterBinding.bind(view)

        val filterType = arguments?.getString(FILTER_TYPE)
        val radionGrp = binding.radioGrp
//        radionGrp.clearCheck()

        if (!filterType.isNullOrEmpty()) {
            when (filterType) {
                ALL -> binding.radioAll.isChecked = true
                DATE_RANGE -> binding.radioDateRange.isChecked = true
                THIS_DAY -> binding.radioSingleDay.isChecked = true
                LAST_MONTH -> binding.radioLastMonth.isChecked = true
                LAST_WEEK -> binding.radioLastWeek.isChecked = true
                THIS_MONTH -> binding.radioThisMonth.isChecked = true
            }
        } else {
            radionGrp.clearCheck()
        }

        radionGrp.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton: RadioButton = radioGroup.findViewById(i)
            when (radioButton.id) {
                R.id.radio_all -> {
                    listener.onSelect(ALL)
                    dismiss()
                }

                R.id.radio_date_range -> {
                    listener.onSelect(DATE_RANGE)
                    dismiss()
                }

                R.id.radio_this_month -> {
                    listener.onSelect(THIS_MONTH)
                    dismiss()
                }

                R.id.radio_last_month -> {
                    listener.onSelect(LAST_MONTH)
                    dismiss()
                }

                R.id.radio_last_week -> {
                    listener.onSelect(LAST_WEEK)
                    dismiss()
                }

                R.id.radio_single_day -> {
                    listener.onSelect(THIS_DAY)
                    dismiss()
                }
            }
        }
    }

    interface OnFilterOptionSelected {
        fun onSelect(type: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnFilterOptionSelected
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnFilterOptionsSelected")
        }
    }


}