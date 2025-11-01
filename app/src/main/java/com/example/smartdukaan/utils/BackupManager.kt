package com.example.smartdukaan.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.smartdukaan.Item
import com.example.smartdukaan.Sale
import com.example.smartdukaan.DataManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object BackupManager {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    data class BackupData(
        val items: List<Item>,
        val sales: List<Sale>,
        val exportDate: Long,
        val appVersion: String,
        val deviceInfo: String
    )

    fun exportToJson(context: Context): File? {
        return try {
            val backupData = BackupData(
                items = DataManager.getItems(),
                sales = DataManager.getSales(),
                exportDate = System.currentTimeMillis(),
                appVersion = "1.0.0",
                deviceInfo = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
            )

            val json = gson.toJson(backupData)
            val fileName = "smartdukaan_backup_${
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            }.json"

            val file = File(context.getExternalFilesDir(null), fileName)
            file.writeText(json)

            ErrorHandler.logEvent("BackupManager", "Backup created: $fileName (${file.length() / 1024} KB)")
            file
        } catch (e: Exception) {
            ErrorHandler.logError("BackupManager", "Failed to create backup", e)
            null
        }
    }

    fun shareBackup(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "SmartDukaan Backup")
                putExtra(Intent.EXTRA_TEXT, "SmartDukaan data backup - ${file.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Backup Via"))
            ErrorHandler.logEvent("BackupManager", "Backup shared successfully")
        } catch (e: Exception) {
            ErrorHandler.logError("BackupManager", "Failed to share backup", e)
        }
    }

    fun importFromJson(context: Context, jsonString: String): Boolean {
        return try {
            val backupData = gson.fromJson(jsonString, BackupData::class.java)

            if (backupData == null) {
                ErrorHandler.logError("BackupManager", "Invalid backup file format")
                return false
            }

            // Validate backup data
            if (backupData.items.isEmpty() && backupData.sales.isEmpty()) {
                ErrorHandler.logWarning("BackupManager", "Backup file is empty")
                return false
            }

            // Clear existing data
            DataManager.clearAllData()

            // Import items
            backupData.items.forEach { item ->
                try {
                    DataManager.addItem(item)
                } catch (e: Exception) {
                    ErrorHandler.logWarning("BackupManager", "Failed to import item: ${item.name}")
                }
            }

            // Import sales (manually save without updating stock)
            val sales = DataManager.getSales()
            sales.addAll(backupData.sales)
            DataManager.saveSales(sales)

            ErrorHandler.logEvent(
                "BackupManager",
                "Backup restored: ${backupData.items.size} items, ${backupData.sales.size} sales"
            )
            true
        } catch (e: Exception) {
            ErrorHandler.logError("BackupManager", "Failed to restore backup", e)
            false
        }
    }

    fun getBackupInfo(file: File): String {
        return try {
            val json = file.readText()
            val backupData = gson.fromJson(json, BackupData::class.java)

            val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(Date(backupData.exportDate))

            """
            Backup Date: $date
            Items: ${backupData.items.size}
            Sales: ${backupData.sales.size}
            Version: ${backupData.appVersion}
            Device: ${backupData.deviceInfo}
            Size: ${file.length() / 1024} KB
            """.trimIndent()
        } catch (e: Exception) {
            "Unable to read backup info"
        }
    }
}

