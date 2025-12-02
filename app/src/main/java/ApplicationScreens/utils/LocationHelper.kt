package ApplicationScreens.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Utility class for handling location services
 */
class LocationHelper(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    /**
     * Check if location services are enabled on the device
     */
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    
    /**
     * Check if location permission is granted
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Get current location using FusedLocationProviderClient
     * Returns a formatted address string
     */
    suspend fun getCurrentLocation(): LocationResult {
        if (!hasLocationPermission()) {
            return LocationResult.Error("Location permission not granted")
        }
        
        if (!isLocationEnabled()) {
            return LocationResult.Error("Location services are disabled")
        }
        
        return try {
            val location = getCurrentLocationInternal()
            if (location != null) {
                LocationResult.Success(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    formattedAddress = formatLocation(location.latitude, location.longitude)
                )
            } else {
                LocationResult.Error("Unable to get location")
            }
        } catch (e: Exception) {
            LocationResult.Error(e.message ?: "Failed to get location")
        }
    }
    
    /**
     * Internal method to get current location
     */
    private suspend fun getCurrentLocationInternal(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            val cancellationTokenSource = CancellationTokenSource()
            
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        } catch (e: SecurityException) {
            continuation.resumeWithException(e)
        }
    }
    
    /**
     * Format location coordinates to a readable string
     */
    private fun formatLocation(latitude: Double, longitude: Double): String {
        return "Lat: ${String.format("%.6f", latitude)}, Lon: ${String.format("%.6f", longitude)}"
    }
    
    /**
     * Get address from coordinates using Geocoder (optional enhancement)
     * Note: This requires additional implementation with Geocoder API
     */
    fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        // For now, return formatted coordinates
        // In production, you would use Geocoder to get actual address
        return formatLocation(latitude, longitude)
    }
}

/**
 * Sealed class representing location result
 */
sealed class LocationResult {
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val formattedAddress: String
    ) : LocationResult()
    
    data class Error(val message: String) : LocationResult()
}
