package com.example.malawiroadtraffficsafetysystem.data.repository

import com.example.malawiroadtraffficsafetysystem.data.model.CofApplication
import com.example.malawiroadtraffficsafetysystem.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class CofRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : CofRepository {

    private val collection = firestore.collection("cof_applications")

    override suspend fun submitApplication(application: CofApplication): Resource<String> {
        return try {
            val docRef = collection.add(application).await()
            Resource.Success(docRef.id)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to submit application", e)
        }
    }

    override suspend fun getApplicationStatus(plateNumber: String, userId: String): Resource<CofApplication?> {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .whereEqualTo("plateNumber", plateNumber)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val doc = snapshot.documents[0]
                val app = doc.toObject(CofApplication::class.java)?.copy(id = doc.id)
                Resource.Success(app)
            } else {
                Resource.Error("No application found for this plate number.")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch status", e)
        }
    }

    override suspend fun getUserApplications(userId: String): Resource<List<CofApplication>> {
        return try {
            val snapshot = collection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val apps = snapshot.documents.mapNotNull { doc ->
                 doc.toObject(CofApplication::class.java)?.copy(id = doc.id)
            }
            Resource.Success(apps)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch applications", e)
        }
    }
}
