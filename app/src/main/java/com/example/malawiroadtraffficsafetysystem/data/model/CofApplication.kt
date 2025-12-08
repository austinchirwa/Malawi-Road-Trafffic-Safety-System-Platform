package com.example.malawiroadtraffficsafetysystem.data.model

import com.google.firebase.firestore.FieldValue

/**
 * Data class to represent a COF application.
 */
data class CofApplication(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val plateNumber: String = "",
    val vehicleModel: String = "",
    val chassisNumber: String = "",
    val status: String = "Pending", // Pending, Approved, Rejected
    val inspectionDate: String = "",
    val timestamp: Any = FieldValue.serverTimestamp(),
    val createdAt: Long = System.currentTimeMillis()
)
