package com.example.smartdukaan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivityNew : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize DataManager
        DataManager.init(applicationContext)

        // Add some demo data if this is first launch
        addDemoDataIfNeeded()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }

    private fun addDemoDataIfNeeded() {
        // Check if data already exists
        if (DataManager.getItems().isEmpty()) {
            // Add some demo items for testing
            val demoItems = listOf(
                Item("1", "Rice", "چاول", 80.0, 100.0, 50.0, "Grocery", ""),
                Item("2", "Sugar", "چینی", 90.0, 110.0, 30.0, "Grocery", ""),
                Item("3", "Oil", "تیل", 200.0, 250.0, 20.0, "Grocery", ""),
                Item("4", "Tea", "چائے", 150.0, 180.0, 15.0, "Beverage", ""),
                Item("5", "Flour", "آٹا", 60.0, 75.0, 40.0, "Grocery", "")
            )
            demoItems.forEach { DataManager.addItem(it) }
        }
    }
}

