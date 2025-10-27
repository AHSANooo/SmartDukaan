package com.example.smartdukaan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartdukaan.ui.home.HomeFragment
import java.util.UUID

class AddItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)
        setupClickListeners(view)
        return view
    }

    private fun setupClickListeners(view: View) {
        // Back button
        view.findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            (activity as? MainActivity)?.replaceFragment(HomeFragment())
        }

        // Save button
        view.findViewById<Button>(R.id.btnSaveItem)?.setOnClickListener {
            saveItem(view)
        }
    }

    private fun saveItem(view: View) {
        val etNameUrdu = view.findViewById<EditText>(R.id.etNameUrdu)
        val etName = view.findViewById<EditText>(R.id.etName)
        val etBuying = view.findViewById<EditText>(R.id.etBuying)
        val etSelling = view.findViewById<EditText>(R.id.etSelling)
        val etQty = view.findViewById<EditText>(R.id.etQty)
        val etBarcode = view.findViewById<EditText>(R.id.etBarcode)

        val nameUrdu = etNameUrdu?.text.toString()
        val nameEnglish = etName?.text.toString()
        val buyingPrice = etBuying?.text.toString().toDoubleOrNull() ?: 0.0
        val sellingPrice = etSelling?.text.toString().toDoubleOrNull() ?: 0.0
        val qty = etQty?.text.toString().toDoubleOrNull() ?: 0.0
        val barcode = etBarcode?.text.toString()

        // Validation
        if (nameUrdu.isEmpty() || nameEnglish.isEmpty()) {
            Toast.makeText(context, "Please fill item name in both languages", Toast.LENGTH_SHORT).show()
            return
        }

        if (sellingPrice <= 0) {
            Toast.makeText(context, "Please enter valid selling price", Toast.LENGTH_SHORT).show()
            return
        }

        // Create and save item
        val item = Item(
            id = UUID.randomUUID().toString(),
            name = nameEnglish,
            nameUrdu = nameUrdu,
            buyingPrice = buyingPrice,
            sellingPrice = sellingPrice,
            qty = qty,
            category = "",
            barcode = barcode
        )

        DataManager.addItem(item)

        Toast.makeText(context, "✓ Item added successfully!", Toast.LENGTH_SHORT).show()
        (activity as? MainActivity)?.replaceFragment(HomeFragment())
    }
}

