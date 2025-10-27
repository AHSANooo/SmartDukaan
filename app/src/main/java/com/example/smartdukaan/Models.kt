package com.example.smartdukaan

data class Item(
    var id: String,
    var name: String,
    var nameUrdu: String = "",
    var buyingPrice: Double,
    var sellingPrice: Double,
    var qty: Double,
    var category: String = "",
    var barcode: String = ""
)

data class SaleItem(
    var itemId: String,
    var name: String,
    var nameUrdu: String = "",
    var unitPrice: Double,
    var qty: Double
)

data class Sale(
    var id: String,
    var items: List<SaleItem>,
    var total: Double,
    var discount: Double = 0.0,
    var profit: Double,
    var timestamp: Long
)
