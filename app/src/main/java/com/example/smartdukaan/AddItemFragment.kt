package com.example.smartdukaan

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.smartdukaan.ui.home.HomeFragment
import com.example.smartdukaan.utils.ErrorHandler
import com.example.smartdukaan.utils.PermissionHelper
import com.example.smartdukaan.utils.ValidationHelper
import java.util.UUID

class AddItemFragment : Fragment() {

    private var selectedCategory: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_item, container, false)
        setupCategorySpinner(view)
        setupClickListeners(view)
        return view
    }

    private fun setupCategorySpinner(view: View) {
        val spinner = view.findViewById<Spinner>(R.id.spinnerCategory)
        val categories = listOf(
            "Select Category",
            "Grocery",
            "Beverage",
            "Snacks",
            "Dairy",
            "Bakery",
            "Frozen",
            "Personal Care",
            "Household",
            "Other"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner?.adapter = adapter

        spinner?.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = if (position == 0) "" else categories[position]
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedCategory = ""
            }
        }
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

        // Barcode scanner button
        view.findViewById<ImageButton>(R.id.btnScanBarcode)?.setOnClickListener {
            if (PermissionHelper.hasCameraPermission(requireContext())) {
                launchBarcodeScanner()
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun requestCameraPermission() {
        if (PermissionHelper.shouldShowPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            )) {
            // Show explanation
            AlertDialog.Builder(requireContext())
                .setTitle("Camera Permission Needed")
                .setMessage("Camera access is required to scan barcodes. This helps you quickly add items by scanning their barcodes.")
                .setPositiveButton("Grant Permission") { _, _ ->
                    PermissionHelper.requestCameraPermission(requireActivity())
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            PermissionHelper.requestCameraPermission(requireActivity())
        }
    }

    private fun launchBarcodeScanner() {
        // TODO: Implement barcode scanning with ML Kit in Phase 3
        Toast.makeText(
            context,
            "Barcode scanning feature coming soon!\nYou can enter barcode manually for now.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionHelper.CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchBarcodeScanner()
                } else {
                    Toast.makeText(
                        context,
                        "Camera permission denied. You can still enter barcode manually.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun saveItem(view: View) {
        val etName = view.findViewById<EditText>(R.id.etNameUrdu)
        val etBuying = view.findViewById<EditText>(R.id.etBuying)
        val etSelling = view.findViewById<EditText>(R.id.etSelling)
        val etQty = view.findViewById<EditText>(R.id.etQty)
        val etBarcode = view.findViewById<EditText>(R.id.etBarcode)

        val nameEnglish = etName?.text.toString().trim()
        val buyingPriceStr = etBuying?.text.toString()
        val sellingPriceStr = etSelling?.text.toString()
        val qtyStr = etQty?.text.toString()
        val barcode = etBarcode?.text.toString().trim()

        // Parse values
        val buyingPrice = buyingPriceStr.toDoubleOrNull() ?: 0.0
        val sellingPrice = sellingPriceStr.toDoubleOrNull() ?: 0.0
        val qty = qtyStr.toDoubleOrNull() ?: 0.0

        // Validate name
        val nameValidation = ValidationHelper.validateItemName(nameEnglish)
        if (!nameValidation.isValid) {
            etName?.error = nameValidation.errorMessage
            etName?.requestFocus()
            Toast.makeText(context, nameValidation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // Validate buying price
        val buyingValidation = ValidationHelper.validatePrice(buyingPrice, "Buying price")
        if (!buyingValidation.isValid) {
            etBuying?.error = buyingValidation.errorMessage
            etBuying?.requestFocus()
            Toast.makeText(context, buyingValidation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // Validate selling price
        val sellingValidation = ValidationHelper.validatePrice(sellingPrice, "Selling price")
        if (!sellingValidation.isValid) {
            etSelling?.error = sellingValidation.errorMessage
            etSelling?.requestFocus()
            Toast.makeText(context, sellingValidation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // Validate price relationship
        val priceRelation = ValidationHelper.validatePriceRelation(buyingPrice, sellingPrice)
        if (!priceRelation.isValid) {
            etSelling?.error = priceRelation.errorMessage
            etSelling?.requestFocus()
            Toast.makeText(context, priceRelation.errorMessage, Toast.LENGTH_LONG).show()
            return
        }

        // Validate quantity
        val qtyValidation = ValidationHelper.validateQuantity(qty)
        if (!qtyValidation.isValid) {
            etQty?.error = qtyValidation.errorMessage
            etQty?.requestFocus()
            Toast.makeText(context, qtyValidation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // Validate barcode
        val barcodeValidation = ValidationHelper.validateBarcode(barcode)
        if (!barcodeValidation.isValid) {
            etBarcode?.error = barcodeValidation.errorMessage
            etBarcode?.requestFocus()
            Toast.makeText(context, barcodeValidation.errorMessage, Toast.LENGTH_SHORT).show()
            return
        }

        // All validations passed, create and save item
        try {
            val item = Item(
                id = UUID.randomUUID().toString(),
                name = nameEnglish,
                buyingPrice = buyingPrice,
                sellingPrice = sellingPrice,
                qty = qty,
                category = selectedCategory,
                barcode = barcode
            )

            DataManager.addItem(item)

            Toast.makeText(context, "✓ Item added successfully!", Toast.LENGTH_SHORT).show()
            ErrorHandler.logEvent("AddItemFragment", "Item added: $nameEnglish")
            (activity as? MainActivity)?.replaceFragment(HomeFragment())
        } catch (e: IllegalArgumentException) {
            // Show validation error from DataManager
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            // Show generic error
            ErrorHandler.handleError(requireContext(), e, "Failed to add item. Please try again.")
        }
    }
}

