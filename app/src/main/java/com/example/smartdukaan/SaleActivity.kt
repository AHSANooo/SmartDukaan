package com.example.smartdukaan

import android.os.Bundle

class SaleActivity : BaseActivity() {

    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun getCurrentNavItem(): NavItem = NavItem.SALE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SaleSelectFragment())
                .commit()
        }
    }
}

