package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseInitializer {
    private const val TAG = "FirebaseInitializer"

    fun init(context: Context): Boolean {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                try {
                    val defaultApp = FirebaseApp.initializeApp(context)
                    if (defaultApp == null) {
                        initWithFallbackOptions(context)
                    } else {
                        Log.d(TAG, "FirebaseApp initialized with google-services.json configuration.")
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Default FirebaseApp init failed: ${e.message}. Using fallback options.")
                    initWithFallbackOptions(context)
                }
            }
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            Log.e(TAG, "Critical Firebase initialization error: ${e.message}")
            false
        }
    }

    private fun initWithFallbackOptions(context: Context) {
        try {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:109064487095:android:com.aistudio.gbvnerp.school")
                .setApiKey("AIzaSyA_DummyKey_For_Local_Testing_12345")
                .setProjectId("gbvnerp-school")
                .setGcmSenderId("109064487095")
                .build()
            FirebaseApp.initializeApp(context, options)
            Log.d(TAG, "FirebaseApp initialized with fallback FirebaseOptions.")
        } catch (e: Exception) {
            Log.e(TAG, "Fallback Firebase initialization failed: ${e.message}")
        }
    }
}
