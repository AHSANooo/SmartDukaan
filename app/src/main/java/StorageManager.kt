package com.example.smartdukaan

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object StorageManager {
    private const val PREF = "smart_dukaan_pref"
    private const val KEY_ITEMS = "items_v1"
    private const val KEY_SALES = "sales_v1"

    private fun prefs(ctx: Context) = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveItems(ctx: Context, items: List<Item>) {
        prefs(ctx).edit().putString(KEY_ITEMS, gson.toJson(items)).apply()
    }

    fun loadItems(ctx: Context): MutableList<Item> {
        val json = prefs(ctx).getString(KEY_ITEMS, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Item>>() {}.type
        return gson.fromJson(json, type)
    }

    fun saveSales(ctx: Context, sales: List<Sale>) {
        prefs(ctx).edit().putString(KEY_SALES, gson.toJson(sales)).apply()
    }

    fun loadSales(ctx: Context): MutableList<Sale> {
        val json = prefs(ctx).getString(KEY_SALES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Sale>>() {}.type
        return gson.fromJson(json, type)
    }
}
