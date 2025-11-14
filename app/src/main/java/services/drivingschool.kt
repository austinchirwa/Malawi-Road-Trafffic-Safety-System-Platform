package services

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.malawiroadtraffficsafetysystem.ui.theme.MalawiRoadTraffficSafetySystemTheme

/**
 * A data class to hold all information about a driving school.
 * Note: In a real app, this list would be fetched from a database or API.
 */
data class DrivingSchool( 
    val id: String,
    val name: String,
    val description: String,
    val price: String,
    val duration: String,
    val rating: Float,
    val features: List<String>
)

// A static list of driving schools for demonstration purposes.
val allDrivingSchools = listOf(
    DrivingSchool(
        id = "premier",
        name = "Success Driving School",
        description = "Success Driving School is a government-certified institution with over 5 years of experience in producing safe and confident drivers. Our certified instructors provide both theoretical and practical lessons.",
        price = "MWK 550,000",
        duration = "4 Weeks",
        rating = 4.8f,
        features = listOf("Certified Instructors", "Dual Control Vehicles", "Flexible Scheduling")
    ),
    DrivingSchool(
        id = "safety_first",
        name = "Safety-First Driving Academy",
        description = "As our name suggests, we prioritize safety above all else. Our curriculum is designed to teach defensive driving techniques to handle any road situation.",
        price = "MWK 440,000",
        duration = "5 Weeks",
        rating = 4.6f,
        features = listOf("Defensive Driving Focus", "Modern Training Fleet", "Weekend Classes Available")
    ),
    DrivingSchool(
        id = "city_motors",
        name = "City Motors Driving College",
        description = "Located in Mzuzu, we offer comprehensive driving courses for all vehicle classes. Our goal is to make you a competent driver in urban environments.",
        price = "MWK 565,000",
        duration = "7 Weeks",
        rating = 4.7f,
        features = listOf("All Vehicle Classes", "City Driving Specialists", "Job Placement Assistance")
    )
)

/**
 * A screen displaying detailed information about a specific driving school.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrivingSchoolDetailScreen(
    schoolId: String?,
    onConfirm: () -> Unit,
    onBackClick: () -> Unit
) {
    val school = remember { allDrivingSchools.find { it.id == schoolId } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding -> // Use explicit 'innerPadding' name
        if (school != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Apply padding from Scaffold
                    .padding(16.dp)
            ) {
                // Header with school name and icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, "School", modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Text(text = school.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // School Information Card
                DetailCard(
                    icon = Icons.Default.Info,
                    title = "About this School",
                    content = school.description
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Key Details (Price, Duration, Rating)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    InfoChip("Price", school.price, Icons.Default.WorkspacePremium)
                    InfoChip("Duration", school.duration, Icons.Default.DateRange)
                    InfoChip("Rating", "${school.rating}/5.0", Icons.Default.Star)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Features Section
                Text("What's Included", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                school.features.forEach {
                    feature -> FeatureItem(feature)
                }
                
                Spacer(modifier = Modifier.weight(1f)) // Pushes button to bottom

                // Confirmation Button
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("CONFIRM")
                }
            }
        } else {
            // Fallback if school is not found
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Apply padding here too
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("School not found.", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = onBackClick) {
                    Text("Go Back")
                }
            }
        }
    }
}

@Composable
private fun DetailCard(icon: ImageVector, title: String, content: String) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun FeatureItem(feature: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(feature, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DrivingSchoolDetailScreenPreview() {
    MalawiRoadTraffficSafetySystemTheme {
        DrivingSchoolDetailScreen(schoolId = "premier", onConfirm = {}, onBackClick = {})
    }
}
