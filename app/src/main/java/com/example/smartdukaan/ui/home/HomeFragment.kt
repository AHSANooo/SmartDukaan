package com.example.smartdukaan.ui.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.smartdukaan.R
import com.example.smartdukaan.SaleActivity
import com.example.smartdukaan.StockActivity
import com.example.smartdukaan.ReportsActivity
import com.example.smartdukaan.ProfileActivity
import com.example.smartdukaan.MainActivity
import com.example.smartdukaan.AddItemFragment
import com.example.smartdukaan.DataManager

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setupClickListeners(view)
        updateStats(view)
        return view
    }

    private fun updateStats(view: View) {
        // Update today's sales and items sold
        val tvTotalSales = view.findViewById<TextView>(R.id.tvTotalSales)
        val tvItemsSold = view.findViewById<TextView>(R.id.tvItemsSold)

        val totalSales = DataManager.getTodayTotalSales()
        val itemsSold = DataManager.getTodayItemsSold()

        tvTotalSales?.text = "Rs ${totalSales.toInt()}"
        tvItemsSold?.text = itemsSold.toString()
    }

    private fun setupClickListeners(view: View) {
        // New Sale button
        view.findViewById<CardView>(R.id.tile_new_sale)?.setOnClickListener {
            val intent = Intent(requireContext(), SaleActivity::class.java)
            startActivity(intent)
        }

        // Add Item button
        view.findViewById<CardView>(R.id.tile_add_item)?.setOnClickListener {
            // Navigate to AddItemFragment within MainActivity
            (activity as? MainActivity)?.replaceFragment(AddItemFragment())
        }

        // Stock button
        view.findViewById<CardView>(R.id.tile_stock)?.setOnClickListener {
            val intent = Intent(requireContext(), StockActivity::class.java)
            startActivity(intent)
        }

        // Reports button
        view.findViewById<CardView>(R.id.tile_reports)?.setOnClickListener {
            val intent = Intent(requireContext(), ReportsActivity::class.java)
            startActivity(intent)
        }

        // Menu button - opens Profile
        view.findViewById<View>(R.id.btnMenu)?.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let { updateStats(it) }
    }
}
