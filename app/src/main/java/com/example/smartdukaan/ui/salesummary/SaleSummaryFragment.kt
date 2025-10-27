package com.example.smartdukaan

import android.os.Bundle
import com.example.smartdukaan.StorageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartdukaan.ui.home.HomeFragment
import java.util.*

class SaleSummaryFragment : Fragment() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var etDiscount: EditText
    private lateinit var btnComplete: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_sale_summary, container, false)
        rvCart = v.findViewById(R.id.rvCart)
        tvTotal = v.findViewById(R.id.tvTotal)
        etDiscount = v.findViewById(R.id.etDiscount)
        btnComplete = v.findViewById(R.id.btnComplete)
        btnCancel = v.findViewById(R.id.btnCancelSale)

        rvCart.layoutManager = LinearLayoutManager(requireContext())
        refreshCart()

        btnComplete.setOnClickListener { completeSale() }
        btnCancel.setOnClickListener {
            (activity as MainActivity).clearCart()
            Toast.makeText(requireContext(),"Sale cancelled", Toast.LENGTH_SHORT).show()
            (activity as MainActivity).replaceFragment(HomeFragment())
        }

        return v
    }

    private fun refreshCart() {
        val cart = (activity as MainActivity).cart
        rvCart.adapter = CartAdapter(cart)
        val total = cart.sumOf { it.unitPrice * it.qty }
        tvTotal.text = "Total: Rs ${"%.2f".format(total)}"
    }

    private fun completeSale() {
        val main = activity as MainActivity
        val cart = main.cart
        if (cart.isEmpty()) {
            Toast.makeText(requireContext(),"Cart is empty", Toast.LENGTH_SHORT).show()
            return
        }
        val discount = etDiscount.text.toString().ifEmpty { "0" }.toDouble()
        val totalBefore = cart.sumOf { it.unitPrice * it.qty }
        val total = (totalBefore - discount).coerceAtLeast(0.0)
        // compute profit: using buyingPrice from items
        val items = StorageManager.loadItems(requireContext())
        var profit = 0.0
        // reduce stock
        for (si in cart) {
            val it = items.find { it.id == si.itemId }
            if (it != null) {
                profit += (si.unitPrice - it.buyingPrice) * si.qty
                it.qty = (it.qty - si.qty).coerceAtLeast(0.0)
            }
        }
        StorageManager.saveItems(requireContext(), items)

        // record sale
        val sales = StorageManager.loadSales(requireContext())
        val sale = Sale(UUID.randomUUID().toString(), cart.toList(), total, profit, System.currentTimeMillis())
        sales.add(sale)
        StorageManager.saveSales(requireContext(), sales)

        // clear cart
        main.clearCart()
        Toast.makeText(requireContext(),"Sale completed. Total: Rs ${"%.2f".format(total)}", Toast.LENGTH_LONG).show()
        main.replaceFragment(ReportsFragment())
    }

    // simple adapter for cart items
    inner class CartAdapter(private val list: List<SaleItem>) : RecyclerView.Adapter<CartAdapter.VH>() {
        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvName: TextView = v.findViewById(R.id.tvName)
            val tvPrice: TextView = v.findViewById(R.id.tvPrice)
            val tvQty: TextView = v.findViewById(R.id.tvQty)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = layoutInflater.inflate(R.layout.item_card, parent, false)
            return VH(v)
        }
        override fun onBindViewHolder(holder: VH, position: Int) {
            val it = list[position]
            holder.tvName.text = it.name
            holder.tvPrice.text = "Rs ${it.unitPrice}"
            holder.tvQty.text = "${it.qty}"
        }
        override fun getItemCount(): Int = list.size
    }
}
