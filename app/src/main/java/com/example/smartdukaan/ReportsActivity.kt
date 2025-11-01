package com.example.smartdukaan

import android.os.Bundle

class ReportsActivity : BaseActivity() {

    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun getCurrentNavItem(): NavItem = NavItem.REPORTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ReportsFragment())
                .commit()
        }
    }
}

