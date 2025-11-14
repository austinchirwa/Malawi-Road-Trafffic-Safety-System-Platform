package ApplicationScreens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Data class to represent a renewal application.
 */
data class RenewalApplication(
    val userId: String,
    val fullName: String,
    val idNumber: String,
    val licenseNumber: String,
    val licenseCode: String,
    val dateOfBirth: String,
    val vehicleRegNumber: String,
    val trnNumber: String,
    val insuranceProvider: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Sealed class to represent the state of the renewal submission.
 */
sealed class RenewalState {
    object Idle : RenewalState()
    object Loading : RenewalState()
    object Success : RenewalState()
    data class Error(val message: String) : RenewalState()
}

class RenewalsViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("renewals")
    private val auth = FirebaseAuth.getInstance()

    private val _renewalState = MutableStateFlow<RenewalState>(RenewalState.Idle)
    val renewalState: StateFlow<RenewalState> = _renewalState

    /**
     * Submits a renewal application to the Firebase Realtime Database.
     */
    fun submitApplication(application: RenewalApplication) {
        viewModelScope.launch {
            _renewalState.value = RenewalState.Loading
            try {
                // Save the application under a unique key within the user's ID node
                val applicationKey = database.child(application.userId).push().key
                if (applicationKey != null) {
                    database.child(application.userId).child(applicationKey).setValue(application).await()
                    _renewalState.value = RenewalState.Success
                } else {
                    _renewalState.value = RenewalState.Error("Could not generate a unique ID for the application.")
                }
            } catch (e: Exception) {
                _renewalState.value = RenewalState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    /**
     * Resets the renewal state back to Idle.
     */
    fun resetState() {
        _renewalState.value = RenewalState.Idle
    }

    /**
     * Gets the current logged-in user's ID.
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
