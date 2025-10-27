package com.example.smartdukaan

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataManager {
    private const val PREFS_NAME = "SmartDukaanPrefs"
    private const val KEY_ITEMS = "items"
    private const val KEY_SALES = "sales"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Items Management
    fun saveItems(items: List<Item>) {
        val json = gson.toJson(items)
        prefs.edit().putString(KEY_ITEMS, json).apply()
    }

    fun getItems(): MutableList<Item> {
        val json = prefs.getString(KEY_ITEMS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Item>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun addItem(item: Item) {
        val items = getItems()
        items.add(item)
        saveItems(items)
    }

    fun updateItem(updatedItem: Item) {
        val items = getItems()
        val index = items.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            items[index] = updatedItem
            saveItems(items)
        }
    }

    fun deleteItem(itemId: String) {
        val items = getItems()
        items.removeAll { it.id == itemId }
        saveItems(items)
    }

    fun getItemById(id: String): Item? {
        return getItems().find { it.id == id }
    }

    // Sales Management
    fun saveSales(sales: List<Sale>) {
        val json = gson.toJson(sales)
        prefs.edit().putString(KEY_SALES, json).apply()
    }

    fun getSales(): MutableList<Sale> {
        val json = prefs.getString(KEY_SALES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Sale>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun addSale(sale: Sale) {
        val sales = getSales()
        sales.add(sale)
        saveSales(sales)

        // Update item quantities
        sale.items.forEach { saleItem ->
            val item = getItemById(saleItem.itemId)
            if (item != null) {
                item.qty -= saleItem.qty
                updateItem(item)
            }
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

