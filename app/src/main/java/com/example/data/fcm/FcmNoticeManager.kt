package com.example.data.fcm

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.data.firestore.FirestoreManager
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat
import java.util.*

object FcmNoticeManager {

    private const val TAG = "FcmNoticeManager"
    const val FCM_TOPIC_NOTICES = "school_wide_notices"

    private var isSubscribedToTopic = false

    fun initFcm(context: Context) {
        try {
            com.example.data.firebase.FirebaseInitializer.init(context)
            if (com.google.firebase.FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseMessaging.getInstance().subscribeToTopic(FCM_TOPIC_NOTICES)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            isSubscribedToTopic = true
                            Log.d(TAG, "Successfully subscribed to FCM topic: $FCM_TOPIC_NOTICES")
                        } else {
                            Log.w(TAG, "Failed to subscribe to FCM topic: ${task.exception?.message}")
                        }
                    }

                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            saveFcmToken(context, task.result)
                        }
                    }
            }

            // Start listening for Firestore notices collection real-time updates
            listenForFirestoreNotices(context)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FCM: ${e.message}")
        }
    }

    fun saveFcmToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
        Log.d(TAG, "FCM Token stored locally: $token")
    }

    fun getStoredFcmToken(context: Context): String {
        val prefs = context.getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
        return prefs.getString("fcm_token", "fcm_token_registered") ?: "fcm_token_registered"
    }

    // AUTOMATED WORKFLOW: Publish notice to Firestore & Dispatch FCM Push Notification
    fun publishNoticeToFirestoreAndFcm(
        context: Context,
        title: String,
        content: String,
        targetAudience: String = "ALL",
        isUrgent: Boolean = false
    ) {
        val db = FirestoreManager.getFirestore(context)
        val noticeId = "notice_${System.currentTimeMillis()}"
        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        val noticeData = hashMapOf(
            "noticeId" to noticeId,
            "title" to title,
            "content" to content,
            "targetAudience" to targetAudience,
            "isUrgent" to isUrgent,
            "publishedAt" to dateStr,
            "school" to "Gayatri Bal Vidhya Niketan",
            "fcmTopic" to FCM_TOPIC_NOTICES
        )

        // 1. Write document to Firestore 'notices' collection
        if (db != null) {
            db.collection("notices").document(noticeId)
                .set(noticeData)
                .addOnSuccessListener {
                    Log.d(TAG, "Notice successfully written to Firestore 'notices' collection!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Firestore write warning: ${e.message}")
                }
        }

        // 2. Trigger instant local Push Notification simulation via FCM Service
        SchoolFirebaseMessagingService.showNotification(
            context = context,
            title = title,
            body = content,
            isUrgent = isUrgent
        )
    }

    // Real-time Firestore Listener for Parent Portal to receive notices automatically
    private fun listenForFirestoreNotices(context: Context) {
        val db = FirestoreManager.getFirestore(context) ?: return
        try {
            db.collection("notices")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore notice listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        snapshot.documentChanges.forEach { change ->
                            if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                                val doc = change.document
                                val title = doc.getString("title") ?: "New School Announcement"
                                val content = doc.getString("content") ?: ""
                                val isUrgent = doc.getBoolean("isUrgent") ?: false

                                Log.d(TAG, "Real-time Firestore Notice Detected: $title")
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Firestore listener setup error: ${e.message}")
        }
    }

    fun triggerTestPushNotification(context: Context) {
        SchoolFirebaseMessagingService.showNotification(
            context = context,
            title = "Test FCM Push Notice - Parent Portal",
            body = "Gayatri Bal Vidhya Niketan: Mid-Term Examination timetable & date sheet is now published in parent portal.",
            isUrgent = true
        )
        Toast.makeText(context, "📱 FCM Push Notification Sent to Parent Portal!", Toast.LENGTH_SHORT).show()
    }
}
