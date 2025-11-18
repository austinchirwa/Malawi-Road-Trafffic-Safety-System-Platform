package ApplicationScreens.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Data class to hold user profile information.
 */
data class UserData(
    val username: String = "",
    val email: String = "",
    val dob: String = "",
    val address: String = "",
    val photoUrl: String = "" // Field for the profile image URL
)

/**
 * Data class to represent the result of an authentication operation.
 */
data class AuthResult(
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val _authResult = MutableStateFlow<AuthResult?>(null)
    val authResult: StateFlow<AuthResult?> = _authResult

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser: StateFlow<UserData?> = _currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val user = UserData(username = username, email = email)
                    db.collection("users").document(firebaseUser.uid).set(user).await()
                    _currentUser.value = user
                }
                _authResult.value = AuthResult(isSuccess = true)
            } catch (e: Exception) {
                _authResult.value = AuthResult(isError = true, errorMessage = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logIn(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                loadCurrentUser()
                _authResult.value = AuthResult(isSuccess = true)
            } catch (e: Exception) {
                _authResult.value = AuthResult(isError = true, errorMessage = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.sendPasswordResetEmail(email).await()
                _authResult.value = AuthResult(isSuccess = true)
            } catch (e: Exception) {
                _authResult.value = AuthResult(isError = true, errorMessage = e.message)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    val doc = db.collection("users").document(firebaseUser.uid).get().await()
                    _currentUser.value = doc.toObject(UserData::class.java)
                }
            } catch (e: Exception) {
                // Handle exceptions
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Updates the user's profile, including uploading a new profile image if provided.
     */
    fun updateUserProfile(name: String, dob: String, address: String, imageUri: Uri?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    // 1. Upload image if a new one is selected
                    val imageUrl = if (imageUri != null) {
                        uploadProfileImage(firebaseUser.uid, imageUri)
                    } else {
                        _currentUser.value?.photoUrl // Keep the existing URL if no new image
                    }

                    // 2. Create a map of fields to update
                    val updates = mutableMapOf<String, Any>(
                        "username" to name,
                        "dob" to dob,
                        "address" to address
                    )
                    if (imageUrl != null) {
                        updates["photoUrl"] = imageUrl
                    }

                    // 3. Update Firestore
                    db.collection("users").document(firebaseUser.uid).update(updates).await()
                    loadCurrentUser() // Refresh data after update
                }
            } catch (e: Exception) {
                // Handle exceptions
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Uploads an image to Firebase Storage and returns the download URL.
     */
    private suspend fun uploadProfileImage(userId: String, imageUri: Uri): String {
        val storageRef = storage.reference.child("profile_images/$userId.jpg")
        storageRef.putFile(imageUri).await()
        return storageRef.downloadUrl.await().toString()
    }

    fun resetAuthResult() {
        _authResult.value = null
    }

    /**
     * A local, self-contained version of the .await() function.
     */
    private suspend fun <T> Task<T>.await(): T {
        if (isComplete) {
            val e = exception
            return if (e == null) {
                if (isCanceled) throw java.util.concurrent.CancellationException("Task was cancelled.")
                else result
            } else {
                throw e
            }
        }

        return suspendCancellableCoroutine { cont ->
            addOnCompleteListener { task ->
                val e = task.exception
                if (e == null) {
                    if (task.isCanceled) cont.cancel()
                    else cont.resume(task.result)
                } else {
                    cont.resumeWithException(e)
                }
            }
        }
    }
}
