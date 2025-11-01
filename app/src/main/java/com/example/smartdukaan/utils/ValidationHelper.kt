package com.example.smartdukaan.utils

object ValidationHelper {

    fun validateItemName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult(false, "Item name is required")
            name.length < 2 -> ValidationResult(false, "Item name too short (minimum 2 characters)")
            name.length > 100 -> ValidationResult(false, "Item name too long (maximum 100 characters)")
            else -> ValidationResult(true)
        }
    }

    fun validatePrice(price: Double, fieldName: String = "Price"): ValidationResult {
        return when {
            price < 0 -> ValidationResult(false, "$fieldName cannot be negative")
            price == 0.0 -> ValidationResult(false, "$fieldName must be greater than zero")
            price > 1_000_000 -> ValidationResult(false, "$fieldName is too high")
            else -> ValidationResult(true)
        }
    }

    fun validateQuantity(qty: Double): ValidationResult {
        return when {
            qty < 0 -> ValidationResult(false, "Quantity cannot be negative")
            qty > 100_000 -> ValidationResult(false, "Quantity is too high")
            else -> ValidationResult(true)
        }
    }

    fun validateBarcode(barcode: String): ValidationResult {
        if (barcode.isBlank()) return ValidationResult(true) // Optional field

        return when {
            barcode.length < 8 -> ValidationResult(false, "Barcode too short (minimum 8 digits)")
            barcode.length > 20 -> ValidationResult(false, "Barcode too long (maximum 20 digits)")
            !barcode.all { it.isDigit() } -> ValidationResult(false, "Barcode must contain only numbers")
            else -> ValidationResult(true)
        }
    }

    fun validateDiscount(discount: Double, subtotal: Double): ValidationResult {
        return when {
            discount < 0 -> ValidationResult(false, "Discount cannot be negative")
            discount > subtotal -> ValidationResult(false, "Discount cannot exceed subtotal")
            else -> ValidationResult(true)
        }
    }

    fun validatePriceRelation(buyingPrice: Double, sellingPrice: Double): ValidationResult {
        return when {
            sellingPrice < buyingPrice -> ValidationResult(
                false,
                "Selling price (Rs ${sellingPrice.toInt()}) should be higher than buying price (Rs ${buyingPrice.toInt()})"
            )
            else -> ValidationResult(true)
        }
    }

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )
}

