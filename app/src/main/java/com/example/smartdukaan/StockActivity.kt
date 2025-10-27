package com.example.smartdukaan

import android.os.Bundle
import com.example.smartdukaan.StockFragment

class StockActivity : BaseActivity() {

    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun getCurrentNavItem(): NavItem = NavItem.STOCK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StockFragment())
                .commit()
        }
    }
}

