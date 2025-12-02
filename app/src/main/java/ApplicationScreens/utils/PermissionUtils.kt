package ApplicationScreens.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Utility object for handling runtime permissions in the app
 */
object PermissionUtils {
    
    /**
     * Camera permission
     */
    const val CAMERA = Manifest.permission.CAMERA
    
    /**
     * Location permissions
     */
    const val LOCATION_FINE = Manifest.permission.ACCESS_FINE_LOCATION
    const val LOCATION_COARSE = Manifest.permission.ACCESS_COARSE_LOCATION
    
    /**
     * Storage/Media permissions (varies by Android version)
     */
    val STORAGE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    
    /**
     * Check if a single permission is granted
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Check if all permissions in an array are granted
     */
    fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }
    
    /**
     * Check if camera permission is granted
     */
    fun isCameraPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, CAMERA)
    }
    
    /**
     * Check if location permissions are granted
     */
    fun isLocationPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, LOCATION_FINE) ||
                isPermissionGranted(context, LOCATION_COARSE)
    }
    
    /**
     * Check if storage/media permissions are granted
     */
    fun isStoragePermissionGranted(context: Context): Boolean {
        return arePermissionsGranted(context, STORAGE_PERMISSIONS)
    }
}

/**
 * Composable function to request camera permission
 */
@Composable
fun RequestCameraPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }
    
    LaunchedEffect(Unit) {
        if (PermissionUtils.isCameraPermissionGranted(context)) {
            onPermissionGranted()
        } else {
            launcher.launch(PermissionUtils.CAMERA)
        }
    }
}

/**
 * Composable function to request location permissions
 */
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }
    
    LaunchedEffect(Unit) {
        if (PermissionUtils.isLocationPermissionGranted(context)) {
            onPermissionGranted()
        } else {
            launcher.launch(
                arrayOf(
                    PermissionUtils.LOCATION_FINE,
                    PermissionUtils.LOCATION_COARSE
                )
            )
        }
    }
}

/**
 * Composable function to request storage/media permissions
 */
@Composable
fun RequestStoragePermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit = {}
) {
    val context = LocalContext.current
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }
    
    LaunchedEffect(Unit) {
        if (PermissionUtils.isStoragePermissionGranted(context)) {
            onPermissionGranted()
        } else {
            launcher.launch(PermissionUtils.STORAGE_PERMISSIONS)
        }
    }
}

/**
 * Permission request launcher that can be used manually
 */
@Composable
fun rememberPermissionLauncher(
    onResult: (Map<String, Boolean>) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions(),
    onResult = onResult
)
