package com.example.smartdukaan

import ItemAdapter
import android.app.AlertDialog
import android.os.Bundle
import com.example.smartdukaan.StorageManager
import android.view.*
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SaleSelectFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private var items = mutableListOf<Item>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_sale_select, container, false)
        rv = v.findViewById(R.id.rvItems)
        rv.layoutManager = LinearLayoutManager(requireContext())
        loadItems()
        val adapter = ItemAdapter(items) { item -> onItemClicked(item) }
        rv.adapter = adapter

        val etSearch = v.findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener { s ->
            val q = s.toString().lowercase()
            val filtered = items.filter { it.name.lowercase().contains(q) }
            rv.adapter = ItemAdapter(filtered) { item -> onItemClicked(item) }
        }

        return v
    }

    private fun loadItems() {
        items = StorageManager.loadItems(requireContext())
    }

    private fun onItemClicked(item: Item) {
        val et = EditText(requireContext())
        et.hint = "Quantity"
        et.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        AlertDialog.Builder(requireContext())
            .setTitle("Add ${item.name}")
            .setView(et)
            .setPositiveButton("Add") { _, _ ->
                val qty = et.text.toString().ifEmpty { "0" }.toDouble()
                if (qty <= 0) { Toast.makeText(requireContext(),"Enter qty", Toast.LENGTH_SHORT).show(); return@setPositiveButton }
                if (qty > item.qty) {
                    Toast.makeText(requireContext(),"Not enough stock", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val si = SaleItem(item.id, item.name, item.sellingPrice, qty)
                (activity as MainActivity).addToCart(si)
                Toast.makeText(requireContext(),"Added to cart", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null).show()
    }
}
