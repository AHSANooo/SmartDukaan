package com.example.smartdukaan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class SaleSummaryFragment : Fragment() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var etDiscount: EditText
    private lateinit var btnComplete: Button
    private lateinit var btnCancel: Button

    private var cartItems = mutableListOf<SaleItem>()

    companion object {
        fun newInstance(items: List<SaleItem>): SaleSummaryFragment {
            val fragment = SaleSummaryFragment()
            fragment.cartItems = items.toMutableList()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_sale_summary, container, false)

        // Initialize views
        rvCart = v.findViewById(R.id.rvCart)
        tvSubtotal = v.findViewById(R.id.tvSubtotal)
        tvTotal = v.findViewById(R.id.tvTotal)
        etDiscount = v.findViewById(R.id.etDiscount)
        btnComplete = v.findViewById(R.id.btnComplete)
        btnCancel = v.findViewById(R.id.btnCancelSale)

        // Back button
        v.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        rvCart.layoutManager = LinearLayoutManager(requireContext())

        // Setup discount change listener
        etDiscount.addTextChangedListener {
            updateTotals()
        }

        refreshCart()

        btnComplete.setOnClickListener { completeSale() }
        btnCancel.setOnClickListener { cancelSale() }

        return v
    }

    private fun refreshCart() {
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        rvCart.adapter = CartAdapter(cartItems) { item ->
            // Remove item from cart
            AlertDialog.Builder(requireContext())
                .setTitle("Remove Item")
                .setMessage("Remove ${item.name} from cart?")
                .setPositiveButton("Yes") { _, _ ->
                    cartItems.remove(item)
                    refreshCart()
                    Toast.makeText(requireContext(), "Item removed", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        }

        updateTotals()
    }

    private fun updateTotals() {
        val subtotal = cartItems.sumOf { it.unitPrice * it.qty }
        val discount = etDiscount.text.toString().toDoubleOrNull() ?: 0.0
        val total = (subtotal - discount).coerceAtLeast(0.0)

        tvSubtotal.text = "Rs ${subtotal.toInt()}"
        tvTotal.text = "Rs ${total.toInt()}"
    }

    private fun completeSale() {
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val discount = etDiscount.text.toString().toDoubleOrNull() ?: 0.0
        val subtotal = cartItems.sumOf { it.unitPrice * it.qty }
        val total = (subtotal - discount).coerceAtLeast(0.0)

        // Calculate profit
        var profit = 0.0
        cartItems.forEach { saleItem ->
            val item = DataManager.getItemById(saleItem.itemId)
            if (item != null) {
                profit += (saleItem.unitPrice - item.buyingPrice) * saleItem.qty
            }
        }

        // Adjust profit for discount
        profit = (profit - discount).coerceAtLeast(0.0)

        // Create sale record
        val sale = Sale(
            id = UUID.randomUUID().toString(),
            items = cartItems.toList(),
            total = total,
            discount = discount,
            profit = profit,
            timestamp = System.currentTimeMillis()
        )

        // Save sale (this will also update stock quantities)
        DataManager.addSale(sale)

        // Show success message
        AlertDialog.Builder(requireContext())
            .setTitle("✓ Sale Completed!")
            .setMessage(
                "Total Amount: Rs ${total.toInt()}\n" +
                "Profit: Rs ${profit.toInt()}\n" +
                "Items Sold: ${cartItems.size}"
            )
            .setPositiveButton("View Reports") { _, _ ->
                val intent = Intent(requireContext(), ReportsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
            .setNegativeButton("New Sale") { _, _ ->
                activity?.finish()
                val intent = Intent(requireContext(), SaleActivity::class.java)
                startActivity(intent)
            }
            .setCancelable(false)
            .show()
    }

    private fun cancelSale() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Sale")
            .setMessage("Are you sure you want to cancel this sale?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                cartItems.clear()
                Toast.makeText(requireContext(), "Sale cancelled", Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}

