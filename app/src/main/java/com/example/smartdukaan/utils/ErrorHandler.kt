package com.example.smartdukaan.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonSyntaxException
import java.io.IOException

object ErrorHandler {

    private const val TAG = "SmartDukaan"

    fun handleError(context: Context, error: Throwable, userMessage: String? = null) {
        // Log error for debugging
        Log.e(TAG, "Error occurred: ${error.message}", error)

        // Show user-friendly message
        val message = when (error) {
            is JsonSyntaxException -> "Data format error. Please restart the app."
            is IOException -> "Storage error. Please check available space."
            is IllegalStateException -> "App state error. Please restart."
            is IllegalArgumentException -> error.message ?: "Invalid input"
            else -> userMessage ?: "An unexpected error occurred. Please try again."
        }

        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun logEvent(tag: String, message: String) {
        Log.d(TAG, "[$tag] $message")
    }

    fun logWarning(tag: String, message: String) {
        Log.w(TAG, "[$tag] $message")
    }

    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(TAG, "[$tag] $message", throwable)
        } else {
            Log.e(TAG, "[$tag] $message")
        }
    }
}

