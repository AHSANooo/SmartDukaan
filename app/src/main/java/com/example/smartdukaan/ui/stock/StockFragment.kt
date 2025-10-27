package com.example.smartdukaan

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StockFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private lateinit var emptyState: View
    private lateinit var tvTotalItems: TextView
    private lateinit var tvLowStockCount: TextView
    private lateinit var tvStockValue: TextView

    private var allItems = mutableListOf<Item>()
    private var filteredItems = mutableListOf<Item>()
    private var currentFilter = "ALL"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_stock, container, false)

        rv = v.findViewById(R.id.rvStock)
        emptyState = v.findViewById(R.id.emptyState)
        tvTotalItems = v.findViewById(R.id.tvTotalItems)
        tvLowStockCount = v.findViewById(R.id.tvLowStockCount)
        tvStockValue = v.findViewById(R.id.tvStockValue)

        rv.layoutManager = LinearLayoutManager(requireContext())

        setupSearch(v)
        setupFilterChips(v)
        loadItems()
        updateStats()
        refreshRv()

        return v
    }

    private fun setupSearch(v: View) {
        val etSearch = v.findViewById<EditText>(R.id.etStockSearch)
        etSearch.addTextChangedListener { s ->
            val query = s.toString().lowercase()
            applyFilter(currentFilter, query)
        }
    }

    private fun setupFilterChips(v: View) {
        val chipAll = v.findViewById<TextView>(R.id.chipAll)
        val chipLowStock = v.findViewById<TextView>(R.id.chipLowStock)
        val chipOutOfStock = v.findViewById<TextView>(R.id.chipOutOfStock)
        val chipInStock = v.findViewById<TextView>(R.id.chipInStock)

        chipAll.setOnClickListener { selectFilter("ALL", chipAll, chipLowStock, chipOutOfStock, chipInStock) }
        chipLowStock.setOnClickListener { selectFilter("LOW", chipAll, chipLowStock, chipOutOfStock, chipInStock) }
        chipOutOfStock.setOnClickListener { selectFilter("OUT", chipAll, chipLowStock, chipOutOfStock, chipInStock) }
        chipInStock.setOnClickListener { selectFilter("IN", chipAll, chipLowStock, chipOutOfStock, chipInStock) }
    }

    private fun selectFilter(filter: String, vararg chips: TextView) {
        currentFilter = filter
        // Reset all chips
        chips.forEach {
            it.setBackgroundColor(requireContext().getColor(R.color.dividerGray))
            it.setTextColor(requireContext().getColor(R.color.textSecondary))
        }
        // Highlight selected
        val selectedChip = when(filter) {
            "LOW" -> chips[1]
            "OUT" -> chips[2]
            "IN" -> chips[3]
            else -> chips[0]
        }
        selectedChip.setBackgroundColor(requireContext().getColor(R.color.darkBlue))
        selectedChip.setTextColor(requireContext().getColor(android.R.color.white))

        applyFilter(filter, "")
    }

    private fun applyFilter(filter: String, searchQuery: String) {
        filteredItems = when(filter) {
            "LOW" -> allItems.filter { it.qty in 1.0..10.0 }.toMutableList()
            "OUT" -> allItems.filter { it.qty <= 0 }.toMutableList()
            "IN" -> allItems.filter { it.qty > 10 }.toMutableList()
            else -> allItems.toMutableList()
        }

        if (searchQuery.isNotEmpty()) {
            filteredItems = filteredItems.filter {
                it.name.lowercase().contains(searchQuery) ||
                it.nameUrdu.contains(searchQuery)
            }.toMutableList()
        }

        refreshRv()
    }

    private fun loadItems() {
        allItems = DataManager.getItems()
        filteredItems = allItems.toMutableList()
    }

    private fun updateStats() {
        tvTotalItems.text = allItems.size.toString()
        tvLowStockCount.text = DataManager.getLowStockItems().size.toString()
        tvStockValue.text = "Rs ${DataManager.getTotalStockValue().toInt()}"
    }

    private fun refreshRv() {
        if (filteredItems.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            rv.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            rv.visibility = View.VISIBLE
            rv.adapter = ItemAdapter(filteredItems) { item -> showEditDialog(item) }
        }
    }

    private fun showEditDialog(item: Item) {
        val dialogView = layoutInflater.inflate(R.layout.fragment_add_item, null)
        val etNameUrdu = dialogView.findViewById<EditText>(R.id.etNameUrdu)
        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etBuying = dialogView.findViewById<EditText>(R.id.etBuying)
        val etSelling = dialogView.findViewById<EditText>(R.id.etSelling)
        val etQty = dialogView.findViewById<EditText>(R.id.etQty)

        etNameUrdu.setText(item.nameUrdu)
        etName.setText(item.name)
        etBuying.setText(item.buyingPrice.toString())
        etSelling.setText(item.sellingPrice.toString())
        etQty.setText(item.qty.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Edit ${item.name}")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                item.nameUrdu = etNameUrdu.text.toString()
                item.name = etName.text.toString()
                item.buyingPrice = etBuying.text.toString().toDoubleOrNull() ?: 0.0
                item.sellingPrice = etSelling.text.toString().toDoubleOrNull() ?: 0.0
                item.qty = etQty.text.toString().toDoubleOrNull() ?: 0.0

                DataManager.updateItem(item)
                loadItems()
                updateStats()
                refreshRv()
                Toast.makeText(requireContext(),"Item updated", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Delete") { _, _ ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete ${item.name}?")
                    .setPositiveButton("Yes") { _, _ ->
                        DataManager.deleteItem(item.id)
                        loadItems()
                        updateStats()
                        refreshRv()
                        Toast.makeText(requireContext(),"Item deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadItems()
        updateStats()
        refreshRv()
    }
}
