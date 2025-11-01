package com.example.smartdukaan

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.example.smartdukaan.utils.BackupManager
import com.example.smartdukaan.utils.ErrorHandler
import com.example.smartdukaan.utils.PermissionHelper

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
            .setTitle("Backup & Restore")
            .setMessage("Export your data to share or backup, or restore from a previous backup.")
            .setPositiveButton("Export Data") { _, _ ->
                exportData()
            }
            .setNegativeButton("Import Data") { _, _ ->
                importData()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun exportData() {
        if (PermissionHelper.hasStoragePermission(this)) {
            performExport()
        } else {
            PermissionHelper.requestStoragePermission(this)
        }
    }

    private fun performExport() {
        try {
            val file = BackupManager.exportToJson(this)
            if (file != null) {
                val info = BackupManager.getBackupInfo(file)
                AlertDialog.Builder(this)
                    .setTitle("✓ Backup Created Successfully")
                    .setMessage(info)
                    .setPositiveButton("Share Backup") { _, _ ->
                        BackupManager.shareBackup(this, file)
                    }
                    .setNegativeButton("OK", null)
                    .show()
            } else {
                Toast.makeText(this, "Failed to create backup", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            ErrorHandler.handleError(this, e, "Failed to export data")
        }
    }

    private fun importData() {
        AlertDialog.Builder(this)
            .setTitle("Import Backup")
            .setMessage("⚠️ Warning: This will replace all current data with the backup data. Make sure you have exported your current data first.\n\nDo you want to continue?")
            .setPositiveButton("Yes, Import") { _, _ ->
                launchFilePicker()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun launchFilePicker() {
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/json"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            @Suppress("DEPRECATION")
            startActivityForResult(intent, REQUEST_CODE_IMPORT_BACKUP)
        } catch (e: Exception) {
            Toast.makeText(this, "No file manager app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionHelper.STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performExport()
                } else {
                    Toast.makeText(
                        this,
                        "Storage permission denied. Cannot export data.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMPORT_BACKUP && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    val json = contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                    if (json != null) {
                        if (BackupManager.importFromJson(this, json)) {
                            AlertDialog.Builder(this)
                                .setTitle("✓ Data Restored Successfully!")
                                .setMessage("Your data has been restored. The app will now restart to load the new data.")
                                .setPositiveButton("OK") { _, _ ->
                                    // Restart app
                                    val intent = Intent(this, SplashActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                .setCancelable(false)
                                .show()
                        } else {
                            Toast.makeText(this, "Failed to restore data. Invalid backup file.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Failed to read backup file", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    ErrorHandler.handleError(this, e, "Failed to import backup")
                }
            }
        }
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

    companion object {
        private const val REQUEST_CODE_IMPORT_BACKUP = 200
    }
}

