package ApplicationScreens.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Data class to represent a renewal application.
 */
data class RenewalApplication(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val fullName: String = "",
    val idNumber: String = "",
    val licenseNumber: String = "",
    val licenseCode: String = "",
    val dateOfBirth: String = "",
    val vehicleRegNumber: String = "",
    val trnNumber: String = "",
    val insuranceProvider: String = "",
    val status: String = "Pending", // Pending, Approved, Rejected
    val timestamp: Any = FieldValue.serverTimestamp(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Sealed class to represent the state of the renewal submission.
 */
sealed class RenewalState {
    object Idle : RenewalState()
    object Loading : RenewalState()
    data class Success(val message: String = "Application submitted! Proceeding to payment...") : RenewalState()
    data class Error(val message: String) : RenewalState()
}

class RenewalsViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val renewalsCollection = db.collection("renewals")

    private val _renewalState = MutableStateFlow<RenewalState>(RenewalState.Idle)
    val renewalState: StateFlow<RenewalState> = _renewalState

    /**
     * Submits a renewal application to Firestore.
     */
    fun submitApplication(
        fullName: String,
        idNumber: String,
        licenseNumber: String,
        licenseCode: String,
        dateOfBirth: String,
        vehicleRegNumber: String,
        trnNumber: String,
        insuranceProvider: String
    ) {
        viewModelScope.launch {
            _renewalState.value = RenewalState.Loading
            try {
                val user = auth.currentUser
                if (user == null) {
                    _renewalState.value = RenewalState.Error("User not authenticated")
                    return@launch
                }

                val application = hashMapOf(
                    "userId" to user.uid,
                    "userEmail" to (user.email ?: ""),
                    "fullName" to fullName,
                    "idNumber" to idNumber.uppercase(),
                    "licenseNumber" to licenseNumber.uppercase(),
                    "licenseCode" to licenseCode.uppercase(),
                    "dateOfBirth" to dateOfBirth,
                    "vehicleRegNumber" to vehicleRegNumber.uppercase(),
                    "trnNumber" to trnNumber.uppercase(),
                    "insuranceProvider" to insuranceProvider,
                    "status" to "Pending",
                    "timestamp" to FieldValue.serverTimestamp(),
                    "createdAt" to System.currentTimeMillis()
                )

                val docRef = renewalsCollection.add(application).await()
                
                _renewalState.value = RenewalState.Success("Application submitted successfully! Reference: ${docRef.id}")
            } catch (e: Exception) {
                _renewalState.value = RenewalState.Error(e.message ?: "Failed to submit application")
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
