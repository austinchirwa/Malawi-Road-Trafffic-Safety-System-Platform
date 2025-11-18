package ApplicationScreens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Data class to represent a COF application.
 */
data class CofApplication(
    val plateNumber: String = "",
    val vehicleModel: String = "",
    val chassisNumber: String = "",
    val status: String = "Pending", // Pending, Approved, Rejected
    val inspectionDate: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

sealed class CofState {
    object Idle : CofState()
    object Loading : CofState()
    object Success : CofState()
    data class Error(val message: String) : CofState()
}

class CofViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("cof_applications")

    private val _cofState = MutableStateFlow<CofState>(CofState.Idle)
    val cofState: StateFlow<CofState> = _cofState

    private val _applicationStatus = MutableStateFlow<CofApplication?>(null)
    val applicationStatus: StateFlow<CofApplication?> = _applicationStatus

    /**
     * Submits a COF application.
     */
    fun submitApplication(plateNumber: String, vehicleModel: String, chassisNumber: String) {
        viewModelScope.launch {
            _cofState.value = CofState.Loading
            try {
                // Use plate number as the key for easy retrieval (sanitized)
                val key = plateNumber.replace(Regex("[^a-zA-Z0-9]"), "").uppercase()
                val application = CofApplication(
                    plateNumber = plateNumber,
                    vehicleModel = vehicleModel,
                    chassisNumber = chassisNumber
                )
                
                database.child(key).setValue(application).await()
                _cofState.value = CofState.Success
            } catch (e: Exception) {
                _cofState.value = CofState.Error(e.message ?: "Failed to submit application")
            }
        }
    }

    /**
     * Fetches the status of a COF application by plate number.
     */
    fun fetchApplicationStatus(plateNumber: String) {
        viewModelScope.launch {
            _cofState.value = CofState.Loading
            try {
                val key = plateNumber.replace(Regex("[^a-zA-Z0-9]"), "").uppercase()
                val snapshot = database.child(key).get().await()
                
                if (snapshot.exists()) {
                    val application = snapshot.getValue(CofApplication::class.java)
                    _applicationStatus.value = application
                    _cofState.value = CofState.Success
                } else {
                    _cofState.value = CofState.Error("No application found for this plate number.")
                }
            } catch (e: Exception) {
                _cofState.value = CofState.Error(e.message ?: "Failed to fetch status")
            }
        }
    }

    fun resetState() {
        _cofState.value = CofState.Idle
    }
}
