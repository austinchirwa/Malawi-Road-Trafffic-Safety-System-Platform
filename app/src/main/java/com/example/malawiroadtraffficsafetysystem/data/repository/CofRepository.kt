package com.example.malawiroadtraffficsafetysystem.data.repository

import com.example.malawiroadtraffficsafetysystem.data.model.CofApplication
import com.example.malawiroadtraffficsafetysystem.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CofRepository {
    suspend fun submitApplication(application: CofApplication): Resource<String>
    suspend fun getApplicationStatus(plateNumber: String, userId: String): Resource<CofApplication?>
    suspend fun getUserApplications(userId: String): Resource<List<CofApplication>>
}
