package com.example.malawiroadtraffficsafetysystem.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.malawiroadtraffficsafetysystem.data.model.CofApplication
import com.example.malawiroadtraffficsafetysystem.data.repository.CofRepository
import com.example.malawiroadtraffficsafetysystem.data.repository.CofRepositoryImpl
import com.example.malawiroadtraffficsafetysystem.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI State for COF operations
sealed class CofUiState {
    object Idle : CofUiState()
    object Loading : CofUiState()
    data class Success(val message: String) : CofUiState()
    data class Error(val message: String) : CofUiState()
}

class CofViewModel(
    private val repository: CofRepository = CofRepositoryImpl() // Default to Impl for simple injection
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    
    private val _uiState = MutableStateFlow<CofUiState>(CofUiState.Idle)
    val uiState: StateFlow<CofUiState> = _uiState

    private val _applicationStatus = MutableStateFlow<CofApplication?>(null)
    val applicationStatus: StateFlow<CofApplication?> = _applicationStatus

    fun submitApplication(plateNumber: String, vehicleModel: String, chassisNumber: String) {
        viewModelScope.launch {
            _uiState.value = CofUiState.Loading
            val user = auth.currentUser
            if (user == null) {
                _uiState.value = CofUiState.Error("User not authenticated")
                return@launch
            }

            val application = CofApplication(
                userId = user.uid,
                userEmail = user.email ?: "",
                plateNumber = plateNumber.uppercase(),
                vehicleModel = vehicleModel,
                chassisNumber = chassisNumber.uppercase(),
                status = "Pending"
            )

            when (val result = repository.submitApplication(application)) {
                is Resource.Success -> _uiState.value = CofUiState.Success("Application submitted successfully with ID: ${result.data}")
                is Resource.Error -> _uiState.value = CofUiState.Error(result.message)
                is Resource.Loading -> _uiState.value = CofUiState.Loading
            }
        }
    }

    fun fetchApplicationStatus(plateNumber: String) {
        viewModelScope.launch {
            _uiState.value = CofUiState.Loading
            val user = auth.currentUser
            if (user == null) {
                _uiState.value = CofUiState.Error("User not authenticated")
                return@launch
            }
            
            val cleanPlate = plateNumber.replace(Regex("[^a-zA-Z0-9]"), "").uppercase()

            when (val result = repository.getApplicationStatus(cleanPlate, user.uid)) {
                is Resource.Success -> {
                    _applicationStatus.value = result.data
                    _uiState.value = CofUiState.Success("Application found")
                }
                is Resource.Error -> {
                    _uiState.value = CofUiState.Error(result.message)
                    _applicationStatus.value = null
                }
                is Resource.Loading -> _uiState.value = CofUiState.Loading
            }
        }
    }

    fun resetState() {
        _uiState.value = CofUiState.Idle
    }
}
