package com.itechnowizard.chotu.presentation.report

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityDashboardBinding
import com.itechnowizard.chotu.databinding.ActivityReportBinding
import com.itechnowizard.chotu.domain.model.BankModel
import com.itechnowizard.chotu.domain.model.ReportModel
import com.itechnowizard.chotu.presentation.inventory.InventoryViewModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Report : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ToolbarUtils.setToolbar(binding.toolbarLayout,true,
            Constants.TOOLBAR_REPORTS,
            Constants.TOOLBAR_NO_MENU_TEXT)

        binding.toolbarLayout.toolbarBack.setOnClickListener { finish() }

    }

    override fun onStart() {
        super.onStart()
        viewModel.loadReport()

        viewModel.reportState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.data != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                setData(state.data)
            }
        }
    }

    private fun setData(data: ReportModel) {

        binding.apply {
            totalClient.text = data.totalClient.toString()
            totalVendor.text = data.totalVendor.toString()
            totalSale.text = data.totalSell.toString()
            totalPurchase.text = data.totalPurchase.toString()
            highStockName.text = data.highLowStockModel!!.highStockName
            highStockValue.text = data.highLowStockModel.highStockValue.toString()
            lowStockName.text = data.highLowStockModel.lowStockName
            lowStockValue.text = data.highLowStockModel.lowStockValue.toString()

            val total = data.totalPurchase!! + data.totalSell!!
            val buyPercentage = (data.totalPurchase / total) * 100
            val sellPercentage = (data.totalSell / total) * 100

            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

            // on below line we are setting drag for our pie chart
            pieChart.dragDecelerationFrictionCoef = 0.95f

            // on below line we are setting hole
            // and hole color for pie chart
            pieChart.isDrawHoleEnabled = true
            pieChart.setHoleColor(Color.WHITE)

            // on below line we are setting circle color and alpha
            pieChart.setTransparentCircleColor(Color.WHITE)
            pieChart.setTransparentCircleAlpha(110)

            // on  below line we are setting hole radius
            pieChart.holeRadius = 58f
            pieChart.transparentCircleRadius = 61f

            // on below line we are setting center text
            pieChart.setDrawCenterText(true)

            // on below line we are setting
            // rotation for our pie chart
            pieChart.rotationAngle = 0f

            // enable rotation of the pieChart by touch
            pieChart.isRotationEnabled = true
            pieChart.isHighlightPerTapEnabled = true

            // on below line we are setting animation for our pie chart
            pieChart.animateY(1400, Easing.EaseInOutQuad)

            // on below line we are disabling our legend for pie chart
            pieChart.legend.isEnabled = false
            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setEntryLabelTextSize(12f)

            // on below line we are creating array list and
            // adding data to it to display in pie chart
            val entries: ArrayList<PieEntry> = ArrayList()
            entries.add(PieEntry(buyPercentage.toFloat()))
            entries.add(PieEntry(sellPercentage.toFloat()))

            // on below line we are setting pie data set
            val dataSet = PieDataSet(entries, "Buy/Sale")

            // on below line we are setting icons.
            dataSet.setDrawIcons(false)

            // on below line we are setting slice for pie
            dataSet.sliceSpace = 3f
            dataSet.iconsOffset = MPPointF(0f, 40f)
            dataSet.selectionShift = 5f

            // add a lot of colors to list
            val colors: ArrayList<Int> = ArrayList()
            colors.add(ContextCompat.getColor(this@Report, R.color.pie_chart_color_1))
            colors.add(ContextCompat.getColor(this@Report, R.color.pie_chart_color_2))

            // on below line we are setting colors.
            dataSet.colors = colors

            // on below line we are setting pie data set
            val data = PieData(dataSet)
            data.setValueFormatter(PercentFormatter())
            data.setValueTextSize(15f)
            data.setValueTypeface(Typeface.DEFAULT_BOLD)
            data.setValueTextColor(Color.WHITE)
            pieChart.data = data

            // undo all highlights
            pieChart.highlightValues(null)

            // loading chart
            pieChart.invalidate()

        }

    }

    override fun onStop() {
        super.onStop()
        viewModel.reportState.removeObservers(this)
    }
}