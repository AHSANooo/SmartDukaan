package com.example.smartdukaan

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class ProfileActivity : BaseActivity() {

    override fun getLayoutResourceId(): Int = R.layout.activity_profile

    override fun getCurrentNavItem(): NavItem = NavItem.PROFILE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<LinearLayout>(R.id.btnEditProfile)?.setOnClickListener {
            Toast.makeText(this, "Edit Profile - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.btnSettings)?.setOnClickListener {
            Toast.makeText(this, "App Settings - Coming Soon!", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.btnBackup)?.setOnClickListener {
            showBackupDialog()
        }

        findViewById<LinearLayout>(R.id.btnHelp)?.setOnClickListener {
            showHelpDialog()
        }

        findViewById<Button>(R.id.btnLogout)?.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showBackupDialog() {
        AlertDialog.Builder(this)
            .setTitle("Backup Data")
            .setMessage("Your data is automatically saved locally on this device.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showHelpDialog() {
        AlertDialog.Builder(this)
            .setTitle("Help & Support")
            .setMessage(
                "SmartDukaan - Your Shop's Easy Accounting\n\n" +
                "Features:\n" +
                "• Add & manage inventory\n" +
                "• Process sales\n" +
                "• Track daily reports\n" +
                "• Monitor stock levels\n\n" +
                "For support, contact: support@smartdukaan.com"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                // Navigate to MainActivity (Home)
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
