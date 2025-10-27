package com.example.smartdukaan

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.smartdukaan.ui.home.HomeFragment
import java.util.*

class MainActivity : BaseActivity() {

    // in-memory cart (cleared on app restart)
    val cart = mutableListOf<SaleItem>()

    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun getCurrentNavItem(): NavItem = NavItem.HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // default fragment = Home
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(R.id.container, HomeFragment()).commit()
        }
    }

    fun replaceFragment(f: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.container, f).commit()
    }

    fun addToCart(si: SaleItem) {
        // combine same item if exists
        val existing = cart.find { it.itemId == si.itemId }
        if (existing != null) {
            existing.qty += si.qty
        } else {
            cart.add(si)
        }
    }

    fun clearCart() { cart.clear() }
}