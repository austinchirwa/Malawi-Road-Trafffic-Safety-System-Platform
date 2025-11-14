package ApplicationScreens

import ApplicationScreens.viewmodels.AuthViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme

@Composable
fun ProfileUpdateScreen(
    authViewModel: AuthViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onSaveChanges: () -> Unit = {}
) {
    val user by authViewModel.currentUser.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()

    var name by remember(user) { mutableStateOf(user?.username ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var dob by remember(user) { mutableStateOf(user?.dob ?: "") }
    var address by remember(user) { mutableStateOf(user?.address ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // When the user object is loaded, get the remote photo URL
    val photoLink = user?.photoUrl

    // Load user data when the screen is first displayed
    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }

    ProfileUpdateScreenContent(
        isLoading = isLoading,
        name = name,
        onNameChange = { name = it },
        email = email,
        onEmailChange = { email = it },
        dob = dob,
        onDobChange = { dob = it },
        address = address,
        onAddressChange = { address = it },
        imageUri = imageUri,
        photoLink = photoLink, // Pass the remote URL
        onImageChange = { imageUri = it },
        onNavigateBack = onNavigateBack,
        onNavigateToChangePassword = onNavigateToChangePassword,
        onSaveChanges = {
            // Pass all fields, including the new image URI, to the ViewModel
            authViewModel.updateUserProfile(name, dob, address, imageUri)
            onSaveChanges()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileUpdateScreenContent(
    isLoading: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    imageUri: Uri?,
    photoLink: String?,
    onImageChange: (Uri?) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onSaveChanges: () -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> onImageChange(uri) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading && name.isEmpty()) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Box(contentAlignment = Alignment.Center) {
                        // Use the new local URI if it exists, otherwise use the remote URL
                        val painter = rememberAsyncImagePainter(model = imageUri ?: photoLink)
                        Image(
                            painter = painter,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(120.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("Change Photo")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Full Name") }, leadingIcon = { Icon(Icons.Default.Person, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Email Address") }, leadingIcon = { Icon(Icons.Default.Email, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = false)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = dob, onValueChange = onDobChange, label = { Text("Date of Birth") }, placeholder = { Text("YYYY-MM-DD") }, leadingIcon = { Icon(Icons.Default.DateRange, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Physical Address") }, leadingIcon = { Icon(Icons.Default.LocationOn, null) }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = "••••••••",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is PressInteraction.Release) onNavigateToChangePassword()
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onSaveChanges,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("SAVE CHANGES", modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileUpdateScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        ProfileUpdateScreenContent(
            isLoading = false,
            name = "John Doe",
            onNameChange = {},
            email = "john.doe@example.com",
            onEmailChange = {},
            dob = "1990-01-01",
            onDobChange = {},
            address = "123 Main St, Lilongwe",
            onAddressChange = {},
            imageUri = null,
            photoLink = null, // Added for preview
            onImageChange = {},
            onNavigateBack = {},
            onNavigateToChangePassword = {},
            onSaveChanges = {}
        )
    }
}
