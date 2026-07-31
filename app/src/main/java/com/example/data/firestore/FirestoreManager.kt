package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

object FirestoreManager {

    private var firestoreInstance: FirebaseFirestore? = null
    private var isOfflinePersistenceConfigured: Boolean = false

    fun getFirestore(context: Context): FirebaseFirestore? {
        if (firestoreInstance != null) return firestoreInstance

        return try {
            com.example.data.firebase.FirebaseInitializer.init(context)
            val db = FirebaseFirestore.getInstance()

            if (!isOfflinePersistenceConfigured) {
                try {
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setLocalCacheSettings(
                            PersistentCacheSettings.newBuilder()
                                .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                                .build()
                        )
                        .build()
                    db.firestoreSettings = settings
                    isOfflinePersistenceConfigured = true
                    Log.d("FirestoreManager", "Firestore offline persistent cache configured successfully!")
                } catch (e: Exception) {
                    Log.w("FirestoreManager", "Firestore settings warning: ${e.message}")
                }
            }

            firestoreInstance = db
            db
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error initializing Firestore: ${e.message}")
            null
        }
    }

    fun isPersistenceActive(): Boolean = isOfflinePersistenceConfigured

    // Helper to sync student data to Firestore offline cache
    fun syncStudentsToFirestore(context: Context, studentList: List<com.example.data.entity.StudentEntity>) {
        val db = getFirestore(context) ?: return
        try {
            val batch = db.batch()
            studentList.forEach { student ->
                val docRef = db.collection("students").document(student.id.toString())
                val data = hashMapOf(
                    "id" to student.id,
                    "rollNo" to student.rollNo,
                    "name" to student.name,
                    "className" to student.className,
                    "section" to student.section,
                    "parentName" to student.parentName,
                    "parentPhone" to student.parentPhone,
                    "address" to student.address,
                    "gender" to student.gender,
                    "admissionDate" to student.admissionDate,
                    "lastUpdated" to System.currentTimeMillis()
                )
                batch.set(docRef, data)
            }
            batch.commit()
                .addOnSuccessListener {
                    Log.d("FirestoreManager", "Student data synced to Firestore offline cache!")
                }
                .addOnFailureListener { e ->
                    Log.w("FirestoreManager", "Student sync error (available offline): ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Sync exception: ${e.message}")
        }
    }

    // Helper to sync attendance data to Firestore offline cache
    fun syncAttendanceToFirestore(context: Context, attendanceList: List<com.example.data.entity.AttendanceEntity>) {
        val db = getFirestore(context) ?: return
        try {
            val batch = db.batch()
            attendanceList.forEach { att ->
                val docRef = db.collection("attendance").document("${att.date}_${att.personId}_${att.personType}")
                val data = hashMapOf(
                    "id" to att.id,
                    "personId" to att.personId,
                    "personType" to att.personType,
                    "personName" to att.personName,
                    "className" to att.className,
                    "date" to att.date,
                    "status" to att.status,
                    "lastUpdated" to System.currentTimeMillis()
                )
                batch.set(docRef, data)
            }
            batch.commit()
                .addOnSuccessListener {
                    Log.d("FirestoreManager", "Attendance data synced to Firestore offline persistent cache!")
                }
                .addOnFailureListener { e ->
                    Log.w("FirestoreManager", "Attendance sync error (available in offline cache): ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Attendance sync exception: ${e.message}")
        }
    }
}
