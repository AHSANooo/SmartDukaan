package com.example.smartdukaan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getLayoutResourceId(): Int
    abstract fun getCurrentNavItem(): NavItem

    enum class NavItem {
        HOME, SALE, STOCK, REPORTS, PROFILE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
    }

    override fun onResume() {
        super.onResume()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<LinearLayout>(R.id.bottom_navigation) ?: return

        val navHome = bottomNav.findViewById<LinearLayout>(R.id.nav_home)
        val navSale = bottomNav.findViewById<LinearLayout>(R.id.nav_sale)
        val navStock = bottomNav.findViewById<LinearLayout>(R.id.nav_stock)
        val navReports = bottomNav.findViewById<LinearLayout>(R.id.nav_reports)
        val navProfile = bottomNav.findViewById<LinearLayout>(R.id.nav_profile)

        // Set click listeners
        navHome?.setOnClickListener { navigateToActivity(MainActivity::class.java, NavItem.HOME) }
        navSale?.setOnClickListener { navigateToActivity(SaleActivity::class.java, NavItem.SALE) }
        navStock?.setOnClickListener { navigateToActivity(StockActivity::class.java, NavItem.STOCK) }
        navReports?.setOnClickListener { navigateToActivity(ReportsActivity::class.java, NavItem.REPORTS) }
        navProfile?.setOnClickListener { navigateToActivity(ProfileActivity::class.java, NavItem.PROFILE) }

        // Highlight current tab
        highlightTab(navHome, NavItem.HOME)
        highlightTab(navSale, NavItem.SALE)
        highlightTab(navStock, NavItem.STOCK)
        highlightTab(navReports, NavItem.REPORTS)
        highlightTab(navProfile, NavItem.PROFILE)
    }

    private fun highlightTab(tab: LinearLayout?, item: NavItem) {
        if (tab == null) return
        val isSelected = getCurrentNavItem() == item
        val icon = tab.getChildAt(0) as? ImageView
        val text = tab.getChildAt(1) as? TextView

        if (isSelected) {
            icon?.setColorFilter(ContextCompat.getColor(this, R.color.darkBlue))
            text?.setTextColor(ContextCompat.getColor(this, R.color.darkBlue))
        } else {
            icon?.setColorFilter(ContextCompat.getColor(this, R.color.textSecondary))
            text?.setTextColor(ContextCompat.getColor(this, R.color.textSecondary))
        }
    }

    private fun navigateToActivity(activityClass: Class<*>, navItem: NavItem) {
        // Don't navigate if already on this activity
        if (getCurrentNavItem() == navItem) {
            return
        }

        val intent = Intent(this, activityClass)
        // Remove FLAG_ACTIVITY_CLEAR_TASK to prevent black screen
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(intent)
        // Add smooth transition animation
        overridePendingTransition(0, 0)
    }
}
