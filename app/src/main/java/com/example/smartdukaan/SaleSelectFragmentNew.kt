package com.example.smartdukaan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SaleSelectFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private var allItems = mutableListOf<Item>()
    private var cart = mutableListOf<SaleItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_sale_select, container, false)

        rv = v.findViewById(R.id.rvItems)
        rv.layoutManager = LinearLayoutManager(requireContext())

        // Back button
        v.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            activity?.finish()
        }

        loadItems()
        setupSearch(v)
        refreshRv()

        return v
    }

    private fun setupSearch(v: View) {
        val etSearch = v.findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener { s ->
            val query = s.toString().lowercase()
            val filtered = if (query.isEmpty()) {
                allItems
            } else {
                allItems.filter {
                    it.name.lowercase().contains(query) ||
                    it.nameUrdu.contains(query) ||
                    it.barcode.contains(query)
                }
            }
            rv.adapter = ItemAdapter(filtered) { item -> onItemClicked(item) }
        }
    }

    private fun loadItems() {
        allItems = DataManager.getItems().filter { it.qty > 0 }.toMutableList()
    }

    private fun refreshRv() {
        rv.adapter = ItemAdapter(allItems) { item -> onItemClicked(item) }
    }

    private fun onItemClicked(item: Item) {
        val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
        val et = EditText(requireContext())
        et.hint = "Enter quantity"
        et.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        AlertDialog.Builder(requireContext())
            .setTitle("Add ${item.name} to cart")
            .setMessage("Available: ${item.qty.toInt()} • Price: Rs ${item.sellingPrice.toInt()}")
            .setView(et)
            .setPositiveButton("Add to Cart") { _, _ ->
                val qty = et.text.toString().toDoubleOrNull() ?: 0.0

                if (qty <= 0) {
                    Toast.makeText(requireContext(), "Please enter valid quantity", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (qty > item.qty) {
                    Toast.makeText(requireContext(), "Not enough stock! Only ${item.qty.toInt()} available", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val saleItem = SaleItem(
                    itemId = item.id,
                    name = item.name,
                    nameUrdu = item.nameUrdu,
                    unitPrice = item.sellingPrice,
                    qty = qty
                )

                cart.add(saleItem)
                Toast.makeText(requireContext(), "✓ Added to cart", Toast.LENGTH_SHORT).show()

                // Ask if user wants to continue or go to summary
                AlertDialog.Builder(requireContext())
                    .setTitle("Item Added")
                    .setMessage("${item.name} added to cart!\n\nWhat would you like to do?")
                    .setPositiveButton("Continue Shopping") { _, _ -> }
                    .setNegativeButton("View Cart (${cart.size})") { _, _ ->
                        goToSummary()
                    }
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton("View Cart (${cart.size})") { _, _ ->
                if (cart.isEmpty()) {
                    Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                } else {
                    goToSummary()
                }
            }
            .show()
    }

    private fun goToSummary() {
        if (cart.isEmpty()) {
            Toast.makeText(requireContext(), "Please add items to cart first", Toast.LENGTH_SHORT).show()
            return
        }

        val fragment = SaleSummaryFragment.newInstance(cart)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}

