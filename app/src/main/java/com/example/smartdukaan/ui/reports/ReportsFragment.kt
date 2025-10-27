package com.example.smartdukaan

import android.os.Bundle
import com.example.smartdukaan.StorageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.*

class ReportsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_reports, container, false)
        val tvSales = v.findViewById<TextView>(R.id.tvTodaySales)
        val tvProfit = v.findViewById<TextView>(R.id.tvTodayProfit)
        val lowStockContainer = v.findViewById<LinearLayout>(R.id.lowStockContainer)

        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        val sales = StorageManager.loadSales(requireContext())
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        val start = today.timeInMillis
        val end = start + 24*60*60*1000 - 1

        val todaysSales = sales.filter { it.timestamp in start..end }
        val total = todaysSales.sumOf { it.total }
        val profit = todaysSales.sumOf { it.profit }

        tvSales.text = "Today's Sales: Rs ${"%.2f".format(total)}"
        tvProfit.text = "Today's Profit: Rs ${"%.2f".format(profit)}"

        // low stock
        lowStockContainer.removeAllViews()
        val items = StorageManager.loadItems(requireContext())
        val low = items.filter { it.qty <= 5.0 } // threshold 5
        if (low.isEmpty()) {
            val t = TextView(requireContext()); t.text = "No low stock items"; lowStockContainer.addView(t)
        } else {
            low.forEach {
                val t = TextView(requireContext())
                t.text = "${it.name} — ${it.qty}"
                t.setTextColor(resources.getColor(R.color.errorRed))
                lowStockContainer.addView(t)
            }
        }

        return v
    }
}
