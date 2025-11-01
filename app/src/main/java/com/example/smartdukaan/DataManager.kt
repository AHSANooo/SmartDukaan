package com.example.smartdukaan

import android.content.Context
import android.content.SharedPreferences
import com.example.smartdukaan.utils.ErrorHandler
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

object DataManager {
    private const val PREFS_NAME = "SmartDukaanPrefs"
    private const val KEY_ITEMS = "items"
    private const val KEY_SALES = "sales"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        try {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ErrorHandler.logEvent("DataManager", "Initialized successfully")
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to initialize", e)
            throw e
        }
    }

    // Items Management
    fun saveItems(items: List<Item>) {
        try {
            val json = gson.toJson(items)
            prefs.edit().putString(KEY_ITEMS, json).apply()
            ErrorHandler.logEvent("DataManager", "Saved ${items.size} items successfully")
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to save items", e)
            throw e
        }
    }

    fun getItems(): MutableList<Item> {
        return try {
            val json = prefs.getString(KEY_ITEMS, null) ?: return mutableListOf()
            val type = object : TypeToken<MutableList<Item>>() {}.type
            val items = gson.fromJson<MutableList<Item>>(json, type) ?: mutableListOf()
            ErrorHandler.logEvent("DataManager", "Loaded ${items.size} items")
            items
        } catch (e: JsonSyntaxException) {
            ErrorHandler.logError("DataManager", "Corrupted items data detected, clearing", e)
            // Clear corrupted data
            prefs.edit().remove(KEY_ITEMS).apply()
            mutableListOf()
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to load items", e)
            mutableListOf()
        }
    }

    fun addItem(item: Item) {
        try {
            // Validate item
            if (item.name.isBlank()) {
                throw IllegalArgumentException("Item name cannot be empty")
            }
            if (item.sellingPrice <= 0) {
                throw IllegalArgumentException("Selling price must be greater than zero")
            }

            val items = getItems()

            // Check for duplicate barcode
            if (item.barcode.isNotBlank()) {
                val duplicate = items.find { it.barcode == item.barcode && it.id != item.id }
                if (duplicate != null) {
                    throw IllegalArgumentException("Item with barcode '${item.barcode}' already exists: ${duplicate.name}")
                }
            }

            items.add(item)
            saveItems(items)
            ErrorHandler.logEvent("DataManager", "Added item: ${item.name}")
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to add item: ${item.name}", e)
            throw e
        }
    }

    fun updateItem(updatedItem: Item) {
        try {
            // Validate item
            if (updatedItem.name.isBlank()) {
                throw IllegalArgumentException("Item name cannot be empty")
            }
            if (updatedItem.sellingPrice <= 0) {
                throw IllegalArgumentException("Selling price must be greater than zero")
            }

            val items = getItems()
            val index = items.indexOfFirst { it.id == updatedItem.id }
            if (index != -1) {
                // Check for duplicate barcode (excluding current item)
                if (updatedItem.barcode.isNotBlank()) {
                    val duplicate = items.find {
                        it.barcode == updatedItem.barcode && it.id != updatedItem.id
                    }
                    if (duplicate != null) {
                        throw IllegalArgumentException("Item with barcode '${updatedItem.barcode}' already exists: ${duplicate.name}")
                    }
                }

                items[index] = updatedItem
                saveItems(items)
                ErrorHandler.logEvent("DataManager", "Updated item: ${updatedItem.name}")
            } else {
                ErrorHandler.logWarning("DataManager", "Item not found for update: ${updatedItem.id}")
            }
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to update item", e)
            throw e
        }
    }

    fun deleteItem(itemId: String) {
        try {
            val items = getItems()
            val removed = items.removeAll { it.id == itemId }
            if (removed) {
                saveItems(items)
                ErrorHandler.logEvent("DataManager", "Deleted item: $itemId")
            } else {
                ErrorHandler.logWarning("DataManager", "Item not found for deletion: $itemId")
            }
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to delete item", e)
            throw e
        }
    }

    fun getItemById(id: String): Item? {
        return getItems().find { it.id == id }
    }

    // Sales Management
    fun saveSales(sales: List<Sale>) {
        try {
            val json = gson.toJson(sales)
            prefs.edit().putString(KEY_SALES, json).apply()
            ErrorHandler.logEvent("DataManager", "Saved ${sales.size} sales successfully")
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to save sales", e)
            throw e
        }
    }

    fun getSales(): MutableList<Sale> {
        return try {
            val json = prefs.getString(KEY_SALES, null) ?: return mutableListOf()
            val type = object : TypeToken<MutableList<Sale>>() {}.type
            val sales = gson.fromJson<MutableList<Sale>>(json, type) ?: mutableListOf()
            ErrorHandler.logEvent("DataManager", "Loaded ${sales.size} sales")
            sales
        } catch (e: JsonSyntaxException) {
            ErrorHandler.logError("DataManager", "Corrupted sales data detected, clearing", e)
            // Clear corrupted data
            prefs.edit().remove(KEY_SALES).apply()
            mutableListOf()
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to load sales", e)
            mutableListOf()
        }
    }

    fun addSale(sale: Sale) {
        try {
            // Validate sale
            if (sale.items.isEmpty()) {
                throw IllegalArgumentException("Sale must contain at least one item")
            }
            if (sale.total < 0) {
                throw IllegalArgumentException("Sale total cannot be negative")
            }

            // Update item quantities first
            sale.items.forEach { saleItem ->
                val item = getItemById(saleItem.itemId)
                if (item != null) {
                    if (item.qty < saleItem.qty) {
                        throw IllegalArgumentException("Insufficient stock for ${item.name}. Available: ${item.qty}, Required: ${saleItem.qty}")
                    }
                    item.qty -= saleItem.qty
                    updateItem(item)
                } else {
                    ErrorHandler.logWarning("DataManager", "Item not found during sale: ${saleItem.itemId}")
                }
            }

            // Save sale
            val sales = getSales()
            sales.add(sale)
            saveSales(sales)
            ErrorHandler.logEvent("DataManager", "Added sale: Rs ${sale.total.toInt()} with ${sale.items.size} items")
        } catch (e: Exception) {
            ErrorHandler.logError("DataManager", "Failed to add sale", e)
            throw e
        }
    }

    // Analytics
    fun getTodaySales(): List<Sale> {
        val today = System.currentTimeMillis()
        val startOfDay = today - (today % 86400000)
        return getSales().filter { it.timestamp >= startOfDay }
    }

    fun getTodayTotalSales(): Double {
        return getTodaySales().sumOf { it.total }
    }

    fun getTodayTotalProfit(): Double {
        return getTodaySales().sumOf { it.profit }
    }

    fun getTodayItemsSold(): Int {
        return getTodaySales().sumOf { sale -> sale.items.sumOf { it.qty.toInt() } }
    }

    fun getLowStockItems(threshold: Double = 10.0): List<Item> {
        return getItems().filter { it.qty <= threshold && it.qty > 0 }
    }

    fun getOutOfStockItems(): List<Item> {
        return getItems().filter { it.qty <= 0 }
    }

    fun getTotalStockValue(): Double {
        return getItems().sumOf { it.buyingPrice * it.qty }
    }

    // Clear all data (for testing)
    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}

