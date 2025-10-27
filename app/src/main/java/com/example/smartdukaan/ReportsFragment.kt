package com.example.smartdukaan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {

    private lateinit var tvTodaySales: TextView
    private lateinit var tvTodayProfit: TextView
    private lateinit var tvTransactionCount: TextView
    private lateinit var tvItemsSoldCount: TextView
    private lateinit var tvLowStockBadge: TextView
    private lateinit var tvNoLowStock: TextView
    private lateinit var lowStockContainer: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_reports, container, false)

        // Initialize views
        tvTodaySales = v.findViewById(R.id.tvTodaySales)
        tvTodayProfit = v.findViewById(R.id.tvTodayProfit)
        tvTransactionCount = v.findViewById(R.id.tvTransactionCount)
        tvItemsSoldCount = v.findViewById(R.id.tvItemsSoldCount)
        tvLowStockBadge = v.findViewById(R.id.tvLowStockBadge)
        tvNoLowStock = v.findViewById(R.id.tvNoLowStock)
        lowStockContainer = v.findViewById(R.id.lowStockContainer)

        val tvReportDate = v.findViewById<TextView>(R.id.tvReportDate)
        val btnViewAllSales = v.findViewById<Button>(R.id.btnViewAllSales)
        val btnExportReport = v.findViewById<Button>(R.id.btnExportReport)

        // Set today's date
        val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault())
        tvReportDate.text = "Today • ${dateFormat.format(Date())}"

        // Load and display data
        loadReportData()

        // Setup buttons
        btnViewAllSales.setOnClickListener {
            Toast.makeText(requireContext(), "View All Sales - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        btnExportReport.setOnClickListener {
            Toast.makeText(requireContext(), "Export Report - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        return v
    }

    private fun loadReportData() {
        // Get today's sales data
        val todaySales = DataManager.getTodaySales()
        val totalSales = DataManager.getTodayTotalSales()
        val totalProfit = DataManager.getTodayTotalProfit()
        val itemsSold = DataManager.getTodayItemsSold()

        // Update sales and profit
        tvTodaySales.text = "Rs ${totalSales.toInt()}"
        tvTodayProfit.text = "Rs ${totalProfit.toInt()}"

        // Update transaction count
        tvTransactionCount.text = todaySales.size.toString()

        // Update items sold count
        tvItemsSoldCount.text = itemsSold.toString()

        // Load and display low stock items
        displayLowStockItems()
    }

    private fun displayLowStockItems() {
        val lowStockItems = DataManager.getLowStockItems()

        tvLowStockBadge.text = lowStockItems.size.toString()
        lowStockContainer.removeAllViews()

        if (lowStockItems.isEmpty()) {
            tvNoLowStock.visibility = View.VISIBLE
            lowStockContainer.visibility = View.GONE
        } else {
            tvNoLowStock.visibility = View.GONE
            lowStockContainer.visibility = View.VISIBLE

            lowStockItems.forEach { item ->
                val itemView = layoutInflater.inflate(
                    android.R.layout.simple_list_item_2,
                    lowStockContainer,
                    false
                )

                val text1 = itemView.findViewById<TextView>(android.R.id.text1)
                val text2 = itemView.findViewById<TextView>(android.R.id.text2)

                text1.text = "${item.name} (${item.nameUrdu})"
                text1.setTextColor(requireContext().getColor(R.color.textPrimary))

                text2.text = "Only ${item.qty.toInt()} left in stock"
                text2.setTextColor(requireContext().getColor(R.color.errorRed))

                itemView.setPadding(0, 16, 0, 16)
                lowStockContainer.addView(itemView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadReportData()
    }
}

