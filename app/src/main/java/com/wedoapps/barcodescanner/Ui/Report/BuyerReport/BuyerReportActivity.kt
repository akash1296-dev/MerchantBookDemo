package com.wedoapps.barcodescanner.Ui.Report.BuyerReport

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.wedoapps.barcodescanner.Adapter.BuyerReportAdapter
import com.wedoapps.barcodescanner.BarcodeViewModel
import com.wedoapps.barcodescanner.Model.VendorModel
import com.wedoapps.barcodescanner.Utils.BarcodeApplication
import com.wedoapps.barcodescanner.Utils.ViewModelProviderFactory
import com.wedoapps.barcodescanner.databinding.ActivityBuyerReportBinding


class BuyerReportActivity : AppCompatActivity(), BuyerReportAdapter.OnBuyerClick,
    OnChartValueSelectedListener {

    private lateinit var binding: ActivityBuyerReportBinding
    private val viewModel: BarcodeViewModel by viewModels {
        ViewModelProviderFactory(
            application,
            (application as BarcodeApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar.customToolbar
        setSupportActionBar(toolbar)

        binding.toolbar.apply {
            ivSearch.visibility = View.GONE
            ivSetting.visibility = View.GONE
            ivInsertItem.visibility = View.GONE
            ivBack.visibility = View.VISIBLE
            ivUserAdd.visibility = View.GONE
        }

        binding.toolbar.ivBack.setOnClickListener {
            finish()
        }

        binding.chartPie.setOnChartValueSelectedListener(this)
        binding.apply {
            chartPie.setUsePercentValues(true)
            chartPie.description.isEnabled = false
            chartPie.setExtraOffsets(5f, 10f, 5f, 5f)

            chartPie.dragDecelerationFrictionCoef = 0.95f

            chartPie.centerText = "Vendor List"

            chartPie.isDrawHoleEnabled = true
            chartPie.setHoleColor(Color.WHITE)
            chartPie.setEntryLabelColor(Color.BLACK)

            chartPie.setTransparentCircleColor(Color.WHITE)
            chartPie.setTransparentCircleAlpha(110)

            chartPie.holeRadius = 58f
            chartPie.transparentCircleRadius = 61f

            chartPie.setDrawCenterText(true)

            chartPie.rotationAngle = 0f
            chartPie.isRotationEnabled = true
            chartPie.isHighlightPerTapEnabled = true
            chartPie.animateY(1400, Easing.EaseInOutQuad)
            val l: Legend = chartPie.legend
            l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            l.orientation = Legend.LegendOrientation.VERTICAL
            l.setDrawInside(false)
            l.xEntrySpace = 7f
            l.yEntrySpace = 0f
            l.yOffset = 0f
            chartPie.setEntryLabelColor(Color.WHITE)
            chartPie.setEntryLabelTextSize(12f)
        }


        val buyerAdapter = BuyerReportAdapter(this)
        viewModel.vendorList().observe(this) {
            if (it.isNullOrEmpty()) {
                showEmpty()
            } else {
                hideEmpty()
                buyerAdapter.differ.submitList(it.sortedByDescending { list -> list.duePayment })
                binding.rvBuyer.apply {
                    setHasFixedSize(true)
                    adapter = buyerAdapter
                }
                setData(it.sortedByDescending { list -> list.duePayment }[0])
            }
        }
    }

    private fun hideEmpty() {
        binding.apply {
            ivNoData.visibility = View.GONE
            rvBuyer.visibility = View.VISIBLE
            chartPie.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        binding.apply {
            ivNoData.visibility = View.VISIBLE
            rvBuyer.visibility = View.GONE
            chartPie.visibility = View.GONE
        }
    }

    override fun onClick(VendorModel: VendorModel) {
        setData(VendorModel)
    }

    private fun setData(vendorModel: VendorModel) {
        binding.chartPie.animateY(1400, Easing.EaseInOutQuad)
        binding.chartPie.centerText = vendorModel.name.toString()
        val entries: ArrayList<PieEntry> = ArrayList()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        entries.add(
            PieEntry(
                vendorModel.paidPayment?.toFloat() ?: 0f,
                "Paid"
            )
        )
        entries.add(
            PieEntry(
                vendorModel.duePayment?.toFloat() ?: 0f,
                "Due"
            )
        )

        val dataSet = PieDataSet(entries, "Vendor Payment")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueLineColor = Color.BLACK
        // add a lot of colors
        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.GREEN)
        colors.add(Color.RED)
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.BLACK)
        binding.chartPie.data = data
        // undo all highlights
        binding.chartPie.highlightValues(null)

        binding.chartPie.invalidate()
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
    }

    override fun onNothingSelected() {
    }


}